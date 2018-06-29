package com.topaiebiz.filter;

import com.nebulapaas.common.ServletRequestUtil;
import com.topaiebiz.context.RequestInfoContext;
import com.topaiebiz.model.RequestInfo;
import com.topaiebiz.util.InputRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/***
 * @author yfeng
 * @date 2018-01-30 20:57
 */
@Slf4j
public class InputLogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        //获取当前请求信息
        HttpServletRequest request = new InputRequestWrapper((HttpServletRequest) servletRequest);
        String ipAddr = ServletRequestUtil.getIpAddress(request);
        String reqUri = ServletRequestUtil.getRequestUri(request);
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setReqAgent(request.getHeader("User-Agent"));
        requestInfo.setReqIp(ipAddr);
        requestInfo.setReqMethod(request.getMethod().toUpperCase());
        requestInfo.setReqRef(request.getHeader("Referer"));
        requestInfo.setUriAndQuery(reqUri);
        RequestInfoContext.set(requestInfo);

        ThreadContext.put("ip", ipAddr);
        ThreadContext.put("reqUri", reqUri);

        if (isNotUploadRequest(request)) {
            String input = ((InputRequestWrapper) request).getBody();
            log.warn("{} ip:{} ua:{} ref:{} path: {} input param: {}", requestInfo.getReqMethod(),
                    requestInfo.getReqIp(), requestInfo.getReqAgent(), requestInfo.getReqRef(), requestInfo.getUriAndQuery(), input);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private boolean isNotUploadRequest(ServletRequest req) {
        String reqContentType = req.getContentType();
        if (StringUtils.isBlank(reqContentType)) {
            return true;
        }
        return !reqContentType.contains("multipart/form-data");
    }
}