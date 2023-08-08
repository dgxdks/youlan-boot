package com.youlan.system.service;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.Platform;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.plugin.region.helper.RegionHelper;
import com.youlan.system.entity.LoginLog;
import com.youlan.system.enums.LoginStatus;
import com.youlan.system.enums.SourceType;
import com.youlan.system.mapper.LoginLogMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class LoginLogService extends BaseServiceImpl<LoginLogMapper, LoginLog> {

    /**
     * 新增登录日志(异步)
     */
    public void addAsync(String userName, String sourceType, String loginStatus, String loginMsg) {
        String userAgent = ServletHelper.getUserAgent();
        String loginIp = ServletHelper.getClientIp();
        String loginLocation = RegionHelper.ip2Region(loginIp);
        UserAgent userAgentParse = UserAgentUtil.parse(userAgent);
        if (StrUtil.isBlank(sourceType)) {
            Platform userPlatform = userAgentParse.getPlatform();
            if (Platform.desktopPlatforms.contains(userPlatform)) {
                sourceType = SourceType.PC.getCode();
            } else if (Platform.mobilePlatforms.contains(userPlatform)) {
                sourceType = SourceType.MOBILE.getCode();
            } else {
                sourceType = SourceType.OTHER.getCode();
            }
        }
        LoginLog loginLog = new LoginLog();
        loginLog.setUserName(userName);
        loginLog.setSourceType(sourceType);
        loginLog.setLoginIp(loginIp);
        loginLog.setLoginLocation(loginLocation);
        loginLog.setLoginStatus(loginStatus);
        loginLog.setLoginTime(new Date());
        loginLog.setLoginMsg(loginMsg);
        loginLog.setUserAgent(userAgent);
        ThreadUtil.execAsync(() -> this.save(loginLog));
    }

    /**
     * 新增登录日志(异步)
     */
    public void addAsync(String userName, String loginStatus, String loginMsg) {
        addAsync(userName, null, loginStatus, loginMsg);
    }

    /**
     * 新增登录日志(异步)
     */
    public void addAsync(String userName, LoginStatus loginStatus, String loginMsg) {
        addAsync(userName, loginStatus.getCode(), StrUtil.blankToDefault(loginMsg, loginStatus.getText()));
    }
}

