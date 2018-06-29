package com.topaiebiz.trade.refund.api.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.topaiebiz.trade.api.refund.RefundServiceApi;
import com.topaiebiz.trade.constants.RefundConstants;
import com.topaiebiz.trade.dto.refund.RefundDTO;
import com.topaiebiz.trade.dto.refund.RefundDetailDTO;
import com.topaiebiz.trade.refund.dao.RefundOrderDao;
import com.topaiebiz.trade.refund.dao.RefundOrderDetailDao;
import com.topaiebiz.trade.refund.entity.RefundOrderDetailEntity;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/***
 * @author yfeng
 * @date 2018-03-26 11:26
 */
@Service
public class RefundServiceApiImpl implements RefundServiceApi {
    @Autowired
    private RefundOrderDao refundOrderDao;
    @Autowired
    private RefundOrderDetailDao refundOrderDetailDao;

    @Override
    public List<RefundDTO> queryFinishedRefundOrders(List<Long> storeOrderIds) {
        if (CollectionUtils.isEmpty(storeOrderIds)) {
            return Collections.emptyList();
        }
        EntityWrapper<RefundOrderEntity> cond = new EntityWrapper<>();
        cond.in("orderId", storeOrderIds);
        cond.eq("refundState", RefundConstants.RefundStatus.COMPLETED);
        cond.eq("refundRange", RefundConstants.RefundRange.GOODS);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<RefundOrderEntity> refundEntities = refundOrderDao.selectList(cond);
        if (CollectionUtils.isEmpty(refundEntities)) {
            return Collections.emptyList();
        }
        return PageDataUtil.copyList(refundEntities, RefundDTO.class);
    }

    @Override
    public Map<Long, List<RefundDetailDTO>> querySKURefundDetails(List<Long> refundIds) {
        if (CollectionUtils.isEmpty(refundIds)) {
            return Collections.emptyMap();
        }
        EntityWrapper<RefundOrderDetailEntity> cond = new EntityWrapper<>();
        cond.in("refundOrderId", refundIds);
        List<RefundOrderDetailEntity> refundDetails = refundOrderDetailDao.selectList(cond);
        if (CollectionUtils.isEmpty(refundDetails)) {
            return Collections.emptyMap();
        }
        List<RefundDetailDTO> refunds = BeanCopyUtil.copyList(refundDetails, RefundDetailDTO.class);

        Map<Long, List<RefundDetailDTO>> resultMap = new HashMap<>();
        for (RefundDetailDTO detailDTO : refunds) {
            List<RefundDetailDTO> refundDetailDTOS = resultMap.get(detailDTO.getRefundOrderId());
            if (refundDetailDTOS == null) {
                refundDetailDTOS = new ArrayList<>();
                resultMap.put(detailDTO.getRefundOrderId(), refundDetailDTOS);
            }
            refundDetailDTOS.add(detailDTO);
        }
        return resultMap;
    }

}