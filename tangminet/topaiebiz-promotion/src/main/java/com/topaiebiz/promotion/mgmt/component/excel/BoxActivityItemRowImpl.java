package com.topaiebiz.promotion.mgmt.component.excel;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.promotion.common.util.ReadExcelUtil;
import com.topaiebiz.promotion.mgmt.component.ExcelRow;
import com.topaiebiz.promotion.mgmt.dto.box.json.ResBoxJsonDTO;
import com.topaiebiz.promotion.mgmt.dto.init.data.InitBoxActivityItemDTO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;

import static com.topaiebiz.promotion.constants.PromotionConstants.AwardType.RES_AWARD;

public class BoxActivityItemRowImpl implements ExcelRow<InitBoxActivityItemDTO> {
    @Override
    public InitBoxActivityItemDTO readRow(Row currentRow) {
        InitBoxActivityItemDTO item = new InitBoxActivityItemDTO();
        Cell awardTypeCell = currentRow.getCell(0);
        Cell promotionBoxIdCell = currentRow.getCell(1);

        //总库存
        Cell totalStorageCell1 = currentRow.getCell(2);
        Cell dayStorageCell = currentRow.getCell(3);
        Cell rateCell = currentRow.getCell(4);

        Integer awardType = Integer.parseInt(ReadExcelUtil.getCellValue(awardTypeCell, true));
        Long promotionBoxId = Long.parseLong(ReadExcelUtil.getCellValue(promotionBoxIdCell, true));
        Integer totalStorage = Integer.parseInt(ReadExcelUtil.getCellValue(totalStorageCell1, true));
        Integer dayStorage = Integer.parseInt(ReadExcelUtil.getCellValue(dayStorageCell, true));
        BigDecimal awardRate = new BigDecimal(rateCell.getNumericCellValue()).setScale(2, BigDecimal.ROUND_HALF_UP);

        //奖品类型
        item.setAwardType(awardType);
        item.setPromotionBoxId(promotionBoxId);
        item.setTotalStorage(totalStorage);
        item.setTotalStorageRest(totalStorage);
        item.setDayStorage(dayStorage);
        item.setDayStorageRest(dayStorage);
        item.setAwardRate(awardRate.doubleValue());

        if (awardType.equals(RES_AWARD)) {
            //实物奖品名称
            Cell resNameCell = currentRow.getCell(5);
            //图片地址
            Cell resImageCell = currentRow.getCell(6);
            ResBoxJsonDTO resBox = new ResBoxJsonDTO();
            resBox.setAwardName(resNameCell.getStringCellValue());
            resBox.setResCover(resImageCell.getStringCellValue());
            item.setResContent(JSON.toJSONString(resBox));
        } else {
            Cell awardIdCell = currentRow.getCell(5);
            Long awardId = Long.parseLong(ReadExcelUtil.getCellValue(awardIdCell, true));
            item.setAwardId(awardId);
        }

        return item;
    }

}
