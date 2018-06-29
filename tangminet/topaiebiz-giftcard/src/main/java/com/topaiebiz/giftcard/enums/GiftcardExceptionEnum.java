package com.topaiebiz.giftcard.enums;

import com.nebulapaas.web.exception.ExceptionInfo;

/**
 * @description: 礼卡异常枚举
 * @author: Jeff Chen
 * @date: created in 下午5:06 2018/1/16
 */
public enum GiftcardExceptionEnum implements ExceptionInfo{

    INVALID_REQ("5000099", "非法操作"),
    UNIT_NOT_EXIST("5000100", "礼卡单元不存在"),
    ISSUE_NOT_EXIST("5000101", "礼卡批次信息不存在"),
    NO_CHANGE("5000102", "状态没有变化"),
    NOT_AUDIT("500103", "只有审核通过才能上架或生产"),
    NOT_IMPORT("5000104", "只有已生产的实体卡才能入库"),
    NOT_EXPORT("5000105", "只有已入库的实体卡才能出库"),
    ONLY_SOLID_AUDIT("5000106", "只有审核通过的实体卡才能生成"),
    SOLID_OR_ELECT("5000107", "请指定实体卡或电子卡"),
    INVALID_RENEWAL("5000108", "续期的天数必须大于0"),
    SELECT_NOT_EXIST("5000109", "该条精选不存在"),
    NOT_TO_MOVE("5000110", "无法移动"),
    NOT_TO_EDIT("5000111","只有电子卡可以编辑"),
    PRIORIRTY_OVERFLOW("5000112", "优先级在1-10直接"),
    NEED_TO_PRODUCE("5000113","礼卡还未生产"),
    ONLY_PRODUCE_SOLID("5000114", "只能生产通过审核的实体卡"),
    ONLY_PRODUCE_ELEC("5000115", "只能购买上架的电子卡"),
    NOT_OWER("5000201", "该卡不是您购买的"),
    GIVEN_ERROR("5000201", "转赠失败，稍后再试哈"),
    REPEAT_GIVEN("5000202", "该卡只能转赠给一个朋友"),
    GIVEN_NOT_EXIST("5000203", "转赠无效"),
    GIVEN_RECEIVED("5000204", "该礼卡已被领取!"),
    NOT_TO_RENEWAL("5000205","只能续期已过期的卡片"),
    NOT_TO_UNFREEZE("5000206", "只有冻结的礼卡才能解冻"),
    RENEWAL_LIMIT("5000207", "至少续期一天"),
    LIMIT_TO_BUY("5000301", "卡余量不足或超过购买上限"),
    ORDER_NOT_EXIST("5000302", "订单不存在"),
    ORDER_NOT_YOURS("5000303", "该订单不是你的有效订单"),
    UNPAID_CAN_CANCEL("5000304", "只有未支付的才能取消"),
    PAY_ERROR_PRICE("5000305", "支付了错误的金额"),
    REPEAT_PAY("5000306", "宝宝，你重复支付了"),
    BIND_ERROR("5000307", "卡号或密码不正确"),
    NOT_GET_GIVEN("5000308", "转赠不可领取"),
    ONLY_ACTIVED_SOLID("5000309", "只有激活的实体卡可以绑定"),
    ONLY_UNUSED_ELEC("5000310", "只有未使用的电子卡可以绑定"),
    ORDER_EXCEPTION("5000311","订单异常"),
    PHONE_ERROR("5000312", "手机号不正确"),
    CAPTCHA_ERROR("5000313", "验证码错误"),
    ORDER_AGAIN("5000314", "重复下单"),
    ORDER_REQ("5000315", "请购买指定卡和具体数量"),
    INVALID_PLACE("5000316", "非法下单"),
    ONLY_SELECT_COMMON("5000317","只能精选有效的普通卡"),
    HAD_BOUND("5000318", "该卡已被绑定"),
    INVALID_NO_SPAN("5000319", "卡号区间已经被占用了"),
    CARD_NO_SPAN_ERROR("5000320", "发行数量和卡号区间不符合"),
    CARD_PRODUCE_ERROR("5000321", "卡生产失败"),
    FOBBIDEN_ACTIVITY("5000322","无法参加活动"),
    CARD_QTY_OUT("5000322", "卡余量不足，稍后再试"),
    USED_NOT_GET("5000323", "该卡已使用不能被领取"),
    USED_NOT_BOUND("5000324", "该卡已使用不可再次绑定"),
    INACTIVED_NOT_BOUND("5000325", "未激活不可绑定"),
    BATCH_SOLD_OUT("5000326", "该卡片未上架不能购买"),
    ONLY_BUY_ELEC("5000327", "目前只能购买电子卡"),
    SEC_KILL_MISS("5000328","抢购失败，请稍后再试"),
    BIND_CARD_MISS("5000329", "绑定失败，请稍后再试"),
    LEAST_BATCH_PARAM("5000330", "亲，最起码填个标题再保存吧"),
    ONLY_IN_2000("5000331", "一次最多导出2000"),
    NO_ORDER_DATA("5000332","订单数据不存在"),
    CARD_HAD_GOTTEN("5000333", "该卡已被领取"),
    CARD_CANNOT_GIVEN("5000334", "该卡未设置转赠"),
    ACTIVITY_STORAGE_ERROR("5000400", "活动库存扣减失败"),
    MEMBER_LOCK("5000401", "账号异常，无法购买"),
    REFUND_PARAM_ERROR("5000402", "退款参数错误"),
    REFUND_ERROR("5000403", "退款失败"),
    CARD_PAID_PARAM_ERROR("5000404", "礼卡支付参数错误"),
    CARD_PAID_ERROR("5000405", "礼卡支付失败"),
    CARD_BALANCE_EXCEPTION("5000406", "卡金额异常，请联系客服"),
    BIND_TRY_LIMIT("5000407","密码错误次数过多，稍后再试"),
    ;

    private String code;
    private String message;

    GiftcardExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getDefaultMessage() {
        return this.message;
    }

    @Override
    public String getOriginalMessage() {
        return null;
    }

    @Override
    public void setOriginalMessage(String s) {

    }

    @Override
    public String getRequestUrl() {
        return null;
    }

    @Override
    public void setRequestUrl(String s) {

    }

    @Override
    public String getDefaultRedirectUrl() {
        return null;
    }

    @Override
    public void setDefaultRedirectUrl(String s) {

    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public void setData(Object o) {

    }
}
