package com.topaiebiz.merchant.util;

import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.DateUtils;
import com.topaiebiz.merchant.info.entity.MerchantAccountEntity;
import com.topaiebiz.merchant.info.entity.MerchantInfoEntity;
import com.topaiebiz.merchant.info.entity.MerchantQualificationEntity;
import com.topaiebiz.merchant.store.dto.MerchantModifyDetailDto;
import com.topaiebiz.merchant.store.dto.ModifyMerchantDto;
import com.topaiebiz.merchant.store.entity.MerchantModifyInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class FiledUpdateUtil {
    private static List<String> updateAbleFields = new ArrayList<>();

    static {
        //基本信息
        updateAbleFields.add("name");
        updateAbleFields.add("districtId");
        updateAbleFields.add("address");
        updateAbleFields.add("storeNumber");
        updateAbleFields.add("telephone");
        updateAbleFields.add("contactTele");
        updateAbleFields.add("contactName");
        updateAbleFields.add("idCard");
        updateAbleFields.add("idCardImage");
        updateAbleFields.add("email");
        updateAbleFields.add("capital");
        updateAbleFields.add("staffNo");
        //资质
        updateAbleFields.add("accountName");
        updateAbleFields.add("account");
        updateAbleFields.add("accountDistrictId");
        updateAbleFields.add("bankName");
        updateAbleFields.add("bankNum");
        updateAbleFields.add("electronicImage");
        updateAbleFields.add("licenseNo");
        updateAbleFields.add("licenseRegionId");
        updateAbleFields.add("licenseBegin");
        updateAbleFields.add("licenseEnd");
        updateAbleFields.add("manageScope");
        updateAbleFields.add("licenseImage");
        updateAbleFields.add("establishTime");
        updateAbleFields.add("licenseLocation");
        //店铺名称
        updateAbleFields.add("storeName");
        updateAbleFields.add("imgages");
        updateAbleFields.add("settleCycle");
        //店铺结算信息
        updateAbleFields.add("settleAccount");
        updateAbleFields.add("settleAccountName");
        updateAbleFields.add("settleBankName");
        updateAbleFields.add("settleBankDistrictId");
        updateAbleFields.add("settleBankNum");
    }

    public static ModifyMerchantDto getUpdateInfos(List<MerchantModifyDetailDto> entityss) {
        ModifyMerchantDto modifyMerchantDto = new ModifyMerchantDto();
        List<Object> objects = new ArrayList<>();
        MerchantAccountEntity account = new MerchantAccountEntity();
        objects.add(account);
        MerchantInfoEntity infoEntity = new MerchantInfoEntity();
        objects.add(infoEntity);
        MerchantQualificationEntity qualificationEntity = new MerchantQualificationEntity();
        objects.add(qualificationEntity);
        mergeUpdate(entityss, objects);
        modifyMerchantDto.setMerchantAccountEntity(account);
        modifyMerchantDto.setMerchantInfoEntity(infoEntity);
        modifyMerchantDto.setMerchantQualificationEntity(qualificationEntity);
        return modifyMerchantDto;
    }

    public static void mergeUpdate(List<MerchantModifyDetailDto> updateInfos, List<Object> entitys) {
        Map<String, String> updates = updateInfos.stream().collect(Collectors.toMap(MerchantModifyDetailDto::getFieldName, MerchantModifyDetailDto::getModifiedValue));
        for (String fieldName : updateAbleFields) {
            for (Object entity : entitys) {
                String updateValue = updates.get(fieldName);
                if (updateValue == null) {
                    continue;
                }

                PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(entity.getClass(), fieldName);
                if (pd == null) {
                    continue;
                }
                try {
                    if ("accountDistrictId".equals(fieldName) || "licenseRegionId".equals(fieldName) || "settleBankDistrictId".equals(fieldName) ||"staffNo".equals(fieldName) || "storeNumber".equals(fieldName)) {
                        pd.getWriteMethod().invoke(entity, Long.parseLong(updateValue));
                    } else if ("licenseBegin".equals(fieldName) || "licenseEnd".equals(fieldName) || "establishTime".equals(fieldName)) {
                        pd.getWriteMethod().invoke(entity, DateUtils.parseStringToDate(updateValue,DateUtils.DATE_FORMAT));
                    }
                    else if ("capital".equals(fieldName)){
                        pd.getWriteMethod().invoke(entity, Double.parseDouble(updateValue));
                    }
                    else {
                        pd.getWriteMethod().invoke(entity, updateValue);
                    }

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}
