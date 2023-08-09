package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.framework.anno.SystemLog;
import com.youlan.framework.constant.SystemLogType;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.dto.UserDTO;
import com.youlan.system.entity.dto.UserPageDTO;
import com.youlan.system.entity.dto.UserPasswdDTO;
import com.youlan.system.entity.vo.UserVO;
import com.youlan.system.service.biz.UserBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
@AllArgsConstructor
public class UserController extends BaseController {
    private final UserBizService userBizService;

    @SaCheckPermission("system:user:add")
    @Operation(summary = "用户新增")
    @PostMapping("/addUser")
    @SystemLog(name = "用户", type = SystemLogType.OPERATION_LOG_TYPE_ADD)
    public ApiResult addUser(@Validated @RequestBody UserDTO dto) {
        return toSuccess(userBizService.addUser(dto));
    }

    @SaCheckPermission("system:user:update")
    @Operation(summary = "用户修改")
    @PostMapping("/updateUser")
    @SystemLog(name = "用户", type = SystemLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateUser(@Validated @RequestBody UserDTO dto) {
        if (ObjectUtil.isNull(dto.getId())) {
            return toError(ApiResultCode.C0001);
        }
        return toSuccess(userBizService.updateUser(dto));
    }

    @SaCheckPermission("system:user:remove")
    @Operation(summary = "用户删除")
    @PostMapping("/removeUser")
    @SystemLog(name = "用户", type = SystemLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeUser(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        return toSuccess(userBizService.removeUser(dto.getList()));
    }

    @SaCheckPermission("system:user:load")
    @Operation(summary = "用户详情")
    @PostMapping("/loadUser")
    public ApiResult loadUser(@RequestParam Long id) {
        return toSuccess(userBizService.loadUser(id));
    }

    @SaCheckPermission("system:user:pageList")
    @Operation(summary = "用户分页")
    @PostMapping("/getUserPageList")
    @SystemLog(name = "用户", type = SystemLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getUserPageList(@RequestBody UserPageDTO dto) {
        return toSuccess(userBizService.getUserPageList(dto));
    }

    @SaCheckPermission("system:user:export")
    @Operation(summary = "用户导出")
    @PostMapping("/exportUserList")
    @SystemLog(name = "用户", type = SystemLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportUserList(@RequestBody UserPageDTO dto, HttpServletResponse response) throws IOException {
        List<UserVO> userList = userBizService.exportUserList(dto);
        toExcel("用户.xlsx", "用户", UserVO.class, userList, response);
    }

    @SaCheckPermission("system:user:import")
    @Operation(summary = "用户导入")
    @PostMapping("/importUserList")
    @SystemLog(name = "用户", type = SystemLogType.OPERATION_LOG_TYPE_IMPORT)
    public void importUserList() {

    }

    @SaCheckPermission("system:user:updatePasswd")
    @Operation(summary = "用户密码修改")
    @PostMapping("/updateUserPasswd")
    @SystemLog(name = "用户", type = SystemLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateUserPasswd(@Validated @RequestBody UserPasswdDTO dto) {
        return toSuccess(userBizService.updateUserPasswd(dto));
    }
}
