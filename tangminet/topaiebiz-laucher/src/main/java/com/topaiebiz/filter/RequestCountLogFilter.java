package com.topaiebiz.filter;

import com.topaiebiz.monitor.api.MonitorApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/***
 * @author yfeng
 * @date 2018-01-30 20:57
 */
@Slf4j
public class RequestCountLogFilter implements Filter {

    @Autowired
    private MonitorApi monitorApi;
    private String monitorApiURI = "/monitor/";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String reqUri = request.getRequestURI();
        if (!reqUri.startsWith(monitorApiURI)) {
            monitorApi.requestCountIncrease();
        }
        chain.doFilter(servletRequest, response);
    }

    @Override
    public void destroy() {
    }
}