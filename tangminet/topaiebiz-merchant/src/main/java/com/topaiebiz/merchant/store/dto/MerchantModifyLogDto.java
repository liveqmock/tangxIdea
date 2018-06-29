package com.topaiebiz.merchant.store.dto;

import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.goods.dto.category.backend.BackendCategoryStatusDTO;
import com.topaiebiz.goods.dto.category.backend.BackendMerchantCategoryDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/4/9 0009 下午 12:11
 */
@Data
public class MerchantModifyLogDto extends PagePO implements Serializable {

    /**
     * 商家id
     */
    private Long merchantId;
    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 审核结果
     */
    private Integer auditResult;

    /**
     * 审核人
     */
    private String examineAuditor;

    /**
     * 审核时间
     */
    private Date examineTime;

    /**
     * 再次审核详情
     */
    private List<MerchantModifyDetailDto> modifyDetailDtoList;

    /**
     * 再次审核id
     */
    private Long modifyId;

    /**
     * 类目
     */
    private Long[] ids;
    private List<BackendCategoryStatusDTO> backendCategoryStatusdtos;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 修改的值
     */
    private String modifiedValue;

    /**
     * 不通过原因
     */
    private String noPassReason;

    /**
     * 类目状态
     */
    private Integer categoryStatus;

    /**
     *地段审核状态
     */
    private Integer modifyStatus;

}
