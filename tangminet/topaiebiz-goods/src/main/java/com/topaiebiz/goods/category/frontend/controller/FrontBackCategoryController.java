package com.topaiebiz.goods.category.frontend.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.goods.category.frontend.dto.FrontBackCategoryDto;
import com.topaiebiz.goods.category.frontend.entity.FrontBackCategoryEntity;
import com.topaiebiz.goods.category.frontend.service.FrontBackCategoryService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by dell on 2018/1/10.
 */
@RestController
@RequestMapping(value = "/goods/frontBackCategory",method = RequestMethod.POST)
public class FrontBackCategoryController {

    @Autowired
    private FrontBackCategoryService frontBackCategoryService;

    /**
     * Description 绑定前后台类目
     *
     * Author Hedda
     *
     * @param frontBackCategoryDto
     *            前后台类目对照表
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "绑定前后台类目")
    @RequestMapping(path = "/addFrontBackCategory")
    public ResponseInfo addFrontBackCategory(@RequestBody @Valid FrontBackCategoryDto frontBackCategoryDto,
                                             BindingResult result) throws GlobalException {
        /** 对商品前台类目属性字段进行校验 */
        if (result.hasErrors()) {
            /** 初始化非法参数的提示信息。 */
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            /** 获取非法参数异常信息对象，并抛出异常。 */
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        return new ResponseInfo(frontBackCategoryService.saveFrontBackCategory(frontBackCategoryDto));
    }

    /**
     * Description 商品前后台类目绑定类目逻辑删除
     *
     * Author Hedda
     *
     * @param id
     *            商品前后台关联表id
     * @return b
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "绑定类目删除")
    @RequestMapping(path = "/cancelFrontBackCategory/{id}")
    public ResponseInfo cancelFrontBackCategory(@PathVariable  Long id) throws GlobalException {
        return new ResponseInfo( frontBackCategoryService.removeFrontBackCategory(id));
    }
}
