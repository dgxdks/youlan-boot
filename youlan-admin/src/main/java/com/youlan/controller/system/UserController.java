package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.excel.helper.ExcelHelper;
import com.youlan.common.validator.Insert;
import com.youlan.common.validator.Update;
import com.youlan.framework.anno.SystemLog;
import com.youlan.framework.constant.SystemLogType;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.dto.UserDTO;
import com.youlan.system.entity.dto.UserPageDTO;
import com.youlan.system.entity.dto.UserResetPasswdDTO;
import com.youlan.system.entity.vo.UserTemplateVO;
import com.youlan.system.entity.vo.UserVO;
import com.youlan.system.excel.listener.UserExcelListener;
import com.youlan.system.service.UserService;
import com.youlan.system.service.biz.UserBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
@AllArgsConstructor
public class UserController extends BaseController {
    private final UserBizService userBizService;
    private final UserService userService;

    @SaCheckPermission("system:user:add")
    @Operation(summary = "用户新增")
    @PostMapping("/addUser")
    @SystemLog(name = "用户", type = SystemLogType.OPERATION_LOG_TYPE_ADD)
    public ApiResult addUser(@Validated(Insert.class) @RequestBody UserDTO dto) {
        return toSuccess(userBizService.addUser(dto));
    }

    @SaCheckPermission("system:user:update")
    @Operation(summary = "用户修改")
    @PostMapping("/updateUser")
    @SystemLog(name = "用户", type = SystemLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateUser(@Validated(Update.class) @RequestBody UserDTO dto) {
        // TODO: 2023/8/28 数据权限
        if (ObjectUtil.isNull(dto.getId())) {
            return toError(ApiResultCode.C0001);
        }
        checkUserNotAdmin(dto.getId());
        return toSuccess(userBizService.updateUser(dto));
    }

    @SaCheckPermission("system:user:remove")
    @Operation(summary = "用户删除")
    @PostMapping("/removeUser")
    @SystemLog(name = "用户", type = SystemLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeUser(@Validated @RequestBody ListDTO<Long> dto) {
        // TODO: 2023/8/28 数据权限
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        //不能删除自己
        if (CollectionUtil.contains(dto.getList(), getUserId())) {
            throw new BizRuntimeException(ApiResultCode.A0012);
        }
        dto.getList().forEach(this::checkUserNotAdmin);
        return toSuccess(userBizService.removeUser(dto.getList()));
    }

    @SaCheckPermission("system:user:load")
    @Operation(summary = "用户详情")
    @PostMapping("/loadUser")
    public ApiResult loadUser(@RequestParam(required = false) Long id) {
        // TODO: 2023/8/15 数据权限
        return toSuccess(userBizService.loadUser(id));
    }

    @SaCheckPermission("system:user:list")
    @Operation(summary = "用户分页")
    @PostMapping("/getUserPageList")
    @SystemLog(name = "用户", type = SystemLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getUserPageList(@RequestBody UserPageDTO dto) {
        // TODO: 2023/8/30 缺少数据权限
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
    public ApiResult importUserList(@RequestPart("file") MultipartFile file, boolean cover) throws IOException {
        UserExcelListener userExcelListener = new UserExcelListener(cover);
        ExcelHelper.read(file.getInputStream(), UserTemplateVO.class, userExcelListener)
                .sheet()
                .doRead();
        return toSuccess(userExcelListener.getResultMsg());
    }

    @SaCheckPermission("system:user:resetPasswd")
    @Operation(summary = "用户密码重置")
    @PostMapping("/resetUserPasswd")
    @SystemLog(name = "用户", type = SystemLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult resetUserPasswd(@Validated @RequestBody UserResetPasswdDTO dto) {
        // TODO: 2023/8/23 数据权限
        if (ObjectUtil.isNull(dto.getId())) {
            return toError(ApiResultCode.C0001);
        }
        checkUserNotAdmin(dto.getId());
        return toSuccess(userBizService.resetUserPasswd(dto));
    }

    @SaCheckPermission("system.user.update")
    @Operation(summary = "用户状态修改")
    @PostMapping("/updateUserStatus")
    @SystemLog(name = "用户", type = SystemLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateUserStatus(@RequestParam Long id, @RequestParam String status) {
        // TODO: 2023/8/23 数据权限
        checkUserNotAdmin(id);
        return toSuccess(userService.updateStatus(id, status));
    }

    @Operation(summary = "下载用户导入模版")
    @PostMapping("/downloadUserTemplate")
    public void downloadUserTemplate() throws IOException {
        toExcel("用户数据", UserTemplateVO.class, new ArrayList<>());
    }

}
