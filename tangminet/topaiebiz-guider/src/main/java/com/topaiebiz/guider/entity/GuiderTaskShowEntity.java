package com.topaiebiz.guider.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * Created by admin on 2018/6/5.
 */
@Data
@TableName("t_guider_task_show")
public class GuiderTaskShowEntity  extends BaseBizEntity<Long> {

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * banner图片
     */
    private String bannerImage;

    /**
     *列表图片
     */
    private String pageImage;

    /**
     * 背景图片
     */
    private String bgImage;

    /**
     * 页面标题
     */
    private String pageTitle;

    /**
     * 头部图片
     */
    private String headImage;

    /**
     * 内容图片
     */
    private String contentImage;

    /**
     *活动规则
     */
    private String taskRule;

    /**
     *分享页面标题
     */
    private String sharePageTitle;

    /**
     *分享背景图片
     */
    private  String shareBgImage;

    /**
     *二维码下方方案
     */
    private String dimensionCode;

    /**
     * 微信主标题
     */
    private String weixinMainTitle;

    /**
     * 微信副标题
     */
    private String weixinSubTitle;

    /**
     *微信图片
     */
    private String weixinImage;

}
