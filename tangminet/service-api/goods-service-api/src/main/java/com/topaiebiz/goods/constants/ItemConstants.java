package com.topaiebiz.goods.constants;

/**
 * Created by hecaifeng on 2018/4/12.
 */
public interface ItemConstants {

    /**
     * 商品是否为冻结状态 0 为正常 1为
     */
    class FrozenFlag {
        public static final Integer YES_FROZEN = 1;
        public static final Integer NO_FROZEN = 0;
    }

    /**
     * 商品是否为删除状态 0 为正常 1为
     */
    class DeletedFlag {
        public static final Integer DELETEDFLAG_YES = 1;
        public static final Integer DELETEDFLAG_NO = 0;
    }

}
