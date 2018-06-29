package com.topaiebiz.goods.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.dto.BackendCategoryDTO;
import com.topaiebiz.goods.dto.CategoryTreeDTO;
import com.topaiebiz.goods.enums.GoodsExceptionEnum;
import com.topaiebiz.goods.service.BackendCategoryNewService;
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
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 后台类目编辑与正式数据控制器
 * @author: Jeff Chen
 * @date: created in 下午4:06 2018/5/18
 */
@RestController
@RequestMapping("/goods/category/")
public class BackendCategoryNewController extends AbstractController {

    @Autowired
    private BackendCategoryNewService backendCategoryNewService;

    /**
     * 添加类目编辑项
     *
     * @param backendCategoryDTO
     * @return
     */
    @RequestMapping("/addEdit")
    @PermissionController(value = PermitType.PLATFORM ,operationName = "添加类目编辑项")
    public ResponseInfo addCategoryEdit(@RequestBody @Valid BackendCategoryDTO backendCategoryDTO, BindingResult result) {
        ResponseInfo responseInfo = validParam(result);
        if (null != responseInfo) {
            return responseInfo;
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        backendCategoryDTO.setCreatorId(currentUserDto.getId());
        backendCategoryDTO.setCreatedTime(new Date());
        backendCategoryDTO.setLastModifierId(currentUserDto.getId());
        backendCategoryDTO.setLastModifiedTime(new Date());

        return new ResponseInfo(backendCategoryNewService.saveBackendCategoryEdit(backendCategoryDTO));
    }

    /**
     * 类目详情
     * @param categoryId
     * @return
     */
    @RequestMapping("/detailEdit/{categoryId}")
    @PermissionController(value = PermitType.PLATFORM ,operationName = "类目编辑详情")
    public ResponseInfo detailEdit(@PathVariable Long categoryId) {
        return new ResponseInfo(backendCategoryNewService.getCategoryEditById(categoryId));
    }

    /**
     * 删除类目
     * @param categoryId
     * @return
     */
    @RequestMapping("/deleteEdit/{categoryId}")
    @PermissionController(value = PermitType.PLATFORM ,operationName = "类目编辑删除")
    public ResponseInfo deleteEdit(@PathVariable Long categoryId) {
        BackendCategoryDTO backendCategoryDTO = new BackendCategoryDTO();
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        backendCategoryDTO.setLastModifierId(currentUserDto.getId());
        backendCategoryDTO.setLastModifiedTime(new Date());
        backendCategoryDTO.setCategoryId(categoryId);
        return new ResponseInfo(backendCategoryNewService.deleteCategoryEditById(backendCategoryDTO));
    }

    /**
     * 更新类目编辑项
     * @param backendCategoryDTO
     * @return
     */
    @RequestMapping("/updateEdit")
    @PermissionController(value = PermitType.PLATFORM ,operationName = "更新类目编辑项")
    public ResponseInfo updateCategoryEdit(@RequestBody BackendCategoryDTO backendCategoryDTO) {
        if (null == backendCategoryDTO.getCategoryId()) {
            return new ResponseInfo(GoodsExceptionEnum.CATEGORY_ID_NEED.getCode(), GoodsExceptionEnum.CATEGORY_ID_NEED.getDefaultMessage());
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        backendCategoryDTO.setLastModifierId(currentUserDto.getId());
        backendCategoryDTO.setLastModifiedTime(new Date());

        return new ResponseInfo(backendCategoryNewService.updateBackendCategoryEdit(backendCategoryDTO));
    }

    /**
     * 同级移动类目编实体
     * @param backendCategoryDTO
     * @return
     */
    @RequestMapping("/siblingMove")
    @PermissionController(value = PermitType.PLATFORM ,operationName = "同级移动类目")
    public ResponseInfo siblingMove(@RequestBody BackendCategoryDTO backendCategoryDTO) {
        if (null == backendCategoryDTO) {
            return new ResponseInfo(GoodsExceptionEnum.CATEGORY_ID_NEED.getCode(), GoodsExceptionEnum.CATEGORY_ID_NEED.getDefaultMessage());
        }
        if (null == backendCategoryDTO.getSiblingId()) {
            return new ResponseInfo(GoodsExceptionEnum.NEAD_SIBLING_CATEGORY.getCode(), GoodsExceptionEnum.NEAD_SIBLING_CATEGORY.getDefaultMessage());
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        backendCategoryDTO.setLastModifierId(currentUserDto.getId());
        backendCategoryDTO.setLastModifiedTime(new Date());
        return new ResponseInfo(backendCategoryNewService.siblingMoveCategoryEdit(backendCategoryDTO));
    }

    /**
     * 自由移动
     * @param backendCategoryDTO
     * @return
     */
    @RequestMapping("/freeMove")
    @PermissionController(value = PermitType.PLATFORM ,operationName = "自由移动类目")
    public ResponseInfo freeMove(@RequestBody BackendCategoryDTO backendCategoryDTO) {
        if (null == backendCategoryDTO.getParentId() || null == backendCategoryDTO.getCategoryId()) {
            return paramError();
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        backendCategoryDTO.setLastModifierId(currentUserDto.getId());
        backendCategoryDTO.setLastModifiedTime(new Date());
        return new ResponseInfo(backendCategoryNewService.freeMoveCategoryEdit(backendCategoryDTO));
    }

    /**
     * 获取指定类目的子树
     * @param parentId 0-根类目
     * @return
     */
    @RequestMapping("/treeEdit/{parentId}")
    @PermissionController(value = PermitType.PLATFORM ,operationName = "获取类目编辑树")
    public ResponseInfo treeEdit(@PathVariable Long parentId) {
        CategoryTreeDTO categoryTreeDTO = new CategoryTreeDTO();
        categoryTreeDTO.setCategoryTree(backendCategoryNewService.getChildrenByCategoryEdit(parentId));
        return new ResponseInfo(categoryTreeDTO);
    }

    /**
     * 类目编辑实体概要
     * @param categoryId
     * @return
     */
    @RequestMapping("/editProfile/{categoryId}")
    @PermissionController(value = PermitType.PLATFORM ,operationName = "类目编辑概要")
    public ResponseInfo editProfile(@PathVariable Long categoryId) {
        return new ResponseInfo(backendCategoryNewService.profileEdit(categoryId));
    }
    /**
     * 获取指定类目的子树 正式数据
     * @param parentId 0-根类目
     * @return
     */
    @RequestMapping("/treeFormal/{parentId}")
    @PermissionController(value = PermitType.PLATFORM ,operationName = "获取类目正式树")
    public ResponseInfo treeFormal(@PathVariable Long parentId) {
        CategoryTreeDTO categoryTreeDTO = new CategoryTreeDTO();
        categoryTreeDTO.setCategoryTree(backendCategoryNewService.getChildrenByCategoryFormal(parentId));
        return new ResponseInfo(categoryTreeDTO);
    }

    /**
     * 类目编辑实体概要 正式数据
     * @param categoryId
     * @return
     */
    @RequestMapping("/formalProfile/{categoryId}")
    @PermissionController(value = PermitType.PLATFORM ,operationName = "类目正式概要")
    public ResponseInfo formalProfile(@PathVariable Long categoryId) {
        return new ResponseInfo(backendCategoryNewService.profileFormal(categoryId));
    }

    /**
     * 获取指定类目的儿子类目列表
     * @param categoryId
     * @return
     */
    @RequestMapping("/getSonCategoryEdit/{categoryId}")
    @PermissionController(value = PermitType.PLATFORM ,operationName = "编辑类目的儿子列表")
    public ResponseInfo getSonCategoryEdit(@PathVariable Long categoryId) {
        Map<String, Object> result = new HashMap<>(1);
        result.put("sonCategoryList", backendCategoryNewService.getSonCategoryEdit(categoryId));
        return new ResponseInfo(result);
    }

    /**
     * 同步类目树
     * @return
     */
    @RequestMapping("/syncCategoryTree")
    @PermissionController(value = PermitType.PLATFORM ,operationName = "同步类目树")
    public ResponseInfo syncCategoryTree() {

        return new ResponseInfo(backendCategoryNewService.syncCategoryTree());
    }
    /**
     * 同步类目树
     * @return
     */
    @RequestMapping("/syncCategoryData/{categoryId}")
    @PermissionController(value = PermitType.PLATFORM ,operationName = "同步类目树")
    public ResponseInfo syncCategoryData(@PathVariable Long categoryId) {

        return new ResponseInfo(backendCategoryNewService.syncCategoryDataByCategory(categoryId));
    }
}
