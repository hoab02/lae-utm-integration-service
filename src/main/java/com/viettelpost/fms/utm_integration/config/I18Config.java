package com.viettelpost.fms.utm_integration.config;

import com.viettelpost.fms.common.i18n.I18nMessageService;
import com.viettelpost.fms.common.i18n.I18nMessageServiceImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Configuration
public class I18Config extends AcceptHeaderLocaleResolver {

    @Bean
    public MessageSource messageSource() {
        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:i18n/message");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.getDefault());
        return messageSource;
    }

    @Bean
    public I18nMessageService i18nMessageService(MessageSource messageSource) {
        return new I18nMessageServiceImpl(messageSource);
    }
}
