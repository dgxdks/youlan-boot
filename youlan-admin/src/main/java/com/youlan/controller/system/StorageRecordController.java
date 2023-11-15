package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.common.storage.entity.StorageRecord;
import com.youlan.common.storage.service.StorageRecordService;
import com.youlan.controller.base.BaseController;
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

@Tag(name = "存储管理")
@RestController
@RequestMapping("/system/storageRecord")
@AllArgsConstructor
public class StorageRecordController extends BaseController {
    private final StorageRecordService storageRecordService;

    @SaCheckPermission("system:storageRecord:update")
    @Operation(summary = "存储记录修改")
    @PostMapping("/updateStorageRecord")
    @OperationLog(name = "存储记录", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateStorageRecord(@Validated @RequestBody StorageRecord storageRecord) {
        if (ObjectUtil.isNull(storageRecord.getId())) {
            return toError(ApiResultCode.C0001);
        }
        return toSuccess(storageRecordService.updateById(storageRecord));
    }

    @SaCheckPermission("system:storageRecord:remove")
    @Operation(summary = "存储记录删除")
    @PostMapping("/removeStorageRecord")
    @OperationLog(name = "存储记录", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeStorageRecord(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        return toSuccess(storageRecordService.removeBatchByIds(dto.getList()));
    }

    @SaCheckPermission("system:storageRecord:load")
    @Operation(summary = "存储记录详情")
    @PostMapping("/loadStorageRecord")
    public ApiResult loadStorageRecord(@RequestParam Long id) {
        return toSuccess(storageRecordService.loadStorageRecord(id));
    }

    @SaCheckPermission("system:storageRecord:list")
    @Operation(summary = "存储记录分页")
    @PostMapping("/getStorageRecordPageList")
    @OperationLog(name = "存储记录", type = OperationLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getStorageRecordPageList(@RequestBody StorageRecord storageRecord) {
        return toSuccess(storageRecordService.getStorageRecordPageList(storageRecord));
    }

    @SaCheckPermission("system:storageRecord:export")
    @Operation(summary = "存储记录导出")
    @PostMapping("/exportStorageRecordList")
    @OperationLog(name = "存储记录", type = OperationLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportStorageRecordList(@RequestBody StorageRecord storageRecord, HttpServletResponse response) throws IOException {
        List<StorageRecord> storageRecords = storageRecordService.loadMore(DBHelper.getQueryWrapper(storageRecord));
        toExcel("存储记录.xlsx", "存储记录", StorageRecord.class, storageRecords, response);
    }

    @SaCheckPermission("system:storageRecord:remove")
    @Operation(summary = "存储记录清空")
    @PostMapping("/cleanStorageRecordList")
    @OperationLog(name = "存储记录", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult cleanStorageRecordList() {
        storageRecordService.getBaseMapper().cleanStorageRecordList();
        return toSuccess();
    }
}
