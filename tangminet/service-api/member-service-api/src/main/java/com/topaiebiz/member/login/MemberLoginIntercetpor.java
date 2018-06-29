package com.topaiebiz.member.login;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.member.api.MemberApi;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ward on 2017-12-29.
 */
@Component
@Slf4j
public class MemberLoginIntercetpor implements HandlerInterceptor {

    @Autowired
    private MemberApi memberApi;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MemberContext.clear();
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            MemberLogin memberLogin = (MemberLogin) handlerMethod.getMethodAnnotation(MemberLogin.class);
            if (memberLogin == null) {
                memberLogin = (MemberLogin) handlerMethod.getBeanType().getAnnotation(MemberLogin.class);
            }
            if (memberLogin == null) {
                return true;
            }

            return verify(request, response, handlerMethod);
        }
        //不需要登录校验，直接放过
        return true;
    }

    private boolean verify(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        String sessionId = request.getHeader("sessionId");
        log.info("verify member login status with sessionId: {}", sessionId);
        MemberTokenDto token = memberApi.getMemberToken(sessionId);
        if (null != token && null != token.getMemberId() && token.getMemberId() > 0) {
            log.debug("login success:{}", JSON.toJSON(token));
            MemberContext.saveMemberToken(token);
            // return true;
        }
        // throw new GlobalException(MemberExceptionEnum.NOT_LOGIN);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
       // log.warn("memberContext clear success:============================");
        MemberContext.clear();
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

}
