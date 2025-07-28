package com.lovedbug.geulgwi.core.common.config;

import com.lovedbug.geulgwi.core.common.log.GeulgwiLogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<GeulgwiLogFilter> geulgwiLogFilterRegistration() {
        FilterRegistrationBean<GeulgwiLogFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new GeulgwiLogFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registrationBean;
    }
}
