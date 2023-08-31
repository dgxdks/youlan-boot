package com.youlan.system.service.biz;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.captcha.helper.CaptchaHelper;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.system.entity.Org;
import com.youlan.system.entity.User;
import com.youlan.system.entity.auth.SystemAuthInfo;
import com.youlan.system.entity.dto.AccountLoginDTO;
import com.youlan.system.entity.vo.LoginInfoVO;
import com.youlan.system.entity.vo.UserVO;
import com.youlan.system.enums.LoginStatus;
import com.youlan.system.helper.SystemAuthHelper;
import com.youlan.system.helper.SystemConfigHelper;
import com.youlan.system.service.LoginLogService;
import com.youlan.system.service.OrgService;
import com.youlan.system.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.youlan.common.core.restful.enums.ApiResultCode.A0002;
import static com.youlan.system.constant.SystemConstant.CONFIG_VALUE_LOGIN_RETRY_STRATEGY_USERNAME_IP;
import static com.youlan.system.constant.SystemConstant.REDIS_PREFIX_LOGIN_RETRY;

@Service
@AllArgsConstructor
public class LoginBizService {
    private final UserService userService;
    private final OrgService orgService;
    private final LoginLogService loginLogService;
    private final RedisHelper redisHelper;
    private final RoleBizService roleBizService;

    /**
     * 用户登录
     */
    public SaTokenInfo login(AccountLoginDTO dto) {
        //优先处理验证码逻辑,前提是系统开启的验证码功能
        boolean captchaEnabled = SystemConfigHelper.captchaImageEnabled();
        if (captchaEnabled) {
            doImageCaptchaCheck(dto.getCaptchaId(), dto.getCaptchaCode());
        }
        String userName = dto.getUserName();
        String plainTextPassword = dto.getUserPassword();
        User user = userService.loadUserByUserNameOpt(userName)
                .orElseThrow(() -> new BizRuntimeException(ApiResultCode.A0001));
        //判断用户是否被禁用
        if (userService.userIsDisabled(user)) {
            throw new BizRuntimeException(ApiResultCode.A0009);
        }
        Supplier<Boolean> passwordMatch = () -> userService.validUserPassword(plainTextPassword, user.getUserPassword());
        doLoginCheck(user, passwordMatch);
        //发起用户登录并返回Token信息
        SystemAuthInfo systemAuthInfo = createSystemAuthInfo(user);
        SystemAuthHelper.login(user.getId());
        SystemAuthHelper.setSystemAuthInfo(systemAuthInfo);
        return SystemAuthHelper.getTokenInfo();
    }

    /**
     * 图片验证码校验
     */
    public void doImageCaptchaCheck(String captchaId, String captchaCode) {
        if (!StrUtil.isAllNotBlank(captchaId, captchaCode)) {
            throw new BizRuntimeException(ApiResultCode.A0010);
        }
        boolean verifyCaptcha = CaptchaHelper.verifyCaptcha(captchaId, captchaCode);
        if (!verifyCaptcha) {
            throw new BizRuntimeException(ApiResultCode.A0007);
        }
    }

    /**
     * 登录校验
     */
    public void doLoginCheck(User user, Supplier<Boolean> passwordMatch) {
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
        String loginRetryKey = REDIS_PREFIX_LOGIN_RETRY + user.getUserName();
        if (CONFIG_VALUE_LOGIN_RETRY_STRATEGY_USERNAME_IP.equals(loginRetryStrategy)) {
            loginRetryKey = loginRetryKey + StrPool.COLON + ServletHelper.getClientIp();
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
        //更新用户登录信息
        userService.updateUserLoginInfo(user.getId());
        redisHelper.delete(loginRetryKey);
    }

    /**
     * 构建用户登录信息
     */
    public SystemAuthInfo createSystemAuthInfo(User user) {
        Org org = orgService.loadOrgIfExist(user.getOrgId());
        return new SystemAuthInfo()
                .setOrgId(org.getOrgId())
                .setOrgType(org.getOrgType())
                .setUserName(user.getUserName())
                .setUserId(user.getId());
    }

    /**
     * 用户注销
     */
    public void logout() {
        SystemAuthHelper.logout();
    }

    /**
     * 用户登录信息
     */
    public LoginInfoVO getLoginInfo() {
        Long userId = SystemAuthHelper.getUserId();
        UserVO user = userService.loadOne(userId, UserVO.class);
        List<String> roleList = SystemAuthHelper.getRoleStrList(userId);
        List<String> permissionList = SystemAuthHelper.getMenuPermsList(userId);
        return new LoginInfoVO().setUser(user)
                .setRoleList(roleList)
                .setPermissionList(permissionList);
    }
}
