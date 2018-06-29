package com.topaiebiz.merchant.info.service.impl;

import java.util.Date;
import java.util.List;

import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.topaiebiz.goods.api.BackendCategoryApi;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.api.GoodsSkuApi;
import com.topaiebiz.goods.constants.ItemConstants;
import com.topaiebiz.merchant.constants.MerchantConstants;
import com.topaiebiz.merchant.enter.dao.MerchantAccountDao;
import com.topaiebiz.merchant.enter.dto.StoreInfoDto;
import com.topaiebiz.merchant.info.dto.*;
import com.topaiebiz.merchant.info.entity.MerchantAccountEntity;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.security.api.SystemUserApi;
import com.topaiebiz.system.security.constants.SystemMerchantConstants;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.merchant.enter.dao.StoreInfoDao;
import com.topaiebiz.merchant.info.dao.MerchantInfoDao;
import com.topaiebiz.merchant.info.entity.MerchantInfoEntity;
import com.topaiebiz.merchant.info.entity.StoreInfoEntity;
import com.topaiebiz.merchant.info.exception.MerchantInfoException;
import com.topaiebiz.merchant.info.service.MerchantInfoService;

/**
 * Description: 商家管理业务实现类
 * <p>
 * Author : Anthony
 * <p>
 * Date :2017年9月27日 下午1:25:54
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
public class MerchantInfoServiceImpl implements MerchantInfoService {

    @Autowired
    private MerchantInfoDao merchantInfoDao;

    @Autowired
    private StoreInfoDao storeInfoDao;

    @Autowired
    private BackendCategoryApi backendCategoryApi;

    @Autowired
    private SystemUserApi systemUserApi;

    @Autowired
    private GoodsApi goodsApi;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private MerchantAccountDao merchantAccountDao;


    @Override
    public Integer saveMerchantInfo(MerchantInfoDto merchantInfoDto) throws GlobalException {
        // 成功时，直接分发数据并调用业务逻辑方法，并返回响应信息对象。
        MerchantInfoEntity entity = new MerchantInfoEntity();
        BeanUtils.copyProperties(merchantInfoDto, entity);
        /** 联系人手机号进行重复验证 */
        MerchantInfoEntity merchantInfoBycontactTele = merchantInfoDao
                .selectMerchantInfoBycontactTele(entity.getContactTele());
        if (merchantInfoBycontactTele != null) {
            throw new GlobalException(MerchantInfoException.MERCHANTINFO_CONTACTELE_NOT_REPETITION);
        }
        entity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        entity.setCreatedTime(new Date());
        return merchantInfoDao.insert(entity);
    }

    @Override
    public Integer modiMerchantInfoById(MerchantInfoDto merchantInfoDto) {
        //修改商家管理中已经入住成功的，状态为9，并且修改【变更状态为1】;
        MerchantInfoEntity merchantInfoEntity = merchantInfoDao.selectById(merchantInfoDto.getMerchantId());
        merchantInfoDto.setId(merchantInfoEntity.getId());
        BeanCopyUtil.copy(merchantInfoDto, merchantInfoEntity);
        merchantInfoEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        merchantInfoEntity.setLastModifiedTime(new Date());
        return merchantInfoDao.updateById(merchantInfoEntity);
    }

   /* @Override
    public MerchantInfoDto getBackendCategorysList(MerchantInfoDto merchantInfoDto) {
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        Integer integer = backendCategoryApi.addBackendCategoryDtoByStoreId(merchantId, merchantInfoDto.getIds());
        MerchantInfoEntity merchantInfoEntity = merchantInfoDao.selectById(merchantId);
        MerchantInfoDto merchantInfoDtos = new MerchantInfoDto();
        //修改变更状态 changeState为1
        merchantInfoEntity.setChangeState(merchantInfoDto.getChangeState());
        merchantInfoDao.updateById(merchantInfoEntity);
        BeanCopyUtil.copy(merchantInfoEntity, merchantInfoDtos);
        return merchantInfoDtos;
    }*/

    @Override
    public Integer removeMerchantInfoById(MerchantFrozenDto merchantFrozenDto) {
        Integer i = 0;
        MerchantInfoEntity merchantInfoEntity = merchantInfoDao.selectById(merchantFrozenDto.getMerchantId());
        if (merchantFrozenDto.getChangeState() == 2) {
            merchantInfoEntity.setChangeState(merchantFrozenDto.getChangeState());
            //冻结店铺
            StoreInfoEntity storeInfo = new StoreInfoEntity();
            storeInfo.cleanInit();
            storeInfo.setMerchantId(merchantFrozenDto.getMerchantId());
            storeInfo.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            i = merchantInfoDao.updateById(merchantInfoEntity);
            StoreInfoEntity storeInfoEntity = storeInfoDao.selectOne(storeInfo);
            storeInfoEntity.setChangeState(merchantFrozenDto.getChangeState());
            i = storeInfoDao.updateById(storeInfoEntity);
            //冻结店铺不能登录
            CurrentUserDto byStoreId = systemUserApi.getByStoreId(storeInfoEntity.getId());
            if (byStoreId != null) {
                boolean b = systemUserApi.closeUser(byStoreId.getMobilePhone());
            }
            goodsApi.updateGoods(storeInfoEntity.getId(), ItemConstants.FrozenFlag.YES_FROZEN);

            redisCache.set(SystemMerchantConstants.MERCHANT_FROZED_STATUS + merchantFrozenDto.getMerchantId(), MerchantConstants.StoreStatus.FROZED);
        }
        if (merchantFrozenDto.getChangeState() == 0) {
            merchantInfoEntity.setChangeState(merchantFrozenDto.getChangeState());
            //解冻店铺
            StoreInfoEntity storeInfo = new StoreInfoEntity();
            storeInfo.cleanInit();
            storeInfo.setMerchantId(merchantFrozenDto.getMerchantId());
            storeInfo.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            i = merchantInfoDao.updateById(merchantInfoEntity);
            StoreInfoEntity storeInfoEntity = storeInfoDao.selectOne(storeInfo);
            storeInfoEntity.setChangeState(merchantFrozenDto.getChangeState());
            i = storeInfoDao.updateById(storeInfoEntity);
            //恢复登录
            CurrentUserDto byStoreId = systemUserApi.getByStoreId(storeInfoEntity.getId());
            if (byStoreId != null) {
                boolean b = systemUserApi.openUser(byStoreId.getMobilePhone());
            }
            goodsApi.updateGoods(storeInfoEntity.getId(), ItemConstants.FrozenFlag.NO_FROZEN);
            redisCache.delete(SystemMerchantConstants.MERCHANT_FROZED_STATUS + merchantFrozenDto.getMerchantId().toString());
        }
        return i;
    }

    @Override
    public Integer modifyMerchantInfoById(MerchantInfoDto dto) throws GlobalException {
        /** 联系人手机号进行重复验证 */
        MerchantInfoEntity findMerchantInfoBycontactTele = merchantInfoDao
                .selectMerchantInfoBycontactTele(dto.getContactTele());
        if (findMerchantInfoBycontactTele != null) {
            throw new GlobalException(MerchantInfoException.MERCHANTINFO_CONTACTELE_NOT_REPETITION);
        }
        MerchantInfoEntity merchantInfo = merchantInfoDao.selectById(dto.getId());
        BeanUtils.copyProperties(dto, merchantInfo);
        merchantInfo.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        merchantInfo.setLastModifiedTime(new Date());
        return merchantInfoDao.updateById(merchantInfo);
    }

    @Override
    public PageInfo<MerchantInfoListDto> getMerchantInfoList(PagePO pagePO, MerchantInfoListDto merchantInfoListDto) {
        Page<MerchantInfoListDto> page = PageDataUtil.buildPageParam(pagePO);
        page.setRecords(merchantInfoDao.selectMerchantInfoList(page, merchantInfoListDto));
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public MerchantInfoDto getMerchantParticularsById(Long id) {
        MerchantInfoDto merchantInfoDto = merchantInfoDao.selectMerchantParticularsById(id);
        MerchantAccountEntity conn = new MerchantAccountEntity();
        conn.cleanInit();
        conn.setMerchantId(id);
        conn.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        MerchantAccountEntity merchantAccountEntity = merchantAccountDao.selectOne(conn);
        if (merchantAccountEntity != null) {
            merchantInfoDto.setSettleAccount(merchantAccountEntity.getSettleAccount());
            merchantInfoDto.setSettleBankName(merchantAccountEntity.getSettleBankName());
            merchantInfoDto.setSettleAccountName(merchantAccountEntity.getSettleAccountName());
            merchantInfoDto.setSettleBankDistrictId(merchantAccountEntity.getSettleBankDistrictId());
            merchantInfoDto.setSettleBankNum(merchantAccountEntity.getSettleBankNum());
        }
        return merchantInfoDto;

    }

    @Override
    public MerchantInfoDto getStoreInfoById(Long merchantId) {
        return merchantInfoDao.selectStoreInfoById(merchantId);
    }

    @Override
    public List<MerchantInfoDto> getMerchantType() {
        return merchantInfoDao.selectMerchantType();
    }

    @Override
    public List<StoreInfoDto> getStoreInfoByName(MerchantInfoDto dto) {
        return merchantInfoDao.selectStoreInfoByName(dto);
    }

    @Override
    public Integer modifyMerchantInfoByMerchantGradeId(MerchantInfoGradeDto merchantInfoGradeDto) {
        //同时将商家等级设置给对应的店铺
        StoreInfoEntity storeInfoEntity = new StoreInfoEntity();
        storeInfoEntity.cleanInit();
        storeInfoEntity.setMerchantId(merchantInfoGradeDto.getMerchantId());
        StoreInfoEntity storeInfoEntits = storeInfoDao.selectOne(storeInfoEntity);
        storeInfoEntits.setMerchantGradeId(merchantInfoGradeDto.getMerchantGradeId());
        storeInfoDao.updateById(storeInfoEntits);
        return merchantInfoDao.updateMerchantInfoByMerchantGradeId(merchantInfoGradeDto);
    }

    @Override
    public List<MerchantInfoDto> getMerchantInfoByMerchantType() {
        return merchantInfoDao.selectMerchantInfoByMerchantType();
    }

    @Override
    public List<MerchantInfoDto> getMerchantInfoByName(MerchantInfoDto merchantInfoDto) {
        return merchantInfoDao.selectMerchantInfoByName(merchantInfoDto);
    }

    @Override
    public List<StoreInfoDto> getStoreInfoList(StoreInfoDto storeInfoDto) {
        return merchantInfoDao.selectStoreInfoList(storeInfoDto);
    }

    @Override
    public StoreInfoDetailDto getAllStoreByLoginName() {
        StoreInfoDetailDto storeDto = new StoreInfoDetailDto();
        //登录人的所属商家
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        //所有店铺
        List<StoreInfoEntity> selectByMerchantId = storeInfoDao.selectByMerchantId(merchantId);
        //商家信息
        MerchantInfoEntity selectById = merchantInfoDao.selectById(merchantId);
        //用户角色是否为商家2  --根据用户查询角色
        //todo:
        //Long roleId = systemUserRoleDao.selectByMerchantId(SecurityContextUtils.getCurrentUserDto().getId());
        //todo:
        storeDto.setIsCreate(false);
        //商家 ，并且是连锁店  可创建  或者是没有店 可创建
		/*if(null != roleId && roleId == 2) {
			if(selectById.getMerchantType()==1 || null==selectByMerchantId || selectByMerchantId.size()==0) {
				storeDto.setIsCreate(true);
			}
		}
		//标注可管理的店铺
		if(selectByMerchantId != null) {
			//如果是商家，可管理所有店铺
			if(null != roleId && roleId == 2) {
				storeDto.setIsCreate(true);
				for (StoreInfoEntity storeInfoEntity : selectByMerchantId) {
						storeInfoEntity.setFlag(true);
				}
			}else {
				storeDto.setIsCreate(false);
				for (StoreInfoEntity storeInfoEntity : selectByMerchantId) {
					if(memberInfo.getStoreId().equals(storeInfoEntity.getId())) {
						storeInfoEntity.setFlag(true);
						break;
					}
				}
			}
		}*/
        storeDto.setMerchantType(selectById.getMerchantType());
        storeDto.setName(selectById.getName());
        storeDto.setStoreList(selectByMerchantId);
        return storeDto;
    }


}
