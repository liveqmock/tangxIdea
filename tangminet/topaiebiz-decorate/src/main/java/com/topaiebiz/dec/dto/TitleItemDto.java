package com.topaiebiz.dec.dto;

import com.topaiebiz.goods.dto.sku.GoodsDTO;
import lombok.Data;

import java.util.List;

@Data
public class TitleItemDto {

    private Long titleId;

    private String titleName;

    private List<GoodsDTO> items;
}
