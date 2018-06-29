package com.topaiebiz.promotion.mgmt.component;

import org.apache.poi.ss.usermodel.Row;

public interface ExcelRow<T> {
    T readRow(Row currentRow);
}
