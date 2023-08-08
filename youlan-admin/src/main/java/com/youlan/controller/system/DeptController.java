package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.entity.dto.ListDTO;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.utils.QueryWrapperUtil;
import com.youlan.framework.anno.SystemLog;
import com.youlan.framework.constant.SystemLogType;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.Dept;
import com.youlan.system.entity.dto.DeptDTO;
import com.youlan.system.entity.dto.OrgPageDTO;
import com.youlan.system.entity.vo.DeptVO;
import com.youlan.system.entity.vo.OrgVO;
import com.youlan.system.service.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "部门管理")
@RestController
@RequestMapping("/system/dept")
@AllArgsConstructor
public class DeptController extends BaseController {
    private final DeptService deptService;

    @SaCheckPermission("system:dept:addDept")
    @Operation(summary = "部门新增")
    @PostMapping("/addDept")
    @SystemLog(name = "部门管理", type = SystemLogType.OPERATION_LOG_TYPE_ADD)
    public ApiResult addDept(@Validated @RequestBody DeptDTO dto) {
        return toSuccess(deptService.save(BeanUtil.copyProperties(dto, Dept.class)));
    }

    @SaCheckPermission("system:dept:updateDept")
    @Operation(summary = "部门修改")
    @PostMapping("/updateDept")
    @SystemLog(name = "部门管理", type = SystemLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateDept(@Validated @RequestBody DeptDTO dto) {
        if (ObjectUtil.isNull(dto.getOrgId())) {
            return toError(ApiResultCode.C0001);
        }
        return toSuccess(deptService.updateById(BeanUtil.copyProperties(dto, Dept.class)));
    }

    @SaCheckPermission("system:dept:removeDept")
    @Operation(summary = "部门删除")
    @PostMapping("/removeDept")
    @SystemLog(name = "部门管理", type = SystemLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeDept(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        return toSuccess(deptService.removeBatchByIds(dto.getList()));
    }

    @SaCheckPermission("system:dept:loadDept")
    @Operation(summary = "部门详情")
    @PostMapping("/loadDept")
    public ApiResult loadDept(@RequestParam Long id) {
        return toSuccess(deptService.loadOne(id, OrgVO.class));
    }

    @SaCheckPermission("system:dept:getDeptPageList")
    @Operation(summary = "部门分页")
    @PostMapping("/getDeptPageList")
    @SystemLog(name = "部门管理", type = SystemLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getDeptPageList(@RequestBody OrgPageDTO dto) {
        return toSuccess(deptService.loadPage(dto, QueryWrapperUtil.getQueryWrapper(dto), DeptVO.class));
    }

    @SaCheckPermission("system:dept:getDeptTreeList")
    @Operation(summary = "部门树列表")
    @PostMapping("/getDeptTreeList")
    public ApiResult getDeptTreeList(@RequestBody OrgPageDTO dto) {
        return toSuccess(deptService.getOrgTreeList(dto, DeptVO.class));
    }
}
