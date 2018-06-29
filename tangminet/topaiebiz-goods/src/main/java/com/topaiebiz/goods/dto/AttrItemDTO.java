package com.topaiebiz.goods.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @description: 属性名和值DTO
 * @author: Jeff Chen
 * @date: created in 下午1:39 2018/5/19
 */
@Data
public class AttrItemDTO implements Serializable {


    /**
     * 属性id，
     */
    private Long attrId;
    /**
     * 所属类目。
     */
    @NotNull(message = "填写类目ID")
    private Long categoryId;
    /**
     * 属性分组id
     */
    private Long groupId;

    /**
     * 分组名称
     */
    private String groupName;
    /**
     * 类目属性名字。
     */
    @NotEmpty(message = "填写属性名")
    private String attrName;
    /**
     * 属性类型。1 文本 2日期 3 数字 4 时间
     */
    private Integer valueType;
    /**
     * 默认单位。
     */
    private String defaultUnit;
    /**
     * 是否为销售属性（1是，0不是）。
     */
    private Integer isSale;
    /**
     * 是否为必填项（1是，0不是）。
     */
    private Integer isMust;
    /**
     * 是否可以自定义（1是，0不是）
     */
    private Integer isCustom;
    /**
     * 是否用来筛选（1是，0不是）
     */
    private Integer isFilter;
    /**
     * 排序号。
     */
    private Long sortNo;
    /**
     * 描述
     */
    private String description;
    /**
     * 属性类型：1 平台定义 2商家定义
     */
    private Integer attrType;

    /**
     * 待同步，已同步，待删除
     */
    private String status;

    /**
     * 属性值列表
     */
    private List<String> valueList;
    private Long creatorId;
    private Date createdTime;
    private Long lastModifierId;
    private Date lastModifiedTime;

}
