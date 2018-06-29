package com.topaiebiz.merchant.store.service;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.member.po.MemberFilterPo;
import com.topaiebiz.merchant.store.dto.MerchantFollowDto;
import com.topaiebiz.merchant.store.dto.MerchantMemberDto;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/20 0020 上午 10:29
 */
public interface MerchantMemberService {




    /**
     * Description：会员关注店铺信息列表分页检索。
     * @param pagePO
     *            分页参数
     * @param memberFilterPo
     *            商家店铺关注信息Dto
     * @return
     */
    PageInfo<MemberDto> getMerchantMerchantMemberList(PagePO pagePO, MemberFilterPo memberFilterPo);
}
