package com.youlan.system.excel.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.stream.StreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.excel.listener.AbstractExcelListener;
import com.youlan.common.validator.helper.ValidatorHelper;
import com.youlan.system.entity.Org;
import com.youlan.system.entity.User;
import com.youlan.system.entity.dto.UserDTO;
import com.youlan.system.entity.vo.UserTemplateVO;
import com.youlan.system.helper.SystemAuthHelper;
import com.youlan.system.helper.SystemConfigHelper;
import com.youlan.system.service.OrgService;
import com.youlan.system.service.UserService;
import com.youlan.system.service.biz.UserBizService;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@Slf4j
public class UserExcelListener extends AbstractExcelListener<UserTemplateVO> {
    private final UserService userService = SpringUtil.getBean(UserService.class);
    private final OrgService orgService = SpringUtil.getBean(OrgService.class);
    private final UserBizService userBizService = SpringUtil.getBean(UserBizService.class);
    private final String initPassword = SystemConfigHelper.userInitPassword();
    private final StringBuilder resultMsg = new StringBuilder();
    private long resultCount = 0;
    private long successCount = 0;
    private long errorCount = 0;
    private boolean cover = false;

    public UserExcelListener() {

    }

    public UserExcelListener(boolean cover) {
        this.cover = cover;
    }

    @Override
    public void invoke(UserTemplateVO data, AnalysisContext context) {
        // 优先执行数据关联校验,抛出异常后在onException中处理
        ValidatorHelper.validateWithThrow(data);
        User user = userService.loadUserByUserName(data.getUserName());
        Org org = orgService.loadOne(data.getOrgId());
        try {
            if (ObjectUtil.isNull(org)) {
                this.errorCount++;
                this.addResultMsg(StrUtil.format("账号 {} 机构编码不存在", data.getUserName()));
            } else if (ObjectUtil.isNull(user)) {
                UserDTO userDTO = BeanUtil.copyProperties(data, UserDTO.class);
                userDTO.setUserPassword(this.initPassword);
                userDTO.setOrgId(org.getOrgId());
                userBizService.addUser(userDTO);
                this.successCount++;
                this.addResultMsg(StrUtil.format("账号 {} 导入成功", data.getUserName()));
            } else if (this.cover) {
                SystemAuthHelper.checkUserNotAdmin(user.getId());
                SystemAuthHelper.checkHasUserId(user.getId());
                UserDTO userDTO = BeanUtil.copyProperties(data, UserDTO.class);
                //更新要设置ID
                userDTO.setId(user.getId());
                userDTO.setOrgId(org.getOrgId());
                userBizService.updateUser(userDTO);
                this.successCount++;
                this.addResultMsg(StrUtil.format("账号 {} 更新成功", data.getUserName()));
            } else {
                this.errorCount++;
                this.addResultMsg(StrUtil.format("账号 {} 已经存在", data.getUserName()));
            }
        } catch (Exception e) {
            this.errorCount++;
            this.addResultMsg(StrUtil.format("账号 {} 导入失败", data.getUserName()));
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        String defaultErrorDesc = "数据解析异常";
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;
            Integer rowIndex = excelDataConvertException.getRowIndex();
            Integer columnIndex = excelDataConvertException.getColumnIndex();
            if (exception.getCause() instanceof BizRuntimeException) {
                defaultErrorDesc = exception.getCause().getMessage();
            }
            this.addResultMsg(StrUtil.format("第{}行-第{}列: [{}]{}", rowIndex + 1, columnIndex + 1, headMap.get(columnIndex), defaultErrorDesc));
        } else if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) exception;
            Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
            String validateMsg = StreamUtil.join(constraintViolations.stream().map(ConstraintViolation::getMessage), ", ");
            this.addResultMsg(StrUtil.format("第{}行: {}", context.readRowHolder().getRowIndex() + 1, validateMsg));
        } else {
            this.addResultMsg(StrUtil.format("第{}行: {}", context.readRowHolder().getRowIndex() + 1, defaultErrorDesc));
        }
        if (log.isDebugEnabled()) {
            log.error(exception.getMessage(), exception);
        } else {
            log.error(exception.getMessage());
        }
        this.errorCount++;
    }

    public void addResultMsg(String resultMsg) {
        this.resultCount++;
        this.resultMsg.append(StrUtil.format("{}.{}<br/>", this.resultCount, resultMsg));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        this.resultMsg.insert(0, StrUtil.format("导入完成, 共 {} 条数据, 成功 {} 条, 失败 {} 条<br/>", this.resultCount, this.successCount, this.errorCount));
    }

    public String getResultMsg() {
        return this.resultMsg.toString();
    }
}
