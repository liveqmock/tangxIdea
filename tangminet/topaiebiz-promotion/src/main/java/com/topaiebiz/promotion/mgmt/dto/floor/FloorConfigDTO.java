package com.topaiebiz.promotion.mgmt.dto.floor;

import lombok.Data;

/**
 * 楼层配置
 */
@Data
public class FloorConfigDTO {
    /**
     * 楼层分类code
     */
    private String floorCode;
    /**
     * 楼层分类名称
     */
    private String name;
}
