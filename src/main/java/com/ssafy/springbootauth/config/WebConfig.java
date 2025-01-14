package com.ssafy.springbootauth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 전역으로 RESTAPI에 api 접두사를 생성
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {

        configurer.addPathPrefix("/api", c -> true);
    }

    /**
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**");
    }
}
