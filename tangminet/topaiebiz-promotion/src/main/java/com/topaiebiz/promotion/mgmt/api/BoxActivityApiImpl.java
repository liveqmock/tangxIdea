package com.topaiebiz.promotion.mgmt.api;

import com.nebulapaas.common.redis.cache.RedisCache;
import com.topaiebiz.promotion.api.BoxActivityApi;
import com.topaiebiz.promotion.mgmt.dao.BoxActivityDao;
import com.topaiebiz.promotion.mgmt.dao.BoxActivityItemDao;
import com.topaiebiz.promotion.mgmt.dao.PromotionDao;
import com.topaiebiz.promotion.mgmt.dto.box.BoxActivityDTO;
import com.topaiebiz.promotion.mgmt.service.BoxActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.topaiebiz.promotion.constants.PromotionConstants.AwardType.CARD_AWARD;
import static com.topaiebiz.promotion.constants.PromotionConstants.AwardType.COUPON_AWARD;
import static com.topaiebiz.promotion.constants.PromotionConstants.CacheKey.*;
import static com.topaiebiz.promotion.constants.PromotionConstants.SeparatorChar.SEPARATOR_COMMA;

@Service
public class BoxActivityApiImpl implements BoxActivityApi {
    @Autowired
    private BoxActivityDao boxActivityDao;
    @Autowired
    private BoxActivityItemDao boxActivityItemDao;
    @Autowired
    private BoxActivityService boxActivityService;
    @Autowired
    private PromotionDao promotionDao;
    @Autowired
    private RedisCache redisCache;

    @Override
    public void initRestStorage() {
        //删除缓存
        redisCache.delete(OPEN_BOX);
        redisCache.delete(OPEN_BOX_ACTIVITY);
        //删除产生宝箱节点缓存
        redisCache.delKeys(TIME_NODE_PREFIX);
        redisCache.delKeys(FIXED_NODE_PREFIX);

        BoxActivityDTO boxActivity = boxActivityService.getBoxActivity();
        if (boxActivity == null) {
            return;
        }
        /**
         * 批量更新宝箱剩余库存（除实物宝箱外）
         * 实物宝箱配置无日限量，该宝箱的每天剩余库存无需更新
         */
        StringBuilder awardTypes = new StringBuilder();
        awardTypes.append(COUPON_AWARD);
        awardTypes.append(SEPARATOR_COMMA);
        awardTypes.append(CARD_AWARD);
        boxActivityItemDao.batchUpdateRestStorage(boxActivity.getId(), awardTypes.toString());
    }
}
