package com.topaiebiz.promotion.mgmt.component.excel;

import com.topaiebiz.promotion.common.util.ReadExcelUtil;
import com.topaiebiz.promotion.mgmt.component.ExcelRow;
import com.topaiebiz.promotion.mgmt.dto.init.data.InitFloorCardDTO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;

public class FloorCardRowImpl implements ExcelRow<InitFloorCardDTO> {
    @Override
    public InitFloorCardDTO readRow(Row currentRow) {
        InitFloorCardDTO floorCard = new InitFloorCardDTO();

        Cell codeCell = currentRow.getCell(0);
        Cell nameCell = currentRow.getCell(1);
        Cell batchIdCell = currentRow.getCell(2);
        Cell priceCell = currentRow.getCell(3);
        Cell sortCell = currentRow.getCell(4);

        floorCard.setFloorCode(codeCell.getStringCellValue());
        floorCard.setCardName(nameCell.getStringCellValue());
        floorCard.setBatchId(Long.parseLong(ReadExcelUtil.getCellValue(batchIdCell, true)));
        floorCard.setSalePrice(new BigDecimal(priceCell.getNumericCellValue()).setScale(2, BigDecimal.ROUND_HALF_UP));
        floorCard.setSort(Integer.parseInt(ReadExcelUtil.getCellValue(sortCell, true)));
        return floorCard;
    }

}
