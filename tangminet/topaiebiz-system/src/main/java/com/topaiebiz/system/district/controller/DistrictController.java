/**
 *
 */
package com.topaiebiz.system.district.controller;


import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.district.dto.DistrictDto;
import com.topaiebiz.system.district.exception.DistrictExceptionEnum;
import com.topaiebiz.system.district.service.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Description： 区域数据 控制层
 * <p>
 * <p>
 * Author hxpeng
 * <p>
 * Date 2017年10月19日 下午2:28:02
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@RestController
@RequestMapping(path = "/system/district", method = RequestMethod.POST)
public class DistrictController {

    @Autowired
    private DistrictService districtService;

    @RequestMapping(path = "/provices")
    public ResponseInfo getAllProvice() {
        return new ResponseInfo(districtService.selectOneLevelDistrict());
    }

    @RequestMapping(path = "/counties/{cityId}")
    public ResponseInfo getCounties(@PathVariable Long cityId) {
        return new ResponseInfo(districtService.selectChildDistrictData(cityId));
    }


    @RequestMapping(path = "/cities/{proviceId}")
    public ResponseInfo getCities(@PathVariable Long proviceId) {
        return new ResponseInfo(districtService.selectChildDistrictData(proviceId));
    }

    /**
     * Description： 获取所有一级区域数据
     * <p>
     * Author hxpeng
     *
     * @return 返回所有一级区域数据
     * @throws GlobalException
     */
    @Deprecated
    @RequestMapping(path = "/getOneLevelDistrictData")
    public ResponseInfo getOneLevelDistrictData() throws GlobalException {
        return new ResponseInfo(districtService.selectOneLevelDistrict());
    }

    @RequestMapping(path = "/detail/{districtId}")
    public ResponseInfo getOneDistrictById(@PathVariable Long districtId) throws GlobalException {
        return new ResponseInfo(districtService.selectOneById(districtId));
    }

    /**
     * Description： 获取所有区域数据
     * <p>
     * Author hxpeng
     *
     * @return 返回所有区域数据
     * @throws GlobalException
     */
    @RequestMapping(path = "/all")
    public ResponseInfo getAllDistrictData() throws GlobalException {
        return new ResponseInfo(districtService.selectAllDistrict());
    }


    /**
     * Description： 获取区域下的所有子区域数据
     * <p>
     * Author hxpeng
     *
     * @return 返回所有子区域数据
     * @throws GlobalException
     */
    @Deprecated
    @RequestMapping(path = "/getChildDistrictData")
    public ResponseInfo getChildDistrictData(Long parentDistrictId) throws GlobalException {
        if (StringUtils.isEmpty(parentDistrictId)) {
            throw new GlobalException(DistrictExceptionEnum.DISTRICT_ID_NOT_NULL);
        }
        return new ResponseInfo(districtService.selectChildDistrictData(parentDistrictId));
    }


    /**
     * Description：删除指定一条区域数据
     * <p>
     * Author hxpeng
     *
     * @param id
     * @return 返回操作结果标识
     * @throws GlobalException
     */
    @RequestMapping(path = "/delete/{id}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台删除区域信息")
    public ResponseInfo deleteDistrictById(@PathVariable Long id) throws GlobalException {
        if (StringUtils.isEmpty(id)) {
            throw new GlobalException(DistrictExceptionEnum.DISTRICT_ID_NOT_NULL);
        }
        return new ResponseInfo(districtService.deleteDistrictDataById(id));
    }


    /**
     * Description： 新增区域数据信息
     * <p>
     * Author hxpeng
     *
     * @param districtDto
     * @param result
     * @return 返回操作结果标识
     * @throws GlobalException
     */
    @RequestMapping(path = "/create")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台新增区域信息")
    public ResponseInfo createDistrict(@Valid DistrictDto districtDto, BindingResult result) throws GlobalException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        // 成功时，直接分发数据并调用业务逻辑方法，并返回响应信息对象。
        return new ResponseInfo(districtService.createDistrict(districtDto));
    }


    /**
     * Description： 修改区域数据信息
     * <p>
     * Author hxpeng
     *
     * @param districtDto
     * @param result
     * @return 返回操作结果标识
     * @throws GlobalException
     */
    @RequestMapping(path = "/update")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台修改区域信息")
    public ResponseInfo modifyDistrict(@Valid DistrictDto districtDto, BindingResult result) throws GlobalException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        if (districtDto.getId() == null) {
            throw new GlobalException(DistrictExceptionEnum.DISTRICT_ID_NOT_NULL);
        }
        // 成功时，直接分发数据并调用业务逻辑方法，并返回响应信息对象。
        return new ResponseInfo(districtService.modifyDistrict(districtDto));
    }


}
