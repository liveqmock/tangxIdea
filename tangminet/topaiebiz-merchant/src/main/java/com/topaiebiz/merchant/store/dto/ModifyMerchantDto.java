package com.topaiebiz.merchant.store.dto;

import com.topaiebiz.merchant.info.entity.MerchantAccountEntity;
import com.topaiebiz.merchant.info.entity.MerchantInfoEntity;
import com.topaiebiz.merchant.info.entity.MerchantQualificationEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/4/12 0012 下午 6:38
 */
@Data
public class ModifyMerchantDto implements Serializable {

    private MerchantInfoEntity merchantInfoEntity;
    private MerchantQualificationEntity merchantQualificationEntity;
    private MerchantAccountEntity merchantAccountEntity;

}
