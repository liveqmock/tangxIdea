package com.topaiebiz.merchant.store.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.PageDataUtil;
import com.topaiebiz.member.api.MemberApi;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.member.po.MemberFilterPo;
import com.topaiebiz.merchant.store.dao.MerchantMmeberDao;
import com.topaiebiz.merchant.store.dto.MerchantFollowDto;
import com.topaiebiz.merchant.store.dto.MerchantMemberDto;
import com.topaiebiz.merchant.store.entity.MerchantMemberEntity;
import com.topaiebiz.merchant.store.service.MerchantMemberService;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/20 0020 上午 10:29
 */
@Service
public class MerchantMemberSericeImpl implements MerchantMemberService {

    @Autowired
    private MerchantMmeberDao merchantMmeberDao;

    @Autowired
    private MemberApi memberApi;


    @Override
    public PageInfo<MemberDto> getMerchantMerchantMemberList(PagePO pagePO, MemberFilterPo memberFilterPo) {
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        //根据店铺查询所有的会员
        Page<MemberDto> page = PageDataUtil.buildPageParam(pagePO);
        List<Long> merchantMemberids = merchantMmeberDao.selectMerchantMemberByStoreId(page, storeId);
       List<MemberDto> memberList = memberApi.getMemberList(merchantMemberids);
       page.setRecords(memberList);
        return PageDataUtil.copyPageInfo(page);
    }
}
