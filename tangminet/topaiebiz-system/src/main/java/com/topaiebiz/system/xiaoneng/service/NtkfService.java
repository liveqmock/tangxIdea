package com.topaiebiz.system.xiaoneng.service;

import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.system.xiaoneng.dto.XiaonengGoodsInfoDto;
import com.topaiebiz.system.xiaoneng.po.NtkfGoodsPo;

/**
 * Created by Joe on 2018/3/29.
 */
public interface NtkfService {


    String getGoodsNtkf(MemberTokenDto memberTokenDto, NtkfGoodsPo ntkfGoodsPo);

    XiaonengGoodsInfoDto getItemById(Long id);
}
