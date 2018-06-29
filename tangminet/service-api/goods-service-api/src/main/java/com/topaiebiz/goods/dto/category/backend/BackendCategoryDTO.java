package com.topaiebiz.goods.dto.category.backend;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Description 商品后台类目dto 。
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年9月24日 下午3:10:58
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class BackendCategoryDTO implements Serializable {

    /**
     * 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。
     */
    private Long id;

    /**
     * 类目类型。1为商家自定义类目  0或者空为平台类目。暂时不用管
     */
    private Integer cateType;

    /**
     * 类目名称。
     */
    private String name;

    /**
     * 类目描述。
     */
    private String description;

    /**
     * 类目等级 (1 一级 2 二级 3 三级)。
     */
    private Integer level;

    /**
     * 父类目。
     */
    private Long parentId;

    /**
     * 商家类目审核是否通过，1为审核通过。0为待审核,2 为审核不通过。
     */
    private Integer status;


}
