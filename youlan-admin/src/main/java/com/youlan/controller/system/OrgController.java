package com.youlan.controller.system;

import com.youlan.common.core.restful.ApiResult;
import com.youlan.framework.anno.OperationLog;
import com.youlan.framework.constant.OperationLogType;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.dto.OrgPageDTO;
import com.youlan.system.service.OrgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "机构管理")
@RestController
@RequestMapping("/system/org")
@AllArgsConstructor
public class OrgController extends BaseController {
    private final OrgService orgService;

    @Operation(summary = "机构树列表")
    @PostMapping("/getOrgTreeList")
    @OperationLog(name = "机构", type = OperationLogType.OPERATION_LOG_TYPE_LIST)
    public ApiResult getOrgTreeList(@RequestBody OrgPageDTO dto) {
        // TODO: 2023/9/4 缺少数据权限
        return toSuccess(orgService.getOrgTreeList(dto));
    }
}
