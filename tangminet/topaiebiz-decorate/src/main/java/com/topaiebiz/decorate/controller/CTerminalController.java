package com.topaiebiz.decorate.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.decorate.service.CTerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * c端controller
 *
 * @author huzhenjia
 * @since 2018/03/30
 */
@RestController
@RequestMapping(value = "/decorate/cterminal")
public class CTerminalController {

    @Autowired
    private CTerminalService cTerminalService;

    /**
     * 根据页面id返回该页面的组件id JSONP
     *
     * @param suffixUrl
     * @return
     */
    @RequestMapping(value = "/page/{suffixUrl}", method = RequestMethod.GET)
    public ResponseInfo page(@PathVariable String suffixUrl) {
        return new ResponseInfo(cTerminalService.getComponent(suffixUrl));
    }

    /**
     * 根据组件id获取组件内容 JSONP
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/content/{id}", method = RequestMethod.GET)
    public ResponseInfo content(@PathVariable Long id) {
        return new ResponseInfo(cTerminalService.getContent(id));
    }
}
