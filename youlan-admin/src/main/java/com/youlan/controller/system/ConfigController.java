package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.entity.dto.ListDTO;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.utils.QueryWrapperUtil;
import com.youlan.framework.anno.SystemLog;
import com.youlan.framework.constant.SystemLogType;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.Config;
import com.youlan.system.service.ConfigService;
import com.youlan.system.service.biz.ConfigBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "系统配置")
@RestController
@RequestMapping("/system/config")
@AllArgsConstructor
public class ConfigController extends BaseController {
    private final ConfigService configService;
    private final ConfigBizService configBizService;

    @SaCheckPermission("system:config:addConfig")
    @Operation(summary = "系统配置新增")
    @SystemLog(name = "系统配置", type = SystemLogType.OPERATION_LOG_TYPE_ADD)
    @PostMapping("/addConfig")
    public ApiResult addConfig(@Validated @RequestBody Config config) {
        return toSuccess(configBizService.addConfig(config));
    }

    @SaCheckPermission("system:config:updateConfig")
    @Operation(summary = "系统配置修改")
    @PostMapping("/updateConfig")
    @SystemLog(name = "系统配置", type = SystemLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateConfig(@Validated @RequestBody Config config) {
        if (ObjectUtil.isNull(config.getId())) {
            return toError(ApiResultCode.C0001);
        }
        return toSuccess(configBizService.updateConfig(config));
    }

    @SaCheckPermission("system:config:updateConfig")
    @Operation(summary = "系统配置删除")
    @PostMapping("/removeConfig")
    @SystemLog(name = "系统配置", type = SystemLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeConfig(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        return toSuccess(configBizService.removeConfig(dto.getList()));
    }

    @SaCheckPermission("system:config:loadConfig")
    @Operation(summary = "系统配置详情")
    @PostMapping("/loadConfig")
    public ApiResult loadConfig(@RequestParam Long id) {
        return toSuccess(configService.loadOne(id));
    }

    @SaCheckPermission("system:config:getConfigPageList")
    @Operation(summary = "系统配置分页")
    @PostMapping("/getConfigPageList")
    @SystemLog(name = "系统配置", type = SystemLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getConfigPageList(@RequestBody Config config) {
        return toSuccess(configService.loadPage(config, QueryWrapperUtil.getQueryWrapper(config)));
    }
}