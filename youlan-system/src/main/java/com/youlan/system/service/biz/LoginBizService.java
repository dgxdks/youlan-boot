package com.youlan.system.service.biz;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.youlan.common.captcha.helper.CaptchaHelper;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.helper.ListHelper;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.plugin.region.helper.RegionHelper;
import com.youlan.system.entity.Org;
import com.youlan.system.entity.User;
import com.youlan.system.entity.auth.SystemAuthInfo;
import com.youlan.system.entity.dto.AccountLoginDTO;
import com.youlan.system.entity.dto.OnlineUserPageDTO;
import com.youlan.system.entity.vo.LoginInfoVO;
import com.youlan.system.entity.vo.OnlineUserVO;
import com.youlan.system.entity.vo.UserVO;
import com.youlan.system.enums.LoginStatus;
import com.youlan.system.helper.SystemAuthHelper;
import com.youlan.system.helper.SystemConfigHelper;
import com.youlan.system.service.LoginLogService;
import com.youlan.system.service.OrgService;
import com.youlan.system.service.UserService;
import com.youlan.system.utils.SystemUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.youlan.common.core.restful.enums.ApiResultCode.A0002;
import static com.youlan.system.constant.SystemConstant.CONFIG_VALUE_LOGIN_RETRY_STRATEGY_USERNAME_IP;

@Slf4j
@Service
@AllArgsConstructor
public class LoginBizService {
    private final UserService userService;
    private final OrgService orgService;
    private final LoginLogService loginLogService;
    private final RedisHelper redisHelper;

    /**
     * 用户登录
     */
    public SaTokenInfo accountLogin(AccountLoginDTO dto) {
        doImageCaptchaCheck(dto.getUserName(), dto.getCaptchaId(), dto.getCaptchaCode());
        String userName = dto.getUserName();
        String plainTextPassword = dto.getUserPassword();
        User user = userService.loadUserByUserNameOpt(userName)
                .orElseThrow(() -> new BizRuntimeException(ApiResultCode.A0001));
        //判断用户是否被禁用
        if (userService.userIsDisabled(user)) {
            throw new BizRuntimeException(ApiResultCode.A0009);
        }
        Supplier<Boolean> passwordMatch = () -> userService.validUserPassword(plainTextPassword, user.getUserPassword());
        //用户登录检查
        doAccountLoginCheck(user, passwordMatch);
        //更新用户登录信息
        userService.updateUserLoginInfo(user.getId());
        //发起用户登录并返回Token信息
        SystemAuthInfo systemAuthInfo = createSystemAuthInfo(user);
        SystemAuthHelper.login(user.getId());
        SystemAuthHelper.setSystemAuthInfo(systemAuthInfo);
        return SystemAuthHelper.getTokenInfo();
    }

    /**
     * 图片验证码校验
     */
    public void doImageCaptchaCheck(String userName, String captchaId, String captchaCode) {
        //只有系统开启验证码功能才校验
        boolean captchaEnabled = SystemConfigHelper.captchaImageEnabled();
        if (!captchaEnabled) {
            return;
        }
        if (!StrUtil.isAllNotBlank(captchaId, captchaCode)) {
            throw new BizRuntimeException(ApiResultCode.A0010);
        }
        boolean verifyCaptcha = CaptchaHelper.verifyCaptcha(captchaId, captchaCode);
        if (!verifyCaptcha && StrUtil.isNotBlank(userName)) {
            loginLogService.addAsync(userName, LoginStatus.FAILED, ApiResultCode.A0007.getErrorMsg());
            throw new BizRuntimeException(ApiResultCode.A0007);
        }
    }

    /**
     * 登录校验
     */
    public void doAccountLoginCheck(User user, Supplier<Boolean> passwordMatch) {
        //校验登录重试次数,小于等于0则不判断登录重试次数
        int loginMaxRetryTimes = SystemConfigHelper.loginMaxRetryTimes();
        if (loginMaxRetryTimes <= 0) {
            //不处理重试逻辑但是要处理密码是否匹配逻辑
            if (passwordMatch.get()) {
                //更新用户登录信息
                userService.updateUserLoginInfo(user.getId());
                return;
            }
            loginLogService.addAsync(user.getUserName(), LoginStatus.FAILED.getCode(), A0002.getErrorMsg());
            throw new BizRuntimeException(A0002);
        }
        //根据登录重试策略生成redisKey
        String loginRetryStrategy = SystemConfigHelper.loginRetryStrategy();
        String loginRetryKey;
        if (CONFIG_VALUE_LOGIN_RETRY_STRATEGY_USERNAME_IP.equals(loginRetryStrategy)) {
            loginRetryKey = SystemUtil.getLoginRetryRedisKey(user.getUserName(), ServletHelper.getClientIp());
        } else {
            loginRetryKey = SystemUtil.getLoginRetryRedisKey(user.getUserName());
        }
        Integer retryCount = ObjectUtil.defaultIfNull(redisHelper.get(loginRetryKey), 0);
        //超出重试次数则不允许登录
        if (retryCount > loginMaxRetryTimes) {
            loginLogService.addAsync(user.getUserName(), LoginStatus.LOCKED.getCode(), ApiResultCode.A0006.getErrorMsg());
            throw new BizRuntimeException(ApiResultCode.A0006);
        }
        if (!passwordMatch.get()) {
            retryCount++;
            int loginLockTime = SystemConfigHelper.loginLockTime();
            redisHelper.set(loginRetryKey, retryCount, loginLockTime, TimeUnit.SECONDS);
            //密码匹配失败重试次数+1后需在判断一次是否超过重试次数
            if (retryCount > loginMaxRetryTimes) {
                loginLogService.addAsync(user.getUserName(), LoginStatus.LOCKED.getCode(), ApiResultCode.A0006.getErrorMsg());
                throw new BizRuntimeException(ApiResultCode.A0006);
            } else {
                loginLogService.addAsync(user.getUserName(), LoginStatus.FAILED.getCode(), A0002.getErrorMsg());
                throw new BizRuntimeException(A0002);
            }
        }
        redisHelper.delete(loginRetryKey);
    }

    /**
     * 构建用户登录信息
     */
    public SystemAuthInfo createSystemAuthInfo(User user) {
        Org org = orgService.loadOrgIfExist(user.getOrgId());
        SystemAuthInfo systemAuthInfo = new SystemAuthInfo()
                .setOrgId(org.getOrgId())
                .setOrgType(org.getOrgType())
                .setUserName(user.getUserName())
                .setUserId(user.getId())
                .setLoginTime(new Date());
        try {
            String loginIp = ServletHelper.getClientIp();
            String userAgent = ServletHelper.getUserAgent();
            systemAuthInfo.setLoginIp(loginIp)
                    .setUserAgent(userAgent);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return systemAuthInfo;
    }

    /**
     * 用户注销
     */
    public void accountLogout() {
        SystemAuthHelper.logout();
    }

    /**
     * 用户登录信息
     */
    public LoginInfoVO getLoginInfo() {
        Long userId = SystemAuthHelper.getUserId();
        UserVO user = userService.loadOne(userId, UserVO.class);
        List<String> roleList = SystemAuthHelper.getRoleStrList(userId);
        List<String> permissionList = SystemAuthHelper.getUserMenuPerms(userId);
        return new LoginInfoVO().setUser(user)
                .setRoleList(roleList)
                .setPermissionList(permissionList);
    }

    /**
     * 解锁用户登录
     */
    public void unlockLoginUser(String userName) {
        redisHelper.deleteByPattern(SystemUtil.getLoginRetryRedisKey(userName) + StringPool.ASTERISK);
    }

    /**
     * 获取在线用户分页列表
     */
    public IPage<OnlineUserVO> getOnlineUserPageList(OnlineUserPageDTO dto) {
        IPage<OnlineUserVO> page = DBHelper.getPage(dto);
        List<String> tokenValueList = null;
        //如果指定了用户名称则先查询用户ID
        String userName = dto.getUserName();
        if (StrUtil.isNotBlank(userName)) {
            User user = userService.loadUserByUserName(userName);
            if (ObjectUtil.isNull(user)) {
                return page;
            }
            tokenValueList = SystemAuthHelper.getTokenValueList(user.getId());
        } else {
            long start = page.offset();
            tokenValueList = SystemAuthHelper.searchTokenValue(StrUtil.EMPTY, Math.toIntExact(start), (int) page.getSize(), !dto.getIsDesc());
        }
        //通过tokenValue查询token信息
        List<OnlineUserVO> onlineUserList = new ArrayList<>();
        for (String tokenValue : tokenValueList) {
            SystemAuthInfo systemAuthInfo = SystemAuthHelper.getSystemAuthInfoByTokenValue(tokenValue);
            if (ObjectUtil.isNull(systemAuthInfo)) {
                systemAuthInfo = new SystemAuthInfo();
            }
            OnlineUserVO onlineUser = BeanUtil.copyProperties(systemAuthInfo, OnlineUserVO.class);
            onlineUser.setTokenValue(tokenValue);
            onlineUserList.add(onlineUser);
        }
        List<Long> orgIdList = ListHelper.mapList(onlineUserList, OnlineUserVO::getOrgId);
        Map<Long, String> orgNameMap = orgService.loadOrgNameMapByOrgIdList(orgIdList);
        onlineUserList.forEach(onlineUser -> {
            //设置机构名称
            onlineUser.setOrgName(orgNameMap.get(onlineUser.getOrgId()));
            // 设置位置信息
            if (StrUtil.isNotBlank(onlineUser.getLoginIp())) {
                onlineUser.setLoginLocation(RegionHelper.ip2Region(onlineUser.getLoginIp()));
            }
            //设置浏览器信息
            String userAgent = onlineUser.getUserAgent();
            if (StrUtil.isNotBlank(userAgent)) {
                UserAgent userAgentParse = UserAgentUtil.parse(userAgent);
                onlineUser.setOs(userAgentParse.getOs().getName());
                onlineUser.setBrowser(userAgentParse.getBrowser().getName());
            }
        });
        return page.setRecords(onlineUserList);
    }
}
