package com.topaiebiz.member.config;

import com.topaiebiz.member.login.MemberLoginIntercetpor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MemberLoginInterceptorConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private MemberLoginIntercetpor loginInterceptor;//自己定义的拦截器类*/

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }

    
}