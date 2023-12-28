package com.youlan.controller.pay;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.controller.base.BaseController;
import com.youlan.plugin.pay.entity.PayRecord;
import com.youlan.plugin.pay.service.PayRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "支付记录")
@RestController
@RequestMapping("/pay/payRecord")
@AllArgsConstructor
public class PayRecordController extends BaseController {
    private final PayRecordService payRecordService;

    @SaCheckPermission("pay:payOrder:load")
    @Operation(summary = "支付记录详情")
    @PostMapping("/loadPayRecord")
    public ApiResult loadPayRecord(@RequestParam Long id) {
        return toSuccess(payRecordService.loadOne(id));
    }

    @SaCheckPermission("pay:payOrder:load")
    @Operation(summary = "支付记录分页")
    @PostMapping("/getPayRecordPageList")
    public ApiResult getPayRecordPageList(@RequestBody PayRecord payRecord) {
        List<String> sortColumns = List.of("create_time");
        IPage<PayRecord> page = DBHelper.getPage(payRecord, sortColumns);
        return toSuccess(payRecordService.getBaseMapper().getPayRecordPageList(page, payRecord));
    }

}
