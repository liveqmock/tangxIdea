package com.topaiebiz.config;

import com.topaiebiz.filter.InputLogFilter;
import com.topaiebiz.filter.PerformanceLogFilter;
import com.topaiebiz.filter.RequestCountLogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/***
 * @author yfeng
 * @date 2017-12-18 20:19
 */
@Configuration
public class FilterConfig extends WebMvcConfigurerAdapter {

    @Bean
    public FilterRegistrationBean requestCountLogFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(requestCountLogFilter());
        registration.addUrlPatterns("/*");
        registration.setName("requestCountLogFilter");
        registration.setOrder(0);
        return registration;
    }

    @Bean
    public FilterRegistrationBean inputLogFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(inputLogInterceptor());
        registration.addUrlPatterns("/*");
        registration.setName("inputLogFilter");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean performanceLogFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(performanceLogFilter());
        registration.addUrlPatterns("/*");
        registration.setName("performanceFilter");
        registration.setOrder(2);
        return registration;
    }


    @Bean
    public InputLogFilter inputLogInterceptor() {
        return new InputLogFilter();
    }

    @Bean
    public PerformanceLogFilter performanceLogFilter() {
        return new PerformanceLogFilter();
    }

    @Bean
    public RequestCountLogFilter requestCountLogFilter() {
        return new RequestCountLogFilter();
    }
}