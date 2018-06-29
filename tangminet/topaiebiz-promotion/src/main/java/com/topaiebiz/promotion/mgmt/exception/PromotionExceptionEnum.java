package com.topaiebiz.promotion.mgmt.exception;

import com.nebulapaas.web.exception.ExceptionInfo;

/**
 * Description 营销活动枚举
 * <p>
 * <p>
 * Author Joe
 * <p>
 * Date 2017年9月22日 下午10:33:46
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public enum PromotionExceptionEnum implements ExceptionInfo {

    /**
     * 活动ID不能为空
     */
    PROMOTION_ID_NOT_NULL("7000001", "Promotion ID not null!"),

    /**
     * 发放数量不得为空
     */
    QUANTITY_ISSUED_SHALL_NOT_NULL("7000002", "quantity issued shall not null!"),

    /**
     * 补贴比例不能为空
     */
    SUBSIDY_PROPORTION_NOT_NULL("7000003", "subsidy proportion not null!"),

    /**
     * 商品SKU不可为空
     */
    PRODUCT_SKU_NOT_NULL("7000004", "product sku not null!"),

    /**
     * 原有库存不得为空
     */
    ORIGINAL_STOCK_MUST_NOT_NULL("7000005", "original stock must not null!"),

    /**
     * 活动价格不得为空
     */
    PROMOTION_PRICE_CANNOT_BE_EMPTY("7000006", "promotion price cannot be empty!"),

    /**
     * 活动数量不得为空
     */
    PROMOTION_QUANTITY_CANNOT_BE_EMPTY("7000007", "promotion quantity cannot be empty!"),

    /**
     * ID限购不得为空
     */
    ID_RESTRICTION_CANNOT_BE_EMPTY("7000008", "ID restriction cannot be empty!"),

    /**
     * 活动类型不能为空
     */
    PROMOTION_TYPE_NOT_NULL("7000009", "promotion code not null!"),

    /**
     * 活动商品不得为空
     */
    PROMOTIONGOODS_NOT_NULL("70000010", "promotion goods not null!"),

    /**
     * 所属店铺不得为空
     */
    PROMOTIONGOODS_STORE_OWNED_NOT_NULL("70000011", "promotion goods store owned not null!"),

    /**
     * 商品优惠值不可为空
     */
    GOODS_DISCOUNT_NOT_NULL("70000012", "goods discount not null!"),

    /**
     * 商品原价不可为空
     */
    GOODS_ORIGINAL_PRICE_NOT_NULL("70000013", "goods original price not null!"),

    /**
     * 条件值不可为空
     */
    CONDVALUE_NOT_NULL("70000014", "condvalue not null!"),

    /**
     * 请调整开始时间
     */
    PLEASE_ADJUST_THE_START_TIME("70000015", "please adjust the start time!"),

    /**
     * 活动数量大于原库存
     */
    ACTIVITY_NUMBER_GREATER_THAN_STOCK("70000016", "activity number greater than stock!"),

    /**
     * 活动不存在
     */
    ACTIVITY_DOES_NOT_EXIST("70000017", "activity does not exist!"),

    /**
     * 商品itemID不可为空
     */
    PRODUCT_ITEM_ID_NOT_NULL("70000018", "product itemId not null!"),

    /**
     * 优惠券不可领取
     */
    COUPONS_ARE_NOT_TO_BE_OBTAINED("70000019", "coupons are not to be obtained!"),

    /**
     * 会员ID不可为空
     */
    MEMBER_ID_NOT_NULL("70000020", "member id not null!"),

    /**
     * 发布时间过期
     */
    TIME_EXPIRED("70000021", "time expired!"),

    /**
     * 优惠券已领取
     */
    THE_COUPONS_HAVE_BEEN_TAKEN("70000022", "the coupons have been taken!"),

    /**
     * 报名截止时间应在活动开始时间之前
     */
    PLEASE_ADJUST_THE_ENROL_END_TIME("70000023", "please adjust the enrol end time!"),

    /**
     * 商品审核状态不可为空
     */
    PRODUCT_AUDIT_STATE_CANNOT_BE_EMPTY("70000024", "product audit state cannot be empty!"),

    /**
     * 商品数量不得低于最少报名商品数
     */
    GOODS_NUMBER_SHALL_NOT_BE_LESS_THAN_MINIMUM("70000025", "goods number shall not be less than minimum!"),

    /**
     * 商品数量不得大于最大报名商品数
     */
    GOODS_NUMBER_SHALL_NOT_BE_GREATER_THAN_MAXIMUM("70000026", "goods number shall not be greater than maximum!"),

    /**
     * 优惠条件不得大于条件值
     */
    PREFERENTIAL_VALUE_SHALL_NOT_EXCEED_CONDITION_VALUE("70000027", "preferential value shall not exceed condition value!"),

    /**
     * 限制数额不得为空
     */
    FILL_IN_THE_LIMIT_CORRECTLY("70000028", "fill in the limit correctly!"),

    /**
     * 宝箱记录ID不能为空
     */
    BOX_RECORD_ID_NOT_NULL("70000029", "box record id not null!"),

    /**
     * 领取人手机号不能为空
     */
    BOX_RECEIVER_MOBILE_NOT_NULL("70000030", "box receiver mobile not null!"),

    /**
     * 开宝箱活动不存在或无效
     */
    OPEN_BOX_ACTIVITY_INVALID("70000031", "open box activity not exist or invalid!"),

    /**
     * 宝箱奖品领取无效
     */
    BOX_AWARD_RECEIVED_INVALID("70000032", "box award received invalid!"),

    /**
     * 该会员无可开启宝箱
     */
    NO_AVAILABLE_BOX("70000033", "no available box for this member!"),

    /**
     * 奖池配置不可用
     */
    AWARD_POOL_CONFIG_INVALID("70000034", "award pool config invalid!"),

    /**
     * 奖品概率配置不可用
     */
    OPEN_BOX_RATE_CONFIG_INVALID("70000035", "open box rate config invalid!"),

    /**
     * 礼卡秒杀活动不存在或无效
     */
    SEC_KILL_CARD_ACTIVITY_INVALID("70000036", "sec kill card activity not exist or invalid!"),

    /**
     * 礼卡秒杀活动还未开始
     */
    SEC_KILL_CARD_NOT_STARTED_YET("70000037", "sec kill card activity not started yet!"),

    /**
     * 此卡不在本场正在秒杀的活动中
     */
    CARD_CONFIG_NO_EXIST_IN_SEC_KILL_FIELD("70000038", "card not exist in sec kill field!"),

    /**
     * 该礼卡在此次秒杀活动中库存不足
     */
    SEC_KILL_CARD_LACK_OF_STOCK("70000039", "sec kill card lack of stock!"),

    /**
     * 会员在此次秒杀活动中已达到购买上线
     */
    SEC_KILL_CARD_LIMITED_FOR_MEMBER("70000040", "sec kill card limited for this member!"),

    /**
     * 礼卡秒杀活动未配置场次或配置有误
     */
    SEC_KILL_CARD_NOT_CONFIGURED_YET("70000041", "sec kill card not configured yet!"),

    /**
     * 请调整时间为整点
     */
    PLEASE_ADJUST_THE_TIME_TO_THE_HOUR("70000042", "please adjust the time to the hour!"),

    /**
     * 显示标题不可为空
     */
    SHOW_TITLE_NOT_NULL("70000043", "show title not null!"),

    /**
     * 显示类型不可为空
     */
    SHOW_TYPE_NOT_NULL("70000044", "show type not null!"),

    /**
     * 请调整标题长度
     */
    PLEASE_ADJUST_TITLE_LENGTH("70000045", "please Adjust title length!"),

    /**
     * 参数类型错误
     */
    PARAMETER_TYPE_ERROR("70000046", "parameter type error!"),

    /**
     * s
     * 分享优惠券总份数不能为空
     */
    SHARE_COUPONS_WITHOUT_A_NUMBER_OF_COUPONS("70000047", "share coupons without a number of coupons!"),

    /**
     * 优惠券当日领取数量已达上限
     */
    THE_COUPONS_REACHED_THE_UPPER_LIMIT_ON_THE_DAY("70000048", "the number of coupons has reached the upper limit on the day！"),

    /**
     * 该时间段已存在发布的优惠券分享活动
     */
    COUPON_SHARINCG_ACTIVITIES_HAVE_BEEN_RELEASED_IN_THIS_TIME_PERIOD("70000049", "coupon sharincg activities have been released in this time period！"),

    /**
     * 暂时没有该类型活动，请先配置
     */
    THERE_IS_NO_SUCH_TYPE_OF_ACTIVITY_FOR_THE_TIME_BEING("70000050", "There is no such type of activity for the time being！"),

    /**
     * 活动配置问题，请联系管理员
     */
    ACTIVITY_CONFIGURATION_PROBLEM_PLEASE_CONTACT_THE_ADMINISTRATOR("70000051", "Activity configuration problem, please contact the administrator!"),

    /**
     * 优惠券礼包被领取完毕
     */
    THE_COUPON_PACKAGE_WAS_FINISHED("70000052", "The coupon package was finished"),

    /**
     * 该优惠券只限制老用户领取
     */
    THE_COUPON_ONLY_LIMITS_THE_OLD_USER_TO_RECEIVE_IT("70000053", "The coupon only limits the old user to receive it!"),


    /**
     * 该优惠券只限制新用户领取
     */
    THE_COUPON_ONLY_LIMITS_NEW_USERS("70000054", "The coupon only limits new users!"),

    /**
     * 支付单号不存在
     */
    PAYMENT_OF_SINGLE_NUMBER_DOES_NOT_EXIST("70000055", "payment of single number does not exist!"),

    /**
     * 优惠券ID不能为空
     */
    COUPON_ID_NOT_NULL("70000056", "CouponId ID not null!"),

    /**
     * 活动未配置可用优惠券
     */
    ACTIVITY_NOT_CONFIG_COUPON_YET("70000057", "Activity not config coupon yet!"),

    /**
     * 优惠券限制新用户可领
     */
    COUPON_LIMITED_FOR_NEW_USER("70000058", "Coupon limited for new user!"),

    /**
     * 优惠券限制老用户可领
     */
    COUPON_LIMITED_FOR_OLD_USER("70000059", "Coupon limited for old user!"),

    /**
     * 活动未配置可用优惠券
     */
    ACTIVITY_NOT_AVAIL_CONFIG_COUPON_YET("70000060", "Activity not avail config coupon yet!"),

    /**
     * 该优惠券活动尚未开始
     */
    COUPON_ACTIVITY_NOT_STARTED_YET("70000061", "coupon activity not started yet!"),

    /**
     * 该优惠券活动尚已开始
     */
    COUPON_ACTIVITY_OVER("70000062", "coupon activity over!"),

    /**
     * 并发重复操作
     */
    OPERATION_REPAIR("70000063", "operation repair!"),
    SINGLE_PROMOTION_GOODS_REPEAT_ERROR("70000064", "Single promotion goods repeated !");
    /**
     * 异常代码。
     */
    private String code;

    /**
     * 异常对应的默认提示信息。
     */
    private String defaultMessage;

    /**
     * 异常对应的原始提示信息。
     */
    private String originalMessage;

    /**
     * 当前请求的URL。
     */
    private String requestUrl;

    /**
     * 默认的转向（重定向）的URL，默认为空。
     */
    private String defaultRedirectUrl = "";

    /**
     * 异常对应的响应数据。
     */
    private Object data;

    /**
     * Description: 根据异常的代码、默认提示信息构建一个异常信息对象。
     * <p>
     * Author: Joe
     *
     * @param code           异常的代码。
     * @param defaultMessage 异常的默认提示信息。
     */
    PromotionExceptionEnum(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public String getOriginalMessage() {
        return originalMessage;
    }

    @Override
    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    @Override
    public String getRequestUrl() {
        return requestUrl;
    }

    @Override
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    @Override
    public String getDefaultRedirectUrl() {
        return defaultRedirectUrl;
    }

    @Override
    public void setDefaultRedirectUrl(String defaultRedirectUrl) {
        this.defaultRedirectUrl = defaultRedirectUrl;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
    }

}
