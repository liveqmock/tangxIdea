package com.topaiebiz.transport.expressage.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExpressageDto {

    /**快递单号。*/
    private String nu;

    /**快递编码？*/
    private String com;

    /**快递公司名称*/
    private String comName;

    /**快递单当前签收状态，包括0在途中、1已揽收、2疑难、3已签收、4退签、5同城派送中、6退回等状态*/
    private String state;

    /**是否签收。0为未签收。*/
    private String ischeck;

    /**数据信息。以键值对拼接。时间：信息，时间：信息。*/
    private List<String> dataList;

}
