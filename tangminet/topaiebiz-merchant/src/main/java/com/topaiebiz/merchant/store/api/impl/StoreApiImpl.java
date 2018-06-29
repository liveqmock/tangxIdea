package com.topaiebiz.merchant.store.api.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.merchant.api.StoreApi;
import com.topaiebiz.merchant.constants.MerchantConstants;
import com.topaiebiz.merchant.dto.store.MerchantAccountDTO;
import com.topaiebiz.merchant.dto.store.MerchantInfoDTO;
import com.topaiebiz.merchant.dto.store.MerchantMemberDTO;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.merchant.enter.dao.MerchantAccountDao;
import com.topaiebiz.merchant.enter.dao.StoreInfoDao;
import com.topaiebiz.merchant.info.dao.MerchantInfoDao;
import com.topaiebiz.merchant.info.entity.MerchantAccountEntity;
import com.topaiebiz.merchant.info.entity.MerchantInfoEntity;
import com.topaiebiz.merchant.info.entity.StoreInfoEntity;
import com.topaiebiz.merchant.info.exception.MerchantInfoException;
import com.topaiebiz.merchant.store.dao.MerchantMmeberDao;
import com.topaiebiz.merchant.store.entity.MerchantMemberEntity;
import com.topaiebiz.merchant.store.exception.StoreInfoException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * *
 *
 * @author yfeng
 * @date 2018-01-06 17:17
 */
@Service
public class StoreApiImpl implements StoreApi {

    @Autowired
    private StoreInfoDao storeInfoDao;
    @Autowired
    private MerchantInfoDao merchantInfoDao;

    @Autowired
    private MerchantMmeberDao merchantMmeberDao;

    @Autowired
    private MerchantAccountDao merchantAccountDao;

    @Override
    public StoreInfoDetailDTO getStore(Long storeId) {
        if (storeId == null) {
            throw new GlobalException(StoreInfoException.STOREINFO_ID_NOT_NULL);
        }
        StoreInfoDetailDTO storeInfoDetailDTO = new StoreInfoDetailDTO();
        StoreInfoEntity storeInfoEntity = storeInfoDao.selectById(storeId);
        if (storeInfoEntity == null) {
            throw new GlobalException(StoreInfoException.STOREINFO_ID_NOT_EXIST);
        }
        BeanUtils.copyProperties(storeInfoEntity, storeInfoDetailDTO);
        return storeInfoDetailDTO;
    }

    @Override
    public StoreInfoDetailDTO getStoreByMerchantId(Long merchantId) {
        if (merchantId == null) {
            return null;
        }
        StoreInfoEntity condition = new StoreInfoEntity();
        condition.cleanInit();
        condition.setMerchantId(merchantId);

        StoreInfoEntity storeInfoEntity = storeInfoDao.selectOne(condition);
        if (null != storeInfoEntity) {
            StoreInfoDetailDTO storeInfoDetailDTO = new StoreInfoDetailDTO();
            BeanCopyUtil.copy(storeInfoEntity, storeInfoDetailDTO);
            return storeInfoDetailDTO;
        }
        return null;
    }

    @Override
    public MerchantInfoDTO getMerchant(Long merchantId) {
        if (merchantId == null) {
            throw new GlobalException(MerchantInfoException.MERCHANTINFO_ID_NULL);
        }
        MerchantInfoDTO merchantInfoDTO = new MerchantInfoDTO();
        MerchantInfoEntity merchantInfoEntity = merchantInfoDao.selectById(merchantId);
        if (merchantInfoEntity == null) {
            throw new GlobalException(MerchantInfoException.MERCHANTINFO_ID_EXIST);
        }
        BeanUtils.copyProperties(merchantInfoEntity, merchantInfoDTO);
        return merchantInfoDTO;
    }

    @Override
    public Map<Long, StoreInfoDetailDTO> getStoreMap(List<Long> storeIds) {
        if (CollectionUtils.isEmpty(storeIds)) {
            throw new GlobalException(StoreInfoException.STOREINFO_ID_NOT_NULL);
        }
        Map<Long, StoreInfoDetailDTO> storeInfoDetailDTOMap = new HashMap<Long, StoreInfoDetailDTO>();
        for (Long storeId : storeIds) {
            StoreInfoEntity storeInfoEntity = storeInfoDao.selectById(storeId);
            if (storeInfoEntity == null) {
                throw new GlobalException(MerchantInfoException.MERCHANTINFO_ID_EXIST);
            }
            StoreInfoDetailDTO storeInfoDetailDTO = new StoreInfoDetailDTO();
            BeanCopyUtil.copy(storeInfoEntity, storeInfoDetailDTO);
            storeInfoDetailDTOMap.put(storeId, storeInfoDetailDTO);
        }
        return storeInfoDetailDTOMap;
    }

	@Override
	public List<StoreInfoDetailDTO> getStoreList(List<Long> storeIds) {
		if (CollectionUtils.isEmpty(storeIds)) {
			throw new GlobalException(StoreInfoException.STOREINFO_ID_NOT_NULL);
		}
		List<StoreInfoDetailDTO> storeInfoDetailDTOList = new ArrayList<>();
		for (Long storeId : storeIds) {
			StoreInfoEntity storeInfoEntity = storeInfoDao.selectById(storeId);
			if (storeInfoEntity == null) {
				throw new GlobalException(MerchantInfoException.MERCHANTINFO_ID_EXIST);
			}
			StoreInfoDetailDTO storeInfoDetailDTO = new StoreInfoDetailDTO();
			BeanCopyUtil.copy(storeInfoEntity, storeInfoDetailDTO);
			storeInfoDetailDTOList.add(storeInfoDetailDTO);
		}
		return storeInfoDetailDTOList;
	}

    @Override
    public Integer saveMerchantMemberRelation(MerchantMemberDTO merchantMemberDTO) {
        MerchantMemberEntity merchantMemberEntity = new MerchantMemberEntity();
        BeanCopyUtil.copy(merchantMemberDTO, merchantMemberEntity);
        MerchantMemberEntity merchantMemberEntitys =
                merchantMmeberDao.selectMerchantMemberByStoreIdAndMember(merchantMemberEntity);
        if (merchantMemberEntitys != null) {
            throw new GlobalException(StoreInfoException.MEMBER_HAS_ALREADY_EXISTED);
        }
        merchantMemberEntity.setCreatedTime(new Date());
        merchantMemberEntity.setCreatorId(merchantMemberDTO.getMemberId());
        return merchantMmeberDao.insert(merchantMemberEntity);
    }

    @Override
    public Boolean checkStoreMemberRelation(Long memberId, Long storeId) {
        MerchantMemberEntity param = new MerchantMemberEntity();
        param.cleanInit();
        param.setMemberId(memberId);
        param.setStoreId(storeId);
        MerchantMemberEntity merchantMemberEntity = merchantMmeberDao.selectOne(param);
        if (merchantMemberEntity != null) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateNextSettleDate(Long storeId, Date nextSettleDate) {
        if (storeId == null) {
            throw new GlobalException(StoreInfoException.STOREINFO_ID_NOT_NULL);
        }
        StoreInfoEntity update = new StoreInfoEntity();
        update.cleanInit();
        update.setLastModifiedTime(new Date());
        update.setId(storeId);
        update.setNextSettleDate(nextSettleDate);
        int updateCount = storeInfoDao.updateById(update);
        return updateCount > 0;
    }

    @Override
    public List<StoreInfoDetailDTO> queryStores(Long startId, Integer querySize) {
        EntityWrapper<StoreInfoEntity> cond = new EntityWrapper<>();
        cond.gt("id", startId);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.orderBy("id", true);
        RowBounds rowBounds = new RowBounds(0, querySize);
        List<StoreInfoEntity> entityList = storeInfoDao.selectPage(rowBounds, cond);
        if (CollectionUtils.isEmpty(entityList)) {
            return Collections.emptyList();
        }
        return PageDataUtil.copyList(entityList, StoreInfoDetailDTO.class);
    }

    @Override
    public List<StoreInfoDetailDTO> getStoreInfoListByStoreName(String name) {
        EntityWrapper<StoreInfoEntity> storeInfoEntityEntits = new EntityWrapper<>();
        storeInfoEntityEntits.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        storeInfoEntityEntits.like("name", name);
        storeInfoEntityEntits.eq("changeState",MerchantConstants.StoreStatus.OPEN);
        List<StoreInfoEntity> storeInfoList = storeInfoDao.selectList(storeInfoEntityEntits);
        List<StoreInfoDetailDTO> storeInfoDetailDTOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(storeInfoList)) {
            for (StoreInfoEntity storeInfoEntity : storeInfoList) {
                StoreInfoDetailDTO storeInfoDetailDTO = new StoreInfoDetailDTO();
                BeanCopyUtil.copy(storeInfoEntity, storeInfoDetailDTO);
                storeInfoDetailDTOList.add(storeInfoDetailDTO);
            }
        }
        return storeInfoDetailDTOList;
    }

    @Override
    public List<StoreInfoDetailDTO> queryStores(String name) {
        EntityWrapper<StoreInfoEntity> storeInfoEntityEntits = new EntityWrapper<>();
        storeInfoEntityEntits.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        storeInfoEntityEntits.like("name", name);
        storeInfoEntityEntits.eq("changeState",MerchantConstants.StoreStatus.OPEN);
        List<StoreInfoEntity> storeInfoList = storeInfoDao.selectList(storeInfoEntityEntits);
        List<StoreInfoDetailDTO> storeInfoDetailDTOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(storeInfoList)) {
            for (StoreInfoEntity storeInfoEntity : storeInfoList) {
                StoreInfoDetailDTO storeInfoDetailDTO = new StoreInfoDetailDTO();
                BeanCopyUtil.copy(storeInfoEntity, storeInfoDetailDTO);
                storeInfoDetailDTOList.add(storeInfoDetailDTO);
            }
        }
        return storeInfoDetailDTOList;
    }


    @Override
    public MerchantAccountDTO getMerchantAccountInfo(Long merchantId) {
        if (merchantId == null) {
            throw new GlobalException(MerchantInfoException.MERCHANTINFO_ID_NULL);
        }
        MerchantAccountEntity conn = new MerchantAccountEntity();
        conn.cleanInit();
        conn.setMerchantId(merchantId);
        conn.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        MerchantAccountEntity merchantAccountEntity = merchantAccountDao.selectOne(conn);
        MerchantAccountDTO merchantAccountDTO = new MerchantAccountDTO();
        BeanCopyUtil.copy(merchantAccountEntity, merchantAccountDTO);
        return merchantAccountDTO;
    }

    @Override
    public List<MerchantInfoDTO> getMerchantInfo(List<Long> merchantIdList) {
        if (merchantIdList == null) {
            throw new GlobalException(MerchantInfoException.MERCHANTINFO_ID_NULL);
        }
        EntityWrapper<MerchantInfoEntity> entityEntityWrapper = new EntityWrapper<>();
        entityEntityWrapper.in("id",merchantIdList);
        entityEntityWrapper.eq("deletedFlag",Constants.DeletedFlag.DELETED_NO);
        List<MerchantInfoEntity> MerchantInfoList = merchantInfoDao.selectList(entityEntityWrapper);
        List<MerchantInfoDTO> list = new ArrayList<>();
        for(MerchantInfoEntity MerchantInfo : MerchantInfoList){
            MerchantInfoDTO merchantInfoDTO = new MerchantInfoDTO();
            BeanCopyUtil.copy(MerchantInfo,merchantInfoDTO);
            list.add(merchantInfoDTO);
        }
        return list;
    }

    @Override
    public List<Long> getFrozenStoreIds() {
        EntityWrapper<StoreInfoEntity> cond = new EntityWrapper<>();
        cond.eq("changeState", MerchantConstants.StoreStatus.FROZED);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<StoreInfoEntity> storeInfoEntities = storeInfoDao.selectList(cond);
        if (CollectionUtils.isEmpty(storeInfoEntities)) {
            return Collections.EMPTY_LIST;
        }
        List<Long> storeIds = storeInfoEntities.stream().map(storeInfoEntity -> storeInfoEntity.getId()).collect(Collectors.toList());
        return storeIds;
    }


}
