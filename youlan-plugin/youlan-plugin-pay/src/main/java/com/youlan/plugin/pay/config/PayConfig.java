package com.youlan.plugin.pay.config;

import com.youlan.plugin.pay.factory.CachePayClientFactory;
import com.youlan.plugin.pay.factory.PayClientFactory;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import static com.youlan.plugin.pay.constant.PayConstant.*;

@Configuration
@AllArgsConstructor
@EnableConfigurationProperties({PayProperties.class})
public class PayConfig {

    @Bean
    @ConditionalOnMissingBean(PayClientFactory.class)
    public PayClientFactory payClientFactory() {
        return new CachePayClientFactory();
    }

    @Bean(PYA_NOTIFY_THREAD_POOL_NAME)
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        // 线程池核心线程数量
        threadPoolTaskExecutor.setCorePoolSize(availableProcessors);
        // 线程池最大线程数量
        threadPoolTaskExecutor.setMaxPoolSize(availableProcessors * 2);
        // 线程存活时间
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        // 线程池队列容量
        threadPoolTaskExecutor.setQueueCapacity(Integer.MAX_VALUE);
        // 线程名称前缀
        threadPoolTaskExecutor.setThreadNamePrefix(PAY_NOTIFY_THREAD_NAME_PREFIX);
        // 线程池拒绝策略
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务完成关闭线程池
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        // 初始化操作
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

}
