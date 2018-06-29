package com.topaiebiz.goods.favorite.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.redis.lock.DistLockSservice;
import com.nebulapaas.common.redis.vo.LockResult;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.favorite.dao.GoodsFootprintDao;
import com.topaiebiz.goods.favorite.dto.GoodsFootprintDto;
import com.topaiebiz.goods.favorite.entity.GoodsFootprintEntity;
import com.topaiebiz.goods.favorite.exception.GoodsFootprintExceptionEnum;
import com.topaiebiz.goods.favorite.service.GoodsFootprintService;
import com.topaiebiz.goods.sku.dao.ItemDao;
import com.topaiebiz.goods.sku.entity.ItemEntity;
import com.topaiebiz.goods.sku.exception.GoodsSkuExceptionEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2018/1/5.
 */
@Service
public class GoodsFootprintServiceImpl implements GoodsFootprintService{

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private DistLockSservice distLockSservice;

    @Autowired
    private GoodsFootprintDao goodsFootprintDao;

    @Override
    public PageInfo<GoodsFootprintDto> getGoodsFootprintListByMemberId(GoodsFootprintDto goodsFootprintDto, Long memberId) {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(goodsFootprintDto.getPageNo());
        pagePO.setPageSize(goodsFootprintDto.getPageSize());
        Page<GoodsFootprintDto> page = PageDataUtil.buildPageParam(pagePO);
        goodsFootprintDto.setMemberId(memberId);
        List<GoodsFootprintDto> goodsFootprintDtos = goodsFootprintDao.selectGoodsFootprintListByMemberId(page, goodsFootprintDto);
        page.setRecords(goodsFootprintDtos);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public Integer removelGoodsFootprint(Long[] id) {
        Integer i = 0;
        if (id == null) {
            throw new GlobalException(GoodsFootprintExceptionEnum.GOODSFAVOR_ID_NOT_NULL);
        }
        for (Long long1 : id) {
            GoodsFootprintEntity goodsFootprintEntity = goodsFootprintDao.selectById(long1);
            if (goodsFootprintEntity == null) {
                throw new GlobalException(GoodsFootprintExceptionEnum.GOODSFAVOR_ID_NOT_EXIST);
            }
            goodsFootprintEntity.setDeletedFlag(Constants.DeletedFlag.DELETED_YES);
            i = goodsFootprintDao.updateById(goodsFootprintEntity);
        }
        return i;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addGoodsFootprint(Long memberId, Long[] itemIds) throws GlobalException {
        LockResult memberLock = null;
        try {
            if (itemIds.length == 0) {
                throw new GlobalException(GoodsFootprintExceptionEnum.GOODSFAVOR_GOODSID_NOT_NULL);
            }
            memberLock = distLockSservice.tryLock("goods-footprint-add-", memberId);
            if (!memberLock.isSuccess()) {
                throw new GlobalException(GoodsSkuExceptionEnum.GOODS_ALREADY_EXISTS);
            }
            insertGoodsFootprint(memberId, itemIds);
        } finally {
            distLockSservice.unlock(memberLock);
        }
        return true;
    }

    private boolean insertGoodsFootprint(Long memberId, Long[] itemIds) {
        EntityWrapper<GoodsFootprintEntity> condition = new EntityWrapper<>();
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condition.eq("memberId", memberId);
        condition.orderBy("createdTime", false);
        RowBounds rowBounds = new RowBounds(99, 1);
        List<GoodsFootprintEntity> goodsFootprintEntities = goodsFootprintDao.selectPage(rowBounds, condition);
        if (CollectionUtils.isNotEmpty(goodsFootprintEntities)) {
            for (GoodsFootprintEntity goodsFootprintEntity : goodsFootprintEntities) {
                goodsFootprintEntity.setDeletedFlag(Constants.DeletedFlag.DELETED_YES);
                goodsFootprintDao.updateById(goodsFootprintEntity);
            }
        }
        for (Long itemId : itemIds) {
            ItemEntity itemEntity = itemDao.selectById(itemId);
            if (itemEntity == null) {
                throw new GlobalException(GoodsFootprintExceptionEnum.GOODSCART_GOODSATTR_NOT_NULL);
            }
            GoodsFootprintEntity cond = new GoodsFootprintEntity();
            cond.clearInit();
            cond.setMemberId(memberId);
            cond.setGoodsId(itemId);
            cond.setDeletedFlag(Constants.DeletedFlag.DELETED_NO);
            GoodsFootprintEntity goodsFootprint = goodsFootprintDao.selectOne(cond);
            if (goodsFootprint == null) {
                GoodsFootprintEntity goodsFootprintEntity = new GoodsFootprintEntity();
                goodsFootprintEntity.setGoodsId(itemId);
                goodsFootprintEntity.setMemberId(memberId);
                goodsFootprintEntity.setCreatedTime(new Date());
                goodsFootprintEntity.setCreatorId(memberId);
                boolean result = goodsFootprintDao.insert(goodsFootprintEntity) > 0;
                if (!result) {
                    return false;
                }
            }
        }
        return true;

    }


}
