package com.topaiebiz.promotion.worldcup.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;


@Data
@TableName("t_pro_world_cup_member_investment_info")
public class WorldCupMemberInvestmentEntity extends BaseBizEntity<Long>{
    /**
     * id
     * 全局唯一标识符
     */
    private Long id;

    /**
     * memberId
     * 主队id
     */
    private Long memberId;

    /**
     * investmentPoints
     * 投注积分
     */
    private Integer investmentPoints;

    /**
     * investmentType
     * 投注类型 0-夺冠竞猜 1-比赛竞猜
     */
    private Integer investmentType;

    /**
     * investmentType
     * 比赛id-只有投注比赛才有
     */
    private Long matchId;

    /**
     * investmentProject
     * 投注项 if(0)?球队id   if(1)?3-主胜 1-平 0-负
     */
    private String investmentProject;

    /**
     * investmentResult
     * 投注结果 0-未开彩 1-赢2-输
     */
    private Integer investmentResult;

    /**
     * creatorId
     * 创建人编号。取值为创建人的全局唯一主键标识符。
     */
    private Long creatorId;

    /**
     * createdTime
     * 创建时间。取值为系统的当前时间。
     */
    private Date createdTime;

    /**
     * lastModifierId
     * 最后修改人编号。取值为最后修改人的全局唯一主键标识符。
     */
    private Long lastModifierId;

    /**
     * lastModifiedTime
     * 最后修改时间。取值为系统的当前时间。
     */
    private Date lastModifiedTime;



}