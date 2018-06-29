package com.topaiebiz.promotion.worldcup.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_pro_world_cup_team_info")
public class WorldCupTeamEntity extends BaseBizEntity<Long>{
    /**
     * id
     * 全局唯一标识符
     */
    private Long id;

    /**
     * name
     * 球员名称
     */
    private String name;

    /**
     * country
     * 国别
     */
    private String country;

    /**
     * playerPhoto
     * 球员照片
     */
    private String playerPhoto;

    /**
     * nationalFlag
     * 国旗照片
     */
    private String nationalFlag;

    /**
     * flagFeatures
     * 国旗特征
     */
    private String flagFeatures;

    /**
     * group
     * 组别
     */
    private String group;

    /**
     * group
     * 组别
     */
    private Integer state;

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