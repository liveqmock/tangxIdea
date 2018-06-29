package com.topaiebiz.decorate.constant;

import java.util.Random;

public class Constant {

    public static final String PAGE_URL_PREFIX = "";

    public static final Byte NOT_ONLINE = 0;//页面不在线

    public static final Byte ON_LINE = 1;//页面在线

    public static final Byte DO_NOT_DELETE = 0;//页面不可删除

    public static final Byte CAN_BE_DELETED = 1;//页面可删除

    public static final int QRCODE_WIDTH = 200; //二维码宽度

    public static final int QRCODE_HEIGHT = 200;//二维码高度

    public static final String PNG_FORMAT = "png";//图片png格式

    public static final int ACTIVITY_PAGE_EXPIRE_TIME = 3600;//3600秒即一小时，活动页面缓存失效时间

    public static final int RANDOM_MAX_EXPIRE_TIME = 300;//5分钟

    /**
     * 组件类型
     **/

    public static final String HEADER = "header";//页面顶部组件

    public static final String BOTTOM = "bottom";//页面底部组件

    public static final String ITEM = "item";//商品组件

    public static final String IMAGE_LINK = "image_link";//图片链接组件

    public static final String ICON_NAVIGATION = "icon_navigation";//图标导航组件

    public static final String STYLE_AUXILIARY = "style_auxiliary";//样式辅助组件

    public static final String HEADLINE = "headline";//文字链接组件即头条组件

    public static final String TEXT = "text";//文本组件

    public static final String CONTAINER = "container";//

    public static final String COUPON = "coupon";//优惠券组件

    public static final String SECOND_KILL = "second_kill";//秒杀组件

    /**
     * 组件规则
     **/
    public static final int WORDS_MAX_LENGTH = 4;

    public static final int CONTENT_MAX_LENGTH = 20;

    /**
     * 缓存前缀
     **/
    public static final String PAGE_KEY_PREFIX = "decorate_page_";//页面缓存前缀

    public static final String COMPONENT_KEY_PREFIX = "decorate_component_";//组价内容缓存前缀

    /**
     * excel标题名称
     **/
    public static final String SORT_NO = "序号";

    public static final String ITEM_ID = "商品id";

    public static final String ITEM_NAME = "商品名称";

    /**分布式锁preKey**/

    public static final String DECORATE_PAGE_LOCK = "decorate_page_lock_";//页面缓存lock

    public static final String DECORATE_COMPONENT_LOCK = "decorate_component_lock_";//组件内容缓存lock

}
