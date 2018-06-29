package com.topaiebiz.giftcard.vo;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

/**
 * @description: 转赠信息
 * @author: Jeff Chen
 * @date: created in 下午5:16 2018/1/19
 */
@Data
public class GivenInfoVO implements Serializable{

    @NotEmpty(message = "缺少cardNo参数")
    private String cardNo;
    @NotEmpty(message = "手机号不能为空")
    private String doneePhone;
    @NotEmpty(message = "随便说几句呗")
    private String note;
}
