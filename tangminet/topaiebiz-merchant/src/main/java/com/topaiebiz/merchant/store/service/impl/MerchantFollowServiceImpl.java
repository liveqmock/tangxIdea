package com.topaiebiz.merchant.store.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.merchant.store.dao.MerchantFollowDao;
import com.topaiebiz.merchant.store.dto.MerchantFollowDto;
import com.topaiebiz.merchant.store.entity.MerchantFollowEntity;
import com.topaiebiz.merchant.store.entity.MerchantMemberEntity;
import com.topaiebiz.merchant.store.exception.MerchantFollowException;
import com.topaiebiz.merchant.store.service.MerchantFollowService;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/19 0019 上午 10:56
 */
@Service
public class MerchantFollowServiceImpl implements MerchantFollowService {

    @Autowired
    private MerchantFollowDao merchantFollowDao;

    @Override
    public PageInfo<MerchantFollowDto> getMerchantFollowList(PagePO pagePO, MerchantFollowDto merchantFollowDto) {
        Page<MerchantFollowDto> page = PageDataUtil.buildPageParam(pagePO);
        page.setRecords(merchantFollowDao.selectMerchantFollowList(page, merchantFollowDto));
        return PageDataUtil.copyPageInfo(page);
    }


    @Override
    public PageInfo<MerchantFollowDto> selectMerchantFollowList(PagePO pagePO, MerchantFollowDto merchantQualificationDto) {
        Page<MerchantFollowDto> page = PageDataUtil.buildPageParam(pagePO);
        page.setRecords(merchantFollowDao.selectMerchantFollowLists(page, merchantQualificationDto));
        return PageDataUtil.copyPageInfo(page);
    }


    @Override
    public Integer removeMerchantFollowById(MerchantFollowDto merchantFollowDto) throws GlobalException {
        return merchantFollowDao.updateMerchantFollowById(merchantFollowDto);
    }

    @Override
    public Integer saveMerchantFollow(MerchantFollowDto merchantFollowDto) throws GlobalException {
        MerchantFollowEntity merchantFollowEntity = new MerchantFollowEntity();
        BeanCopyUtil.copy(merchantFollowDto, merchantFollowEntity);
        //根据会员id查询记录是否有关注信息
        MerchantFollowEntity Follow = merchantFollowDao.selectFollowByMemberId(merchantFollowEntity);
        if (Follow != null) {
            throw new GlobalException(MerchantFollowException.STORE_HAS_BEEN_CONCERNED);
        }
        merchantFollowEntity.setCreatedTime(new Date());
        merchantFollowEntity.setCreatorId(merchantFollowDto.getMemberId());
        return merchantFollowDao.insert(merchantFollowEntity);
    }

    @Override
    public MerchantFollowDto selectMerchantFollowDetails(Long StoreId) {
        return merchantFollowDao.selectMerchantFollowDetails(StoreId);
    }

    @Override
    public Boolean checkMerchantFollowById(MerchantFollowDto merchantFollowDto) {
        MerchantFollowEntity merchantFollowEntity = new MerchantFollowEntity();
        merchantFollowEntity.clearInit();
        merchantFollowEntity.setMemberId(merchantFollowDto.getMemberId());
        merchantFollowEntity.setStoreId(merchantFollowDto.getStoreId());
        merchantFollowEntity.setDeletedFlag(Constants.DeletedFlag.DELETED_NO);
        MerchantFollowEntity merchantFollowEntits = merchantFollowDao.selectOne(merchantFollowEntity);
        if (merchantFollowEntits != null) {
            return true;
        }
        return false;
    }
}
