package com.topaiebiz.merchant.api;

import com.topaiebiz.merchant.dto.grade.MerchantGradeDTO;

/**
 * @author zhaoxupeng
 * @date 2018/1/4 - 15:37
 */
public interface MerchantGradeApi {

     /**
      *  查询商家等级信息
      * @param id
      * @return
      */
      MerchantGradeDTO getMerhantGradeInfoBy(Long id);



}
