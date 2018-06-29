package com.topaiebiz.merchant.store.controller;

import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.goods.api.BackendCategoryApi;
import com.topaiebiz.goods.dto.category.backend.BackendCategorysDTO;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.merchant.enter.dto.StoreInfoDto;
import com.topaiebiz.merchant.enter.service.MerchantEnterService;
import com.topaiebiz.merchant.info.dto.MerchantInfoDto;
import com.topaiebiz.merchant.info.dto.StoreInfoDetailDto;
import com.topaiebiz.merchant.info.exception.MerchantInfoException;
import com.topaiebiz.merchant.info.service.MerchantInfoService;
import com.topaiebiz.merchant.store.dto.MerchantModifyInfosDto;
import com.topaiebiz.merchant.store.dto.MerchantModifyLogDto;
import com.topaiebiz.merchant.store.service.MerchantModifyInfoService;
import com.topaiebiz.merchant.store.service.StoreInfoService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Description: 店铺管理
 * <p>
 * Author : Aaron
 * <p>
 * Date :2017年11月24日 下午1:25:19
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping(path = "/merchant/storeInfo", method = RequestMethod.POST)
public class StoreInfoController {

    @Autowired
    private MerchantInfoService merchantInfoService;

    @Autowired
    private MerchantEnterService merchantEnterService;

    @Autowired
    private StoreInfoService storeInfoService;

    @Autowired
    private BackendCategoryApi backendCategoryApi;

    @Autowired
    private MerchantModifyInfoService merchantModifyInfoService;


    /**
     * Description：商家登录回显所有店铺
     * <p>
     * Author: Anthony
     * <p>
     * param : id 商家信息id
     * <p>
     * return : 商家详情 dto对象
     * <p>
     * throws : GlobalException 全局异常类
     */
    @RequestMapping(path = "/getAllStoreByLoginName")
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家登录回显所有店铺")
    public ResponseInfo getAllStoreByLoginName() throws GlobalException {
        StoreInfoDetailDto storeInfo = merchantInfoService.getAllStoreByLoginName();
        return new ResponseInfo(storeInfo);
    }

    /**
     * 店铺信息列表搜索
     *
     * @param storeInfoDto
     * @return
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "店铺信息列表")
    @RequestMapping(path = "/getStoreInfoList")
    public ResponseInfo getStoreInfoList(@RequestBody StoreInfoDto storeInfoDto) {
        int pageNo = storeInfoDto.getPageNo();
        int pageSize = storeInfoDto.getPageSize();
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(pageNo);
        pagePO.setPageSize(pageSize);
        return new ResponseInfo(storeInfoService.getStoreInfoList(pagePO, storeInfoDto));
    }


    @MemberLogin
    @RequestMapping(path = "/getStoreInfos")
    public ResponseInfo getStoreInfos(@RequestBody StoreInfoDto storeInfoDto) {
        List<StoreInfoDto> storeInfoDtos = storeInfoService.getstoreinfos(storeInfoDto);
        return new ResponseInfo(storeInfoDtos);
    }

    /**
     * 根据多个店铺id查询店铺信息列表
     *
     * @param storeId
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/getStoreLists")
    @PermissionController(value = PermitType.PLATFORM, operationName = "获取店铺列表")
    public ResponseInfo getStoreLists(@RequestBody Long[] storeId) throws GlobalException {
        Map<Long, StoreInfoDto> storeMap = storeInfoService.getStoreMap(storeId);
        return new ResponseInfo(storeMap);
    }

    @RequestMapping(path = "/getMerchantInfoParticulaById")
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家端查看商家管理")
    public ResponseInfo getMerchantParticularsBymerchantId(@RequestBody MerchantModifyLogDto merchantModifyLogDto) throws GlobalException {
        List<BackendCategorysDTO> backendCategoryDtoByBelongStore = backendCategoryApi.getMerchantBacCategory(merchantModifyLogDto.getMerchantId());
        MerchantInfoDto merchantParticularsById = merchantInfoService.getMerchantParticularsById(merchantModifyLogDto.getMerchantId());
        List<MerchantModifyLogDto> merchantModifysInfoList = merchantModifyInfoService.getMerchantModifysInfoDeail(merchantModifyLogDto);
        if (CollectionUtils.isNotEmpty(merchantModifysInfoList)) {
            merchantParticularsById.setMerchantModifyLogDtos(merchantModifysInfoList);
        }
        if (CollectionUtils.isNotEmpty(backendCategoryDtoByBelongStore)) {
            merchantParticularsById.setBackendCategorysDtos(backendCategoryDtoByBelongStore);
        }
        return new ResponseInfo(merchantParticularsById);
    }

    @PermissionController(value = PermitType.MERCHANT, operationName = "添加重新修改的信息")
    @RequestMapping(path = "/insertMerchantModifyInfo")
    public ResponseInfo addMerchantModifyInfo(@RequestBody MerchantModifyLogDto merchantModifyLogDto, BindingResult result)
            throws GlobalException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result
            );
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        merchantModifyInfoService.saveMerchantModifyInfo(merchantModifyLogDto);
        return new ResponseInfo();
    }


    @PermissionController(value = PermitType.PLATFORM, operationName = "审核不通过添加原因")
    @RequestMapping(path = "/insertMerchantModifyInfoExmine")
    public ResponseInfo addMerchantModifyInfoExmine(@RequestBody MerchantModifyLogDto merchantModifyLogDto, BindingResult result)
            throws GlobalException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result
            );
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        merchantModifyInfoService.saveMerchantModifyInfoExmine(merchantModifyLogDto);
        return new ResponseInfo();
    }

    @PermissionController(value = PermitType.PLATFORM, operationName = "审核通过")
    @RequestMapping(path = "/insertExamineAdoptInfo")
    public ResponseInfo addExamineAdoptInfo(@RequestBody MerchantModifyLogDto merchantModifyLogDto, BindingResult result)
            throws GlobalException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result
            );
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        merchantModifyInfoService.saveExamineAdoptInfo(merchantModifyLogDto);
        return new ResponseInfo();
    }

    @RequestMapping(path = "/adminStoreInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseInfo adminStoreInfo(HttpSession session, Long storeId) throws GlobalException, ParseException {
        if (null == storeId) {
            throw new GlobalException(MerchantInfoException.MERCHANTINFO_ID_NOT_NULL);
        }
        //todo:
        //	SecurityContextUtils.getCurrentSystemUser().setStoreId(storeId);
        SecurityContextUtils.getCurrentUserDto().setStoreId(storeId);
//		SystemUserEntity user = (SystemUserEntity)session.getAttribute("user");
//		user.setStoreId(storeId);
//		session.removeAttribute("user");
//		session.setAttribute("user", user);
        return new ResponseInfo();
    }


    /*@PermissionController(value = PermitType.PLATFORM, operationName = "店铺信息列表")
    @RequestMapping(path = "/getStoreInfoList")
    public ResponseInfo getStoreInfoList(@RequestBody StoreInfoDto storeInfoDto) {
        int pageNo = storeInfoDto.getPageNo();
        int pageSize = storeInfoDto.getPageSize();
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(pageNo);
        pagePO.setPageSize(pageSize);
        return new ResponseInfo(storeInfoService.getStoreInfoList(pagePO, storeInfoDto));
    }
*/

    @PermissionController(value = PermitType.PLATFORM, operationName = "修改审核列表")
    @RequestMapping(path = "/getMerchantModifyInfo")
    public ResponseInfo getMerchantModifyInfo(@RequestBody MerchantModifyInfosDto merchantModifyInfosDto) {
        int pageNo = merchantModifyInfosDto.getPageNo();
        int pageSize = merchantModifyInfosDto.getPageSize();
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(pageNo);
        pagePO.setPageSize(pageSize);
        return new ResponseInfo(merchantModifyInfoService.getMerchantModifyInfoList(pagePO, merchantModifyInfosDto));
    }


    @RequestMapping(path = "/getMerchantModifyInfoBymerchantId")
    @PermissionController(value = PermitType.PLATFORM, operationName = "审核详情")
    public ResponseInfo getMerchantModifyInfoBymerchantId(@RequestBody MerchantModifyLogDto merchantModifyLogDto) throws GlobalException {
        //todo:查看类目待审核-正常的类目
        List<BackendCategorysDTO> backendCategoryDtoByBelongStore = backendCategoryApi.getMerchantBacCategory(merchantModifyLogDto.getMerchantId());
        MerchantInfoDto merchantParticularsById = merchantInfoService.getMerchantParticularsById(merchantModifyLogDto.getMerchantId());
        List<MerchantModifyLogDto> merchantModifysInfoList = merchantModifyInfoService.getMerchantModifysInfoList(merchantModifyLogDto);
        if (backendCategoryDtoByBelongStore != null) {
            merchantParticularsById.setBackendCategorysDtos(backendCategoryDtoByBelongStore);
        }
        if (CollectionUtils.isNotEmpty(merchantModifysInfoList)) {
            merchantParticularsById.setMerchantModifyLogDtos(merchantModifysInfoList);
        }
        return new ResponseInfo(merchantParticularsById);
    }

    @PermissionController(value = PermitType.MERCHANT, operationName = "判断当前审核状态")
    @RequestMapping(path = "/judgeMerchantModifyStatus")
    public ResponseInfo judgeMerchantModifyStatus(@RequestBody MerchantModifyLogDto merchantModifyLogDto, BindingResult result)
            throws GlobalException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result
            );
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        return new ResponseInfo(merchantModifyInfoService.judgeMerchantModifyStatus(merchantModifyLogDto));
    }


    @PermissionController(value = PermitType.PLATFORM, operationName = "店铺信息列表")
    @RequestMapping(path = "/getStoreInfosList")
    public ResponseInfo getStoreInfosList(@RequestBody StoreInfoDto storeInfoDto) {
        int pageNo = storeInfoDto.getPageNo();
        int pageSize = storeInfoDto.getPageSize();
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(pageNo);
        pagePO.setPageSize(pageSize);
        return new ResponseInfo(storeInfoService.getStoreInfosList(pagePO, storeInfoDto));
    }
}