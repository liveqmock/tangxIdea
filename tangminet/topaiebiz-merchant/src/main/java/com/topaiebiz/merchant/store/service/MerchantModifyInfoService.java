package com.topaiebiz.merchant.store.service;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.merchant.store.dto.MerchantModifyInfosDto;
import com.topaiebiz.merchant.store.dto.MerchantModifyLogDto;

import java.util.List;

/**
 * @Aurthor:zhaoxupeng
 * @Description:商家修改信息service
 * @Date 2018/3/30 0030 上午 10:01
 */
public interface MerchantModifyInfoService {

    /***
     * 重新审核信息列表
     * @param pagePO
     * @param merchantModifyInfosDto
     * @return
     */
    PageInfo<MerchantModifyInfosDto> getMerchantModifyInfoList(PagePO pagePO, MerchantModifyInfosDto merchantModifyInfosDto);

    /**
     * 添加重新修改的信息
     *
     * @param merchantModifyLogDto
     */
    void saveMerchantModifyInfo(MerchantModifyLogDto merchantModifyLogDto);


    /**
     * 平台审核未通过
     *
     * @param merchantModifyLogDto
     */
    void saveMerchantModifyInfoExmine(MerchantModifyLogDto merchantModifyLogDto);


    /**
     * 审核通过
     *
     * @param merchantModifyLogDto
     */
    void saveExamineAdoptInfo(MerchantModifyLogDto merchantModifyLogDto);

    /**
     * 判断是否待审核
     *
     * @param merchantModifyLogDto
     * @return
     */
    Boolean judgeMerchantModifyStatus(MerchantModifyLogDto merchantModifyLogDto);

    /**
     * 根据商家id和再次审核id查询对应修改的信息
     */
    List<MerchantModifyLogDto> getMerchantModifysInfoList(MerchantModifyLogDto merchantModifyLogDto);

    /**
     * 商家端根据商家id查询最近的审核信息
     *
     * @param merchantModifyLogDto
     * @return
     */
    List<MerchantModifyLogDto> getMerchantModifysInfoDeail(MerchantModifyLogDto merchantModifyLogDto);

}
