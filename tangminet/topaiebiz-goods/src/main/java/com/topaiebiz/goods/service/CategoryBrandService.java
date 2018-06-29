package com.topaiebiz.goods.service;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.brand.dto.BrandDto;
import com.topaiebiz.goods.dto.*;

import java.util.List;

/**
 * @description: 类目关联品牌服务：操作正式表和编辑表
 * @author: Jeff Chen
 * @date: created in 上午10:28 2018/5/18
 */
public interface CategoryBrandService {

    /**
     * Description 类目关联品牌列表分页
     * <p>
     * Author Hedda
     *
     * @param categoryIdDTO
     * @return
     */
    PageInfo<CategoryBrandEditDTO> getCategoryBrandList(CategoryIdDTO categoryIdDTO);

    /**
     * Description 类目绑定品牌
     * <p>
     * Author Hedda
     *
     * @param categoryBrandAddDTO
     * @return
     */
    boolean saveCategoryBrand(CategoryBrandAddDTO categoryBrandAddDTO);

    /**
     * Description 类目已选品牌
     * <p>
     * Author Hedda
     *
     * @param categoryId 类目ID
     * @return
     */
    List<BrandDto> getCategoryBrand(Long categoryId);

    /**
     * Description 类目没有选的品牌
     * <p>
     * Author Hedda
     *
     * @param categoryId
     * @return
     */
    List<BrandDto> getBrand(Long categoryId);

    /**
     * Description 类目绑定的品牌进行排序
     * <p>
     * Author Hedda
     *
     * @param categoryBrandSortNoDTO
     * @return
     */
    boolean editCategoryNameSortNo(CategoryBrandSortNoDTO categoryBrandSortNoDTO);

    /**
     * Description 类目关联品牌列表分页
     * <p>
     * Author Hedda
     *
     * @param categoryIdDTO
     * @return
     */
    PageInfo<CategoryBrandDTO> queryCategoryBrand(CategoryIdDTO categoryIdDTO);

    /**
     * Description 根据品牌id查询关联类目
     * <p>
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    List<String> getCategorys(Long brandId);
}
