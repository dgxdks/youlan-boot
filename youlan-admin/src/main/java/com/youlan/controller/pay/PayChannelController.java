package com.youlan.controller.pay;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.controller.base.BaseController;
import com.youlan.plugin.pay.entity.PayChannel;
import com.youlan.plugin.pay.service.PayChannelService;
import com.youlan.system.anno.OperationLog;
import com.youlan.system.constant.OperationLogType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Tag(name = "支付通道")
@RestController
@RequestMapping("/pay/payChannel")
@AllArgsConstructor
public class PayChannelController extends BaseController {

    private final PayChannelService payChannelService;

    @SaCheckPermission("pay:payChannel:add")
    @Operation(summary = "支付通道新增")
    @OperationLog(name = "支付通道", type = OperationLogType.OPERATION_LOG_TYPE_ADD)
    @PostMapping("/addPayChannel")
    public ApiResult addPayChannel(@Validated @RequestBody PayChannel payChannel) {
        return toSuccess();
    }

    @SaCheckPermission("pay:payChannel:update")
    @Operation(summary = "支付通道修改")
    @PostMapping("/updatePayChannel")
    @OperationLog(name = "支付通道", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updatePayChannel(@Validated @RequestBody PayChannel payChannel) {
        if (ObjectUtil.isNull(payChannel.getId())) {
            return toError(ApiResultCode.C0001);
        }
        return toSuccess();
    }

    @SaCheckPermission("pay:payChannel:remove")
    @Operation(summary = "支付通道删除")
    @PostMapping("/removePayChannel")
    @OperationLog(name = "支付通道", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removePayChannel(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        return toSuccess();
    }

    @SaCheckPermission("pay:payChannel:load")
    @Operation(summary = "支付通道详情")
    @PostMapping("/loadPayChannel")
    public ApiResult loadPayChannel(@RequestParam Long id) {
        return toSuccess();
    }

    @SaCheckPermission("pay:payChannel:list")
    @Operation(summary = "支付通道分页")
    @PostMapping("/getPayChannelPageList")
    @OperationLog(name = "支付通道", type = OperationLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getPayChannelPageList(@RequestBody PayChannel payChannel) {
        return toSuccess();
    }

    @SaCheckPermission("pay:payChannel:export")
    @Operation(summary = "支付通道导出")
    @PostMapping("/exportPayChannelList")
    @OperationLog(name = "支付通道", type = OperationLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportPayChannelList(@RequestBody PayChannel payChannel, HttpServletResponse response) throws IOException {
        return;
    }

    @SaCheckPermission("pay:payChannel:update")
    @Operation(summary = "支付通道状态修改")
    @PostMapping("/updatePayChannelStatus")
    @OperationLog(name = "支付通道", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updatePayChannelStatus(@RequestParam Long id, @RequestParam String status) {
        return toSuccess();
    }

}
