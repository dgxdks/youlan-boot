package com.youlan.framework.aspect;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.framework.anno.SystemLog;
import com.youlan.framework.config.FrameworkProperties;
import com.youlan.plugin.region.helper.RegionHelper;
import com.youlan.system.constant.SystemConstant;
import com.youlan.system.entity.OperationLog;
import com.youlan.system.service.OperationLogService;
import com.youlan.system.helper.SystemAuthHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@Aspect
@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "youlan.framework.system-log", name = "enabled", havingValue = "true")
public class SystemLogAspect {
    private final FrameworkProperties frameworkProperties;
    private final OperationLogService operationLogService;

    @AfterReturning(pointcut = "@annotation(systemLog)", returning = "apiResult")
    public void afterReturning(JoinPoint joinPoint, SystemLog systemLog, Object apiResult) {
        handleOperationLog(joinPoint, systemLog, apiResult, null);
    }

    @AfterThrowing(pointcut = "@annotation(systemLog)", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, SystemLog systemLog, Exception exception) {
        handleOperationLog(joinPoint, systemLog, null, exception);
    }

    public void handleOperationLog(final JoinPoint joinPoint, SystemLog systemLog, Object apiResult, final Exception exception) {
        boolean enabled = frameworkProperties.getSystemLog().getEnabled();
        if (!enabled) {
            return;
        }
        boolean regionEnabled = frameworkProperties.getSystemLog().getRegionEnabled();
        boolean asyncEnabled = frameworkProperties.getSystemLog().getAsyncEnabled();
        HttpServletRequest httpServletRequest = ServletHelper.getHttpServletRequest();
        String method = joinPoint.getSignature().getName();
        String logName = systemLog.name();
        String logType = systemLog.type();
        Long logUser = SystemAuthHelper.getUserId();
        String logBy = SystemAuthHelper.getUserName();
        String httpMethod = httpServletRequest.getMethod();
        String httpQuery = httpServletRequest.getQueryString();
        String sourceIp = ServletUtil.getClientIP(httpServletRequest);
        boolean saveResponse = systemLog.saveResponse();
        boolean saveBody = systemLog.saveBody();
        boolean saveQuery = systemLog.saveQuery();
        OperationLog operationLog = new OperationLog();
        operationLog.setLogName(logName);
        operationLog.setLogType(logType);
        operationLog.setLogUser(logUser);
        operationLog.setLogBy(logBy);
        operationLog.setLogTime(new Date());
        operationLog.setLogStatus(SystemConstant.OPERATION_LOG_STATUS_OK);
        operationLog.setMethod(method);
        operationLog.setHttpMethod(httpMethod);
        operationLog.setSourceIp(sourceIp);
        if (saveQuery) {
            operationLog.setHttpQuery(httpQuery);
        }
        if (saveBody) {
            String httpBody = ServletHelper.getBody();
            operationLog.setHttpBody(httpBody);
        }
        if (saveResponse && ObjectUtil.isNotNull(apiResult)) {
            try {
                operationLog.setHttpResponse(JSONUtil.toJsonStr(apiResult));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        if (ObjectUtil.isNotNull(exception)) {
            int errorLengthLimit = frameworkProperties.getSystemLog().getErrorLengthLimit();
            operationLog.setLogStatus(SystemConstant.OPERATION_LOG_STATUS_ERROR);
            operationLog.setErrorMsg(ExceptionUtil.stacktraceToString(exception, errorLengthLimit));
        }
        if (regionEnabled) {
            String sourceLocation = RegionHelper.ip2Region(sourceIp);
            operationLog.setSourceLocation(sourceLocation);
        }
        if (asyncEnabled) {
            ThreadUtil.execAsync(() -> this.doSaveOperationLog(operationLog));
        } else {
            this.doSaveOperationLog(operationLog);
        }
    }

    public void doSaveOperationLog(OperationLog operationLog) {
        try {
            boolean save = operationLogService.save(operationLog);
            if (!save) {
                log.error("系统操作日志保存失败: {}", JSONUtil.toJsonStr(operationLog));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
