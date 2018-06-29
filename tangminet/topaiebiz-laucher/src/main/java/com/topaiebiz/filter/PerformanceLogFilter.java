package com.topaiebiz.filter;

import com.google.common.base.Stopwatch;
import com.topaiebiz.context.RequestInfoContext;
import com.topaiebiz.model.RequestInfo;
import com.topaiebiz.monitor.api.MonitorApi;
import com.topaiebiz.monitor.dto.MonitorPerformanceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/***
 * @author yfeng
 * @date 2018-01-24 15:52
 */
@Slf4j(topic = "globalPerformance")
public class PerformanceLogFilter implements Filter {
    private long SLOW_MILLISEC = 300;
    @Autowired
    private MonitorApi monitorLogApi;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        Stopwatch stopwatch = Stopwatch.createStarted();

        RequestInfo requestInfo = RequestInfoContext.get();

        //执行责任链下一个组件
        try {
            chain.doFilter(servletRequest, servletResponse);
        } catch (Exception ex) {
            throw ex;
        } finally {
            long spendMs = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            if (spendMs >= SLOW_MILLISEC) {
                log.warn("{} ip:{} ua:{} ref:{} path: {} spend: {} ms", requestInfo.getReqMethod(),
                        requestInfo.getReqIp(), requestInfo.getReqAgent(), requestInfo.getReqRef(), requestInfo.getUriAndQuery(), spendMs);
                //发布性能提示
                MonitorPerformanceDTO monitorPerformance = new MonitorPerformanceDTO();
                monitorPerformance.setReqIp(requestInfo.getReqIp());
                monitorPerformance.setReqMethod(requestInfo.getReqMethod());
                monitorPerformance.setReqUa(requestInfo.getReqAgent());
                monitorPerformance.setReqRef(requestInfo.getReqRef());
                monitorPerformance.setReqPath(requestInfo.getUriAndQuery());
                monitorPerformance.setSpend(spendMs);
                monitorLogApi.publishPerformance(monitorPerformance);
            }
            RequestInfoContext.clean();
        }
    }


    @Override
    public void destroy() {

    }
}