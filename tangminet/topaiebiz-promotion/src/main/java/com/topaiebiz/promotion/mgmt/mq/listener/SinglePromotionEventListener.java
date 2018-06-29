package com.topaiebiz.promotion.mgmt.mq.listener;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Sets;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.msg.core.MessageListener;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.topaiebiz.elasticsearch.api.ElasticSearchApi;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.promotion.api.PromotionApi;
import com.topaiebiz.promotion.mgmt.dao.PromotionGoodsDao;
import com.topaiebiz.promotion.mgmt.entity.PromotionGoodsEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nebulapaas.common.msg.dto.MessageTypeEnum.SINGLE_PROMOTION_UPDATE;

/***
 * 单品营销活动需要更新item的最低价格
 * @author yfeng
 * @date 2018-06-28 16:47
 */
@Component
public class SinglePromotionEventListener implements MessageListener {

    @Autowired
    private PromotionApi promotionApi;
    @Autowired
    private GoodsApi goodsApi;

    @Autowired
    private PromotionGoodsDao promotionGoodsDao;

    @Autowired
    private ElasticSearchApi elasticSearchApi;

    @Override
    public Set<MessageTypeEnum> getTargetMessageTypes() {
        return Sets.newHashSet(SINGLE_PROMOTION_UPDATE);
    }

    /**
     * @param msg
     */
    @Override
    public void onMessage(MessageDTO msg) {
        List<Long> promotionIds = (List<Long>) msg.getParams().get("promotionIds");

        List<PromotionGoodsEntity> promotionGoodsLists = null;
        Integer offset = 0;
        do {
            promotionGoodsLists = queryPromotionGoods(promotionIds, offset);
            List<Long> itemIds = promotionGoodsLists.stream().map(item -> item.getItemId()).collect(Collectors.toList());

            //批量同步更新item的minPrice字段
            goodsApi.updateMinPrice(itemIds);

            //通知异步搜索引擎批量更新商品
            elasticSearchApi.syncItems(itemIds);

            offset += promotionGoodsLists.size();
        } while (CollectionUtils.isEmpty(promotionGoodsLists));
    }

    private List<PromotionGoodsEntity> queryPromotionGoods(List<Long> promotionIds, Integer offset) {
        RowBounds rowBounds = new RowBounds(offset, 100);
        EntityWrapper<PromotionGoodsEntity> cond = new EntityWrapper<>();
        cond.in("promotionId", promotionIds);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        return promotionGoodsDao.selectPage(rowBounds, cond);
    }
}