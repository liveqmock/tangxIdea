package com.topaiebiz.goods.sku.exception;

import com.nebulapaas.web.exception.ExceptionInfo;

/**
 * Description 商品评价异常枚举。
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年9月23日 下午8:00:09
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

public enum GoodsSkuExceptionEnum implements ExceptionInfo {

    GOODSITEM_ID_NOT_NULL("2000022", "Goods item ID cannot be empty!"),

    GOODSITEM_ID_NOT_EXIST("2000023", "Goods item ID cannot be exist!"),

    GOODSITEM_ITEMCODE_NOT_REPETITION("2000024", "Goods sku code cannot be repeated!"),

    GOODSITEM_STOCKNUMBER_DEFICIENCY("2000034", "Goods sku is not in stock!"),

    MEMBER_ID_NOT_EXIST("2000023", "Member ID cannot be exist!"),

    GOODS_ARE_OUT_OF_DATE("2000037", "Goods are out of date!"),

    GOODSPICTURE_NAME_NOT_NULL("2000038", "Please upload at least one picture!"),

    GOODSSKU_PRICE_NOT_NULL("2000039", "The price of the goods should not be empty!"),

    GOODSSKU_STOCKNUMBER_NOT_NULL("2000040", "Goods inventory cannot be zero！"),

    GOODSKU_ID_NOT_EXIST("2000041", "This commodity does not exist！"),

    GOOD_FROZEN("2000048", "The goods are frozen, please defrost and then hit the shelves"),

    EMPTY_CATEGORY("2000049", "The item is hung in the blank class, please edit the category and then hit the shelves！"),

    GOODS_BRAND("2000050", "The brand of the product has been deleted, please edit it and then put it on the shelves！"),

    SKU_IMAGE("2000051", "Please upload the sku image"),

    GOODSITEM_STOCKBACK_FAIL("2000053", "Goods sku back storage fail!"),

    SALES_OF_SYNCHRONIZATION("2000054", "Simultaneous sales failure！"),

    NONEXISTENCE("2000055", "The category of the item already does not exist, the product can only perform the delete operation！"),

    ORDER_NUMBER_ERRO("2000056", "Order number error！"),

    ARTICLE_NUMBER_DOES_NOT_EXIST("2000057", "Article number does not exist!"),

    NUMBER_NOT_FIND_GOODSSKU("2000058", "This item number does not find the corresponding product！"),

    GOODS_NOT_SHOP("2000059", "The goods do not belong to this shop!"),

    GOODS_SECKILL_EXIST("2000070", "所选商品中含有被锁价商品，暂不能下架，请排除之后再进行下架！"),

    GOODS_PICTURE("2000061", "请上传至少一张商品图片!"),

    GOODS_SKU("2000062", "请添加商品SKU，至少一条!"),

    STOREID("2000063", "店铺ID不能为空!"),

    ITEM_INSERT_FIAL("2000064", "商品添加失败!"),

    GOODS_PICTURE_MAIN("2000065", "请上传至少一张商品图片为主图!"),

    GOODS_BROKERAGERATIO_IS_NULL("2000066", "所选商品佣金比例为空,暂不能上架请联系平台设置!"),

    CATEGORY_BROKERAGERATIO_IS_NULL("2000067", "该商品对应类目佣金比例为空，暂不能上架请放置仓库，请联系平台设置!"),

    GOODS_SKU_IS_NULL("2000068", "该商品没有SKU！"),

    GOODS_ALREADY_EXISTS("2000069", "该商品已经加入足迹！"),

    GOODS_SKU_ID_NOT_EXISTS("2000070", "该商品SKUID不存在！"),;

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
     * Description 根据异常的代码、默认提示信息构建一个异常信息对象。
     * <p>
     * Author Hedda
     *
     * @param code           异常的代码。
     * @param defaultMessage 异常的默认提示信息。
     */
    GoodsSkuExceptionEnum(String code, String defaultMessage) {
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
