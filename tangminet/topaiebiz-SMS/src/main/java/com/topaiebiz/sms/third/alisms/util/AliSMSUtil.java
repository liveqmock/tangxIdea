package com.topaiebiz.sms.third.alisms.util;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.sms.exception.MessageExceptionEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 阿里云短信对接新sdk
 * @author: Jeff Chen
 * @date: created in 下午3:59 2018/1/11
 */
@Slf4j
public class AliSMSUtil {

    private static String accessKeyId;

    private static String accessKeySecret;

    private AliSMSUtil() {
    }

    /**
     * 发送短信：阿里云sdk升级，在老系统中也升级一下
     *
     * @param phone        手机号
     * @param signName     签名
     * @param templateCode 模板code
     * @param paramJson    模板参数json
     * @param outId        扩展字段 可传null
     * @return
     */
    public static String sendSMS(String phone, String signName, String templateCode, String paramJson, String outId) {
        try {
            //短信API产品名称（短信产品名固定，无需修改）
            final String product = "Dysmsapi";
            //短信API产品域名（接口地址固定，无需修改）
            final String domain = "dysmsapi.aliyuncs.com";
            //你的accessKeyId,参考本文档步骤2
            //final String accessKeyId = "LTAInc7bjkM1OfaK";
            //你的accessKeySecret，参考本文档步骤2
            //final String accessKeySecret = "ZviTV6qw2wsdsiV6RkXPqxCegwD78F";
            //初始化ascClient,暂时不支持多region（请勿修改）
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象
            SendSmsRequest request = new SendSmsRequest();
            //使用post提交
            request.setMethod(MethodType.POST);
            request.setPhoneNumbers(phone);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(signName);
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(templateCode);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            request.setTemplateParam(paramJson);
            //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");
            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
            request.setOutId(outId);
            //请求失败这里会抛ClientException异常
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            String resultCode = sendSmsResponse.getCode();
            if (Constants.SMS.SEND_SMS_SUCCESS_CODE.equals(resultCode)) {
                return Constants.SMS.SEND_SMS_SUCCESS_CODE;
            } else {
                log.error(">>>>>>>>>>send sms to phone:{} fail, error code:{}, error message:{}", phone, sendSmsResponse.getCode(), sendSmsResponse.getMessage());
            }
        } catch (ClientException e) {
            log.error(">>>>>>>>>>send sms to phone:{} fail, error code:{}, error message:{}", phone, e.getErrCode(), e.getErrMsg());
        }
        throw new GlobalException(MessageExceptionEnum.CAPTCHA_CAN_NOT_SEND_AGAIN);
    }

    protected static void setAccessKeyId(String accessKeyId) {
        AliSMSUtil.accessKeyId = accessKeyId;
    }

    protected static void setAccessKeySecret(String accessKeySecret) {
        AliSMSUtil.accessKeySecret = accessKeySecret;
    }
}
