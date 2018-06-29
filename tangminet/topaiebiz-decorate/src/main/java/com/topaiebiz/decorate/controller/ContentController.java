package com.topaiebiz.decorate.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.decorate.dto.ComponentContentDto;
import com.topaiebiz.decorate.dto.ExportItemDto;
import com.topaiebiz.decorate.service.ContentService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 组件内容controller
 *
 * @author huzhenjia
 * @since 2018/03/28
 */
@RestController
@RequestMapping(value = "/decorate/content", method = RequestMethod.POST)
public class ContentController {

    @Autowired
    private ContentService contentService;

    /**
     * 添加组件内容
     *
     * @param componentContentDto
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "添加组件内容")
    @RequestMapping(value = "/create")
    public ResponseInfo create(@RequestBody ComponentContentDto componentContentDto) {
        return new ResponseInfo(contentService.create(componentContentDto));
    }


    /**
     * 修改组件内容
     *
     * @param componentContentDto
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "修改组件内容")
    @RequestMapping(value = "/modify")
    public ResponseInfo modify(@RequestBody ComponentContentDto componentContentDto) {
        return new ResponseInfo(contentService.modify(componentContentDto));
    }

    /**
     * 预览组件内容
     *
     * @param id
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "预览组件内容")
    @RequestMapping(value = "/preview")
    public ResponseInfo preview(@RequestBody Long id) {
        return new ResponseInfo(contentService.preview(id));
    }


    /**
     * 导入商品excel
     *
     * @param file
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "导入商品excel")
    @RequestMapping(value = "/importItem")
    public ResponseInfo importItem(@RequestParam("excel") MultipartFile file) {
        return new ResponseInfo(contentService.importItem(file));
    }

    /**
     * 导出
     *
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "导出")
    @RequestMapping(value = "/export")
    public void export(HttpServletResponse response, @RequestBody ExportItemDto exportItemDto) {
        contentService.export(response,exportItemDto);
    }

}
