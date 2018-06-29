package com.topaiebiz.system.config.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.config.dto.ConfigDto;
import com.topaiebiz.system.config.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 系统公共配置表 前端控制器
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
@RestController
@RequestMapping(path = "/system/config", method = RequestMethod.POST)
public class ConfigController {
    @Autowired
    ConfigService configService;

    @RequestMapping(path = "/getConfigDto")
    public ResponseInfo getConfigDto(String configCode) throws GlobalException {
        return new ResponseInfo(configService.getConfigDto(configCode));
    }

    @RequestMapping(path = "/editConfig")
    @PermissionController(value = PermitType.PLATFORM, operationName = "编辑系统配置信息")
    public ResponseInfo editConfig(@RequestBody ConfigDto config) throws GlobalException {
        return new ResponseInfo(configService.editConfig(config));
    }
}
