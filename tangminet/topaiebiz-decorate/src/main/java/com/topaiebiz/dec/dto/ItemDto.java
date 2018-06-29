package com.topaiebiz.dec.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemDto {

    private String pictureName;

    private BigDecimal marketPrice;

    private BigDecimal defaultPrice;

    private String name;
}
