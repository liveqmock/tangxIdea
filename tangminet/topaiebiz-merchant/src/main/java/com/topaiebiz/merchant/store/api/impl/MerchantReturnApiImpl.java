package com.topaiebiz.merchant.store.api.impl;

import com.topaiebiz.basic.api.DistrictApi;
import com.topaiebiz.basic.dto.DistrictDto;
import com.topaiebiz.merchant.api.MerchantReturnApi;
import com.topaiebiz.merchant.dto.merchantReturn.MerchantReturnDTO;
import com.topaiebiz.merchant.info.dao.MerchantReturnDao;
import com.topaiebiz.merchant.info.entity.MerchantReturnEntity;
import com.topaiebiz.system.api.DataDictApi;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.dto.DataDictDto;
import com.topaiebiz.system.security.api.SystemUserApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/2/8 0008 下午 7:40
 */
@Service
public class MerchantReturnApiImpl implements MerchantReturnApi {

    @Autowired
    private MerchantReturnDao merchantReturnDao;
    
    @Autowired
    private SystemUserApi systemUserApi;

    @Autowired
    private DistrictApi districtApi;



    @Override
    public MerchantReturnDTO getMerchantReturnInfo(Long storeId) {
        MerchantReturnDTO merchantReturnDTO = new MerchantReturnDTO();
        CurrentUserDto byStoreId = systemUserApi.getByStoreId(storeId);
        if (byStoreId !=null){
            MerchantReturnEntity merchantReturnEntity = merchantReturnDao.selectMerchantReturnInfoByMerchantId(byStoreId.getMerchantId());
            if (null == merchantReturnEntity){
                return null;
            }
            DistrictDto districtDto = districtApi.getDistrict(merchantReturnEntity.getDistrictId());
            if (StringUtils.isNotBlank(districtDto.getSerialName())){
                String serialNameArray = districtDto.getSerialName().replaceAll(".", " ");
                merchantReturnDTO.setDistrictStr(serialNameArray);
            }
            merchantReturnDTO.setDistrictStr(districtDto.getSerialName());
            BeanUtils.copyProperties(merchantReturnEntity,merchantReturnDTO);
        }
        return merchantReturnDTO;
    }
}
