package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.framework.anno.SystemLog;
import com.youlan.framework.constant.SystemLogType;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.dto.DeptDTO;
import com.youlan.system.entity.dto.OrgPageDTO;
import com.youlan.system.entity.vo.DeptVO;
import com.youlan.system.helper.SystemAuthHelper;
import com.youlan.system.service.biz.DeptBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "部门管理")
@RestController
@RequestMapping("/system/dept")
@AllArgsConstructor
public class DeptController extends BaseController {
    private final DeptBizService deptBizService;

    @SaCheckPermission("system:dept:add")
    @Operation(summary = "部门新增")
    @PostMapping("/addDept")
    @SystemLog(name = "部门管理", type = SystemLogType.OPERATION_LOG_TYPE_ADD)
    public ApiResult addDept(@Validated @RequestBody DeptDTO dto) {
        deptBizService.addDept(dto);
        return toSuccess();
    }

    @SaCheckPermission("system:dept:update")
    @Operation(summary = "部门修改")
    @PostMapping("/updateDept")
    @SystemLog(name = "部门管理", type = SystemLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateDept(@Validated @RequestBody DeptDTO dto) {
        if (ObjectUtil.isNull(dto.getOrgId())) {
            return toError(ApiResultCode.C0001);
        }
        SystemAuthHelper.checkHasOrgId(dto.getOrgId());
        deptBizService.updateDept(dto);
        return toSuccess();
    }

    @SaCheckPermission("system:dept:remove")
    @Operation(summary = "部门删除")
    @PostMapping("/removeDept")
    @SystemLog(name = "部门管理", type = SystemLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeDept(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        deptBizService.removeDept(dto.getList());
        return toSuccess();
    }

    @SaCheckPermission("system:dept:load")
    @Operation(summary = "部门详情")
    @PostMapping("/loadDept")
    public ApiResult loadDept(@RequestParam Long id) {
        return toSuccess(deptBizService.loadDept(id));
    }

    @SaCheckPermission("system:dept:list")
    @Operation(summary = "部门分页")
    @PostMapping("/getDeptPageList")
    @SystemLog(name = "部门管理", type = SystemLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getDeptPageList(@RequestBody OrgPageDTO dto) {
        return toSuccess(deptBizService.getDeptPageList(dto));
    }

    @SaCheckPermission("system:dept:list")
    @Operation(summary = "部门树列表")
    @PostMapping("/getDeptTreeList")
    public ApiResult getDeptTreeList(@RequestBody OrgPageDTO dto) {
        List<DeptVO> deptList = deptBizService.getDeptTreeList(dto);
        return toSuccess(deptList);
    }
}
