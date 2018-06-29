package com.topaiebiz.member.point.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

/**
 * Created by ward on 2018-01-17.
 */
@TableName("t_mem_point_crm_log")
@Data
public class PointCrmLogEntity extends BaseBizEntity<Long> {


    /**
     * ID
     **/
    private Long id;

    /**
     * 会员ID
     **/
    private Long memberId;

    /**
     * 用户名
     **/
    private String userName;

    /**
     * 会员手机号
     **/
    private String telephone;

    /**
     * 妈妈购积分
     **/
    private Integer mmgPoint;

    private Integer trueMmgPoint;

    /**
     * 积分转换比例
     **/
    private String pointRate;

    /**
     * 贝因美crm积分
     **/
    private Integer crmPoint;

    /**
     * 备注
     */
    private String meno;

    /**
     * 请求提交内容
     */
    private String requestParam;

    /**
     * 响应返回结果
     */
    private String responResult;

    /**
     * 执行消耗时间
     */
    private String executeTime;


    /**
     * 执行状态
     */
    private Integer executeStatus;


    @TableField(exist = false)
    private Long lastModifierId;
    @TableField(exist = false)
    private Date lastModifiedTime;


}
