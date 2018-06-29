package com.topaiebiz.dec.service;


import java.util.List;

public interface MQProducerService {

    void produceMQByModuleId(Long moduleId);

    void produceMQByModuleIds(List<Long> moduleIds);

    void produceMQByTitileId(Long titleId);
}
