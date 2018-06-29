package com.topaiebiz.timetask.order;

import com.topaiebiz.basic.api.ConfigApi;
import com.topaiebiz.timetask.quartzaop.aop.QuartzContextOperation;
import com.topaiebiz.trade.api.OrderTaskServiceApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Description 订单相关定时任务
 * <p>
 * Author hxpeng
 * <p>
 * Date 2017/11/22 15:56
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Component
public class OrderTimeTask {

    @Autowired
    private OrderTaskServiceApi orderTaskServiceApi;

    @Autowired
    private ConfigApi configApi;

    @QuartzContextOperation
    @Scheduled(cron = "0 0/15 * * * ?")
    public void cancelOrders() {
        log.info("----------cancelOrders task start;");
        orderTaskServiceApi.cancelUnPayOrder();
    }

    @QuartzContextOperation
    @Scheduled(cron = "0 0/15 * * * ?")
    public void receiveOrders() {
        log.info("----------receiveOrders task start;");
        orderTaskServiceApi.receivingOrder();
    }

    @QuartzContextOperation
    @Scheduled(cron = "0 0/15 * * * ?")
    public void completeOrders() {
        log.info("----------completeOrders task start;");
        orderTaskServiceApi.completeOrders();
    }


    @Value(value = "${daily.order.data.url}")
    private String downloadDailyDataUrl;

    private final static String DINDIN_MSG_POST_URL_CONFIG_CODE = "dindin.msg.post.url";


    /**
     * Description: 每天九点十分，把昨日订单数据下载地址推送钉钉群里
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/24
     *
     * @param:
     **/

//    @QuartzContextOperation
//    @Scheduled(cron = "0 10 9 * * ?")
//    public void downloadDailyOrderData() {
//        log.info(">>>>>>>>>>start push daily order data's download url to dindin!");
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.add(Calendar.DATE, -1);
//
//        HttpClient httpclient = HttpClients.createDefault();
//
//        String postUrl = configApi.getConfig(DINDIN_MSG_POST_URL_CONFIG_CODE);
//        if (StringUtils.isBlank(postUrl)) {
//            return;
//        }
//
//        HttpPost httppost = new HttpPost(postUrl);
//        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
//        String textMsg = "{" +
//                "   'msgtype': 'link', " +
//                "   'link': {" +
//                "   'text':'点击此链接下载昨天的订单明细数据', " +
//                "   'title': '" + simpleDateFormat.format(calendar.getTime()) + "订单数据报表.csv', " +
//                "   'picUrl': '', " +
//                "   'messageUrl': '" + downloadDailyDataUrl + "'" +
//                "   }" +
//                "}";
//        StringEntity se = new StringEntity(textMsg, "utf-8");
//        httppost.setEntity(se);
//        HttpResponse response;
//        try {
//            response = httpclient.execute(httppost);
//            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                log.info(EntityUtils.toString(response.getEntity(), "utf-8"));
//            }
//        } catch (IOException e) {
//            log.error(">>>>>>>>>>push fail! error:{}", e);
//        }
//    }
}