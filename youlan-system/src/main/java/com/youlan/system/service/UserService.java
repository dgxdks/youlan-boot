package com.youlan.system.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.constant.SystemConstant;
import com.youlan.system.entity.User;
import com.youlan.system.mapper.UserMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserService extends BaseServiceImpl<UserMapper, User> {

    /**
     * 更新用户登录信息
     */
    public void updateUserLoginInfo(Long userId) {
        this.lambdaUpdate()
                .eq(User::getId, userId)
                .set(User::getLoginIp, ServletHelper.getClientIp())
                .set(User::getLoginTime, new Date())
                .update();
    }

    /**
     * 如果用户存在返回用户信息
     */
    public User loadUserIfExist(Long userId) {
        return this.loadOneOpt(userId).orElseThrow(() -> new BizRuntimeException(ApiResultCode.A0018));
    }

    /**
     * 根据用户名获取用户信息
     */
    public User loadUserByUserName(String userName) {
        return this.lambdaQuery()
                .eq(User::getUserName, userName)
                .one();
    }

    /**
     * 根据用户名获取用户信息Optional
     */
    public Optional<User> loadUserByUserNameOpt(String userName) {
        return Optional.ofNullable(loadUserByUserName(userName));
    }

    /**
     * 用户是否被禁用
     */
    public boolean userIsDisabled(User user) {
        return DBConstant.VAL_STATUS_DISABLED.equals(user.getStatus());
    }

    /**
     * 是否为超级管理员用户
     */
    public boolean isSuperAdminUser(User user) {
        return isSuperAdminUser(user.getId());
    }

    /**
     * 是否为超级管理员用户
     */
    public boolean isSuperAdminUser(Long userId) {
        return SystemConstant.SUPER_ADMIN_USER_ID.equals(userId);
    }

    /**
     * 生成加密后的用户密码
     */
    public String genUserPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword);
    }

    /**
     * 验证用户密码
     */
    public boolean validUserPassword(String plainTextPassword, String userPassword) {
        return BCrypt.checkpw(plainTextPassword, userPassword);
    }

    /**
     * 校验用户名是否重复
     */
    public void checkUserNameRepeat(User user) {
        String userName = user.getUserName();
        List<User> userList = this.lambdaQuery()
                .select(User::getId)
                .eq(User::getUserName, userName)
                .list();
        if (CollectionUtil.isEmpty(userList)) {
            return;
        }
        for (User retUser : userList) {
            if (!retUser.getId().equals(user.getId())) {
                throw new BizRuntimeException(ApiResultCode.A0019);
            }
        }
    }

    /**
     * 校验手机号是否重复
     */
    public void checkUserMobileRepeat(User user) {
        String userMobile = user.getUserMobile();
        if (StrUtil.isBlank(userMobile)) {
            return;
        }
        List<User> userList = this.lambdaQuery()
                .select(User::getId)
                .eq(User::getUserMobile, userMobile)
                .list();
        if (CollectionUtil.isEmpty(userList)) {
            return;
        }
        for (User retUser : userList) {
            if (!retUser.getId().equals(user.getId())) {
                throw new BizRuntimeException(ApiResultCode.A0020);
            }
        }
    }
}
