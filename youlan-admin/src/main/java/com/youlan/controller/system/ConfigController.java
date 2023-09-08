package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.framework.anno.OperationLog;
import com.youlan.framework.constant.OperationLogType;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.Config;
import com.youlan.system.service.ConfigService;
import com.youlan.system.service.biz.ConfigBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Tag(name = "系统配置")
@RestController
@RequestMapping("/system/config")
@AllArgsConstructor
public class ConfigController extends BaseController {
    private final ConfigService configService;
    private final ConfigBizService configBizService;

    @SaCheckPermission("system:config:add")
    @Operation(summary = "系统配置新增")
    @OperationLog(name = "系统配置", type = OperationLogType.OPERATION_LOG_TYPE_ADD)
    @PostMapping("/addConfig")
    public ApiResult addConfig(@Validated @RequestBody Config config) {
        configBizService.addConfig(config);
        return toSuccess();
    }

    @SaCheckPermission("system:config:update")
    @Operation(summary = "系统配置修改")
    @PostMapping("/updateConfig")
    @OperationLog(name = "系统配置", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateConfig(@Validated @RequestBody Config config) {
        if (ObjectUtil.isNull(config.getId())) {
            return toError(ApiResultCode.C0001);
        }
        configBizService.updateConfig(config);
        return toSuccess();
    }

    @SaCheckPermission("system:config:remove")
    @Operation(summary = "系统配置删除")
    @PostMapping("/removeConfig")
    @OperationLog(name = "系统配置", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeConfig(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        configBizService.removeConfig(dto.getList());
        return toSuccess();
    }

    @SaCheckPermission("system:config:load")
    @Operation(summary = "系统配置详情")
    @PostMapping("/loadConfig")
    public ApiResult loadConfig(@RequestParam Long id) {
        return toSuccess(configService.loadOne(id));
    }

    @SaCheckPermission("system:config:list")
    @Operation(summary = "系统配置分页")
    @PostMapping("/getConfigPageList")
    @OperationLog(name = "系统配置", type = OperationLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getConfigPageList(@RequestBody Config config) {
        return toSuccess(configService.loadPage(config, DBHelper.getQueryWrapper(config)));
    }

    @SaCheckPermission("system:config:export")
    @Operation(summary = "系统配置导出")
    @PostMapping("/exportConfigList")
    @OperationLog(name = "系统配置", type = OperationLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportConfigList(@RequestBody Config config, HttpServletResponse response) throws IOException {
        List<Config> configList = configService.loadMore(DBHelper.getQueryWrapper(config));
        toExcel("系统配置.xlsx", "系统配置", Config.class, configList, response);
    }

    @SaCheckPermission("system:config:load")
    @Operation(summary = "系统配置详情")
    @PostMapping("/loadConfigByConfigKey")
    public ApiResult loadConfigByConfigKey(@RequestParam String configKey) {
        return toSuccess(configService.loadOne(Config::getConfigKey, configKey));
    }

    @SaCheckPermission("system:config:remove")
    @Operation(summary = "系统配置缓存刷新")
    @PostMapping("/refreshConfigCache")
    public ApiResult refreshConfigCache() {
        configBizService.refreshConfigCache();
        return toSuccess();
    }
}