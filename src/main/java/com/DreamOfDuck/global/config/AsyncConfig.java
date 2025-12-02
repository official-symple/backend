package com.DreamOfDuck.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);            // 스레드 풀이 최소한으로 유지할 스레드 수를 의미합니다.
        executor.setMaxPoolSize(50);            // 스레드 풀이 동시에 사용할 수 있는 최대 스레드 수를 의미합니다.
        executor.setQueueCapacity(100);         // 작업 큐의 용량을 500으로 설정합니다. 큐가 가득 차면 새로운 작업은 거부됩니다.
        executor.setThreadNamePrefix("Async-"); // 생성되는 스레드의 이름 접두사를 "Async-"로 설정합니다. 이는 디버깅 및 모니터링 시 유용할 수 있습니다.
        executor.initialize();                  // 설정된 값을 바탕으로 executor를 초기화합니다.
        return executor;
    }
}
