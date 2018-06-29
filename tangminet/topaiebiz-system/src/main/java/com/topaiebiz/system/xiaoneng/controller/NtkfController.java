package com.topaiebiz.system.xiaoneng.controller;

import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.system.xiaoneng.dto.XiaonengGoodsInfoDto;
import com.topaiebiz.system.xiaoneng.po.ItemPo;
import com.topaiebiz.system.xiaoneng.po.NtkfGoodsPo;
import com.topaiebiz.system.xiaoneng.service.NtkfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 小能接口-商品
 * Created by Joe on 2018/3/29.
 */
@RestController
@RequestMapping(path = "/xiaoneng")
public class NtkfController {

    @Autowired
    private NtkfService ntkfService;

    /**
     * @param
     * @return
     */
    @MemberLogin
    @RequestMapping(path = "/getGoodsNtkf", method = RequestMethod.POST)
    public String getGoodsNtkf(@RequestBody NtkfGoodsPo ntkfGoodsPo) {
        MemberTokenDto memberTokenDto = MemberContext.tryGetCurrentMemberToken();
        return ntkfService.getGoodsNtkf(memberTokenDto, ntkfGoodsPo);
    }


    /**
     * 根据itemId获取item商品信息
     *
     * @param
     * @return
     */
    @RequestMapping(path = "/getItemById")
    public Map<String, XiaonengGoodsInfoDto> getItemById(ItemPo itemPo) {
        XiaonengGoodsInfoDto xiaonengGoodsInfoDto = ntkfService.getItemById(itemPo.getItemId());
        Map<String, XiaonengGoodsInfoDto> returnMap = new HashMap<>();
        returnMap.put("item", xiaonengGoodsInfoDto);
        return returnMap;
    }


}
