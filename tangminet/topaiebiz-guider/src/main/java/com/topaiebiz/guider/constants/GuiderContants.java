package com.topaiebiz.guider.constants;

/**
 * Created by admin on 2018/6/6.
 */
public interface GuiderContants {


    /**
     * 审核状态
     */
    class TaskStatus {
        /**
         * 未开始
         */
        public static Integer NOT_BEGUN = 1;
        /**
         * 进行中
         */
        public static Integer UNDERWAY = 2;
        /**
         * 结束中
         */
        public static Integer FINISH = 0;
    }

    /**
     * 阶梯奖励类型
     */
    class LevelType {

        /**
         * 订单
         */
        public static Integer ORDER = 2;

        /**
         * 拉新
         */
        public static Integer PULL_NEWUSER = 1;
    }

    class PrizeObjType {
        //订单比例
        public static Integer ORDER_RATIO = 1;
        //优惠券
        public static Integer DISCOUNT_COUPON = 2;
        //美礼卡
        public static Integer GIFTCARD = 3;
        //现金
        public static Integer CASH = 4;
        //积分
        public static Integer INTEGRAL = 5;
        //实物奖
        public static Integer REAL_PRIZE = 6;
    }

    class GuiderPayStatus {
        public static Integer CLOSE = 2;
        //优惠券
        public static Integer COMPLETE = 1;
        //美礼卡
        public static Integer WAITING = 0;
    }

    class IsOnLine {
        //1.上线
        public static Integer ON_LINE = 1;
        //0.下线
        public static Integer DOWN_LINE = 0;

    }
}
