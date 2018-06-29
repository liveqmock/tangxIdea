package com.topaiebiz.payment.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Description 支付宝 支付API 配置常量
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/15 13:14
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Component
public class AlipayConfig implements InitializingBean {

    @Autowired
    private Environment environment;

    /**
     * 支付宝网关（固定）
     */
    public static String POST_WAY;

    /**
     * 应用ID
     */
    public static String APP_ID;

    /**
     * 编码格式
     */
    public static String CHARSET;

    /**
     * 应用私钥
     */
    public static String APP_PRIVATE_KEY;

    /**
     * 支付宝公钥
     */
    public static String ALIPAY_PUBLIC_KEY;

    /**
     * 签名算法类型
     */
    public static String SIGN_TYPE;

    /**
     * 实例化客户端(线程安全类)
     */
    public static AlipayClient alipayClient;

    /**
     * 支付宝异步回调地址
     */
    public static String ASYNC_REQ_URL;

    /**
     * 支付宝同步请求地址
     */
    public static String SYNC_REQ_URL;

    /**
     * 支付成功，前台跳转页面(商品)
     */
    public static String PAY_SUCCESS_URL;

    /**
     * 支付成功，前台跳转页面(美礼卡)
     */
    public static String CARD_PAY_SUCCESS_URL;

    /**
     * 商户ID
     */
    public static String PID;

    /**
     * 字符编码
     */
    public static String INPUT_CHARSET = "utf-8";

    /**
     * 报关接口签名
     */
    public static String HAIGUANG_SIGN_TYPE = "RSA";

    /**
     * 报关-合作者身份ID
     */
    public static String PARTNER;

    /**
     * mapi 私钥
     */
    public static String MAPI_PRIVETE_KEY;

    /**
     * 商户海关备案code
     * 商户海关备案名称
     * 海关编号
     */
    public static String MERCHANT_CUSTOMS_CODE;
    public static String MERCHANT_CUSTOMS_NAME;
    public static String CUSTOMS_PLACE;


    @Override
    public void afterPropertiesSet() {
        POST_WAY = environment.getProperty("alipay.post.way");
        APP_ID = environment.getProperty("alipay.appid");
        CHARSET = environment.getProperty("alipay.charset");
        APP_PRIVATE_KEY = environment.getProperty("alipay.app.private.key");
        ALIPAY_PUBLIC_KEY = environment.getProperty("alipay.public.key");
        SIGN_TYPE = environment.getProperty("alipay.sign.type");
        ASYNC_REQ_URL = environment.getProperty("alipay.async.req.url");
        SYNC_REQ_URL = environment.getProperty("alipay.sync.req.url");
        PAY_SUCCESS_URL = environment.getProperty("alipay.pay.success.url");
        CARD_PAY_SUCCESS_URL = environment.getProperty("alipay.card.pay.success.url");
        PID = environment.getProperty("alipay.pid");
        alipayClient = new DefaultAlipayClient(POST_WAY, APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
        PARTNER = environment.getProperty("alipay.pid");
        MERCHANT_CUSTOMS_CODE = environment.getProperty("alipay.merchant.customs.code");
        MERCHANT_CUSTOMS_NAME = environment.getProperty("alipay.merchant.customs.name");
        CUSTOMS_PLACE = environment.getProperty("alipay.customs.place");
        MAPI_PRIVETE_KEY = environment.getProperty("alipay.mapi.private.key");
    }


    /**
     *
     * Description: 测试demo
     *
     * Author: hxpeng
     * createTime: 2018/3/6
     *
     * @param:
     **/
//    public static void test(){
//        POST_WAY = "https://openapi.alipay.com/gateway.do";
//        APP_ID = "2017041206663340";
//        CHARSET = "utf-8";
//        APP_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC+8aRzC3WvUU+PIxm9kuflw3hQ+0rSzseJxlX9Jrf1ONNSnKcMwO814gNbu8jF2x36YewdYAxF1KybWIyGBLErne7iMhYWj3SfqMzi5H/SC4yISPT9wmb0P62A7Y3CNiTV8/841HVBZCmS5KfWa7sreEgdIeuhoOMHETZLBqXCJee1nCvA+RQ5Pms2GuMivOh2ow/KCUA2Z3dPdTmM6X+3+oYcIQOArVCqhZSCzqNxDpJCtwWShOU8nvuja7zvUNg1Morlabwipuds+gwlH8HCB4b9apU0Q2kckODYerCocmINnMz1Do6+271WmZYKe87lmjj04vr39jwjQsenTpPfAgMBAAECggEAZiswHirElsGAO4vBizFfCg8UguhjOfdQx4SroCAmkH0Ofga6T9WwaXl86InLPSsomD/rMjChgH/yt9CiqFc8YaVJHBlGvl2WPpP4xXMUfdSWJpo7FMj/g7PK59C0aHXGMgEz0DnGX+3zXNkigVX1uaXLmGL9DOfkcnSgQ3bUHNR0A4NyQ95V8h/cjJT/e+5ibWrRuXKx9lflLL6yX5A+4EEooy+DIFFG1qws0ZMuc+AIHLkDgFGPsK0P6tdxopQY7Cjs8wgg1BITq8WZftzdmEWMwHXz1ZzzVscncahcV/FphvLmDERQv26P3eb9sO6Xu64GPpRzZzDv8FgH3vHlmQKBgQDwMkvZ/zYIY04S9DC0iQKGgZ5ge2W07GfQQvw3W7sXpOHJPlOn0lK/dnC07XtY/UbiYuBjpA6wYVcohvptfODyf5EZ4s4eJ+Ewmnm07nh7JcjuK6k+gkbiD6w9j1KchZ9nNo702PaKCTrMzcyfCSHcv8+eou66VuGgbxQITVh8EwKBgQDLgcUgPm6mEOXGutcmTtAvoVVB3cQDTHAf6HJT8kb7xVVeHPh+TsSS6X6BWkuHyCGvcgiUYxGz+aMWvA3RdyP0sBc+6m+coobf/WVfEpKZFpbhIttGRdG9owNbHcPSz4WmyfgEKnkIOQgxaKpsOhg3YbxtGPwV+AR1f892VXkqhQKBgDHr8jHjxiImKZLs57aOB1VXWUhqPTBGNill0q/aw97dKQQ/jPmDUCl0/2XLOei782CX9lUfQuJLyOambyQUYVhlRQ4hQn+oADiu0n7VVXsa1lESCAHy57KSWVUOFbhsWT4Fa97dvRfhZTQtzcp8Bmqv/wHUkVNvPUOv8nTrRmm7AoGADkpMh1ka9/pTyEKyfPQn1lBj5j4mV3z0mqj97jsjFObkeblbEbOCRjazNW4bw6sgAmvlglX6lkK/WwdViRHuqm37E+XudHdNNUlb5vMs7CcT7Tc/Yo3gjop1394VNCZyYiXojpJU8Wjf1GhfHz5tGOZ6DsckXH6QAZg5/zs9SOkCgYANn3aYVO6MN9jFFnBiYf5CIGugLJEyufilx2Nv00egEzC/z5U+UvxBRbu2POmr3RT5VZ/cFEq43olar+1esuqtffRxzx3t7eNRjWdb/DuE1+vpH/XDLjGf5sg9UVmNWJJwCl6T4vOAEJD3LcBYR4z2em5bYEDZjf1E9zdVSZBsLg==";
//        ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlol1XB86QCHDx9h5Qq/5JHIi8gmdOFR5bRzcDDAklAH9ej+pFeSDP72iIhnLmuZcss0G8IE/0gvsLpSsXFmlK3bTmtajoOddew7iCUyWT7BtaBY34xRBKaC29JtAXlS+fp50zV1bFloNS8KEwQH3VZpns72sEl5omdsAXvFP4fMAn3tPLkpSlo0wErvVgpW7mipXQgh5mcDgiZqovwRr/MUC/DBlgz1yl8jjs9kKHajhKmYN6cfVQORV4o31WvLpb0MlQzSwJR2RO1jO5HhCGT2+rWZPGvzkbydgDzw40Qq9NbNFr/imsMKmVX37Mt0MWYXI+u/H4dpDYHKaM3vnXwIDAQAB";
//        SIGN_TYPE = "RSA2";
//        ASYNC_REQ_URL = "http://pre.mamago.com/payment/alipay/payAsynNotice";
//        SYNC_REQ_URL = "http://pre.mamago.com/payment/alipay/paySyncNotice";
//        PAY_SUCCESS_URL = "http://eshop.mamago.com/motherbuy/#/payTrue";
//        CARD_PAY_SUCCESS_URL = "http://eshop.mamago.com/motherbuy/#/paymentResults";
//        PID = "2088911598628704";
//        alipayClient = new DefaultAlipayClient(POST_WAY, APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
//
//        PARTNER = "2088911598628704";
//        MERCHANT_CUSTOMS_CODE = "6557860400012158";
//        MERCHANT_CUSTOMS_NAME = "宁波妈妈购网络科技有限公司";
//        CUSTOMS_PLACE = "ZONGSHU";
//        MAPI_PRIVETE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANE3RQmQoQqbWlCUvNRgzBCq4eKKsycXEJ15PszXhAHyYkz/RIHb7MFADmCBCs/cwNIq17S8s6bbuOVCsEbBlAJMYe4WE9I5ZiaV6/i3Rls/pfkNCfS0Jm4kkG+pIwdhqdphpNzADRru044vwee/CVJqWZVosf9/aByanIDP2oafAgMBAAECgYEAj4/4jK61Ax1FZSQbSzS88vb/l7+LXdiC6zwmrtN4XwmfEKzdqqpqFNXKx8fkzmvx2IXTjM8sC+ScRJ11bqgo0gjopb8IPlwlN+Ns5IOo5wPQ0MFX+zP+gontn7LUHLZd0kpNQ6mwYGEb1b/6h4y5AQkA09PhV2slD41TKrbZFnkCQQDzb7I/Zuz2ve0+kiPfxOg04uPLnaCRF/6O/vOCvvqv7kMmdxh4xyiiNQelbn5zVDUaVL+98jAetSL41GyQVZy1AkEA3ANzM1VL1opwuePr/A+WtR/J7M979N5daT7AX2iW07+bH8np7G5hXTBl7EyA08vaGbrQtd8eECzX1rffRem+gwJAMew2Icp5ziAlBy/xK3K2LCJblOY+h4LkMW8XgTipMGssWjcSTbKKIrm9V6/RPtWGDmBS8iE4vRNomJ73pKHqAQJASgfPJ6K31gTXUXVj3njQWTnNFCXsq8R24gb/bBshRTbf1551W9z4Xgb/BLfpUVrylF3MBKaC0yaDqfhM5JV2SQJBAKOrCnaomT+EZM5/la6ko6oibbbvej4xIsnpnagP57jh3JE8mEJGjFYe2BcMTHaEt+GfxAS0JYsPkmOa+Y8TDlU=";
//    }
}
