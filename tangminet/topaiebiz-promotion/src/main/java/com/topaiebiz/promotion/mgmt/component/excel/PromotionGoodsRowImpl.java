package com.topaiebiz.promotion.mgmt.component.excel;

import com.topaiebiz.promotion.common.util.ReadExcelUtil;
import com.topaiebiz.promotion.constants.PromotionConstants;
import com.topaiebiz.promotion.mgmt.component.ExcelRow;
import com.topaiebiz.promotion.mgmt.dto.init.data.InitPromotionGoodsDTO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;

public class PromotionGoodsRowImpl implements ExcelRow<InitPromotionGoodsDTO> {
    @Override
    public InitPromotionGoodsDTO readRow(Row currentRow) {
        InitPromotionGoodsDTO promotionGoods = new InitPromotionGoodsDTO();

        Cell itemIdCell = currentRow.getCell(0);
        Cell skuCell = currentRow.getCell(2);
        Cell promotionNumCell = currentRow.getCell(6);
        Cell promotionPriceCell = currentRow.getCell(5);
        Cell confineNumCell = currentRow.getCell(8);

        String itemIdStr = ReadExcelUtil.getCellValue(itemIdCell, true);
        Long itemId;
        if (itemIdStr.indexOf("-") > 0) {
            int itemIdLength = itemIdStr.length();
            itemId = Long.parseLong(itemIdStr.substring(0, itemIdLength - 1));
        } else {
            itemId = Long.parseLong(itemIdStr);
        }
        promotionGoods.setItemId(itemId);

        String skuIdStr = ReadExcelUtil.getCellValue(skuCell, true);
        Long skuId;
        if (skuIdStr.indexOf("-") > 0) {
            int skuIdLength = skuIdStr.length();
            skuId = Long.parseLong(skuIdStr.substring(0, skuIdLength - 1));
        } else {
            skuId = Long.parseLong(skuIdStr);
        }
        promotionGoods.setGoodsSkuId(skuId);
        Long promotionNum = new Double(ReadExcelUtil.getCellValue(promotionNumCell, false)).longValue();
        promotionGoods.setPromotionNum(promotionNum.intValue());
        BigDecimal promotionPrice = new BigDecimal(promotionPriceCell.getNumericCellValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
        promotionGoods.setPromotionPrice(promotionPrice);
        promotionGoods.setConfineNum(Integer.parseInt(ReadExcelUtil.getCellValue(confineNumCell, true)));
        //减价
        promotionGoods.setDiscountType(PromotionConstants.DiscountType.THE_SALE);
        //审核通过
        promotionGoods.setState(PromotionConstants.AuditState.APPROVED_AUDIT);

        return promotionGoods;
    }

}
