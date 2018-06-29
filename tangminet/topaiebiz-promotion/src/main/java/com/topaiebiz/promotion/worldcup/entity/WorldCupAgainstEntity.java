package com.topaiebiz.promotion.worldcup.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_pro_world_cup_against_info")
public class WorldCupAgainstEntity extends BaseBizEntity<Long> {
    /**
     * id
     * 全局唯一标识符
     */
    private Long id;

    /**
     * homeId
     * 主队id
     */
    private Long homeId;

    /**
     * visitingId
     * 客队id
     */
    private Long visitingId;

    /**
     * competitionTime
     * 比赛时间
     */
    private Date competitionTime;

    /**
     * resultGame
     * 比赛结果-{"主队进球数":"x","客队进球数":"Y"}
     */
    private String resultGame;

    /**
     * odds
     * 赔率-{"主":"x","和":"y","客","z"}
     */
    private String odds;

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