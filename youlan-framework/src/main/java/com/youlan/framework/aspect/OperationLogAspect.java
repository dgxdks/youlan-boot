package com.youlan.framework.aspect;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.framework.anno.OperationLog;
import com.youlan.framework.config.FrameworkProperties;
import com.youlan.plugin.region.helper.RegionHelper;
import com.youlan.system.constant.SystemConstant;
import com.youlan.system.helper.SystemAuthHelper;
import com.youlan.system.service.OperationLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@Aspect
@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "youlan.framework.operation-log", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OperationLogAspect {
    private static final ThreadLocal<Long> COST_TIME_THREADLOCAL = new ThreadLocal<>();
    private final FrameworkProperties frameworkProperties;
    private final OperationLogService operationLogService;

    @Before("@annotation(operationAnno)")
    public void before(JoinPoint joinPoint, OperationLog operationAnno) {
        COST_TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    @AfterReturning(pointcut = "@annotation(operationAnno)", returning = "apiResult")
    public void afterReturning(JoinPoint joinPoint, OperationLog operationAnno, Object apiResult) {
        handleOperationLog(joinPoint, operationAnno, apiResult, null);
    }

    @AfterThrowing(pointcut = "@annotation(operationAnno)", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, OperationLog operationAnno, Exception exception) {
        handleOperationLog(joinPoint, operationAnno, null, exception);
    }

    public void handleOperationLog(final JoinPoint joinPoint, OperationLog operationAnno, Object apiResult, final Exception exception) {
        try {
            //项目开关判断逻辑
            boolean enabled = frameworkProperties.getOperationLog().getEnabled();
            if (!enabled) {
                return;
            }
            //注解开关判断逻辑
            if (!operationAnno.enabled()) {
                return;
            }
            boolean regionEnabled = frameworkProperties.getOperationLog().getRegionEnabled();
            boolean asyncEnabled = frameworkProperties.getOperationLog().getAsyncEnabled();
            HttpServletRequest httpServletRequest = ServletHelper.getHttpServletRequest();
            String method = joinPoint.getSignature().getName();
            String logName = operationAnno.name();
            String logType = operationAnno.type();
            Long logUser = SystemAuthHelper.getUserId();
            String logBy = SystemAuthHelper.getUserName();
            String httpMethod = httpServletRequest.getMethod();
            String httpQuery = httpServletRequest.getQueryString();
            String httpUrl = httpServletRequest.getRequestURI();
            String sourceIp = ServletUtil.getClientIP(httpServletRequest);
            boolean saveResponse = operationAnno.saveResponse();
            boolean saveBody = operationAnno.saveBody();
            boolean saveQuery = operationAnno.saveQuery();
            com.youlan.system.entity.OperationLog operationLog = new com.youlan.system.entity.OperationLog();
            operationLog.setCostTime(System.currentTimeMillis() - COST_TIME_THREADLOCAL.get());
            operationLog.setHttpUrl(httpUrl);
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
                int errorLengthLimit = frameworkProperties.getOperationLog().getErrorLengthLimit();
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            COST_TIME_THREADLOCAL.remove();
        }
    }

    public void doSaveOperationLog(com.youlan.system.entity.OperationLog operationLog) {
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
