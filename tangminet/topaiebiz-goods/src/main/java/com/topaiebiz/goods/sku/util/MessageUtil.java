package com.topaiebiz.goods.sku.util;

import com.nebulapaas.common.msg.core.MessageSender;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by hecaifeng on 2018/6/25.
 */
@Component
@Slf4j
public class MessageUtil {

    @Autowired
    private MessageSender messageSender;

    /**
     * 商品下架(下架，冻结，库存为0)
     *
     * @param itemId
     */
    public void outItem(Long itemId) {
        //异步通知ES模块
        try {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setType(MessageTypeEnum.GOODS_OUT);
            messageDTO.getParams().put("itemId", itemId);
            messageSender.publicMessage(messageDTO);
        } catch (Exception e) {
            log.error("通知失败", e);
        }
    }

    /**
     * 商品新增(上架，解冻，库存为0到有)
     *
     * @param itemId
     */
    public void putItem(Long itemId) {
        //异步通知ES模块
        try {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setType(MessageTypeEnum.GOODS_PUT);
            messageDTO.getParams().put("itemId", itemId);
            messageSender.publicMessage(messageDTO);
        } catch (Exception e) {
            log.error("通知失败", e);
        }
    }

    /**
     * 商品库存变更(订单生成，订单取消。)
     *
     * @param itemId
     */
    public void changItem(Long itemId) {
        //异步通知ES模块
        try {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setType(MessageTypeEnum.GOODS_UNDERCARRIAGE);
            messageDTO.getParams().put("itemId", itemId);
            messageSender.publicMessage(messageDTO);
        } catch (Exception e) {
            log.error("通知失败", e);

        }
    }
}
