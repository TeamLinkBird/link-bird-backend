package com.example.demo.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor ();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger-ui/**")
                .excludePathPatterns("/refresh_Token/**","/login/**", "/logout/**");
    }
}
