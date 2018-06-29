package com.topaiebiz.decorate.dto;


import lombok.Data;

import java.util.List;

/**
 * 商品导出 dto
 *
 * @author huzhenjia
 */
@Data
public class ExportItemDto {

    private List<ItemExcelDto> itemExcelDtos;

}
