package com.topaiebiz.goods.api;

import com.topaiebiz.goods.dto.Brand.BrandDTO;

import java.util.List;

/**
 * Created by hecaifeng on 2018/6/22.
 */
public interface BrandApi {

    /**
     * Description 根据brandId查询品牌信息
     * <p>
     * Author Hedda
     *
     * @param brandId 品牌id
     * @return
     */
    BrandDTO getBrand(Long brandId);

    /**
     * Description 根据brandIds批量查询品牌信息
     * <p>
     * Author Hedda
     *
     * @param brandIds 品牌id
     * @return
     */
    List<BrandDTO> getBrands(List<Long> brandIds);
}
