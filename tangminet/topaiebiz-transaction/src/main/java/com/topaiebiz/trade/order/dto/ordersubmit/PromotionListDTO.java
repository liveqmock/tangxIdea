package com.topaiebiz.trade.order.dto.ordersubmit;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-23 16:04
 */
@Data
public class PromotionListDTO {
    public List<PromotionInfoDTO> availableList = new ArrayList<>();
    public List<PromotionInfoDTO> unavailableList = new ArrayList<>();
}