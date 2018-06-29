package com.topaiebiz.goods.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by hecaifeng on 2018/5/21.
 */
@Data
public class AttrGroupEditDTO extends PagePO implements Serializable {

    private Long id;

    /**
     * 类目id
     */
    @NotNull(message = "{validation.attrGroup.categoryId}")
    private Long categoryId;
    /**
     * 属性分组名称。
     */
    @NotNull(message = "{validation.attrGroup.name}")
    private String name;
    /**
     * 类目描述。
     */
    private String description;
    /**
     * 排序号
     */
    private Integer sortNo;

    /**
     * 同步状态：0未同步 1已同步
     */
    private Integer syncStatus;

    /**
     * 删除状态：0正常 1删除
     */
    private Integer deletedFlag;

}
