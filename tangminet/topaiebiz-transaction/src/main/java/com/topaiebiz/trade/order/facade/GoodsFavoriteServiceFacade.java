package com.topaiebiz.trade.order.facade;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.topaiebiz.goods.api.GoodsFavoriteApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-10 20:00
 */
@Component
@Slf4j
public class GoodsFavoriteServiceFacade {

    @Autowired
    private GoodsFavoriteApi goodsFavoriteApi;

    public Boolean addFavorite(Long memberId, Long itemId) {
        List<Long> itemIds = Lists.newArrayList(itemId);
        Integer res = goodsFavoriteApi.addFavorite(memberId, itemIds);
        log.info("goodsFavoriteApi.addFavorite({}, {}) return:{}", memberId, JSON.toJSONString(itemIds), res);
        return res > 0;
    }
}