package com.topaiebiz.guider.bo;

import com.topaiebiz.guider.entity.GuiderTaskOrderEntity;
import com.topaiebiz.guider.entity.GuiderTaskPayEntity;
import lombok.Data;

import java.util.List;

/**
 * Created by ward on 2018-06-07.
 */
@Data
public class PayOrderBo {

    private Long taskId;

    private Long payId;

    private Integer payStatus;

    private Integer soldierOrderStatus;

    private GuiderTaskPayEntity currentTaskPay;

    private GuiderTaskOrderEntity currentTaskOrder;

    private List<GuiderTaskOrderEntity> otherTaskOrders;
}
