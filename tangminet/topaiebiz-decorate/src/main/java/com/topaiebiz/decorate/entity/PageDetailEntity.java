package com.topaiebiz.decorate.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

/**
 * 页面管理entity
 *
 * @author huzhenjia
 * @since 2018/03/26
 */
@Data
@TableName("t_dec_page_detail")
public class PageDetailEntity extends BaseBizEntity<Long> {

    //页面地址
    private String pageUrl;

    //页面名称
    private String pageName;

    //页面地址后缀
    private String suffixUrl;

    //页面地址后缀CRC32
    private Long cRC32;

    //二维码
    private String qrCode;

    //定时上线时间
    private Date startTime;

    //定时下线时间
    private Date endTime;

    //备注
    private String memo;

    //状态 0不在线,1在线
    private Byte status;

    //类型 0不可删除，1可删除
    private Byte type;
}
