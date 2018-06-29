package com.topaiebiz.thirdparty.access.service;

import com.topaiebiz.thirdparty.access.util.AesException;
import com.topaiebiz.thirdparty.dto.AccessTokenDTO;
import org.dom4j.DocumentException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Joe on 2018/5/8.
 */
public interface AccessService {

    /**
     * 获取微信ACCESS_TOKEN
     *
     * @return
     * @throws IOException
     */
    AccessTokenDTO getAccessToken() throws IOException;

    /**
     * 微信事件回复消息
     *
     * @param request
     * @param response
     * @return
     */
    String dispatch(HttpServletRequest request, HttpServletResponse response) throws AesException, IOException, DocumentException;
}
