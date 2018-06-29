package com.topaiebiz.merchant.info.service;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.merchant.enter.dto.StoreInfoDto;
import com.topaiebiz.merchant.grade.dto.MerchantGradeDto;
import com.topaiebiz.merchant.info.dto.*;

/**
 * Description: 商家管理业务层接口
 * 
 * Author : Anthony
 * 
 * Date :2017年9月27日 下午1:26:24
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface MerchantInfoService {

	/**
	 * Description： 添加商家信息
	 * 
	 * Author: Anthony
	 * 
	 * param : entity 商家信息实体类对象
	 * 
	 * return : 执行成功或失败的提示信息
	 */
	Integer saveMerchantInfo(MerchantInfoDto merchantInfoDto) throws GlobalException;

	/**
	 * Description：冻结/解冻商家
	 * 
	 * Author: Anthony
	 * 
	 * @param merchantFrozenDto
	 * @return 返回结果参数集
	 */
	Integer removeMerchantInfoById(MerchantFrozenDto merchantFrozenDto);

	/**
	 * Description：编辑(修改)商家信息。
	 * 
	 * Author: Anthony
	 * 
	 * param : dto 商家信息dto对象
	 * 
	 * return : 返回执行结果成功参数
	 */
	Integer modifyMerchantInfoById(MerchantInfoDto dto) throws GlobalException;

	/**
	 * Description： 商家信息列表分页检索
	 * 
	 * Author: Anthony
	 * 
	 * param : page 分页参数
	 * 
	 * param : merchantInfoDto 商家信息Dto
	 * 
	 * return : list 商家信息列表数据
	 */
	PageInfo<MerchantInfoListDto> getMerchantInfoList(PagePO pagePO, MerchantInfoListDto merchantInfoListDto);

	/**
	 * Description：查看商家详情(根据Id查看商家详情数据回显)。
	 * 
	 * Author: Anthony
	 * 
	 * param : id 商家信息id
	 * 
	 * return : 商家详情 dto对象
	 */
	MerchantInfoDto getMerchantParticularsById(Long id);

	/**
	 * Description：查看门店信息(根据Id查看门店信息数据回显)。
	 * 
	 * Author: Anthony
	 * 
	 * param : id 门店信息id
	 * 
	 * return : 门店信息实体类对象
	 */
	MerchantInfoDto getStoreInfoById(Long merchantId);

	/**
	 * Description： 商户类型下拉框展示
	 * 
	 * Author: Anthony
	 * 
	 * return : MerchantType 商户类型的名称和对应的id
	 */
	List<MerchantInfoDto> getMerchantType();

	/**
	 * Description： 获取店铺id和name
	 * 
	 * Author: Anthony
	 * 
	 * return : StoreInfoByName 店铺的name和id
	 * 
	 * @param dto
	 */
	List<StoreInfoDto> getStoreInfoByName(MerchantInfoDto dto);

	/**
	 * Description：商家等级设置
	 * 
	 * Author: Anthony
	 * 
	 * param : id 商家id
	 * 
	 * param : merchantGradeId 等级id
	 * 
	 * return : 返回成功结果参数
	 */
	Integer modifyMerchantInfoByMerchantGradeId(MerchantInfoGradeDto merchantInfoGradeDto);


	/**
	 * 添加商家退货信息
	 * @param merchantReturnDto
	 * @return
	 */
	//Integer insertMerchantReturnInfo(MerchantReturnDto merchantReturnDto);

	/**
	 * Description： 商户类型下拉框
	 * 
	 * Author: Anthony
	 * 
	 * return 商家信息Dto
	 * 
	 * throws GlobalException
	 */
	List<MerchantInfoDto> getMerchantInfoByMerchantType();
    
	/**
	 * Description： 所属商家。
	 * 
	 * Author: Anthony   
	 * 
	 * @return
	 */
	List<MerchantInfoDto> getMerchantInfoByName(MerchantInfoDto merchantInfoDto);
    
	
	/**
	 * Description：所属店铺
	 * 
	 * Author: Anthony   
	 * 
	 * @return
	 */
	List<StoreInfoDto> getStoreInfoList(StoreInfoDto storeInfoDto);

	/**
	 * Description： 根据登录用户查询所有店铺
	 * 
	 * Author Aaron.Xue  
	 * 
	 * @return
	 */
	StoreInfoDetailDto getAllStoreByLoginName();

	/**
	 * 商家端修改商家名称与商家
	 * @param merchantInfoDto
	 * @return
	 */
	Integer modiMerchantInfoById(MerchantInfoDto merchantInfoDto);

	/**
	 * 再次申请类目
	 * @return
	 */
	//MerchantInfoDto getBackendCategorysList(MerchantInfoDto merchantInfoDto);

	

}
