package com.topaiebiz.decorate.dto;

import lombok.Data;

/**
 * 商品导入dto
 *
 * @author huzhenjia
 */
@Data
public class ItemExcelDto {

    private Long sortNo;

    private Long goodsId;

    private String itemName;

    private String pictureName;
}
