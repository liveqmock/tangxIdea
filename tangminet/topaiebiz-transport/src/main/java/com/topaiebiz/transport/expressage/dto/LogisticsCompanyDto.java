package com.topaiebiz.transport.expressage.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Description TODO
 * <p>
 * Author Aaron.Xue
 * <p>
 * Date 2017年10月24日 下午5:11:57
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class LogisticsCompanyDto extends PagePO {

    private Long id;

    /**
     * 快递公司编码。
     */
    private String company;

    /**
     * 快递公司名称。
     */
    @NotNull(message = "{validation.expressage.comName}")
    private String comName;

    /**
     * 状态 1 禁用 0 启用。
     */
    private Integer status;

    /**
     * 公司图片
     */
    private String image;

    /**
     * 说明。
     */
    private String description;

    /**
     * 最后修改时间。取值为系统的当前时间。
     */
    private Date lastModifiedTime;

}
