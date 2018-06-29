package com.topaiebiz.promotion.constants;

/**
 * Created by Administrator on 2018/1/9.
 */
public interface PromotionConstants {


    /**
     * 显示类型
     */
    class SubType {

        /**
         * 普通优惠券
         */
        public static final Integer ORDINARY_COUPONS = 0;

        /**
         * 优惠券分享
         */
        public static final Integer COUPON_SHARE = 1;

        /**
         * 优惠券抽奖
         */
        public static final Integer COUPON_LOTTERY = 2;


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

    class PromotionGoodsFlag {
        /**
         *
         */
        public static final Integer NO_LIMIT = 1;

        public static final Integer TARGET_GOODS = 0;
    }

    /**
     * 审核/报名状态
     */
    class AuditState {
        /**
         * 未审核
         */
        public static final Integer NO_AUDIT = 0;
        /**
         * 审核通过
         */
        public static final Integer APPROVED_AUDIT = 1;
        /**
         * 审核不通过
         */
        public static final Integer FAILURE_AUDIT = 2;
    }

    class ApplyState {
        /**
         * 不需要报名
         */
        public static final Integer NO_APPLY = 0;
        /**
         * 可以报名
         */
        public static final Integer APPLY_ALLOW = 1;
        /**
         * 报名结束
         */
        public static final Integer APPLY_OVER = 2;
    }


    /**
     * 优惠券使用状态
     */
    class UsageState {
        /**
         * 未使用
         */
        public static final Integer USAGE_NO = 0;
        /**
         * 使用
         */
        public static final Integer USAGE_YES = 1;
    }

    /**
     * 优惠类型
     */
    class DiscountType {
        /**
         * 打折
         */
        public static final Integer DISCOUNT = 1;
        /**
         * 减价
         */
        public static final Integer THE_SALE = 2;
        /**
         * 包邮
         */
        public static final Integer FREE_SHIPPING = 3;
    }

    /**
     * 分隔符
     */
    class SeparatorChar {
        /**
         * 逗号分隔符
         */
        public static final String SEPARATOR_COMMA = ",";
        /**
         * 下划线分隔符
         */
        public static final String SEPARATOR_UNDERLINE = "_";
        /**
         * 逗号分隔符
         */
        public static final String SEPARATOR_COLON = ":";
    }

    /**
     * 领取状态
     */
    class ReceiveState {
        /**
         * 未领取
         */
        public static final Integer RECEIVE_NO = 0;
        /**
         * 已领取
         */
        public static final Integer RECEIVE_YES = 1;
        /**
         * 已领完
         */
        public static final Integer RECEIVE_OVER = 2;
    }

    /**
     * 缓存前缀
     */
    class CacheKey {
        /**
         * 礼卡秒杀活动场次前缀
         */
        public static final String SEC_KILL_CARD_ACTIVITY_PREFIX = "sec_kill:card:activity:";
        /**
         * 礼卡秒杀活动整场购买礼卡次数
         */
        public static final String SEC_KILL_CARD_COUNT_PREFIX = "sec_kill:card:count:";
        /**
         * 礼卡秒杀活动
         */
        public static final String SEC_KILL_CARD = "sec_kill:card";
        /**
         * 礼卡秒杀活动当前场次
         */
        public static final String SEC_KILL_CARD_ACTIVITY = "sec_kill:card:activity";
        /**
         * 礼卡秒杀活动所有场次
         */
        public static final String SEC_KILL_CARD_ACTIVITIES = "sec_kill:card:activities";
        /**
         * 礼卡秒杀活动该场的礼卡配置
         */
        public static final String SEC_KILL_CARD_ACTIVITY_ITEMS = "sec_kill:card:activity:items";
        /**
         * 礼卡秒杀活动所有场次的所有礼卡配置
         */
        public static final String SEC_KILL_CARD_ACTIVITIES_ITEMS = "sec_kill:card:activities:items";
        /**
         * 商品楼层前缀
         */
        public static final String FLOOR_PREFIX = "floor:";
        /**
         * 商品楼层前缀
         */
        public static final String FLOOR_GOODS_PREFIX = "floor_goods:";
        /**
         * 礼卡楼层前缀
         */
        public static final String FLOOR_CARDS_PREFIX = "floor_cards:";
        /**
         * 开宝箱固定节点
         */
        public static final String FIXED_NODE_PREFIX = "fixed:node:";
        /**
         * 开宝箱时间节点
         */
        public static final String TIME_NODE_PREFIX = "time:node:";
        /**
         * 开宝箱活动
         */
        public static final String OPEN_BOX = "open:box";
        /**
         * 开宝箱活动宝箱配置
         */
        public static final String OPEN_BOX_ACTIVITY = "open:box:activity";
        /**
         * 优惠券活动前缀
         */
        public static final String COUPON_ACTIVITY_PREFIX = "coupon:activity:";
        /**
         * 优惠券配置
         */
        public static final String COUPON_CONFIGS_PREFIX = "coupon:configs:";
        /**
         * 活动优惠券列表前缀
         */
        public static final String ACTIVITY_COUPONS_PREFIX = "activity:coupons:";
        /**
         * 优惠券日领取数量
         */
        public static final String COUPON_DAY_BIND_NUM_PREFIX = "coupon:day:bind:num:";
        /**
         * 优惠券总领取数量
         */
        public static final String COUPON_BIND_NUM_PREFIX = "coupon:bind:num:";
        /**
         * 正在进行的商品秒杀活动
         */
        public static final String SEC_KILL_GOODS_ONGOING = "sec_kill:goods:ongoing";
        /**
         * 所有有效的日常商品秒杀活动
         */
        public static final String SEC_KILL_GOODS_DAILY = "sec_kill:goods:daily";
        /**
         * 正在进行的商品秒杀活动
         */
        public static final String PLATE_GOODS_PREFIX = "plate:goods:";
        /**
         * 所有有效的日常商品秒杀活动
         */
        public static final String SEC_KILL_GOODS_LIST_PREFIX = "sec_kill:goods:list:";
        /**
         * 秒杀活动下的商品详情列表
         */
        public static final String SEC_KILL_ITEMS_PREFIX = "sec_kill:items:";
        /**
         * 会员锁并发锁前缀
         */
        public static final String MEMBER_LOCK_PREFIX = "member_lock_";
    }

    /**
     * 奖品配置类型
     */
    class AwardType {
        /**
         * 未中奖
         */
        public static final Integer NO_AWARD = 0;
        /**
         * 优惠券
         */
        public static final Integer COUPON_AWARD = 1;
        /**
         * 美礼卡
         */
        public static final Integer CARD_AWARD = 2;
        /**
         * 实物奖
         */
        public static final Integer RES_AWARD = 3;
    }

    /**
     * 可用状态
     */
    class AvailState {
        /**
         * 可用
         */
        public static final Integer AVAILABLE = 0;
        /**
         * 不可用
         */
        public static final Integer UNAVAILABLE = 1;
    }

    class SecKillState {
        /**
         * 即将开始
         */
        public static final Integer STARTED_YET = 0;
        /**
         * 进行中
         */
        public static final Integer STARTING = 1;
        /**
         * 已抢光
         */
        public static final Integer NO_STORAGE = 2;
        /**
         * 已结束
         */
        public static final Integer FINISHED = 3;
    }

    /**
     * 产生宝箱节点类型
     */
    class NodeType {
        /**
         * 时间节点
         */
        public static final Integer TIME_NODE = 0;
        /**
         * 登录节点
         */
        public static final Integer LOGIN_NODE = 1;
        /**
         * 分享节点
         */
        public static final Integer SHARE_NODE = 2;
        /**
         * 支付节点
         */
        public static final Integer PAY_NODE = 3;
    }

    /**
     * 是否全店可用
     */
    class IsGoodsArea {

        /**
         * 全店可用
         */
        public static final Integer ALL = 1;

        /**
         * 平台优惠券/店铺优惠券 - 指定商品可用
         */
        public static final Integer NOT_ALL = 0;
        /**
         * 店铺优惠券 - 排除商品可用
         */
        public static final Integer EXCLUDING_PART_OF_THE_GOODS = 2;

        /**
         * 平台优惠券 - 所有店铺通用
         **/
        public static final Integer ALL_STORE = 3;
        /**
         * 平台优惠券 - 部分店铺通用
         **/
        public static final Integer INCLUDE_STORE = 4;

        /****
         * 平台优惠券 - 排除某些店铺通用
         */
        public static final Integer EXCLUDE_STORE = 5;
    }

    /**
     * 首页秒杀活动场数
     */
    class SeckillPromotionNum {

        public static final Integer SECKILL_NUM = 5;

    }

    /**
     * 初始化数据类型
     */
    class InitDataRecord {
        /**
         * 活动商品记录
         */
        public static final Integer PROMOTION_GOODS = 1;
        /**
         * 楼层商品记录
         */
        public static final Integer FLOOR_GOODS = 2;
        /**
         * 活动商品记录
         */
        public static final Integer BOX_ACTIVITY_ITEMS = 3;
        /**
         * 楼层礼卡记录
         */
        public static final Integer FLOOR_CARD = 4;
    }

    /**
     * 首页秒杀商品数量
     */
    class SecKillGoodsNum {

        /**
         * 首页秒杀商品数量
         */
        public static final Integer SECKILL_GOODS_NUM = 8;

    }

    /**
     * 显示类型
     */
    class ShowType {

        public static final Integer SHOW_TITLE = 0;

        public static final Integer SHOW_DATE = 1;

    }

    /**
     * 显示标题限制长度
     */
    class ShowTitleLength {

        /**
         * 限制字数
         */
        public static final Integer SHOW_TITLE_LENGTH = 6;

    }

    /**
     * 日志caozu
     */
    class OperationType {

        /**
         * 新增
         */
        public static final Integer TYPE_NEWLY_ADDED = 0;

        /**
         * 发布
         */
        public static final Integer TYPE_RELEASE = 1;

        /**
         * 编辑
         */
        public static final Integer TYPE_EDIT = 2;

        /**
         * 停止
         */
        public static final Integer TYPE_STOP = 3;

        /**
         * 结束
         */
        public static final Integer TYPE_END = 4;

    }

    /**
     * 显示标题限制长度
     */
    class IsRslease {

        /**
         * 新增
         */
        public static final Integer TYPE_NEWLY_ADDED = 0;

        /**
         * 新增
         */
        public static final Integer TYPE_RELEASE = 1;


    }

    /**
     * 显示标题限制长度
     */
    class IsRsleaseDate {

        /**
         * 新增
         */
        public static final Byte TYPE_NO = 0;

        /**
         * 新增
         */
        public static final Byte TYPE_YES = 1;


    }


    class BannerActiveType {

        /**
         * 优惠券分享活动
         */
        public static final Integer TYPE_COUPON_SHARE_ACTIVE = 1;

    }

    /**
     * 返回活动结果 0-正常领取 1-当日领取达到上限  2-活动期间领取数量达到上限 3-限制新用户领取 4-限制老用户领取
     */
    class ShareCouponResult {

        /**
         * 正常领取
         */
        public static final Integer TYPE_NORMAL = 0;

        /**
         * 当日限制上限
         */
        public static final Integer TYPE_DAY_LIMIT = 1;

        /**
         * 活动期间领取上限
         */
        public static final Integer TYPE_ACTIVE_LIMIT = 2;

        /**
         * 只限制新用户领取
         */
        public static final Integer TYPE_NEW_LIMIT = 3;

        /**
         * 只限制老用户领取
         */
        public static final Integer TYPE_OLD_LIMIT = 4;

        /**
         * 红包领取人数上限
         */
        public static final Integer TYPE_BAG_LIMIT = 5;

        /**
         * 礼包领取完毕
         */
        public static final Integer TYPE_GIFT_BAG_LIMIT = 6;

    }

    //用户类型
    class UserType {
        /**
         * 老用户
         */
        public static final Integer OLD_USER = 0;
        /**
         * 新用户
         */
        public static final Integer NEW_USER = 1;

    }

    /**
     * 可变异常码
     */
    class VariableExceptionCode {
        /**
         * 读取文件异常
         */
        public static final String READ_FILE = "7001";

        /**
         * 发布活动异常
         */
        public static final String RELEASE_PROMOTION = "7002";
    }

    /**
     * 世界杯投注类型
     **/
    class InvestmentType {
        /**
         * 竞猜冠军
         */
        public static final Integer CHAMPION = 0;
        /**
         * 竞猜输赢
         */
        public static final Integer WIN_AND_LOSE = 1;

    }

    class TeamState {

        public static final Integer NORMAL = 0;

        public static final Integer ELIMINATE = 1;

        public static final Integer CHAMPION = 2;


    }

    class WinoOrLose {

        public static final Integer WIN = 3;

        public static final Integer DRAW = 1;

        public static final Integer LOSE = 0;


    }

    class InvestmentResult {

        public static final Integer WIN = 1;

        public static final Integer LOSE = 0;


    }

    class PointType {

        public static final String CONSUME = "consume";

        public static final String ADD = "add";
    }
}
