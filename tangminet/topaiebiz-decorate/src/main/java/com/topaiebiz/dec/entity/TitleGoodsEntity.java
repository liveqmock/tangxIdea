package com.topaiebiz.dec.entity;

import java.util.Date;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 标题商品详情表
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
@Data
@TableName("t_dec_title_goods")
public class TitleGoodsEntity extends BaseBizEntity<Long> {



    /**
     * 全局唯一标识符。
     */
	private Long id;
    /**
     * 标题ID。
     */
	private Long titleId;
    /**
     * 父标题ID。
     */
    private Long parentId;
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
