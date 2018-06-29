package com.nebulapaas.base.contants;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-01-05 15:43
 */
public interface Constants {

    /**
     * 短信模块枚举
     */
    class SMS {

        /**
         * 一天
         */
        public static final Integer ONT_DAY_SECONDS = 60 * 60 * 24;

        /**
         * 手机号 + 验证码类型，  按类型，每种类型最多三条验证码， 验证用户输入的验证码为三次一个节点， 到达一个节点， 就只能重新发送一个验证码
         * 最大校验次数(已区分类型) --> 每种类型 最多只能发送三次短信， 验证次数最多9次
         */
        public static final Long GROUP_BY_TYPE_MAX_VERIFY_NUM = 9L;

        /**
         * 验证失败超过三次，则若干分钟后才允许重新发送的一个验证码短信
         */
        public static final Integer SMS_VERIFY_FAIL_SEND_AGAIN_NUM = 3;

        /**
         * 每种短信类型最多只允许发送三次
         */
        public static final Integer SEND_CAPTCHA_MAX_COUNT_GROUP_BY_TYPE = 3;

        /**
         * 连续验证失败超过三次， 发送短信的时间冷却10分钟（毫秒单位）
         */
        public static final Long VERIFY_FAIL_TO_MUCH_SEND_AGAIN_COOLING_TIME = 1000 * 60 * 10L;

        /**
         * 发送验证码接口CODE
         */
        public static final String SEND_SMS_SUCCESS_CODE = "OK";

        /**
         * 短信模板 中 替换值
         * 日期 / 验证码
         */
        public static final String SMS_TEMPLATE_DATE_TIME = "{date_time}";
        public static final String SMS_TEMPLATE_CAPTCHA = "{captcha}";


    }

    class OrderLockFlag {
        /**
         * 已锁定
         */
        public static final Integer LOCK_YES = 1;

        /**
         * 未锁定
         */
        public static final Integer LOCK_NO = 0;
    }

    /**
     * 删除Flag常量
     */
    class DeletedFlag {
        /**
         * 已经删除
         */
        public static final Byte DELETED_YES = 1;
        /**
         * 没有删除
         */
        public static final Byte DELETED_NO = 0;
    }

    class FrozenFlag {
        /**
         * 已经删除
         */
        public static final Byte FROZEN_YES = 1;
        /**
         * 没有删除
         */
        public static final Byte FROZEN_NO = 0;
    }

    class Order {
        /**
         * 定时器执行者ID
         */
        public static final Long TIME_TASK_USER_ID = 1000000000000000L;
        public static final Long OPEN_API_USER_ID = 1000000000000001L;

        /**
         * 延长收货 /  延长时间
         */
        public static final Integer EXTEND_SHIP_YES = 1;
        public static final Integer EXTEND_SHIP_NO = 0;
        public static final Integer EXTEND_DAYS = 3;

        /**
         * 下单未支付自动取消时间 // 发货自动收货时间 // 收货自动完成时间
         */
        public static final Long UNPAY_AUDIT_CANCEL_SECONDS = 60 * 60 * 2L;
        public static final Integer UNPAY_AUDIT_CANCEL_HOURS = 2;
        public static final Long SHIP_AUDIT_RECEIVE_SECONDS = 60 * 60 * 24 * 7L;
        public static final Integer SHIP_AUDIT_RECEIVE_DAYS = 7;
        public static final Long RECEIVE_AUTO_COMPLETE_SECONDS = 60 * 60 * 24 * 7L;
        public static final Integer RECEIVE_AUTO_COMPLETE_DAYS = 7;

        /**
         * 订单类型
         */
        public final static String ORDER_TYPE_GOOD = "good";
        public final static String ORDER_TYPE_CARD = "card";

        /**
         * 支付类型
         */
        public final static String ALIPAY = "alipay";
        //        public final static String WECHATPAY = "wxpay";
        public final static String WECHATPAY = "wx_jsapi";
        public final static String PREDEPOSIT = "predeposit";


        /**
         * 支付描述
         */
        public final static String CARD_BODY = "妈妈购--美礼卡购买订单支付";
        public final static String GOOD_BODY = "妈妈购--商品购买订单支付";

        /**
         * 积分--现金 转汇率
         */
        public final static BigDecimal INTEGRAL_RATE = new BigDecimal(100);

        /**
         * 退款结果
         */
        public final static Integer REFUND_SUCCESS = 1;
        public final static Integer REFUND_FAIL = 0;

        /**
         * 评价状态
         */
        public final static Integer COMMENT_YES = 1;
        public final static Integer COMMENT_NO = 0;
    }

    /**
     * 售后常量
     */
    class Refund {
        /**
         * 售后不能被拒绝1， 能拒绝0
         */
        public static final Integer REFUND_CAN_REFUSE_NO = 1;
        public static final Integer REFUND_CAN_REFUSE_YES = 0;

        /**
         * 未发货 整单退 申请仅退款 ，自动审核时间
         */
        public static final Long ALL_REFUND_AUTO_AUDIT_SECONDS = 60 * 60 * 24 * 2L;
        public static final Integer ALL_REFUND_AUTO_AUDIT_DAYS = 2;

        /**
         * 发货后 整单退， 自动退 售后申请自动审核通过时间
         */
        public static final Long REFUND_AUTO_AUDIT_SECONDS = 60 * 60 * 24 * 5L;
        public static final Integer REFUND_AUTO_AUDIT_DAYS = 5;
        /**
         * 订单收货之后被允许售后的最大时间
         */
        public static final Long ORDER_ALLOW_REFUND_MAX_SECONDS = 60 * 60 * 24 * 7L;
        /**
         * 用户寄回商品时间 // 商品寄回自动收货时间
         */
        public static final Long WAIT_GOODS_RETURN_MAX_SECONDS = 60 * 60 * 24 * 7L;
        public static final Integer WAIT_GOODS_RETURN_MAX_DAYS = 7;
        public static final Long ACCEPT_REFUND_GOODS_MAX_SECONDS = 60 * 60 * 24 * 7L;

        /**
         * 商品退货，用户寄回，商家签收的时间
         */
        public static final int ACCEPT_REFUND_GOODS_MAX_DAYS = 7;


        /**
         * 售后被商家拒绝，未处理售后订单自动关闭时间
         */
        public static final Integer REJECTED_AND_DO_NOT_DEAL_DAYS = 7;
        public static final Long REJECTED_AND_DO_NOT_DEAL_SECONDS = 60 * 60 * 24 * 7L;

        /**
         * 仅退款
         */
        public static final Integer REFUND = 0;

        /**
         * 退货退款
         */
        public static final Integer RETURNS = 1;

        /**
         * 平台已介入
         */
        public static final Integer PLATFORM_HAS_BEEN_INVOLVED = 1;

        /**
         * 平台未介入
         */
        public static final Integer PLATFORM_IS_NOT_INVOLVED = 0;

        /**
         * 审核通过
         */
        public static final Integer AUDIT_SUCCESS = 1;

        /**
         * 审核失败
         */
        public static final Integer AUDIT_FAIL = 0;

        /**
         * 售后各个路径的退款结果
         */
        public static final String REFUND_YES = "Y";
        public static final String REFUND_NO = "N";

        /**
         * 售后各个路径的退款都已退完/部分退完
         */
        public static final String REFUND_ALL = "refund all";
        public static final String REFUND_PART = "refund part";
        public static final String NO_REFUND = "no refund";

        /**
         * 定时器最多退款次数
         */
        public static final Integer MAX_TIME_TASK_REFUND_COUNT = 3;
    }

    /**
     * 分布式锁前缀
     */
    class LockOperatons {

        /**
         * 下订分布式锁前缀
         */
        public static String ORDER_SUBMIT_LOCK = "ORDER_SUBMIT_LOCK_";

        /**
         * 订单支付/取消锁前缀
         */
        public static String TRADE_ORDER_PAY_ = "TRADE_ORDER_PAY_";

        /**
         * 订单其他操作前缀
         */
        public static String ORDER_INFO_UPDATE_LOCK = "ORDER_INFO_UPDATE_LOCK";

        /**
         * 订单操作锁
         */
        public static String ORDER_OPERATION_LOCK = "ORDER_OPERATION_LOCK_";

        /**
         * 针对订单商品快照的退货退款操作
         */
        public static String REFUND_GOODS_SNAPSHOT_LOCK = "REFUND_SUBMIT";

        /**
         * 退货退款操作
         */
        public static String REFUND_ORDER_LOCK = "REFUND_ORDER_LOCK";

        /**
         * 第三方回调
         */
        public static String PAY_NOTICE_LOCK = "PAY_NOTICE_LOCK";

        /**
         * 店铺订单导出锁
         */
        public static String STORE_ORDER_EXPORT_LOCK = "STORE_ORDER_EXPORT_LOCK";
    }

    /**
     * 导出 redis key
     */
    class ExportKey {

        /**
         * 商家导出key
         */
        public final static String STORE_ORDER_EXPORT_KEY = "store_order_export_";

        /**
         * key存活时间
         */
        public final static Long keyTime = 60 * 60 * 5L;
    }

    class OpenApiPush {
        /**
         * 推送订单到第三方 订单KEY
         */
        public static String STORE_ORDER_PUSH_THIRD_KEY = "store_order_push_third_key_";

        /**
         * 需要报关
         */
        public static Integer NEED_EXPORT_CUSTOMS = 1;

    }

    /**
     * 过滤拒绝的URL 系统配置表的config 的key
     */
    class BlockUrlKey {

        public final static String DOWN_GRADE_CONFIG_CODE = "down_grade";

    }


    /**
     * 上架状态
     */
    class Status {

        public final static Integer GROUNDING = 2;

    }
}