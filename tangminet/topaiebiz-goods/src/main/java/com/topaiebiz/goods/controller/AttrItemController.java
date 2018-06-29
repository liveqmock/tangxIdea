package com.topaiebiz.goods.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.dto.AttrItemDTO;
import com.topaiebiz.goods.dto.AttrItemReq;
import com.topaiebiz.goods.entity.AttrItem;
import com.topaiebiz.goods.entity.AttrItemEdit;
import com.topaiebiz.goods.service.AttrItemService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

/**
 * @description: 属性名和值得控制器
 * @author: Jeff Chen
 * @date: created in 下午1:30 2018/5/19
 */
@RestController
@RequestMapping("/goods/attrItem")
public class AttrItemController extends AbstractController {

    @Autowired
    private AttrItemService attrItemService;

    /**
     * 添加属性
     *
     * @param attrItemDTO
     * @return
     */
    @RequestMapping("/addEdit")
    @PermissionController(value = PermitType.PLATFORM, operationName = "添加属性编辑项")
    public ResponseInfo addAttrItemEdit(@RequestBody @Valid AttrItemDTO attrItemDTO, BindingResult result) {
        ResponseInfo responseInfo = validParam(result);
        if (null != responseInfo) {
            return responseInfo;
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        attrItemDTO.setCreatedTime(new Date());
        attrItemDTO.setCreatorId(currentUserDto.getId());
        attrItemDTO.setLastModifiedTime(new Date());
        attrItemDTO.setLastModifierId(currentUserDto.getId());
        return new ResponseInfo(attrItemService.saveAttrItemEdit(attrItemDTO));
    }

    /**
     * 属性项编辑详情
     *
     * @param attrId
     * @return
     */
    @RequestMapping("/detailEdit/{attrId}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "属性项编辑详情")
    public ResponseInfo detail(@PathVariable Long attrId) {
        return new ResponseInfo(attrItemService.getAttrItemEditById(attrId));
    }

    /**
     * 删除某项属性
     *
     * @param attrId
     * @return
     */
    @RequestMapping("/deleteEdit/{attrId}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "属性项编辑删除")
    public ResponseInfo deleteEdit(@PathVariable Long attrId) {
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        AttrItemEdit attrItemEdit = new AttrItemEdit();
        attrItemEdit.setId(attrId);
        attrItemEdit.setLastModifiedTime(new Date());
        attrItemEdit.setLastModifierId(currentUserDto.getId());
        return new ResponseInfo(attrItemService.deleteAttrItemEdit(attrItemEdit));
    }

    /**
     * 放弃删除
     *
     * @param attrId
     * @return
     */
    @RequestMapping("/abortDel/{attrId}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "属性项放弃删除")
    public ResponseInfo abortDel(@PathVariable Long attrId) {
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        AttrItemEdit attrItemEdit = new AttrItemEdit();
        attrItemEdit.setId(attrId);
        attrItemEdit.setLastModifiedTime(new Date());
        attrItemEdit.setLastModifierId(currentUserDto.getId());
        return new ResponseInfo(attrItemService.abortDel(attrItemEdit));
    }

    /**
     * 更新属性项编辑
     *
     * @param attrItemDTO
     * @return
     */
    @RequestMapping("/updateEdit")
    @PermissionController(value = PermitType.PLATFORM, operationName = "属性项编辑更新")
    public ResponseInfo updateEdit(@RequestBody AttrItemDTO attrItemDTO) {
        if (null == attrItemDTO.getAttrId()) {
            return paramError();
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        attrItemDTO.setLastModifierId(currentUserDto.getId());
        attrItemDTO.setLastModifiedTime(new Date());

        return new ResponseInfo(attrItemService.updateAttrItemEdit(attrItemDTO));
    }
    /**
     * 更新属性项编辑
     *
     * @param attrItemDTO
     * @return
     */
    @RequestMapping("/updateEditSort")
    @PermissionController(value = PermitType.PLATFORM, operationName = "属性项编辑更新")
    public ResponseInfo updateEditSort(@RequestBody AttrItemDTO attrItemDTO) {
        if (null == attrItemDTO.getAttrId()) {
            return paramError();
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        attrItemDTO.setLastModifierId(currentUserDto.getId());
        attrItemDTO.setLastModifiedTime(new Date());

        return new ResponseInfo(attrItemService.updateAttrItemEditSort(attrItemDTO));
    }

    /**
     * 指定类目下的属性编辑列表
     *
     * @return
     */
    @RequestMapping("/listEdit")
    @PermissionController(value = PermitType.PLATFORM, operationName = "属性项编辑列表")
    public ResponseInfo listEdit(@RequestBody AttrItemReq attrItemReq) {
        return new ResponseInfo(attrItemService.selectAttrItemEdit(attrItemReq));
    }

    /**
     * 指定类目下的属性列表
     *
     * @return
     */
    @RequestMapping("/listFormal")
    @PermissionController(value = PermitType.PLATFORM, operationName = "属性项编辑列表")
    public ResponseInfo listFormal(@RequestBody AttrItemReq attrItemReq) {
        return new ResponseInfo(attrItemService.selectAttrItemFormal(attrItemReq));
    }
}
