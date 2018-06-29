package com.topaiebiz.goods.repair.dto;

import lombok.Data;

/***
 * @author yfeng
 * @date 2018-03-09 17:56
 */
@Data
public class ItemPicDTO {
    private Long storeId;
    private Long itemId;
    private Boolean isDef;
    private String imgUrl;
    private Boolean isMain;
}