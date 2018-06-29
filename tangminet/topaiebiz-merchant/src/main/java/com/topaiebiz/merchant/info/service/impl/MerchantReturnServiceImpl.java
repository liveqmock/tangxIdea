package com.topaiebiz.merchant.info.service.impl;

import com.nebulapaas.common.BeanCopyUtil;
import com.topaiebiz.merchant.info.dao.MerchantReturnDao;
import com.topaiebiz.merchant.info.dto.MerchantReturnDto;
import com.topaiebiz.merchant.info.entity.MerchantReturnEntity;
import com.topaiebiz.merchant.info.service.MerchantReturnService;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/24 0024 下午 4:43
 */
@Service
public class MerchantReturnServiceImpl implements MerchantReturnService {

    @Autowired
    private MerchantReturnDao merchantReturnDao;

    @Override
    public Integer insertMerchantReturnInfo(MerchantReturnDto merchantReturnDto) {
        Integer i = 0;
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        //查询退货信息
        MerchantReturnEntity merchantReturnEntits = merchantReturnDao.selectMerchantReturnInfoByMerchantId(merchantId);
        if (merchantReturnEntits != null) {
            BeanCopyUtil.copy(merchantReturnDto, merchantReturnEntits);
            merchantReturnEntits.setMerchantId(merchantId);
            merchantReturnEntits.setLastModifiedTime(new Date());
            merchantReturnEntits.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
            i = merchantReturnDao.updateById(merchantReturnEntits);
        } else {
            MerchantReturnEntity merchantReturnEntity = new MerchantReturnEntity();
            BeanCopyUtil.copy(merchantReturnDto, merchantReturnEntity);
            merchantReturnEntity.setMerchantId(merchantId);
            merchantReturnEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
            merchantReturnEntity.setCreatedTime(new Date());
            i = merchantReturnDao.insert(merchantReturnEntity);
        }
        return i;
    }

    @Override
    public MerchantReturnDto selectMerchantReturnByMerchantId(Long merchantId) {
        MerchantReturnEntity merchantReturnEntits = merchantReturnDao.selectMerchantReturnInfoByMerchantId(merchantId);
        if (merchantReturnEntits == null) {
            return null;
        }
        MerchantReturnDto dto = new MerchantReturnDto();
        BeanCopyUtil.copy(merchantReturnEntits, dto);
        return dto;
    }
}
