package com.topaiebiz.goods.controller;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.common.BindResultUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.dto.AttrGroupDTO;
import com.topaiebiz.goods.dto.AttrGroupEditDTO;
import com.topaiebiz.goods.dto.AttrGroupSortNoDTO;
import com.topaiebiz.goods.dto.CategoryIdDTO;
import com.topaiebiz.goods.service.AttrGroupService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @description: 属性分组控制器
 * @author: Jeff Chen
 * @date: created in 上午10:36 2018/5/18
 */
@RestController
@RequestMapping("/goods/attrGroup")
public class AttrGroupController extends AbstractController{

    @Autowired
    private AttrGroupService attrGroupService;

    /**
     * Description 属性分组列表(操作)
     * <p>
     * Author Hedda
     *
     * @param categoryIdDTO
     * @return ResponseInfo
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "属性分组列表分页")
    @RequestMapping(path = "/getAttrGroupList")
    public ResponseInfo getAttrGroupList(@RequestBody CategoryIdDTO categoryIdDTO)
            throws GlobalException {
        PageInfo<AttrGroupEditDTO> list = attrGroupService.getAttrGroupList(categoryIdDTO);
        return new ResponseInfo(list);
    }

    /**
     * Description 属性分组下拉(生产)
     * <p>
     * Author Hedda
     *
     * @return ResponseInfo
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "属性分组下拉")
    @RequestMapping(path = "/queryAttrGroups/{categoryId}")
    public ResponseInfo queryAttrGroups(@PathVariable Long categoryId)
            throws GlobalException {
        List<AttrGroupEditDTO> list = attrGroupService.queryAttrGroups(categoryId);
        return new ResponseInfo(list);
    }

    /**
     * Description 属性分组列表(生产)
     * <p>
     * Author Hedda
     *
     * @return ResponseInfo
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "属性分组列表分页")
    @RequestMapping(path = "/getAttrGroups")
    public ResponseInfo getAttrGroups(@RequestBody CategoryIdDTO categoryIdDTO)
            throws GlobalException {
        PageInfo<AttrGroupDTO> list = attrGroupService.getAttrGroups(categoryIdDTO);
        return new ResponseInfo(list);
    }

    /**
     * Description 属性分组添加(操作)
     * <p>
     * Author Hedda
     *
     * @param attrGroupEditDTO 属性分组
     * @param result
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "属性分组添加")
    @RequestMapping(path = "/addAttrGroupEdit")
    public ResponseInfo addAttrGroupEdit(@RequestBody @Valid AttrGroupEditDTO attrGroupEditDTO, BindingResult result) throws GlobalException {
        BindResultUtil.dealBindResult(result);
        return new ResponseInfo(attrGroupService.saveAttrGroupEdit(attrGroupEditDTO));
    }

    /**
     * Description 属性分组修改(操作)
     * <p>
     * Author Hedda
     *
     * @param attrGroupEditDTO 属性分组
     * @param result
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "属性分组修改")
    @RequestMapping(path = "/editAttrGroupEdit")
    public ResponseInfo editAttrGroupEdit(@RequestBody @Valid AttrGroupEditDTO attrGroupEditDTO, BindingResult result) throws GlobalException {
        BindResultUtil.dealBindResult(result);
        return new ResponseInfo(attrGroupService.modifyAttrGroupEdit(attrGroupEditDTO));
    }

    /**
     * Description 属性分组回显(操作)
     * <p>
     * Author Hedda
     *
     * @param id 属性分组id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "属性分组回显")
    @RequestMapping(path = "/findAttrGroupEdit/{id}")
    public ResponseInfo findAttrGroupEdit(@PathVariable Long id) throws GlobalException {
        return new ResponseInfo(attrGroupService.findAttrGroupEdit(id));
    }

    /**
     * Description 属性分组删除(操作)
     * <p>
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "属性分组删除")
    @RequestMapping(path = "/cancelAttrGroupEdit/{id}")
    public ResponseInfo cancelAttrGroupEdit(@PathVariable Long id) throws GlobalException {
        return new ResponseInfo(attrGroupService.removeAttrGroupEdit(id));
    }

    /**
     * Description 属性分组放弃删除(操作)
     * <p>
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "属性分组放弃删除")
    @RequestMapping(path = "/giveUpAttrGroupEdit/{id}")
    public ResponseInfo giveUpAttrGroupEdit(@PathVariable Long id) throws GlobalException {
        return new ResponseInfo(attrGroupService.giveUpAttrGroupEdit(id));
    }

    /**
     * Description 属性分组修改排序号(操作)
     * <p>
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "属性分组修改排序号")
    @RequestMapping(path = "/editAttrGroupSortNo")
    public ResponseInfo editAttrGroupSortNo(@RequestBody AttrGroupSortNoDTO attrGroupSortNoDTO) throws GlobalException {
        return new ResponseInfo(attrGroupService.modifyAttrGroupSortNo(attrGroupSortNoDTO));
    }


}
