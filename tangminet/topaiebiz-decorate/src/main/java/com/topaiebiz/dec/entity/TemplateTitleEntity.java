package com.topaiebiz.dec.entity;


import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;



/**
 * <p>
 * 商品标题表
 * </p>
 *
 * @author hzj
 * @since 2018-01-08
 */
@Data
@TableName("t_dec_template_title")
public class TemplateTitleEntity extends BaseBizEntity<Long> {

    /**
     * 全局唯一标识符。
     */
	private Long id;
    /**
     * 模块ID。
     */
	private Long moduleId;
    /**
     * 标题名称
     */
    private String titleName;
    /**
     * 父标题ID
     */
    private Long parentId;
    /**
     * 显示顺序。
     */
	private Long sortNo;
    /**
     * 备注。
     */
	private String memo;

    private Integer level;

    public boolean isTopLevel(){
        return 1 == level;
    }

}
