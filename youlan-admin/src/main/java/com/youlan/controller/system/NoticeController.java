package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.controller.base.BaseController;
import com.youlan.system.anno.OperationLog;
import com.youlan.system.constant.OperationLogType;
import com.youlan.system.entity.Notice;
import com.youlan.system.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "通知公告")
@RestController
@RequestMapping("/system/notice")
@AllArgsConstructor
public class NoticeController extends BaseController {
    private final NoticeService noticeService;

    @SaCheckPermission("system:notice:add")
    @Operation(summary = "通知公告新增")
    @OperationLog(name = "通知公告", type = OperationLogType.OPERATION_LOG_TYPE_ADD)
    @PostMapping("/addNotice")
    public ApiResult addNotice(@Validated @RequestBody Notice notice) {
        noticeService.save(notice);
        return toSuccess();
    }

    @SaCheckPermission("system:notice:update")
    @Operation(summary = "通知公告修改")
    @PostMapping("/updateNotice")
    @OperationLog(name = "通知公告", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateNotice(@Validated @RequestBody Notice notice) {
        if (ObjectUtil.isNull(notice.getId())) {
            return toError(ApiResultCode.C0001);
        }
        noticeService.updateById(notice);
        return toSuccess();
    }

    @SaCheckPermission("system:notice:remove")
    @Operation(summary = "通知公告删除")
    @PostMapping("/removeNotice")
    @OperationLog(name = "通知公告", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeNotice(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        noticeService.removeByIds(dto.getList());
        return toSuccess();
    }

    @SaCheckPermission("system:notice:load")
    @Operation(summary = "通知公告详情")
    @PostMapping("/loadNotice")
    public ApiResult loadNotice(@RequestParam Long id) {
        return toSuccess(noticeService.getById(id));
    }

    @SaCheckPermission("system:notice:list")
    @Operation(summary = "通知公告分页")
    @PostMapping("/getNoticePageList")
    @OperationLog(name = "通知公告", type = OperationLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getNoticePageList(@RequestBody Notice notice) {
        return toSuccess(noticeService.loadPage(notice, DBHelper.getQueryWrapper(notice)));
    }
}
