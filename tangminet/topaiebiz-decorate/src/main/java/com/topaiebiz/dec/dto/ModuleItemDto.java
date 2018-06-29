package com.topaiebiz.dec.dto;

import com.topaiebiz.goods.dto.sku.GoodsDTO;
import lombok.Data;

import java.util.List;

@Data
public class ModuleItemDto {

    private Long moduleId;

    private List<GoodsDTO> items;
}
