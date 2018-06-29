package com.topaiebiz.goods.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

/**
 * Created by hecaifeng on 2018/5/21.
 */
@Data
public class CategoryIdDTO extends PagePO {

    /**
     * 类目Id。
     */
    private Long categoryId;
}
