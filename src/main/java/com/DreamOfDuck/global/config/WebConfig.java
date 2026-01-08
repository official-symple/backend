package com.DreamOfDuck.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final String CORS_URL_PATTERN = "/**";
    private static final String CORS_METHOD = "*";

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping(CORS_URL_PATTERN)
                .allowedOrigins("http://localhost:3000", "http://admin.symple.kr", "http://admin.symple.kr.s3-website.ap-northeast-2.amazonaws.com/", "https://api.symple.kr")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .exposedHeaders("Authorization");
    }
}