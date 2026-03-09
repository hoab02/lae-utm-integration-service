package com.viettelpost.fms.utm_integration.config;

import com.viettelpost.fms.utm_integration.constant.SecurityConstant;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class WebMvcConfig {

    private static final String ALLOWED_ORIGINS = "*";

    private static final String[] ALLOWED_METHODS = {"OPTIONS", "GET", "POST", "PUT", "DELETE", "PATCH", "HEAD"};

    private static final String[] ALLOWED_HEADERS = {
            HttpHeaders.CONTENT_TYPE,
            HttpHeaders.ACCEPT_LANGUAGE,
            HttpHeaders.AUTHORIZATION,
            SecurityConstant.HEADER_FORWARDED_ADDRESS,
    };

    @Bean
    public FilterRegistrationBean<CorsFilter> customCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedMethods(Arrays.asList(ALLOWED_METHODS));
        config.setAllowedHeaders(Arrays.asList(ALLOWED_HEADERS));
        config.addAllowedOrigin(ALLOWED_ORIGINS);
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));

        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
