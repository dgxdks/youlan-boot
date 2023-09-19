package com.youlan.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.youlan.system.helper.SystemAuthHelper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
     * 指定当前服务的权限拦截实现类
     */
    @Bean("adminStpInterface")
    public StpInterface stpInterface() {
        return new StpInterface() {
            @Override
            public List<String> getPermissionList(Object loginId, String loginType) {
                List<String> roleStrList = getRoleList(loginId, loginType);
                return roleStrList.stream()
                        .map(SystemAuthHelper::getPermissionList)
                        .flatMap(Collection::stream)
                        .distinct()
                        .collect(Collectors.toList());

            }

            @Override
            public List<String> getRoleList(final Object loginId, String loginType) {
                return SystemAuthHelper.getRoleStrList(Long.valueOf(loginId.toString()));
            }
        };
    }
}
