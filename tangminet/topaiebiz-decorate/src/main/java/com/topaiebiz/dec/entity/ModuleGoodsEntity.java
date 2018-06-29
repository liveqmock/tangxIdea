package com.topaiebiz.dec.entity;

import java.util.Date;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 模块商品详情表
 * </p>
 *
 * @author hzj
 * @since 2018-01-08
 */
@Data
@TableName("t_dec_module_goods")
public class ModuleGoodsEntity extends BaseBizEntity<Long> {

    /**
     * 全局唯一标识符。
     */
	private Long id;
    /**
     * 模块ID。
     */
	private Long moduleId;
    /**
     * 商品ID。
     */
	private Long goodsId;
    /**
     * 显示顺序。
     */
	private Long sortNo;
    /**
     * 备注。
     */
	private String memo;



}
