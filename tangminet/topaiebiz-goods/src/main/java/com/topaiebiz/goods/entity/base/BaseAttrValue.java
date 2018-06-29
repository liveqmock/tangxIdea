package com.topaiebiz.goods.entity.base;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;

import java.io.Serializable;

/**
 * <p>
 * 属性值正式表--基类
 * </p>
 *
 * @author MMG123
 * @since 2018-05-18
 */
public class BaseAttrValue extends BaseBizEntity<Long> {

    /**
     * 属性名id
     */
    @TableField("attrId")
    private Long attrId;
    /**
     * 属性值
     */
    private String value;
    /**
     * 值来源：1 平台定义 2 商家定义
     */
    @TableField("source")
    private Integer source;
    /**
     * 产品Id
     */
    @TableField("itemId")
    private Long itemId;
    public Long getAttrId() {
        return attrId;
    }

    public void setAttrId(Long attrId) {
        this.attrId = attrId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

}
