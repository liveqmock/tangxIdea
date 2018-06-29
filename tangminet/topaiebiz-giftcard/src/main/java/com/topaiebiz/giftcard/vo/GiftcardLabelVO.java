package com.topaiebiz.giftcard.vo;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description: 礼卡标签
 * @author: Jeff Chen
 * @date: created in 下午1:48 2018/1/15
 */
@Data
public class GiftcardLabelVO implements Serializable{

    private static final long serialVersionUID = -2641859641390142463L;

    /**
     * 标签id
     */
    private Long labelId;

    /**
     * label_name
     * 标签名称
     */
    @NotEmpty(message = "标签名称不能为空")
    private String labelName;

    /**
     * sample_pic
     * 样本图片
     */
    @NotEmpty(message = "样图不能为空")
    private String samplePic;

    /**
     * remark
     * 备注
     */

    private String remark;

}
