package com.topaiebiz.goods.brand.service;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.brand.dto.BrandDto;
import com.topaiebiz.goods.brand.dto.BrandQueryDto;

import java.util.List;

/**
 * Description 商品品牌接口
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年8月23日 下午4:14:59
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface BrandService {

    /**
     * Description 商品品牌添加
     * <p>
     * Author Hedda
     *
     * @param brandDto 商品品牌对象
     * @return
     * @throws GlobalException
     */
    Integer saveBrand(BrandDto brandDto) throws GlobalException;

    /**
     * Description 商品品牌修改
     * <p>
     * Author Hedda
     *
     * @param brandDto 商品品牌对象
     * @return
     * @throws GlobalException
     */
    Integer modifyBrand(BrandDto brandDto) throws GlobalException;

    /**
     * Description 根据id查询品牌
     * <p>
     * Author Hedda
     *
     * @param id 商品品牌ID
     * @return
     * @throws GlobalException
     */
    BrandDto findBrandById(Long id) throws GlobalException;

    /**
     * Description 批量逻辑删除商品品牌
     * <p>
     * Author Hedda
     *
     * @param id 商品品牌id
     * @return
     * @throws GlobalException
     */
    Integer removeBrands(Long[] id) throws GlobalException;

    /**
     * Description 商品品牌分页检索
     * <p>
     * Author Hedda
     *
     * @param brandQueryDto 商品品牌dto
     * @return
     */
    PageInfo<BrandDto> getBrandList(BrandQueryDto brandQueryDto);

    /**
     * Description 查看商品品牌列表
     * <p>
     * Author Hedda
     *
     * @return
     */
    List<BrandDto> getBrands();

    /**
     * Description app端商品品牌列表
     * <p>
     * Author Hedda
     *
     * @return ResponseInfo
     */
    List<BrandDto> getAppBrandList();

    /**
     * Description 品牌名称联想查询
     * <p>
     * Author Hedda
     *
     * @param name
     * @return
     * @throws GlobalException
     */
    List<String> queryName(String name);
}
