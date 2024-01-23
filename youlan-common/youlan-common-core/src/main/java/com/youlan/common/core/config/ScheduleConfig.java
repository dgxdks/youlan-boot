package com.youlan.common.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ThreadPoolExecutor;

@EnableScheduling
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {
    public static final String THREAD_NAME_PREFIX = "common-core-schedule-";

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(createThreadPoolTaskExecutor());
    }

    public ThreadPoolTaskScheduler createThreadPoolTaskExecutor() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        // 获取可用核数
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        // 线程池线程数
        threadPoolTaskScheduler.setPoolSize(availableProcessors * 2);
        // 线程名称前缀
        threadPoolTaskScheduler.setThreadNamePrefix(THREAD_NAME_PREFIX);
        // 拒绝策略
        threadPoolTaskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务完成关闭线程池
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        // 初始化线程池
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }

}
