package com.topaiebiz.goods.brand.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.brand.dto.SuitableAgeDto;
import com.topaiebiz.goods.brand.service.SuitableAgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by dell on 2018/1/9.
 */
@RestController
@RequestMapping(value = "/goods/suitableage",method = RequestMethod.POST)
public class SuitableAgeMobileController {

    @Autowired
    private SuitableAgeService suitableAgeService;

    /**
     * Description app端年龄段列表
     *
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    @RequestMapping(value = "/getAppListSuitableAge")
    public ResponseInfo getAppListSuitableAge() throws GlobalException {
        List<SuitableAgeDto> listSuitableAge = suitableAgeService.getAppListSuitableAge();
        return new ResponseInfo(listSuitableAge);
    }
}
