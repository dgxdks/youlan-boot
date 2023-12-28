package com.youlan.common.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {
    public static final String THREAD_NAME_PREFIX = "common-core-async-";

    @Override
    public Executor getAsyncExecutor() {
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
        threadPoolTaskExecutor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        // 线程池拒绝策略
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务完成关闭线程池
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        // 初始化操作
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
