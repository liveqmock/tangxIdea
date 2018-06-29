package com.topaiebiz.trade.order.dto.ordersubmit;

import com.topaiebiz.member.dto.address.MemberAddressDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-09 11:15
 */
@Data
public class GoodsSplitDTO {
    private List<StoreItemDTO> stores = new ArrayList<>();
    private MemberAddressDto address;
    private Boolean hasHaitaoOrder;
}