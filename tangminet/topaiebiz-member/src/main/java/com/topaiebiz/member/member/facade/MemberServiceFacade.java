package com.topaiebiz.member.member.facade;

import com.topaiebiz.basic.api.DistrictApi;
import com.topaiebiz.basic.dto.DistrictDto;
import com.topaiebiz.card.api.GiftCardApi;
import com.topaiebiz.card.dto.CardBalanceDTO;
import com.topaiebiz.card.dto.MemberCardDTO;
import com.topaiebiz.member.dto.member.MemberCenterDto;
import com.topaiebiz.message.api.CaptchaApi;
import com.topaiebiz.message.util.CaptchaType;
import com.topaiebiz.promotion.api.PromotionApi;
import com.topaiebiz.system.security.api.SystemUserApi;
import com.topaiebiz.trade.api.OrderStaticsApi;
import com.topaiebiz.trade.dto.statics.OrderStatusCountDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by ward on 2018-01-13.
 */
@Component
@Slf4j
public class MemberServiceFacade {

    @Autowired
    private CaptchaApi captchaApi;

    @Autowired
    private SystemUserApi systemUserApi;

    @Autowired
    private OrderStaticsApi orderStaticsApi;

    @Autowired
    private PromotionApi promotionApi;

    @Autowired
    private GiftCardApi giftCardApi;

    @Autowired
    private DistrictApi districtApi;

    public boolean sendCaptcha(String telephone, CaptchaType type, String ip) {
        Boolean sendRet = captchaApi.sendCaptcha(telephone, type, ip);
        log.info("会员模块调用captchaApi.sendCaptcha({},{},{}) return {}", telephone, type, ip, sendRet);
        return sendRet;
    }

    public boolean sendCaptcha(String telephone, CaptchaType type, String ip, Long memberId) {
        Boolean sendRet = captchaApi.sendCaptcha(telephone, type, ip, memberId);
        log.info("会员模块调用captchaApi.sendCaptcha({},{},{}) return {}", telephone, type, ip, sendRet);
        return sendRet;
    }


    public boolean verifyCaptcha(String telephone, String captcha, CaptchaType type) {
        return captchaApi.verifyCaptcha(telephone, captcha, type);
    }


    //修改系统用户密码
    public boolean editSysUserPassword(String mobilePhone, String password) {
        boolean editRet = systemUserApi.editUserPassword(mobilePhone, password);
        log.info("会员模块调用systemUserApi.editUserPassword({},{}) return {}", mobilePhone, password, editRet);
        return editRet;
    }

    //修改系统用户手机号
    public boolean editSysUserTelephone(String beforeMobilePhone, String afterMobilePhone) {
        boolean editRet = systemUserApi.editUserTelephone(beforeMobilePhone, afterMobilePhone);
        log.info("会员模块调用systemUserApi.editUserTelephone({},{}) return {}", beforeMobilePhone, afterMobilePhone, editRet);
        return editRet;
    }

    //删除系统用户
    public boolean removeSystemUser(String mobilePhone) {
        boolean removeRet = systemUserApi.removeSystemUser(mobilePhone);
        log.info("会员模块调用ystemUserApi.removeSystemUser({}) return {}", mobilePhone, removeRet);
        return removeRet;
    }


    //会员中心 非会员模块信息聚合
    public MemberCenterDto getMemberCenter(Long memberId) {
        MemberCenterDto memberCenterDto = new MemberCenterDto();
        //优惠券张数统计
        memberCenterDto.setCouponCount(getAllCouponNum(memberId));
        //订单 各状态 数量统计
        OrderStatusCountDTO orderStatusCount = orderStaticsApi.queryOrderStatusCount(memberId);
        memberCenterDto.setOrderUncomment(orderStatusCount.getUnevaluateCount().intValue());
        memberCenterDto.setOrderUnpay(orderStatusCount.getUnpay().intValue());
        memberCenterDto.setOrderUnshipped(orderStatusCount.getUnshipCount().intValue());
        memberCenterDto.setOrderUunreceived(orderStatusCount.getUnreceiveCount().intValue());
        memberCenterDto.setId(memberId.intValue());
        //礼卡金额 数量
        MemberCardDTO memberCardDTO = giftCardApi.getMemberValidCards(memberId);
        if (null != memberCardDTO) {
            memberCenterDto.setCardBalance(memberCardDTO.getTotalCardAmount());
            memberCenterDto.setCardNum(memberCardDTO.getTotalCardNum());
        }
        return memberCenterDto;
    }

    public MemberCardDTO getMemberValidCards(Long memberId) {
        return giftCardApi.getMemberValidCards(memberId);
    }


    public CardBalanceDTO getCardBalance(Long memberId) {
        return giftCardApi.getBalanceByMember(memberId);
    }

    public Map<String, Object> getBalanceByMemberList(List<Long> memberIdList) {
        return giftCardApi.getBalanceByMemberList(memberIdList);
    }

    //统计所有店铺可用优惠券的总数
    public Integer getAllCouponNum(Long memberId) {
        return promotionApi.getCouponNum(memberId, null);
    }

    //统计某个店铺可用优惠券的总数
    public Integer getStoreCouponNum(Long memberId, Long storeId) {
        return promotionApi.getCouponNum(memberId, storeId);
    }


    public DistrictDto getDistrict(Long districtId) {
        return districtApi.getDistrict(districtId);
    }

}
