package com.viettelpost.fms.utm_integration.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

/**
 * This class configures ThreadPoolTaskExecutor of Spring.
 */
@Configuration
@EnableAsync(proxyTargetClass = true)
public class AsyncTaskConfig {

    public static final String BEAN_ASYNC_EXECUTOR = "APP_ASYNC_EXECUTOR";

    private static final int DEFAULT_POOL_SIZE = 25;

    @Autowired
    private Environment env;

    private int corePoolSize;

    private int maxPoolSize;

    private int queueCapacity;

    /**
     * Initialize executor configuration
     */
    @PostConstruct
    public void init() {
        corePoolSize = env.getProperty("executor.corePoolSize", Integer.class, DEFAULT_POOL_SIZE);
        maxPoolSize = env.getProperty("executor.maxPoolSize", Integer.class, Integer.MAX_VALUE);
        queueCapacity = env.getProperty("executor.queueCapacity", Integer.class, Integer.MAX_VALUE);
    }

    /**
     * Create bean for the default executor of application
     *
     * @return async task executor
     */
    @Bean(BEAN_ASYNC_EXECUTOR)
    public AsyncTaskExecutor initTaskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(corePoolSize);
        pool.setMaxPoolSize(maxPoolSize);
        pool.setQueueCapacity(queueCapacity);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.setThreadNamePrefix("task-");

        return pool;
    }

    /**
     * Using this executor to delegate the security context between threads
     *
     * @param executor
     * @return
     */
    @Bean
    public DelegatingSecurityContextAsyncTaskExecutor delegateSecurityContextAsyncTaskExecutor(@Qualifier(BEAN_ASYNC_EXECUTOR) AsyncTaskExecutor executor) {
        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
    }
}
