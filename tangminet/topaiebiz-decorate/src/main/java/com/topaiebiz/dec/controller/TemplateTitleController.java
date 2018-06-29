package com.topaiebiz.dec.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.dec.dto.ModifyTitleDto;
import com.topaiebiz.dec.dto.SecondTitleDto;
import com.topaiebiz.dec.dto.TemplateTitleDto;
import com.topaiebiz.dec.service.TemplateTitleService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 商品标题表 前端控制器
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
@RestController
@Slf4j
@RequestMapping(value = "/decorate/templateTitle", method = RequestMethod.POST)
public class TemplateTitleController {
    @Autowired
    TemplateTitleService templateTitleService;

    @PermissionController(value = PermitType.PLATFORM,operationName = "添加标题")
    @RequestMapping(value = "/addTemplateTitle")
    public ResponseInfo addTemplateTitle(@RequestBody TemplateTitleDto templateTitleDto) throws GlobalException {
        return new ResponseInfo(templateTitleService.saveTemplateTitleDto(templateTitleDto));
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "修改标题")
    @RequestMapping(value = "/modifyTemplateTitle")
    public ResponseInfo modifyTemplateTitle(@RequestBody ModifyTitleDto moveTitleInfo) throws GlobalException {
        templateTitleService.updateTemplateTitleDto(moveTitleInfo.getId(), moveTitleInfo.getTitleName());
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "根据标题id删除标题")
    @RequestMapping(value = "/removeTemplateTitle")
    public ResponseInfo removeTemplateTitle(@RequestBody Long id) throws GlobalException {
        templateTitleService.deleteTemplateTitle(id);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "根据模块id查询标题")
    @RequestMapping(value = "/searchTemplateTitle")
    public ResponseInfo searchTemplateTitle(@RequestBody Long moduleId) throws GlobalException {
        log.info(" search {} ", String.valueOf(moduleId));
        return new ResponseInfo(templateTitleService.getTemplateTitleDto(moduleId));
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "移动标题")
    @RequestMapping(value = "/moveTemplateTitle")
    public ResponseInfo moveTemplateTitle(@RequestBody ModifyTitleDto moveTitleInfo) throws GlobalException {
        templateTitleService.moveTemplateTitle(moveTitleInfo.getId(), moveTitleInfo.getTargetId());
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM,operationName = "单独添加二级标题")
    @RequestMapping(value = "/addSecondTitle")
    public ResponseInfo addSecondTitle(@RequestBody SecondTitleDto secondTitleDto) throws GlobalException {
        return new ResponseInfo(templateTitleService.saveSecondTitle(secondTitleDto));
    }
}
