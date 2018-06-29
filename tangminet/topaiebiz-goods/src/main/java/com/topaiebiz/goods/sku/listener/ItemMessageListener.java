package com.topaiebiz.goods.sku.listener;

import com.nebulapaas.common.msg.core.MessageListener;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.topaiebiz.elasticsearch.api.ElasticSearchApi;
import com.topaiebiz.goods.api.GoodsApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author tangx.w
 * @Description: elasticsearh 监听事件
 * @Date: Create in 15:59 2018/6/22
 * @Modified by:
 */
@Slf4j
@Component
public class ItemMessageListener implements MessageListener {

    @Autowired
    private ElasticSearchApi elasticSearchApi;

    @Autowired
    private GoodsApi goodsApi;

    @Override
    public Set<MessageTypeEnum> getTargetMessageTypes() {
        Set<MessageTypeEnum> type = new HashSet<>();
        type.add(MessageTypeEnum.GOODS_PUT);
        type.add(MessageTypeEnum.GOODS_UNDERCARRIAGE);
        type.add(MessageTypeEnum.GOODS_OUT);
        return type;
    }

    @Override
    public void onMessage(MessageDTO msg) {
        Long itemId = (Long) msg.getParams().get("itemId");
        goodsApi.updateMinPrice(itemId);
        elasticSearchApi.syncItem(itemId);
    }
}