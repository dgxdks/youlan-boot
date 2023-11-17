package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.controller.base.BaseController;
import com.youlan.plugin.sms.entity.SmsRecord;
import com.youlan.plugin.sms.service.SmsRecordService;
import com.youlan.system.anno.OperationLog;
import com.youlan.system.constant.OperationLogType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Tag(name = "短信管理")
@RestController
@RequestMapping("/system/smsRecord")
@AllArgsConstructor
public class SmsRecordController extends BaseController {
    private final SmsRecordService smsRecordService;

    @SaCheckPermission("system:smsRecord:update")
    @Operation(summary = "短信记录修改")
    @PostMapping("/updateSmsRecord")
    @OperationLog(name = "短信记录", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateSmsRecord(@Validated @RequestBody SmsRecord smsRecord) {
        if (ObjectUtil.isNull(smsRecord.getId())) {
            return toError(ApiResultCode.C0001);
        }
        return toSuccess(smsRecordService.updateById(smsRecord));
    }

    @SaCheckPermission("system:smsRecord:remove")
    @Operation(summary = "短信记录删除")
    @PostMapping("/removeSmsRecord")
    @OperationLog(name = "短信记录", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeSmsRecord(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        return toSuccess(smsRecordService.removeBatchByIds(dto.getList()));
    }

    @SaCheckPermission("system:smsRecord:load")
    @Operation(summary = "短信记录详情")
    @PostMapping("/loadSmsRecord")
    public ApiResult loadSmsRecord(@RequestParam Long id) {
        return toSuccess(smsRecordService.getById(id));
    }

    @SaCheckPermission("system:smsRecord:list")
    @Operation(summary = "短信记录分页")
    @PostMapping("/getSmsRecordPageList")
    @OperationLog(name = "短信记录", type = OperationLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getSmsRecordPageList(@RequestBody SmsRecord smsRecord) {
        List<String> sortColumns = List.of("send_time");
        IPage<SmsRecord> pageResult = smsRecordService.loadPage(smsRecord, sortColumns, DBHelper.getQueryWrapper(smsRecord));
        return toSuccess(pageResult);
    }

    @SaCheckPermission("system:smsRecord:export")
    @Operation(summary = "短信记录导出")
    @PostMapping("/exportSmsRecordList")
    @OperationLog(name = "短信记录", type = OperationLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportSmsRecordList(@RequestBody SmsRecord SmsRecord, HttpServletResponse response) throws IOException {
        List<SmsRecord> SmsRecords = smsRecordService.loadMore(DBHelper.getQueryWrapper(SmsRecord));
        toExcel("短信记录.xlsx", "短信记录", SmsRecord.class, SmsRecords, response);
    }

    @SaCheckPermission("system:smsRecord:remove")
    @Operation(summary = "短信记录清空")
    @PostMapping("/cleanSmsRecordList")
    @OperationLog(name = "短信记录", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult cleanSmsRecordList() {
        smsRecordService.getBaseMapper().cleanSmsRecordList();
        return toSuccess();
    }
}
