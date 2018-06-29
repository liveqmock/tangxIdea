package com.topaiebiz.dec.entity;


import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;



/**
 * <p>
 * 模块信息详情表

 * </p>
 *
 * @author hzj
 * @since 2018-01-08
 */
@Data
@TableName("t_dec_module_info")
public class ModuleInfoEntity extends BaseBizEntity<Long> {

    /**
     * 全局唯一标识符。
     */
	private Long id;
    /**
     * 模块ID。
     */
	private Long moduleId;
    /**
     * 图片地址。
     */
	private String image;
    /**
     * 链接类型。1商品  2类目  3品牌  4 自定义url
     */
	private Integer jumpType;
    /**
     * 跳转结果。
     */
	private String jumpValue;
    /**
     * 排序号。
     */
	private Long sortNo;
    /**
     * 备注。
     */
	private String memo;

}
