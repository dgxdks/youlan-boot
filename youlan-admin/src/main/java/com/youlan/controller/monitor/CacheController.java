package com.youlan.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.controller.base.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "缓存监控")
@RestController
@RequestMapping("/monitor/cache")
@AllArgsConstructor
public class CacheController extends BaseController {

    @SaCheckPermission("monitor:cacheMonitor")
    @Operation(summary = "缓存监控信息")
    @PostMapping("/getCacheMonitorInfo")
    public ApiResult getCacheMonitorInfo() {
        return toSuccess(RedisHelper.getRedisMonitorInfo());
    }
}