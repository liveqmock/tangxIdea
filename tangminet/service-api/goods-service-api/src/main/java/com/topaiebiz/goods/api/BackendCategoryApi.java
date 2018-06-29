package com.topaiebiz.goods.api;

import com.topaiebiz.goods.dto.category.backend.BackendCategoryDTO;
import com.topaiebiz.goods.dto.category.backend.BackendCategoryStatusDTO;
import com.topaiebiz.goods.dto.category.backend.BackendCategorysDTO;

import java.util.List;

/**
 * Created by dell on 2018/1/4.
 */
public interface BackendCategoryApi {

    /**
     * Description 根据后台类目id查询商品后台类目信息
     * <p>
     * Author Hedda
     *
     * @param backendCategoryId 商品类目id
     * @return
     */
    BackendCategoryDTO getBackendCategoryById(Long backendCategoryId);

    /**
     * Description 根据后台类目ids查询商品后台类目信息
     * <p>
     * Author Hedda
     *
     * @param backendCategoryIds 商品类目id
     * @return
     */
    List<BackendCategoryDTO> getBackendCategorys(List<Long> backendCategoryIds);

    /**
     * Description 添加商家商品后台类目
     * <p>
     * Author Hedda
     *
     * @param backendCategoryIds
     * @return
     */
    Integer addBackendCategoryDtoByStoreId(Long merchantId, Long[] backendCategoryIds, Integer status);

    /**
     * Description 查询商家类目
     *
     * @param merchantId 商家id
     * @return
     */
    List<BackendCategorysDTO> getMerchantCategory(Long merchantId, List<Integer> statuses);


    /**
     * Description 根据商家id查询对应类目（加状态）
     * @param merchantId
     * @param status
     * @return
     */
    List<Long> getMerchantcategoryIds(Long merchantId,Integer status);

    /**
     * 根据商家id查询对应类目（加状态）
     * @param merchantId
     * @param status
     * @return
     */
    List<Long> getMerchantcategoryListIds(Long merchantId,List<Integer> status);

    /**
     * Description 查询商家类目
     *
     * @param merchantId 商家id
     * @return
     */
    List<BackendCategorysDTO> getMerchantBacCategory(Long merchantId);

    /**
     * Description 删除商家类目
     * <p>
     * Author Hedda
     *
     * @param merchantId  商家id
     * @param categoryIds 类目id
     * @return
     */
    Integer removeBackendMerchantCategoryByMerchantId(Long merchantId, Long[] categoryIds);

    /**
     * Description 删除商家类目
     * <p>
     * Author Hedda
     *
     * @param merchantId 商家id
     * @return
     */
    Integer removeBackendMerchantCategorys(Long merchantId);


    /**
     * Description 删除商家类目
     * <p>
     * Author Hedda
     *
     * @param merchantId 商家id
     * @return
     */
    Integer removeBackendMerchantCategory(Long merchantId, Integer status);

    /**
     * * Description 修改商家类目状态为审核通过
     * <p>
     * Author Hedda
     *
     * @param backendCategoryStatusDTO
     * @return
     */
    boolean modifyBackendMerchanntStatus(BackendCategoryStatusDTO backendCategoryStatusDTO);

    /**
     * Description  查询该商家是否有不通过类目
     * <p>
     * Author Hedda
     *
     * @param merchantId 商家id
     * @return
     */
    boolean getBackendMerchant( Long merchantId);

    /**
     * Description 对销售属性进行拼接
     * <p>
     * Author Hedda
     *
     * @param saleFieldValue 销售属性
     * @return
     */
    String jointSaleFieldValue(String saleFieldValue);


}
