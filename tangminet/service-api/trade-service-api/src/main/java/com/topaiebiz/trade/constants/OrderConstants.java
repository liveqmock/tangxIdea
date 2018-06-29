package com.topaiebiz.trade.constants;

/***
 * @author yfeng
 * @date 2018-01-05 15:39
 */
public interface OrderConstants {

    class PayStatus {
        /**
         * 待支付
         */
        public static final Integer UNPAY = 0;
        /**
         * 已经支付
         */
        public static final Integer SUCCESS = 1;
        /**
         * 已取消
         */
        public static final Integer CANCEL = 2;
    }

    /**
     * 发票状态
     */
    class OrderInvoiceStatus {
        /**
         * 需要
         */
        public static final Integer NO_NEED = 0;
        /**
         * 不需要
         */
        public static final Integer NEED = 1;
    }

    class DeliyerType {
        /**
         * 快递配送
         */
        public static final Short EXPRESS = 1;
    }

    class HaitaoFlag {
        /**
         * 非海淘
         */
        public static final Integer NO = 0;
        /**
         * 是海淘
         */
        public static final Integer YES = 1;

    }

    class OrderRefundStatus {
        /**
         * 售后申请次数
         */
        public static final Integer REFUND_APPLY_COUNT_MAX = 3;

        /**
         * 超过售后申请次数，显示申诉按钮
         */
        public static final Integer REFUND_SHOW_APPEAL_COUNT = 2;

        /**
         * 1：显示申诉按钮， 0：不现实申诉按钮
         */
        public static final Integer REFUND_SHOW_APPEAL_YES = 1;
        public static final Integer REFUND_SHOW_APPEAL_NO = 0;


        /**
         * 整单退 or 非整单退
         */
        public static final Integer ALL_REFUND_YES = 1;
        public static final Integer ALL_REFUND_NO = 0;
        /**
         * 订单无售后
         */
        public static final Integer NO_REFUND = 0;

        /**
         * 订单售后中
         */
        public static final Integer REFUNDING = 1;

        /**
         * 已退款
         */
        public static final Integer REFUND = 2;

        /**
         * 退款关闭
         */
        public static final Integer CLOSE = 3;

        /**
         * 平台拒绝
         */
        public static final Integer PLATFORM_REFUSED = 4;
    }

    class OrderLockStatus {
        /**
         * 未锁定
         */
        public static final Integer NO_LOCK = 0;
    }

    class InvoiceStatus {
        /**
         * 未开发票
         */
        public static final Integer UNDEAL = 0;
    }

    class DetailCommentFlag {
        public static final Integer NO = 0;
        public static final Integer YES = 0;
    }
}