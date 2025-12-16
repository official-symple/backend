package com.DreamOfDuck.subscription.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IapProperties.class)
public class SubscriptionConfig {
}


