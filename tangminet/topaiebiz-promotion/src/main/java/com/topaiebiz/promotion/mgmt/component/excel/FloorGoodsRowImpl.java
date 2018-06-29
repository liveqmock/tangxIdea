package com.topaiebiz.promotion.mgmt.component.excel;

import com.topaiebiz.promotion.common.util.ReadExcelUtil;
import com.topaiebiz.promotion.mgmt.component.ExcelRow;
import com.topaiebiz.promotion.mgmt.dto.init.data.InitFloorGoodsDTO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;

public class FloorGoodsRowImpl implements ExcelRow<InitFloorGoodsDTO> {
    @Override
    public InitFloorGoodsDTO readRow(Row currentRow) {
        InitFloorGoodsDTO floorGoods = new InitFloorGoodsDTO();

        Cell codeCell = currentRow.getCell(0);
        Cell nameCell = currentRow.getCell(1);
        Cell goodsIdCell = currentRow.getCell(2);
        Cell priceCell = currentRow.getCell(3);
        Cell sortCell = currentRow.getCell(4);

        String itemIdStr = ReadExcelUtil.getCellValue(goodsIdCell, true);
        Long itemId;
        if (itemIdStr.indexOf("-") > 0) {
            int itemIdLength = itemIdStr.length();
            itemId = Long.parseLong(itemIdStr.substring(0, itemIdLength - 1));
        } else {
            itemId = Long.parseLong(itemIdStr);
        }
        floorGoods.setGoodsId(itemId);

        floorGoods.setFloorCode(codeCell.getStringCellValue());
        floorGoods.setGoodsName(nameCell.getStringCellValue());
        floorGoods.setDiscountPrice(new BigDecimal(priceCell.getNumericCellValue()).setScale(2, BigDecimal.ROUND_HALF_UP));
        floorGoods.setSort(Integer.parseInt(ReadExcelUtil.getCellValue(sortCell, true)));
        return floorGoods;
    }

}
