package com.topaiebiz.goods.controller;

/**
 * Created by hecaifeng on 2018/5/21.
 */

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.common.BindResultUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.dto.*;
import com.topaiebiz.goods.service.CategoryBrandService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 类目与品牌关联控制器
 */
@RestController
@RequestMapping("/goods/categoryBrand")
public class CategoryBrandController extends AbstractController {

    @Autowired
    private CategoryBrandService categoryBrandService;

    /**
     * Description 类目关联品牌列表分页(操作)
     * <p>
     * Author Hedda
     *
     * @param categoryIdDTO
     * @return ResponseInfo
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "类目关联品牌列表分页")
    @RequestMapping(path = "/getCategoryBrandList")
    public ResponseInfo getCategoryBrandList(@RequestBody CategoryIdDTO categoryIdDTO)
            throws GlobalException {
        PageInfo<CategoryBrandEditDTO> list = categoryBrandService.getCategoryBrandList(categoryIdDTO);
        return new ResponseInfo(list);
    }

    /**
     * Description 类目关联品牌列表分页(生产)
     * <p>
     * Author Hedda
     *
     * @param categoryIdDTO
     * @return ResponseInfo
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "类目关联品牌列表分页")
    @RequestMapping(path = "/queryCategoryBrand")
    public ResponseInfo queryCategoryBrand(@RequestBody CategoryIdDTO categoryIdDTO)
            throws GlobalException {
        PageInfo<CategoryBrandDTO> list = categoryBrandService.queryCategoryBrand(categoryIdDTO);
        return new ResponseInfo(list);
    }

    /**
     * Description 类目绑定品牌(操作)
     * <p>
     * Author Hedda
     *
     * @param categoryBrandAddDTO
     * @param result
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "类目绑定品牌")
    @RequestMapping(path = "/addCategoryBrand")
    public ResponseInfo addCategoryBrand(@RequestBody @Valid CategoryBrandAddDTO categoryBrandAddDTO, BindingResult result) throws GlobalException {
        BindResultUtil.dealBindResult(result);
        return new ResponseInfo(categoryBrandService.saveCategoryBrand(categoryBrandAddDTO));
    }

    /**
     * Description 类目已选品牌(操作)
     * <p>
     * Author Hedda
     *
     * @param categoryId 类目ID
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "类目已选品牌")
    @RequestMapping(path = "/getCategoryBrand/{categoryId}")
    public ResponseInfo getCategoryBrand(@PathVariable Long categoryId) throws GlobalException {
        return new ResponseInfo(categoryBrandService.getCategoryBrand(categoryId));
    }

    /**
     * Description 类目没有选的品牌(操作)
     * <p>
     * Author Hedda
     *
     * @param categoryId 类目ID
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "类目没有选的品牌")
    @RequestMapping(path = "/getBrand/{categoryId}")
    public ResponseInfo getBrand(@PathVariable Long categoryId) throws GlobalException {
        return new ResponseInfo(categoryBrandService.getBrand(categoryId));
    }

    /**
     * Description 类目绑定的品牌进行排序
     * <p>
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "类目绑定的品牌进行排序")
    @RequestMapping(path = "/editCategoryNameSortNo")
    public ResponseInfo editCategoryNameSortNo(@RequestBody CategoryBrandSortNoDTO categoryBrandSortNoDTO) throws GlobalException {
        return new ResponseInfo(categoryBrandService.editCategoryNameSortNo(categoryBrandSortNoDTO));
    }

    /**
     * Description 根据品牌id查询关联类目
     * <p>
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "根据品牌id查询关联类目")
    @RequestMapping(path = "/getCategorys/{brandId}")
    public ResponseInfo getCategorys(@PathVariable Long brandId) throws GlobalException {
        return new ResponseInfo(categoryBrandService.getCategorys(brandId));
    }


}
