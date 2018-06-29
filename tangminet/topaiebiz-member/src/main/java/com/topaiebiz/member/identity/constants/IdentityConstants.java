package com.topaiebiz.member.identity.constants;

/**
 * Created by admin on 2018/5/31.
 */
public interface IdentityConstants {

    /**
     * 审核状态
     */
    class Status {
        /**
         * 待审核
         */
        public static Integer CHECK = 1;
        /**
         * 审核不通过
         */
        public static Integer DISQUALIFICATION = 2;
        /**
         * 审核通过
         */
        public static Integer PASS = 3;
    }
}
