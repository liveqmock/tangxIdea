package com.topaiebiz.goods.category.backend.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.goods.category.backend.dto.BackendCategoryAttrDto;
import com.topaiebiz.goods.category.backend.dto.ItemBackendCategoryAttrDto;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryAttrEntity;
import com.topaiebiz.goods.category.backend.service.BackendCategoryAttrService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by dell on 2018/1/10.
 */
@RestController
@RequestMapping(value = "/goods/backendCategoryAttr",method = RequestMethod.POST)
public class BackendCategoryAttrController {

    @Autowired
    private BackendCategoryAttrService backendCategoryAttrService;


    /**
     * Description 商品三级类目中所对应的规格属性
     * <p>
     * Author Hedda
     *
     * @param itemBackendCategoryAttrDto 商品后台类目id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品后台类目属性列表")
    @RequestMapping(path = "/getListBackendCategoryAttr")
    public ResponseInfo getListBackendCategoryAttr(@RequestBody ItemBackendCategoryAttrDto itemBackendCategoryAttrDto) throws GlobalException {
        List<BackendCategoryAttrEntity> listBackendCategoryAttr = backendCategoryAttrService
                .getListBackendCategoryAttr(itemBackendCategoryAttrDto);
        return new ResponseInfo(listBackendCategoryAttr);
    }

    /**
     * Description 商品模板三级类目中所对应的规格属性
     * <p>
     * Author Hedda
     *
     * @param itemBackendCategoryAttrDto 商品后台类目id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品模板后台类目属性列表")
    @RequestMapping(path = "/getSpuListBackendCategoryAttr")
    public ResponseInfo getSpuListBackendCategoryAttr(@RequestBody ItemBackendCategoryAttrDto itemBackendCategoryAttrDto) throws GlobalException {
        List<BackendCategoryAttrEntity> listBackendCategoryAttr = backendCategoryAttrService
                .getSpuListBackendCategoryAttr(itemBackendCategoryAttrDto);
        return new ResponseInfo(listBackendCategoryAttr);
    }

    /**
     * @param backendCategoryAttrDto 商品后台类目规格属性
     * @return
     * @throws GlobalException
     * @Description 平台端商品后台类目属性修改
     * @Author Hedda
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品后台类目属性修改")
    @RequestMapping(path = "/editBackendCategoryAttr")
    public ResponseInfo editBackendCategoryAttr(@RequestBody @Valid BackendCategoryAttrDto backendCategoryAttrDto,
                                                BindingResult result) throws GlobalException {
        /** 对商品后台类目属性字段进行校验 */
        if (result.hasErrors()) {
            /** 初始化非法参数的提示信息。 */
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            /** 获取非法参数异常信息对象，并抛出异常。 */
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        /** 对商品后台类目进行修改 */
        return new ResponseInfo(backendCategoryAttrService.modifyBackendCategoryAttr(backendCategoryAttrDto));
    }

    /**
     * @Description 商品后台类目中规格属性逻辑删除
     *
     * @Author Hedda
     *
     * @param id 商品后台类目规格属性id
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品后台类目属性删除")
    @RequestMapping(path = "/cancelBackendCategoryAttr/{id}")
    public ResponseInfo cancelBackendCategoryAttr(@PathVariable Long id) throws GlobalException {
        return new ResponseInfo(backendCategoryAttrService.removeBackendCategoryAttr(id));
    }

    /**
     * Description 平台端商品后台类目中规格属性添加
     * <p>
     * Author Hedda
     *
     * @param backendCategoryAttrDto 商品后台类目规格属性
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品后台类目属性添加")
    @RequestMapping(path = "/addBackendCategoryAttr")
    public ResponseInfo addBackendCategoryAttr(@RequestBody @Valid BackendCategoryAttrDto backendCategoryAttrDto,
                                               BindingResult result) throws GlobalException {
        /** 对商品后台类目字段进行校验 */
        if (result.hasErrors()) {
            /** 初始化非法参数的提示信息。 */
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            /** 获取非法参数异常信息对象，并抛出异常。 */
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        BackendCategoryAttrEntity backendCategoryAttr = new BackendCategoryAttrEntity();
        BeanUtils.copyProperties(backendCategoryAttrDto, backendCategoryAttr);
        return new ResponseInfo(backendCategoryAttrService.saveBackendCategoryAttr(backendCategoryAttr));
    }

    /**
     * Description 平台端商品模板类目中规格属性添加
     * <p>
     * Author Hedda
     *
     * @param backendCategoryAttrDto 商品后台类目规格属性
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台端商品模板类目中规格属性添加")
    @RequestMapping(path = "/addGoodsSpuBackendCategoryAttr")
    public ResponseInfo addGoodsSpuBackendCategoryAttr(@RequestBody @Valid BackendCategoryAttrDto backendCategoryAttrDto,
                                               BindingResult result) throws GlobalException {
        /** 对商品后台类目字段进行校验 */
        if (result.hasErrors()) {
            /** 初始化非法参数的提示信息。 */
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            /** 获取非法参数异常信息对象，并抛出异常。 */
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        return new ResponseInfo(backendCategoryAttrService.saveGoodsSpuBackendCategoryAttr(backendCategoryAttrDto));
    }

    /**
     * Description 对商品后台类目属性进行升降序操作
     * <p>
     * Author Hedda
     *
     * @param backendCategoryAttrDto 商品后台类目属性dto
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品后台类目属性升降序")
    @RequestMapping(path = "/getBackendCategoryAttrBySortNo")
    public ResponseInfo editBackendCategoryAttrBySortNo(
            @RequestBody List<BackendCategoryAttrDto> backendCategoryAttrDto) throws GlobalException {
        return new ResponseInfo(backendCategoryAttrService.modifyBackendCategoryAttrBySortNo(backendCategoryAttrDto));
    }

    /**
     * @Description 商品后台类目规格属性根据id进行查询
     *
     * @Author Hedda
     *
     * @param id 商品后台类目规格属性主键id
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商品后台类目属性回显")
    @RequestMapping(path = "/findBackendCategoryAttrById/{id}")
    public ResponseInfo findBackendCategoryAttrById(@PathVariable Long id) throws GlobalException {
        return new ResponseInfo(backendCategoryAttrService.findBackendCategoryAttrById(id));
    }
}
