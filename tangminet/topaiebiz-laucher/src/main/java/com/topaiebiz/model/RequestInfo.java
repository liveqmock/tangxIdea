package com.topaiebiz.model;

import lombok.Data;

/***
 * @author yfeng
 * @date 2018-01-31 9:23
 */
@Data
public class RequestInfo {
    private String reqMethod;
    private String uriAndQuery;
    private String reqIp;
    private String reqAgent;
    private String reqRef;
    private boolean uploadRequest;
}