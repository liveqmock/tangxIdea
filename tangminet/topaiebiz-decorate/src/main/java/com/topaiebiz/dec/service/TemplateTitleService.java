package com.topaiebiz.dec.service;

import com.baomidou.mybatisplus.service.IService;
import com.topaiebiz.dec.dto.SecondTitleDto;
import com.topaiebiz.dec.dto.TemplateTitleDto;
import com.topaiebiz.dec.entity.TemplateTitleEntity;

import java.util.List;

/**
 * <p>
 * 商品标题表 服务类
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
public interface TemplateTitleService extends IService<TemplateTitleEntity> {

    TemplateTitleDto saveTemplateTitleDto(TemplateTitleDto templateTitleDto);

    void updateTemplateTitleDto(Long id, String titleName);

    void deleteTemplateTitle(Long id);

    List<TemplateTitleDto> getTemplateTitleDto(Long moduleId);

    void moveTemplateTitle(Long id,Long targetId);

    Long saveSecondTitle(SecondTitleDto secondTitleDto);
}
