package com.topaiebiz.dec.service;

import com.topaiebiz.dec.dto.AppHomePageDto;

import java.util.List;

public interface AppHomePageService {

    List<AppHomePageDto> search(Long templateId);

    void refreshCache(Long templateId);

    List<AppHomePageDto> storeSearch(Long storeId);

    List<AppHomePageDto> gainTemplate(Long templateId);
}
