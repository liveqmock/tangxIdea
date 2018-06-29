package com.topaiebiz.goods.category.frontend.controller;

import java.util.List;

import javax.validation.Valid;

import com.topaiebiz.goods.category.backend.dto.BackendCategorysDto;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.goods.category.backend.dto.BackendCategoryDto;
import com.topaiebiz.goods.category.frontend.dto.FrontendCategoryDto;
import com.topaiebiz.goods.category.frontend.service.FrontendCategoryService;

/**
 * Description 商品前台类目控制层
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年8月25日 下午3:13:52
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@RestController
@RequestMapping(value = "/goods/frontendCategory",method = RequestMethod.POST)
public class FrontendCategoryController {

    @Autowired
    private FrontendCategoryService frontendCategoryService;

    /**
     * Description 平台端商品前台类目一,二，三级类目列表
     * <p>
     * Author Hedda
     *
     * @param frontendCategoryDto 商品前台类目dto
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品前台类目一二三级列表")
    @RequestMapping(path = "/getFrontendCategoryList")
    public ResponseInfo getFrontendCategoryList(@RequestBody FrontendCategoryDto frontendCategoryDto) throws GlobalException {
        List<FrontendCategoryDto> listFrontendCategoryEntity = frontendCategoryService
                .getFrontendCategoryList(frontendCategoryDto);
        return new ResponseInfo(listFrontendCategoryEntity);
    }

    /**
     * Description 平台端商品前台类目添加
     * <p>
     * Author Hedda
     *
     * @param frontendCategoryDto 商品前台类目
     * @param result              错误结果
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品前台类目添加")
    @RequestMapping(path = "/addFrontendCategory")
    public ResponseInfo addFrontendCategory(@RequestBody @Valid FrontendCategoryDto frontendCategoryDto, BindingResult result)
            throws GlobalException {
        /** 对商品前台类目属性字段进行校验 */
        if (result.hasErrors()) {
            /** 初始化非法参数的提示信息。 */
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            /** 获取非法参数异常信息对象，并抛出异常。 */
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        return new ResponseInfo(frontendCategoryService.saveFrontendCategory(frontendCategoryDto));
    }

    /**
     * Description 平台端商品前台类目修改
     * <p>
     * Author Hedda
     *
     * @param frontendCategoryDto 商品前台类目
     * @param result              错误结果
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品前台类目修改")
    @RequestMapping(path = "/editFrontendCategory")
    public ResponseInfo editFrontendCategory(@RequestBody @Valid FrontendCategoryDto frontendCategoryDto, BindingResult result)
            throws GlobalException {
        /** 对商品前台类目属性字段进行校验 */
        if (result.hasErrors()) {
            /** 初始化非法参数的提示信息。 */
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            /** 获取非法参数异常信息对象，并抛出异常。 */
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        return new ResponseInfo(frontendCategoryService.modifyFrontendCategory(frontendCategoryDto));
    }

    /**
     * Description 平台端商品前台类目逻辑删除
     * <p>
     * Author Hedda
     *
     * @param id 商品前台类目id
     * @return b
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品前台类目删除")
    @RequestMapping(path = "/cancelFrontendCategory/{id}")
    public ResponseInfo removeFrontendCategory(@PathVariable Long id) throws GlobalException {
        return new ResponseInfo(frontendCategoryService.removeFrontendCategory(id));
    }

    /**
     * Description 根据id查询商品前台类目
     * <p>
     * Author Hedda
     *
     * @param id 商品前台类目id
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品前台类目回显")
    @RequestMapping(value = "/getFrontendCategoryById/{id}")
    public ResponseInfo getFrontendCategoryById(@PathVariable Long id) throws GlobalException {
        return new ResponseInfo(frontendCategoryService.getFrontendCategoryById(id));
    }

    /**
     * Description 查询后台类目列表
     * <p>
     * Author Hedda
     *
     * @param frontId 商品前台类目id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品绑定后台类目列表")
    @RequestMapping(value = "/getBackendCategoryList/{frontId}")
    public ResponseInfo getBackendCategoryList(@PathVariable  Long frontId) throws GlobalException {
        List<BackendCategorysDto> backendCategoryList = frontendCategoryService.getBackendCategoryList(frontId);
        return new ResponseInfo(backendCategoryList);
    }

    /**
     * Description 给三级类目添加图片
     * <p>
     * Author Hedda
     *
     * @param frontendCategoryDto 商品前台类目
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品前台类目添加图片")
    @RequestMapping(value = "/addFrontendCategoryImage")
    public ResponseInfo addFrontendCategoryImage(@RequestBody FrontendCategoryDto frontendCategoryDto) throws GlobalException {
        return new ResponseInfo(frontendCategoryService.addFrontendCategoryById(frontendCategoryDto));
    }

    /**
     * Description 给三级类目删除图片
     * <p>
     * Author Hedda
     *
     * @param id 商品后台类目id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品前台类目删除图片")
    @RequestMapping(value = "/cancelFrontendCategoryImage/{id}")
    public ResponseInfo cancelFrontendCategoryImage(@PathVariable Long id) throws GlobalException {
        return new ResponseInfo(frontendCategoryService.cancelFrontendCategoryImage(id));
    }

}
