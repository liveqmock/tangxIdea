package com.topaiebiz.goods.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.dto.CategoryBrandDTO;
import com.topaiebiz.goods.dto.CategoryBrandEditDTO;
import com.topaiebiz.goods.dto.CategoryIdDTO;
import com.topaiebiz.goods.entity.CategoryBrandEdit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 后台类目关联品牌编辑表 Mapper 接口
 * </p>
 *
 * @author MMG123
 * @since 2018-05-18
 */
public interface CategoryBrandEditDao extends BaseMapper<CategoryBrandEdit> {

    /**
     * Description 类目关联品牌列表分页(操作)
     * <p>
     * Author Hedda
     *
     * @param page
     * @param categoryIdDTO
     * @return
     */
    List<CategoryBrandEditDTO> selectCategortBrand(Page<CategoryBrandEditDTO> page, CategoryIdDTO categoryIdDTO);

    /**
     * Description 类目关联品牌列表分页(生产)
     * <p>
     * Author Hedda
     *
     * @param categoryIdDTO
     * @return ResponseInfo
     * @throws GlobalException
     */
    List<CategoryBrandDTO> selectCategortBrands(Page<CategoryBrandDTO> page, CategoryIdDTO categoryIdDTO);

    /**
     * Description 根据类目id查询品牌id
     * <p>
     * Author Hedda
     *
     * @return
     */
    CategoryBrandEdit selectCategortBrandEdit(@Param("categoryId") Long categoryId, @Param("brandId") Long brandId);
}
