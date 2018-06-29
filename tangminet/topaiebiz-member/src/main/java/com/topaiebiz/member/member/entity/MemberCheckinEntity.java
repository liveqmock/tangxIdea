package com.topaiebiz.member.member.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

/**
 * Created by ward on 2018-01-17.
 */
@TableName("t_mem_member_checkin")
@Data
public class MemberCheckinEntity extends BaseBizEntity<Long> {


    /**
     * ID
     **/
    private Long id;

    /**
     * 会员ID
     **/
    private Long memberId;


    private String checkinDate;

    /**
     * 得到的积分
     **/
    private Integer point;

    /**
     * 备注
     */
    private String meno;


    @TableField(exist = false)
    private Long lastModifierId;
    @TableField(exist = false)
    private Date lastModifiedTime;


}
