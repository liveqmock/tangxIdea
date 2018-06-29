package com.topaiebiz.merchant.api;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-02-11 15:23
 */
public interface FreightTemplateMigrateApi {

    /**
     * 将源运费模板拷贝一份到目标店铺ID
     *
     * @param templateId
     * @param targetStoreId
     * @return
     */
    Long copyTemplate(Long templateId, Long targetStoreId);

    boolean createDefaultTemplateDetail(Long temmplateId, BigDecimal fixFreight);

    Long createTemplate(Long belongStore, BigDecimal maxFixFreight);
}