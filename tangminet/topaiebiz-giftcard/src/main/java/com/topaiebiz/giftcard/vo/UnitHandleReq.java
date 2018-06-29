package com.topaiebiz.giftcard.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: 卡片单元操作请求信息
 * @author: Jeff Chen
 * @date: created in 下午2:34 2018/1/17
 */
@Data
public class UnitHandleReq implements Serializable{

    /**
     * 卡单元id
     */
    @NotNull(message = "unitId不能为空")
    private Long unitId;
    /**
     * 状态
     */
    private Integer cardStatus;
    /**
     * 续期天数
     */
    private Integer renewalDays;
    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private Date modifiedTime;

    /**
     * 备注
     */
    private String note;
}
