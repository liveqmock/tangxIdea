package com.topaiebiz.giftcard.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.giftcard.dao.GiftcardGivenDao;
import com.topaiebiz.giftcard.dao.GiftcardUnitDao;
import com.topaiebiz.giftcard.entity.GiftcardGiven;
import com.topaiebiz.giftcard.entity.GiftcardUnit;
import com.topaiebiz.giftcard.enums.BindWayEnum;
import com.topaiebiz.giftcard.enums.GiftcardExceptionEnum;
import com.topaiebiz.giftcard.enums.GivenStatusEnum;
import com.topaiebiz.giftcard.service.GiftcardGivenService;
import com.topaiebiz.giftcard.service.GiftcardUnitService;
import com.topaiebiz.giftcard.util.BizSerialUtil;
import com.topaiebiz.giftcard.util.DateUtil;
import com.topaiebiz.giftcard.vo.CardBindVO;
import com.topaiebiz.giftcard.vo.GiftcardGivenVO;
import com.topaiebiz.giftcard.vo.GivenDetailVO;
import com.topaiebiz.member.api.MemberApi;
import com.topaiebiz.member.dto.member.MemberDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: Jeff Chen
 * @date: created in 下午3:46 2018/1/12
 */
@Service
@Slf4j
public class GiftcardGivenServiceImpl extends ServiceImpl<GiftcardGivenDao,GiftcardGiven> implements GiftcardGivenService{

    @Autowired
    private GiftcardGivenDao giftcardGivenDao;
    @Autowired
    private GiftcardUnitDao giftcardUnitDao;
    @Autowired
    private GiftcardUnitService giftcardUnitService;
    @Autowired
    private MemberApi memberApi;

    @Override
    public GiftcardGivenVO getGiftcard4Given(String cardNo, Long memberId) {
        GiftcardUnit selectUnit = new GiftcardUnit();
        selectUnit.setCardNo(cardNo);
        GiftcardUnit giftcardUnit = giftcardUnitDao.getGiftcardInfo(selectUnit);
        if (null == giftcardUnit) {
            throw new GlobalException(GiftcardExceptionEnum.UNIT_NOT_EXIST);
        }
        //所有权
        if (null == giftcardUnit.getOwner() || !giftcardUnit.getOwner().equals(memberId)) {
            throw new GlobalException(GiftcardExceptionEnum.NOT_OWER);
        }
        GiftcardGivenVO giftcardGivenVO = new GiftcardGivenVO();
        giftcardGivenVO.setCardNo(giftcardUnit.getCardNo());
        giftcardGivenVO.setCover(giftcardUnit.getCover());
        giftcardGivenVO.setCardName(giftcardUnit.getCardName());
        giftcardGivenVO.setSubtitle(giftcardUnit.getSubtitle());
        giftcardGivenVO.setFaceValue(giftcardUnit.getFaceValue());
        return giftcardGivenVO;
    }

    @Override
    public GiftcardGiven generate(GiftcardGiven giftcardGiven) {
        //判断礼卡是否可以转赠
        GiftcardUnit giftcardUnit = new GiftcardUnit();
        giftcardUnit.setCardNo(giftcardGiven.getCardNo());
        giftcardUnit.setOwner(giftcardGiven.getMemberId());
        GiftcardUnit unit = giftcardUnitDao.selectOne(giftcardUnit);
        if (null==unit) {
            throw new GlobalException(GiftcardExceptionEnum.NOT_OWER);
        }
        if (GivenStatusEnum.NOT_GIVEN.getCode()== unit.getGivenStatus()) {
            throw new GlobalException(GiftcardExceptionEnum.CARD_CANNOT_GIVEN);
        }
        if (GivenStatusEnum.HAD_GIVEN.getCode()== unit.getGivenStatus()) {
            throw new GlobalException(GiftcardExceptionEnum.CARD_HAD_GOTTEN);
        }
        GiftcardGiven given = new GiftcardGiven();
        given.setCardNo(giftcardGiven.getCardNo());
        given.setDoneePhone(giftcardGiven.getDoneePhone());
        GiftcardGiven oldGiven = giftcardGivenDao.selectOne(given);
        if (null != oldGiven) {
            return oldGiven;
        } else {
            giftcardGiven.setGivenTime(new Date());
            giftcardGiven.setLinkId(BizSerialUtil.getUUID());
            if (insert(giftcardGiven)) {
                return giftcardGiven;
            }else {
                return null;
            }
        }
    }

    @Override
    public Boolean cancle(GiftcardGiven giftcardGiven) {
        EntityWrapper<GiftcardGiven> wrapper = new EntityWrapper<>();
        wrapper.eq("linkId", giftcardGiven.getLinkId());
        wrapper.eq("memberId", giftcardGiven.getMemberId());
        GiftcardGiven given = selectOne(wrapper);
        if (null==giftcardGiven.getLinkId()||null==giftcardGiven.getMemberId()||null == given) {
            throw new GlobalException(GiftcardExceptionEnum.GIVEN_NOT_EXIST);
        }
        log.info("{}取消了礼卡转赠{}",giftcardGiven.getMemberId(),giftcardGiven.getLinkId());
        return deleteById(given.getId());
    }

    @Override
    public GivenDetailVO getByLinkId(String linkId) {
        EntityWrapper<GiftcardGiven> wrapper = new EntityWrapper<>();
        wrapper.eq("linkId", linkId);

        GiftcardGiven given = selectOne(wrapper);
        if (null == given) {
            throw new GlobalException(GiftcardExceptionEnum.GIVEN_NOT_EXIST);
        }
        GiftcardUnit selectUnit = new GiftcardUnit();
        selectUnit.setCardNo(given.getCardNo());
        GiftcardUnit giftcardUnit = giftcardUnitDao.getGiftcardInfo(selectUnit);
        if (null == giftcardUnit) {
            throw new GlobalException(GiftcardExceptionEnum.UNIT_NOT_EXIST);
        }
        MemberDto member = memberApi.getMemberByMemberId(given.getMemberId());
        GivenDetailVO givenDetailVO = new GivenDetailVO();
        givenDetailVO.setCover(giftcardUnit.getCover());
        givenDetailVO.setLinkId(linkId);
        givenDetailVO.setMemberName(given.getMemberName());
        givenDetailVO.setNote(given.getNote());
        givenDetailVO.setSmallIcon(member.getSmallIcon());
        return givenDetailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.NESTED)
    public Boolean getTheGiven(GiftcardGiven giftcardGiven) {
        //1.转赠是否领取过
        EntityWrapper<GiftcardGiven> wrapper = new EntityWrapper<>();
        wrapper.eq("linkId", giftcardGiven.getLinkId());
        GiftcardGiven given = selectOne(wrapper);
        if (null == given) {
            throw new GlobalException(GiftcardExceptionEnum.GIVEN_NOT_EXIST);
        }
        if (given.getGivenStatus() == 1) {
            throw new GlobalException(GiftcardExceptionEnum.GIVEN_RECEIVED);
        }
        //2.手机号是否可以领取
        if (!giftcardGiven.getDoneePhone().equals(given.getDoneePhone())) {
            throw new GlobalException(GiftcardExceptionEnum.GIVEN_NOT_EXIST);
        }
        //3.领取绑定
        CardBindVO cardBindVO = new CardBindVO();
        cardBindVO.setBindWay(BindWayEnum.GET_GIVEN.getWayId());
        cardBindVO.setCardNo(given.getCardNo());
        cardBindVO.setMemberId(giftcardGiven.getMemberId());

        if (giftcardUnitService.bindCard(cardBindVO)) {
            //更新转赠状态
            given.setDoneeMemberId(giftcardGiven.getMemberId());
            given.setDoneeTime(new Date());
            given.setGivenStatus(1);
            return giftcardGivenDao.updateById(given)>0;
        }
        return false;

    }
}
