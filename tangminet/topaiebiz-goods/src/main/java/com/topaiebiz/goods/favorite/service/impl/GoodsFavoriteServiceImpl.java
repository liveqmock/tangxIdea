package com.topaiebiz.goods.favorite.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.favorite.dao.GoodsFavoriteDao;
import com.topaiebiz.goods.favorite.dto.GoodsFavoriteDto;
import com.topaiebiz.goods.favorite.entity.GoodsFavoriteEntity;
import com.topaiebiz.goods.favorite.exception.GoodsFootprintExceptionEnum;
import com.topaiebiz.goods.favorite.service.GoodsFavoriteService;
import com.topaiebiz.goods.sku.dao.ItemDao;
import com.topaiebiz.goods.sku.dao.ItemPictureDao;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description 收藏夹数据库访问层（以商品最小sku单元为收藏）业务接口实现
 *
 * <p>Author Hedda
 *
 * <p>Date 2017年9月11日 上午11:29:20
 *
 * <p>Copyright Cognieon technology group co.LTD. All rights reserved.
 *
 * <p>Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
public class GoodsFavoriteServiceImpl implements GoodsFavoriteService {

    @Autowired
    private GoodsFavoriteDao goodsFavoriteDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private ItemPictureDao itemPictureDao;

    @Override
    public PageInfo<GoodsFavoriteDto> getGoodsFavoriteListByMemberId(PagePO pagePO, Long memberId) {
        Page<GoodsFavoriteDto> page = PageDataUtil.buildPageParam(pagePO);
        List<GoodsFavoriteDto> goodsFavoriteDtos =
                goodsFavoriteDao.selectGoodsFavoriteListByMemberId(page, memberId);
        page.setRecords(goodsFavoriteDtos);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public Integer removelGoodsFavorite(Long id, Long memberId) throws GlobalException {
        Integer i = 0;
        if (id == null) {
            throw new GlobalException(GoodsFootprintExceptionEnum.GOODSFAVOR_ID_NOT_NULL);
        }
        EntityWrapper<GoodsFavoriteEntity> cond = new EntityWrapper<>();
        cond.eq("memberId", memberId);
        cond.eq("goodsId", id);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        GoodsFavoriteEntity goodsFavorite = new GoodsFavoriteEntity();
        goodsFavorite.clearInit();
        goodsFavorite.clearInit();
        goodsFavorite.setMemberId(memberId);
        goodsFavorite.setDeletedFlag(Constants.DeletedFlag.DELETED_YES);
        goodsFavorite.setGoodsId(id);
        i = goodsFavoriteDao.update(goodsFavorite, cond);
        return i;
    }

    @Override
    public Boolean findGoodsFavorite(Long memberId, Long itemId) {
        Boolean b = false;
        EntityWrapper<GoodsFavoriteEntity> cond = new EntityWrapper<>();
        cond.eq("memberId", memberId);
        cond.eq("goodsId", itemId);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<GoodsFavoriteEntity> goodsFavoriteEntities = goodsFavoriteDao.selectList(cond);
        if (CollectionUtils.isNotEmpty(goodsFavoriteEntities)) {
            b = true;
        }
        return b;
    }
}
