package com.topaiebiz.system.dto;

import lombok.Data;

@Data
public class CurrentUserDto {

    private Long id;

    private String userLoginId;

    /** 系统用户名称。可用作登录的账户名（即使用用户名登录）。 */
    private String username;

    /** 系统用户的账户类型。 */
    private Integer type;

    /** 系统用户的移动电话。可用作登录的账户名（即使用手机号登录） 。 */
    private String mobilePhone;

    /** 系统用户的所属商家。 */
    private Long merchantId;

    /** 系统用户的所属店铺。 */
    private Long storeId;

}
