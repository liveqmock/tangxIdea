package com.topaiebiz.merchant.freight.dto;

import com.topaiebiz.merchant.enter.dto.DistrictInfoDto;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Description:运费模板详情dto类
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 *
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class FreightTempleteDetailDto  implements Serializable {

    /** 全局主键id */
    private Long id;

    /** 关联的运费模板ID */
    private Long freightId;

    /** 配送方式 */
    private Integer type;

    /** 配送区域集合 */
    private String districtIdList;

    /** 首次价格 */
    private BigDecimal firstPrice;

    /** 首次件数 */
    private BigDecimal firstNum;

    /** 续件价格 */
    private BigDecimal addPrice;

    /** 续件件数 */
    private BigDecimal addNum;

    /** 是否为默认运费 */
    private Integer isDefault;

    private List<DistrictInfoDto> districtDtoList;

    private String nameListStr;
}
