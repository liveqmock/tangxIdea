package com.topaiebiz.goods.constants;

import lombok.Getter;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-01-05 11:29
 */
public interface GoodsConstants {

    class PicType {
        /**
         * 主图
         */
        public static final Integer MAIN_PIC = 1;
        /**
         * 详情图
         */
        public static final Integer DETAIL_PIC = 0;
    }

    class SkuStatus {
        /**
         * 销售中
         */
        public static final Integer SALING = 2;
    }

    /**
     * 评价星级。
     */
    class GoodsCommentLevel {
        public static final Integer ONE_LEVEL = 1;
        public static final Integer TWO_LEVEL = 2;
        public static final Integer THREE_LEVEL = 3;
        public static final Integer FOUR_LEVEL = 4;
        public static final Integer FIVE_LEVEL = 5;
    }

    /**
     * 好评，中评，差评，全部，有图
     */
    @Getter
    enum GoodsCommentType {
        ZERO_TYPE(0, "全部"),
        ONE_TYPE(1, "好评"),
        TWO_TYPE(2, "中评"),
        THREE_TYPE(3, "差评"),
        FOUR_TYPE(4, "有图");

        private Integer code;
        private String value;

        private GoodsCommentType(Integer code, String value) {
            this.code = code;
            this.value = value;

        }
    }

    /**
     * 好评度星级。
     */
    class GoodsPraiseRatio {
        public static final BigDecimal TWO_RATIO = new BigDecimal(20);
        public static final BigDecimal THREE_RATIO = new BigDecimal(40);
        public static final BigDecimal FOUR_RATIO = new BigDecimal(60);
        public static final BigDecimal FIVE_RATIO = new BigDecimal(80);

    }

    /**
     * 评价是否有图  0为无图，1为有图
     */
    class IsImage {
        public static final Integer YES_IMAGE = 1;
        public static final Integer NO_IMAGE = 0;

    }

    /**
     * 商家类目是否通过。
     */
    @Getter
    enum BackendMerchantCategoryStatus {
        TO_AUDIT(0, "待审核"),
        AUDOT_APPROVAL(1, "审核通过"),
        AUDIT_FAILURE(2, "审核不通过"),
        NEW_ENTRY(3, "新录入"),
        NORMAL(4, "正常（因商家有其他信息未通过，暂不能使用！）");

        private Integer code;
        private String value;

        private BackendMerchantCategoryStatus(Integer code, String value) {
            this.code = code;
            this.value = value;

        }
    }

    /**
     * 类目等级
     */
    class BackendCateLevel {
        public static final Integer ONE_LEVEL = 1;
        public static final Integer TWO_LEVEL = 2;
        public static final Integer THREE_LEVEL = 3;
    }

    /**
     * 是否查询该店铺商品
     */
    class BelongStore {
        //属性这些店铺
        public static final Integer YES_BELONGSTORE = 0;
        //除这些店铺以外的商品
        public static final Integer NO_BELONGSTORE = 1;
    }



}