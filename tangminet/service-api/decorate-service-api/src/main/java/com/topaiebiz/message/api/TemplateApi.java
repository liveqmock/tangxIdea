package com.topaiebiz.message.api;



public interface TemplateApi {

    void addMerchantTemplate(Long storeId,String storeName);

    void removeItem(Long[] id);

    void editItem(Long titleId);
}
