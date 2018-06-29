package com.topaiebiz.goods.category.backend.api;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.topaiebiz.goods.api.MerchantCategoryCommissionApi;
import com.topaiebiz.goods.category.backend.dao.BackendMerchantCategoryDao;
import com.topaiebiz.goods.category.backend.entity.BackendMerchantCategoryEntity;
import com.topaiebiz.goods.dto.category.backend.MerchantCategoryCommissionDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-03-26 14:39
 */
@Service
public class MerchantCategoryApiImpl implements MerchantCategoryCommissionApi {
    @Autowired
    private BackendMerchantCategoryDao merchantCategoryDao;

    @Override
    public Map<Long, MerchantCategoryCommissionDTO> queryMerchantCategoryMap(Long merchantId) {
        BackendMerchantCategoryEntity cond = new BackendMerchantCategoryEntity();
        cond.cleanInit();
        cond.setMerchantId(merchantId);
        cond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        List<BackendMerchantCategoryEntity> categoryEntities = merchantCategoryDao.selectList(new EntityWrapper<>(cond));
        if (CollectionUtils.isEmpty(categoryEntities)) {
            return Collections.emptyMap();
        }
        Map<Long, MerchantCategoryCommissionDTO> resultMap = new HashMap<>();
        for (BackendMerchantCategoryEntity categoryEntity : categoryEntities) {
            MerchantCategoryCommissionDTO dto = new MerchantCategoryCommissionDTO();
            BeanCopyUtil.copy(categoryEntity, dto);
            dto.setCommissionRatio(new BigDecimal(categoryEntity.getBrokerageRatio()));
            resultMap.put(categoryEntity.getCategoryId(), dto);
        }
        return resultMap;
    }
}