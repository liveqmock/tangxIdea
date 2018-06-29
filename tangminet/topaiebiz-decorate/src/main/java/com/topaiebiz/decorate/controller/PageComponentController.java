package com.topaiebiz.decorate.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.decorate.dto.PageComponentDto;
import com.topaiebiz.decorate.service.PageComponentService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 组件controller
 *
 * @author huzhenjia
 * @since 2018/03/28
 */
@RestController
@RequestMapping(value = "/decorate/component", method = RequestMethod.POST)
public class PageComponentController {

    @Autowired
    private PageComponentService pageComponentService;

    /**
     * 新增一个组件到页面上
     *
     * @param pageComponentDto
     * @return primary key
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "新增一个组件到页面上")
    @RequestMapping(value = "/create")
    public ResponseInfo create(@RequestBody PageComponentDto pageComponentDto) {
        pageComponentService.create(pageComponentDto);
        return new ResponseInfo();
    }

    /**
     * 删除页面上一个组件
     *
     * @param id
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "删除页面上一个组件")
    @RequestMapping(value = "/remove")
    public ResponseInfo remove(@RequestBody Long id) {
        pageComponentService.remove(id);
        return new ResponseInfo();
    }

    /**
     * 修改页面组件
     *
     * @param pageComponentDto
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "修改页面组件")
    @RequestMapping(value = "/modify")
    public ResponseInfo modify(@RequestBody PageComponentDto pageComponentDto) {
        pageComponentService.modify(pageComponentDto);
        return new ResponseInfo();
    }
}
