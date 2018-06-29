package com.topaiebiz.merchant.store.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.merchant.store.dto.MerchantModifyInfosDto;
import com.topaiebiz.merchant.store.dto.MerchantModifyLogDto;
import com.topaiebiz.merchant.store.entity.MerchantModifyInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Aurthor:zhaoxupeng
 * @Description:商家修改dao
 * @Date 2018/3/30 0030 上午 10:03
 */
@Mapper
public interface MerchantModifyInfoDao  extends BaseDao<MerchantModifyInfoEntity> {

    /**
     *重新审核列表
     * @param page
     * @param merchantModifyInfosDto
     * @return
     */
    List<MerchantModifyInfosDto> selectMerchantModifyInfoList(Page<MerchantModifyInfosDto> page, MerchantModifyInfosDto merchantModifyInfosDto);


    /**
     * 查询商家最近再次审核修改的信息
     * @param merchantModifyLogDto
     * @return
     */
    List<MerchantModifyLogDto> selectMerchantModifysInfoDeail(MerchantModifyLogDto merchantModifyLogDto);
}
