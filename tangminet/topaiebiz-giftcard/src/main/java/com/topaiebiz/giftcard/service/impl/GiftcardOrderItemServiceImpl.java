package com.topaiebiz.giftcard.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.card.constant.ApplyScopeEnum;
import com.topaiebiz.giftcard.dao.GiftcardOrderItemDao;
import com.topaiebiz.giftcard.entity.GiftcardBatch;
import com.topaiebiz.giftcard.entity.GiftcardOrder;
import com.topaiebiz.giftcard.entity.GiftcardOrderItem;
import com.topaiebiz.giftcard.enums.GiftcardExceptionEnum;
import com.topaiebiz.giftcard.service.GiftcardBatchService;
import com.topaiebiz.giftcard.service.GiftcardOrderItemService;
import com.topaiebiz.giftcard.service.GiftcardOrderService;
import com.topaiebiz.giftcard.util.DateUtil;
import com.topaiebiz.giftcard.vo.OrderSuccVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: Jeff Chen
 * @date: created in 下午8:45 2018/1/25
 */
@Service
public class GiftcardOrderItemServiceImpl extends ServiceImpl<GiftcardOrderItemDao,GiftcardOrderItem> implements GiftcardOrderItemService {

    @Autowired
    private GiftcardOrderService giftcardOrderService;
    @Autowired
    private GiftcardBatchService giftcardIssueService;

    @Override
    public OrderSuccVO getPaidOrderById(Long orderId,Long memberId) {
        EntityWrapper<GiftcardOrder> orderEntityWrapper = new EntityWrapper<>();
        orderEntityWrapper.eq("id", orderId);
        orderEntityWrapper.eq("memberId", memberId);
        if (null == giftcardOrderService.selectOne(orderEntityWrapper)) {
            throw new GlobalException(GiftcardExceptionEnum.ORDER_NOT_EXIST);
        }

        EntityWrapper<GiftcardOrderItem> wrapper = new EntityWrapper<>();
        wrapper.eq("orderId", orderId);
        GiftcardOrderItem orderItem = selectOne(wrapper);
        if (null == orderItem) {
            throw new GlobalException(GiftcardExceptionEnum.ORDER_NOT_EXIST);
        }
        //没有卡号的为异常订单
        if (StringUtils.isBlank(orderItem.getCardNoList())) {
            throw new GlobalException(GiftcardExceptionEnum.ORDER_EXCEPTION);
        }
        GiftcardBatch giftcardIssue = giftcardIssueService.selectById(orderItem.getBatchId());
        if (null == giftcardIssue) {
            throw new GlobalException(GiftcardExceptionEnum.ISSUE_NOT_EXIST);
        }

        OrderSuccVO orderSuccVO = new OrderSuccVO();
        BeanCopyUtil.copy(orderItem, orderSuccVO);
        orderSuccVO.setCardList(strToList(orderItem.getCardNoList()));
        orderSuccVO.setGivenFlag(giftcardIssue.getGivenFlag());
        orderSuccVO.setScope(ApplyScopeEnum.getById(giftcardIssue.getApplyScope()).getScopeDesc());
        orderSuccVO.setDeadTime(DateUtil.renewalDays(new Date(),giftcardIssue.getValidDays()));
        return orderSuccVO;
    }

    private List<String> strToList(String string) {
        List<String> cardList = new ArrayList<>();
        if (StringUtils.isNotBlank(string)) {
            String[] strings = string.split(",");
            if (null != strings) {
                for (String card : strings) {
                    if (StringUtils.isNotBlank(card)) {
                        cardList.add(card);
                    }
                }
            }
        }

        return cardList;
    }
}
