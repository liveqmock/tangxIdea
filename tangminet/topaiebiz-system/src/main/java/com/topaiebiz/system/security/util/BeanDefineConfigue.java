package com.topaiebiz.system.security.util;

import com.topaiebiz.system.security.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
public class BeanDefineConfigue implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ResourceService resourceService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        resourceService.addResources();
    }
}
