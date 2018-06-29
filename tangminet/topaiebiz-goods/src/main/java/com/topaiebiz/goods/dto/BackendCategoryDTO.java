package com.topaiebiz.goods.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: 后台类目DTO
 * @author: Jeff Chen
 * @date: created in 下午1:09 2018/5/19
 */
@Data
public class BackendCategoryDTO implements Serializable {

    /**
     * 类目id
     */
    private Long categoryId;
    /**
     * 类目名称。
     */
    @NotEmpty(message = "类目名称不能为空")
    private String name;
    /**
     * 类目等级。
     */
    private Integer level;
    /**
     * 类目排序号
     */
    private Integer sortNo;
    /**
     * 父类目。
     */
    @NotNull(message = "上级类目ID未指定")
    private Long parentId;

    /**
     * 移动时的前后类目id
     */
    private Long frontId;

    private Long backId;

    /**
     * 同级移动时交换位置的类目ID
     */
    private Long siblingId;
    /**
     * 是否叶子目录：0不是 1是
     */
    @Range(min = 0, max = 1)
    private Integer isLeaf;
    private Long creatorId;
    private Date createdTime;
    private Long lastModifierId;
    private Date lastModifiedTime;
}
