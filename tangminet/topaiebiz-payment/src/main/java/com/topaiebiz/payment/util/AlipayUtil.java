package com.topaiebiz.payment.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.XmlUtils;
import com.topaiebiz.pay.dto.ReportCustomsDTO;
import com.topaiebiz.pay.dto.refund.RefundResultDTO;
import com.topaiebiz.payment.config.AlipayConfig;
import com.topaiebiz.payment.constants.PaymentConstants;
import com.topaiebiz.payment.sign.RSA;
import com.topaiebiz.payment.util.httpClient.HttpProtocolHandler;
import com.topaiebiz.payment.util.httpClient.HttpRequest;
import com.topaiebiz.payment.util.httpClient.HttpResponse;
import com.topaiebiz.payment.util.httpClient.HttpResultType;
import com.topaiebiz.trade.dto.order.OrderCustomsResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.NameValuePair;

import java.math.BigDecimal;
import java.util.*;

/**
 * Description 支付
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/15 14:35
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
public class AlipayUtil {

    /**
     * 支付宝提供给商户的服务接入网关URL(新)
     */
    private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";


    /**
     * Description： 支付---参数签名
     * <p>
     * Author hxpeng
     *
     * @return payParamDTO 支付参数, returnUrl成功后跳转地址
     */
    public static String buildPayForm(Map<String, String> map) {
        String bizContent = JSONObject.toJSONString(map);
        log.info("----------调用WEB支付接口：参数值{}", bizContent);
        AlipayTradeWapPayRequest payRequest = new AlipayTradeWapPayRequest();
        payRequest.setReturnUrl(AlipayConfig.SYNC_REQ_URL);
        payRequest.setNotifyUrl(AlipayConfig.ASYNC_REQ_URL);
        payRequest.setBizContent(bizContent);
        try {
            return AlipayConfig.alipayClient.pageExecute(payRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            log.error("----------生成支付表单失败,参数{}----------", bizContent);
            e.printStackTrace();
        }
        return null;
    }

    public static String buildPayApp(AlipayTradeAppPayModel appPayModel) {
        String bizContent = JSONObject.toJSONString(appPayModel);
        log.info("----------调用APP支付接口：参数值{}", bizContent);
        AlipayTradeAppPayRequest appPayRequest = new AlipayTradeAppPayRequest();
        appPayRequest.setNotifyUrl(AlipayConfig.ASYNC_REQ_URL);
        appPayRequest.setBizModel(appPayModel);
        try {
            return AlipayConfig.alipayClient.sdkExecute(appPayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            log.error("----------生成APP支付签名失败,参数{}----------", bizContent);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Description: 验证签名
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/19
     *
     * @param:
     **/
    public static Boolean checkResponseSign(Map<String, String[]> map) throws AlipayApiException {
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        for (String name : map.keySet()) {
            String[] values = map.get(name);
            StringBuffer valueString = new StringBuffer("");
            for (int i = 0; i < values.length; i++) {
                valueString = (i == values.length - 1) ? valueString.append(values[i]) : valueString.append(values[i]).append(",");
            }
            params.put(name, valueString.toString());
        }
        return AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, AlipayConfig.SIGN_TYPE);
    }

    /**
     * Description: 查询支付订单信息
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/19
     *
     * @param:
     **/
    public static void queryOrderInfo(String outTradeNo, String tradeNo) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();//创建API对应的request类
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_no", outTradeNo);
        jsonObject.put("trade_no", tradeNo);
        //设置业务参数
        request.setBizContent(jsonObject.toJSONString());
        AlipayTradeQueryResponse response = null;
        try {
            response = AlipayConfig.alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Description: 退款
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/19
     *
     * @param:
     **/
    public static RefundResultDTO refund(Map<String, String> bizContentMap) {
        String bizContent = JSON.toJSONString(bizContentMap);
        log.info("----------alipay interface refund, params's bizContent:{}", bizContent);
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizContent(bizContent);

        RefundResultDTO refundResultDTO = new RefundResultDTO();
        AlipayTradeRefundResponse response;
        try {
            response = AlipayConfig.alipayClient.execute(request);
            log.info("----------print alipay refund response message：{}", JSON.toJSONString(response));
            if (!response.isSuccess() || !response.getCode().equals(PaymentConstants.Alipay.REPONSE_SUCCESS_CODE)) {
                throw new RuntimeException("----------request Alipay refund interface failed， the response code is not success");
            }
            // 退款金额
            BigDecimal refundAmount = new BigDecimal(bizContentMap.get("refund_amount"));
            // 订单号
            String outTradeNo = bizContentMap.get("out_trade_no");
            if (!response.getOutTradeNo().equals(outTradeNo)) {
                throw new RuntimeException("----------request Alipay refund interface failed，outTradeNo is not equals！");
            }
            // 判断退款金额与售后订单金额是否相等
            if (refundAmount.compareTo(new BigDecimal(response.getRefundFee())) != 0) {
                throw new RuntimeException("----------request Alipay refund interface failed，response'amount does not match the amount of refund order！");
            }
            // 判断资金是否发什变化
            if (!response.getFundChange().equals(PaymentConstants.Alipay.FUND_CHANGE)) {
                throw new RuntimeException("----------request Alipay refund interface failed，No change in funds！");
            }
            log.info("----------request Alipay refund interface successed!");
            refundResultDTO.setCallBackNo(response.getTradeNo());
            refundResultDTO.setResultCode(Constants.Order.REFUND_SUCCESS);
            return refundResultDTO;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        refundResultDTO.setResultCode(Constants.Order.REFUND_FAIL);
        return refundResultDTO;
    }


    /**
     * Description: 测试退款demo
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/6
     *
     * @param:
     **/
//    public static void main(String[] args) {
//        AlipayConfig.test();
//
//        Map<String, String> bizContent = new HashMap<>();
//        // 商户支付订单号
//        bizContent.put("out_trade_no", "970845871909724161");
//        // 第三方支付流水
//        bizContent.put("trade_no", "2018030621001004520560423108");
//        // 商户退款订单号
//        bizContent.put("out_request_no", "111");
//        // 退款金额
//        BigDecimal refundPrice = new BigDecimal(10);
//        bizContent.put("refund_amount", String.valueOf(refundPrice.doubleValue()));
//        // 退款理由
//        bizContent.put("refund_reason", "无理由退货");
//
//        refund(bizContent);
//    }

/////////////////////////////////////////////////////////下面都是报关得代码
    @SuppressWarnings("unchecked")
    public static OrderCustomsResultDTO reportCustoms(ReportCustomsDTO customsDTO) {
        //把请求参数打包成数组
        Map<String, String> map = new HashMap<>();
        map.put("service", "alipay.acquire.customs");
        map.put("partner", AlipayConfig.PARTNER);
        map.put("_input_charset", AlipayConfig.INPUT_CHARSET);
        map.put("merchant_customs_code", AlipayConfig.MERCHANT_CUSTOMS_CODE);
        map.put("merchant_customs_name", AlipayConfig.MERCHANT_CUSTOMS_NAME);
        map.put("customs_place", AlipayConfig.CUSTOMS_PLACE);
        // 报关流水号
        map.put("out_request_no", customsDTO.getReportId());
        // 支付宝的交易号
        map.put("trade_no", customsDTO.getThirdTradeNo());
        map.put("amount", customsDTO.getAmount());
        map.put("buyer_name", customsDTO.getBuyerName());
        map.put("buyer_id_no", customsDTO.getBuyerIdNo());
        try {
            log.warn(">>>>>>>>>>alipay: report order to customs, params:{}", JSON.toJSONString(map));
            System.out.println(JSON.toJSONString(map));
            String resultXml = buildRequest("", "", map);
            System.out.println(resultXml);
            log.warn(">>>>>>>>>>alipay: report order to customs, result:{}", resultXml);
            Map<String, Object> resultMap = XmlUtils.xmlDocumentStr(resultXml);

            // 报关结果DTO
            OrderCustomsResultDTO orderCustomsResultDTO = new OrderCustomsResultDTO();
            orderCustomsResultDTO.setMmgReportId(customsDTO.getReportId());
            orderCustomsResultDTO.setMmgOrderId(customsDTO.getOrderId());
            orderCustomsResultDTO.setReportTime(new Date());
            orderCustomsResultDTO.setReportWay(customsDTO.getPayMethodEnum().getName());
            if ("T".equals(resultMap.get("is_success"))) {
                Map<String, Object> response = (Map<String, Object>) resultMap.get("response");
                Map<String, Object> alipay = (Map<String, Object>) response.get("alipay");
                if ("FAIL".equals(alipay.get("result_code"))) {
                    orderCustomsResultDTO.setAlipayResultCode((String) alipay.get("detail_error_code"));
                    orderCustomsResultDTO.setAlipayResultDesc((String) alipay.get("detail_error_des"));
                } else {
                    orderCustomsResultDTO.setAlipayDeclareNo((String) alipay.get("alipay_declare_no"));
                    orderCustomsResultDTO.setOutTradeNo((String) alipay.get("trade_no"));
                }
                return orderCustomsResultDTO;
            }
        } catch (Exception e) {
            log.error(">>>>>>>>>>alipay reportCustoms fail", e);
        }
        return null;
    }

//    public static void main(String[] args) {
//        AlipayConfig.test();
//        ReportCustomsDTO reportCustomsDTO = new ReportCustomsDTO();
//        reportCustomsDTO.setReportId("20180317051302974936537506828289");
//        reportCustomsDTO.setThirdTradeNo("2018031721001004430563337776");
//        reportCustomsDTO.setAmount("0.01");
//        reportCustomsDTO.setBuyerName("玩儿");
//        reportCustomsDTO.setBuyerIdNo("430422199611189211");
//        reportCustoms(reportCustomsDTO);
//    }


    /**
     * 生成要请求给支付宝的参数数组
     *
     * @param sParaTemp 请求前的参数数组
     * @return 要请求的参数数组
     */
    private static Map<String, String> buildRequestParams(Map<String, String> sParaTemp) {
        //除去数组中的空值和签名参数
        Map<String, String> sPara = paraFilter(sParaTemp);
        //生成签名结果
        String mysign = buildRequestMysign(sPara);

        //签名结果与签名方式加入请求提交参数组中
        sPara.put("sign", mysign);
        sPara.put("sign_type", AlipayConfig.HAIGUANG_SIGN_TYPE);

        return sPara;
    }

    /**
     * 生成签名结果
     *
     * @param sPara 要签名的数组
     * @return 签名结果字符串
     */
    private static String buildRequestMysign(Map<String, String> sPara) {
        String prestr = createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String mysign = "";
        if (AlipayConfig.HAIGUANG_SIGN_TYPE.equals("RSA")) {
            mysign = RSA.sign(prestr, AlipayConfig.MAPI_PRIVETE_KEY, AlipayConfig.INPUT_CHARSET);
        }
        return mysign;
    }

    /**
     * 除去数组中的空值和签名参数
     *
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    private static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap<>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                    || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }


    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    private static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }

    /**
     * 建立请求，以模拟远程HTTP的POST请求方式构造并获取支付宝的处理结果
     * 如果接口中没有上传文件参数，那么strParaFileName与strFilePath设置为空值
     * 如：buildRequest("", "",sParaTemp)
     *
     * @param strParaFileName 文件类型的参数名
     * @param strFilePath     文件路径
     * @param sParaTemp       请求参数数组
     * @return 支付宝处理结果
     * @throws Exception
     */
    private static String buildRequest(String strParaFileName, String strFilePath, Map<String, String> sParaTemp) throws Exception {
        //待请求参数数组
        Map<String, String> sPara = buildRequestParams(sParaTemp);
        logParam(sPara);
        HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
        HttpRequest request = new HttpRequest(HttpResultType.BYTES);
        //设置编码集
        request.setCharset(AlipayConfig.INPUT_CHARSET);
        request.setParameters(generatNameValuePair(sPara));
        request.setUrl(ALIPAY_GATEWAY_NEW + "_input_charset=" + AlipayConfig.INPUT_CHARSET);
        HttpResponse response = httpProtocolHandler.execute(request, strParaFileName, strFilePath);
        if (response == null) {
            return null;
        }
        return response.getStringResult();
    }

    private static void logParam(Map<String, String> sPara) {
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String, String> entry : sPara.entrySet()) {
            buf.append(entry.getKey()).append(" - ").append(entry.getValue()).append("\n");
        }
        log.info("buildRequestParams : {}", buf.toString());
    }

    /**
     * MAP类型数组转换成NameValuePair类型
     *
     * @param properties MAP类型数组
     * @return NameValuePair类型数组
     */
    private static NameValuePair[] generatNameValuePair(Map<String, String> properties) {
        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
        }
        return nameValuePair;
    }

}