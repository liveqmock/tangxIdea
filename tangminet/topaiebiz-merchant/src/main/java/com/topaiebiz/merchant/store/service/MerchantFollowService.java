package com.topaiebiz.merchant.store.service;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.merchant.enter.dto.MerchantQualificationDto;
import com.topaiebiz.merchant.grade.dto.MerchantGradeDto;
import com.topaiebiz.merchant.store.dto.MerchantFollowDto;

import java.util.List;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/19 0019 上午 10:27
 */
public interface MerchantFollowService {

    /**
     * Description： 商家关注信息列表分页检索。
     * @param pagePO
     *            分页参数
     * @param merchantFollowDto
     *            商家店铺关注信息Dto
     * @return
     */
    PageInfo<MerchantFollowDto> getMerchantFollowList(PagePO pagePO, MerchantFollowDto merchantFollowDto);

    /**
     * Description： 商家店铺关注列表。
     * @param merchantFollowDto
     * @return
     */
    PageInfo<MerchantFollowDto> selectMerchantFollowList(PagePO pagePO,MerchantFollowDto merchantFollowDto);


    /**
     * Description： 取消/删除商家店铺关注信息
     * @param merchantFollowDto
     * @return
     */
    Integer removeMerchantFollowById(MerchantFollowDto merchantFollowDto) throws GlobalException;

    /**
     * Description：添加商家店铺关注信息
     * @param merchantFollowDto
     * @return
     */
    Integer saveMerchantFollow(MerchantFollowDto merchantFollowDto) throws GlobalException;

    /**
     * 根据店铺查询店铺信息
     * @param StoreId
     * @return
     */
    MerchantFollowDto selectMerchantFollowDetails(Long StoreId);

    /**
     * 判断是否关注
     * @param merchantFollowDto
     * @return
     */
    Boolean checkMerchantFollowById(MerchantFollowDto merchantFollowDto);
}
