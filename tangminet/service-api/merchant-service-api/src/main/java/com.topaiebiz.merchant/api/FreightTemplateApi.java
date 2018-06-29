package com.topaiebiz.merchant.api;

import com.topaiebiz.merchant.dto.template.FreightTemplateDTO;
import com.topaiebiz.merchant.dto.template.FreightTemplateDetailDTO;

import java.util.List;
import java.util.Map;

/**
 * @author zhaoxupeng
 * @date 2018/1/4 - 15:38
 */
public interface FreightTemplateApi {

    /**
     * 批量查询运费模板
     *
     * @param freightIds
     * @return
     */
    Map<Long, FreightTemplateDTO> getFreightTemplete(List<Long> freightIds);

    /**
     * 根据运费模板id查询运费模板详情
     *
     * @param FreightId
     * @return
     */
    FreightTemplateDetailDTO getFreightTempleteDetail(Long FreightId);

    /**
     * 根据运费模板id查询运费模板
     *
     * @param FreightId
     * @return
     */
    FreightTemplateDTO getFreighTemplateDTO(Long FreightId);

}
