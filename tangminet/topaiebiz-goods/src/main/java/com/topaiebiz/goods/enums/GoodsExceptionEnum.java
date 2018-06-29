package com.topaiebiz.goods.enums;

import com.nebulapaas.web.exception.ExceptionInfo;

/**
 * @description: 商品的统一异常类型定义
 * @author: Jeff Chen
 * @date: created in 下午3:20 2018/5/18
 */
public enum GoodsExceptionEnum implements ExceptionInfo {

    CATEGORY_LINKED_DATA("2010000", "类目下挂有属性/品牌/商品"),
    PARENT_LEVEL_NOT_EXIST("2010001", "上级分类错误"),
    LEVEL_OUT_LIMIT("2010002", "最多五级分类"),
    CATEGORY_ID_NEED("2010003", "需要类目ID"),
    CATEGORY_NOT_EXIST("2010004", "类目不存在"),
    CATEGORY_HAS_SON("2010005", "该类目有下级类目"),
    NEAD_FRONT_OR_BACK("2010006", "前后类目ID至少有一个"),
    NEAD_SIBLING_CATEGORY("2010007","需要交换位置的类目ID"),
    ONLY_SIBLING_MOVE("2010008", "只能兄弟类目可以上下移动"),
    FRONT_BACK_NOT_SIBLING("2010009", "前后节点不在同一级"),
    FRONT_BACK_NO_PARENT("2010010", "前后节点不在该父节点下"),
    OUT_OF_MAX_LEVEL("2010011", "超过了最大分类层级"),
    CATEGORY_MOVE_ERROR("2010012", "类目移动失败"),
    CATEGORY_SYNC_ERROR("2010013", "更新类目状态失败"),
    ATTR_UPDATE_ERROR("2010014", "更新属性失败"),
    ONLY_SYNC_LEAF_DATA("2010015", "只能同步叶子关联的数据"),
    ONLY_RELATE_TO_LEAF("2010016", "只能添加到叶子类目"),
    LEVEL_FIVE_ONLY_LEAF("2010017", "五级只能是叶子"),
    PARENT_NOT_LEAF("2010018", "上级类目不能为叶子"),
    SIBILING_CANNOT_SAME("2010019", "同级类目名称不能相同"),
    ATTR_NAME_CANT_SAME("2010020", "同类目下属性名不能相同"),
    FRONT_BACK_ERROR("2010020","前后位置顺序错误"),
    ATTR_GROUP_NAME_NOT_REPETITION("2010100", "属性分组名称不能重复！"),
    ATTR_GROUP_ID("2010101", "参数ID有误！"),
    BRAND_ID_NOT_REPETITION("2010102", "品牌不能重复绑定！"),
    BRAND_ID_ERROR("2010103", "类目ID或者品牌ID有误！"),
    ATTR_GROUP_ID_NOT_EXIST("2010104", "属性分组ID不存在！"),
    CATEGORY_LEAF("2010105", "该类目不为叶子类目！"),
    BRAND_ID_NOT_NULL("2010106", "品牌ID不能为空！"),
    CATEGORY_IS_NULL("2010107", "该品牌下暂无关联类目！"),
    BRAND_HAVE_ITEM("2010108", "该品牌下挂有商品！"),
    BRAND_HAVE_CATEGORY("2010109", "该品牌有关联类目！"),
    ;

    private String code;
    private String message;

    GoodsExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultMessage() {
        return message;
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
