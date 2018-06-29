package com.topaiebiz.goods.controller;

import com.nebulapaas.web.exception.SystemExceptionEnum;
import com.nebulapaas.web.response.ResponseInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @description: 控制器基类
 * @author: Jeff Chen
 * @date: created in 下午1:43 2018/1/15
 */
public abstract class AbstractController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 校验参数
     * @param result
     * @return
     */
    protected ResponseInfo validParam(BindingResult result) {
        StringBuffer stringBuffer = new StringBuffer();
        if (result.hasErrors()) {
            List<ObjectError> objectErrors = result.getAllErrors();
            for (ObjectError error : objectErrors) {
                stringBuffer.append(error.getDefaultMessage()).append(";");
            }
            return new ResponseInfo(SystemExceptionEnum.ILLEGAL_PARAM.getCode(), stringBuffer.toString());
        }
        return null;
    }

    /**
     * 通用参数错误
     * @return
     */
    protected ResponseInfo paramError() {
        return new ResponseInfo(SystemExceptionEnum.ILLEGAL_PARAM.getCode(), SystemExceptionEnum.ILLEGAL_PARAM.getDefaultMessage());
    }

    /**
     * 获取ip
     * @param request
     * @return
     */
    protected String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-real-ip");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        } else {
            ip = request.getHeader("X-Real-IP");
            if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
                return ip;
            } else {
                ip = request.getHeader("X-Forwarded-For");
                if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
                    int index = ip.indexOf(",");
                    return index != -1?ip.substring(0, index):ip;
                } else {
                    return request.getRemoteAddr();
                }
            }
        }
    }
}
