package com.topaiebiz.merchant.grade.api.impl;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.merchant.api.MerchantGradeApi;
import com.topaiebiz.merchant.dto.grade.MerchantGradeDTO;
import com.topaiebiz.merchant.grade.dao.MerchantGradeDao;
import com.topaiebiz.merchant.grade.entity.MerchantGradeEntity;
import com.topaiebiz.merchant.grade.exception.MerchantGradeException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/***
 * @author yfeng
 * @date 2018-01-06 17:24
 */
@Service
public class MerchantGradeApiImpl implements MerchantGradeApi {

    @Autowired
    private MerchantGradeDao merchantGradeDao;
    
    @Override
    public MerchantGradeDTO getMerhantGradeInfoBy(Long id) {
        if (id== null){
         throw new GlobalException(MerchantGradeException.MERCHANTGRADE_ID_NOT_NULL);  
        }
        MerchantGradeDTO merchantGradeDTO = new MerchantGradeDTO();
        MerchantGradeEntity merchantGradeEntity = merchantGradeDao.selectById(id);
        if (merchantGradeEntity == null){
            throw new GlobalException(MerchantGradeException.MERCHANTGRADE_ID_NOT_EXIST);
        }
        BeanUtils.copyProperties(merchantGradeEntity, merchantGradeDTO);
        return merchantGradeDTO;
    }
}