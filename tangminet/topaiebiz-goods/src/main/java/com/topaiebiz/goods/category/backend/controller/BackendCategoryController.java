package com.topaiebiz.goods.category.backend.controller;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.goods.api.BackendCategoryApi;
import com.topaiebiz.goods.category.backend.dto.BackendCategoryAdd;
import com.topaiebiz.goods.category.backend.dto.BackendCategoryDto;
import com.topaiebiz.goods.category.backend.dto.BackendMerchantCategoryDto;
import com.topaiebiz.goods.category.backend.service.BackendCategoryService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Description 商品后台类目控制层
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年8月24日 下午4:46:22
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping(value = "/goods/backendCategory",method = RequestMethod.POST)
public class BackendCategoryController {

    @Autowired
    private BackendCategoryService backendCategoryService;

    @Autowired
    private BackendCategoryApi backendCategoryApi;

    /**
     * Description 平台商品后台类目一，二，三级类目列表
     * <p>
     * Author Hedda
     *
     * @param backendCategoryDto 商品后台类目dto
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品后台类目列表")
    @RequestMapping(path = "/getListLevelBackendCategory")
    public ResponseInfo getListLevelBackendCategory(@RequestBody BackendCategoryDto backendCategoryDto) throws GlobalException {
        List<BackendCategoryDto> listLevelBackendCategory = backendCategoryService
                .getListLevelBackendCategory(backendCategoryDto);
        return new ResponseInfo(listLevelBackendCategory);
    }

    /**
     * Description 平台商品后台类目添加
     * <p>
     * Author Hedda
     *
     * @param backendCategoryDto 商品后台类目dto
     * @param result             错误结果
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品后台类目添加")
    @RequestMapping(path = "/addBackendCategory")
    public ResponseInfo addBackendCategory(@RequestBody @Valid BackendCategoryDto backendCategoryDto, BindingResult result)
            throws GlobalException {
        /** 对商品后台类目字段进行校验 */
        if (result.hasErrors()) {
            /** 初始化非法参数的提示信息。 */
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            /** 获取非法参数异常信息对象，并抛出异常。 */
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        return new ResponseInfo(backendCategoryService.saveBackendCategory(backendCategoryDto));
    }

    /**
     * Description 平台商品后台类目修改
     * <p>
     * Author Hedda
     *
     * @param backendCategoryDto 商品后台类目dto
     * @param result             错误结果
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品后台类目修改")
    @RequestMapping(path = "/editBackendCategory")
    public ResponseInfo editBackendCategory(@RequestBody @Valid BackendCategoryDto backendCategoryDto, BindingResult result)
            throws GlobalException {
        /** 对商品后台类目字段进行校验 */
        if (result.hasErrors()) {
            /** 初始化非法参数的提示信息。 */
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            /** 获取非法参数异常信息对象，并抛出异常。 */
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        return new ResponseInfo(backendCategoryService.modifyBackendCategory(backendCategoryDto));
    }

    /**
     * @Description 平台商品类目根据id进行查询
     *
     * @Author Hedda
     *
     * @param id 类目id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品后台类目回显")
    @RequestMapping(path = "/findBackendCategoryById/{id}")
    public ResponseInfo findBackendCategoryById(@PathVariable  Long id) throws GlobalException {
        return new ResponseInfo(backendCategoryService.findBackendCategoryById(id));
    }

    /**
     * Description 平台商品后台类目逻辑删除
     * <p>
     * Author Hedda
     *
     * @param id 商品后台类目id
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品后台类目删除")
    @RequestMapping(path = "/cancelBackendCategory/{id}")
    public ResponseInfo cancelBackendCategory(@PathVariable  Long id) throws GlobalException {
        return new ResponseInfo(backendCategoryService.removeBackendCategory(id));
    }

    /**
     * Description 商家 商品后台类目一，二，三级类目列表
     * <p>
     * Author Hedda
     *
     * @param backendCategoryDto 商品后台类目dto
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家后台三级类目列表")
    @RequestMapping(path = "/getMerchantListLevelBackendCategory")
    public ResponseInfo getMerchantListLevelBackendCategory(@RequestBody BackendCategoryDto backendCategoryDto)
            throws GlobalException {
        List<BackendCategoryDto> listLevelBackendCategory = backendCategoryService.getMerchantCategory(backendCategoryDto);
        return new ResponseInfo(listLevelBackendCategory);
    }

    /**
     * Description 平台 查看商品后台第三级类目
     * <p>
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品后台第三级列表")
    @RequestMapping(value = "/getThreeBackendCategory")
    public ResponseInfo getThreeBackendCategoryList() throws GlobalException {
        List<BackendCategoryDto> backendCategoryList = backendCategoryService.getThreeBackendCategoryList();
        return new ResponseInfo(backendCategoryList);

    }

    /**
     * Description  装修平台 查看商品后台第三级类目
     * <p>
     * Author Hedda*
     * @param pagePO
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "装修平台商品后台第三级列表")
    @RequestMapping(value = "/getDecorationThreeBackendCategory")
    public ResponseInfo getDecorationThreeBackendCategory(PagePO pagePO) throws GlobalException {
        PageInfo<BackendCategoryDto> backendCategoryList = backendCategoryService.getDecorationThreeBackendCategory(pagePO);
        return new ResponseInfo(backendCategoryList);

    }

    /**
     * Description  装修商家 查看商品后台第三级类目
     * <p>
     * Author Hedda*
     * @param pagePO
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "装修商家商品后台第三级列表")
    @RequestMapping(value = "/getDecorationMerchantThreeBackendCategory")
    public ResponseInfo getDecorationMerchantThreeBackendCategory(PagePO pagePO) throws GlobalException {
        PageInfo<BackendCategoryDto> backendCategoryList = backendCategoryService.getDecorationMerchantThreeBackendCategory(pagePO);
        return new ResponseInfo(backendCategoryList);

    }

    /**
     * Description 商家 查看商品后台第三级类目
     * <p>
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家后台第三级类目")
    @RequestMapping(value = "/getMerchantThreeBackendCategory")
    public ResponseInfo getMerchantThreeBackendCategoryList() throws GlobalException {
        List<BackendMerchantCategoryDto> backendMerchantCategoryList = backendCategoryService.getMerchantThreeCategory();
        return new ResponseInfo(backendMerchantCategoryList);

    }

    /**
     * Description 根据商家id添加商品后台类目
     *
     * Author Hedda
     *
     * @param backendCategoryAdd
     *            商家后台类目
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家添加类目")
    @RequestMapping(value = "/saveBackendCategoryDtoByBelongStore")
    public ResponseInfo saveBackendCategoryDtoByBelongStore(@RequestBody BackendCategoryAdd backendCategoryAdd) throws GlobalException {
        return new ResponseInfo(backendCategoryService.addBackendCategoryDtoByBelongStore(backendCategoryAdd));

    }

    /**
     * Description 根据商家 id删除对应的商家类目
     *
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家删除类目")
    @RequestMapping(path = "/cancelBackendCategoryByMerchantId")
    public ResponseInfo cancelBackendMerchantCategoryByStoreId(@RequestBody Long[] categoryId) throws GlobalException {
        return new ResponseInfo(backendCategoryService.removeBackendMerchantCategoryByStoreId(categoryId));
    }

    /**
     * Description 商家类目回显
     *
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家类目回显")
    @RequestMapping(path = "/getBackendCategoryByMerchantId")
    public ResponseInfo cancelBackendMerchantCategoryByStoreId(@RequestBody BackendCategoryAdd backendCategoryAdd) throws GlobalException {
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        if (merchantId == null) {
            merchantId = backendCategoryAdd.getMerchantId();
        }
        return new ResponseInfo(backendCategoryApi.getMerchantCategory(merchantId, backendCategoryAdd.getStatuses()));
    }

    /**
     * Description 修改商家类目状态
     * <p>
     * Author Hedda
     *
     * @param backendCategoryAdd 类目
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "修改商家类目状态")
    @RequestMapping(path = "/editBackendMerchantStatus")
    public ResponseInfo editBackendMerchantStatus(@RequestBody BackendCategoryAdd backendCategoryAdd) throws GlobalException {
        return new ResponseInfo(backendCategoryService.modifyBackendMerchanntStatus(backendCategoryAdd));
    }


}
