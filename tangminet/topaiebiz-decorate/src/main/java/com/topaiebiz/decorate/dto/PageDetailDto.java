package com.topaiebiz.decorate.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.util.Date;


/**
 * 页面详情Vo
 *
 * @author huzhenjia
 * @since 2018/3/26
 */
@Data
public class PageDetailDto extends PagePO {

    private Long id;

    private String pageName;//页面名称

    private String suffixUrl;//页面地址后缀

    private Date startTime;//上线时间

    private Date endTime;//下线时间

    private String qrCode;//二维码

    private String memo;//备注

    private Long type;//系统页面或活动页面

    private Long status;//在线或不在线

}
