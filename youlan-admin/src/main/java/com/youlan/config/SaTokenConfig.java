package com.youlan.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.youlan.system.satoken.StpInterfaceSystemPermissionImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class SaTokenConfig implements WebMvcConfigurer {
    private final SaTokenProperties saTokenProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
                    SaRouter.match("/**")
                            .notMatch(saTokenProperties.getExcludePathPatterns())
                            .check(router -> StpUtil.checkLogin());
                }))
                .addPathPatterns("/**");
    }

    /**
     * 指定当前服务的权限拦截实现类,不建议将此类默认交给Spring管理
     */
    @Bean
    public StpInterfaceSystemPermissionImpl stpInterfaceSystemPermission() {
        return new StpInterfaceSystemPermissionImpl();
    }
}
