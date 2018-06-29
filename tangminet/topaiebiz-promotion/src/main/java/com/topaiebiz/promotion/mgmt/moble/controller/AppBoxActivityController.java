package com.topaiebiz.promotion.mgmt.moble.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.promotion.common.util.DozerUtils;
import com.topaiebiz.promotion.mgmt.dto.PromotionDto;
import com.topaiebiz.promotion.mgmt.dto.box.AwardRecordDTO;
import com.topaiebiz.promotion.mgmt.dto.box.BoxReceiverDTO;
import com.topaiebiz.promotion.mgmt.dto.box.MemberBoxDTO;
import com.topaiebiz.promotion.mgmt.dto.box.json.ResBoxJsonDTO;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEnum;
import com.topaiebiz.promotion.mgmt.service.BoxActivityService;
import com.topaiebiz.promotion.mgmt.vo.box.AppBoxReceiverVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 活动开宝箱
 */
@RestController
@RequestMapping(path = "/boxActivity/customer", method = RequestMethod.POST)
public class AppBoxActivityController {
    @Autowired
    private BoxActivityService boxActivityService;

    /**
     * 产生宝箱
     *
     * @param type 0-时间节点 1-登录节点 2-分享节点 3-支付节点
     * @return
     */
    @MemberLogin
    @RequestMapping("/produceBox/{type}")
    public ResponseInfo produceBox(@PathVariable("type") Integer type) {
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        Boolean res = boxActivityService.produceBox(memberId, type);
        return new ResponseInfo(res);
    }

    /**
     * 拥有宝箱数量
     *
     * @return
     */
    @MemberLogin
    @RequestMapping("/getBoxCount")
    public ResponseInfo getBoxCount() {
        // 登录会员
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        MemberBoxDTO memberBoxDTO = boxActivityService.getAwardCount(memberId);
        return new ResponseInfo(memberBoxDTO);
    }

    /**
     * 开宝箱
     *
     * @return
     */
    @MemberLogin
    @RequestMapping("/openBox")
    public ResponseInfo openBox() {
        // 登录会员
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        AwardRecordDTO awardRecord = boxActivityService.openBox(memberId);
        return new ResponseInfo(awardRecord);
    }

    /**
     * 获奖列表
     *
     * @return
     */
    @MemberLogin
    @RequestMapping("/getAwardRecords")
    public ResponseInfo getAwardRecords() {
        // 登录会员
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        //获取当前开宝箱活动
        PromotionDto promotion = boxActivityService.getPromotion();
        if (promotion == null) {
            return new ResponseInfo();
        }

        List<AwardRecordDTO> recordDTOLists = boxActivityService.getAwardRecordsList(memberId, promotion.getId());
        return new ResponseInfo(recordDTOLists);
    }

    /**
     * 实物宝箱-实物宝箱领取页
     *
     * @return
     */
    @MemberLogin
    @RequestMapping("/toSaveReceiver/{boxRecordId}")
    public ResponseInfo toSaveReceiver(@PathVariable("boxRecordId") Long boxRecordId) {
        // 登录会员
        MemberContext.getCurrentMemberToken().getMemberId();
        ResBoxJsonDTO resBoxJsonDTO = boxActivityService.getResBox(boxRecordId);
        return new ResponseInfo(resBoxJsonDTO);
    }

    /**
     * 实物宝箱-保存收货人信息
     *
     * @return
     */
    @MemberLogin
    @RequestMapping("/saveReceiver")
    public ResponseInfo saveReceiver(@RequestBody AppBoxReceiverVO appBoxReceiverVO) {
        BoxReceiverDTO boxReceiverDTO = DozerUtils.map(appBoxReceiverVO, BoxReceiverDTO.class);
        // 登录会员
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        if (appBoxReceiverVO.getBoxRecordId() == null) {
            throw new GlobalException(PromotionExceptionEnum.BOX_RECORD_ID_NOT_NULL);
        }
        if (appBoxReceiverVO.getMobile() == null) {
            throw new GlobalException(PromotionExceptionEnum.BOX_RECEIVER_MOBILE_NOT_NULL);
        }
        boxReceiverDTO.setMemberId(memberId);
        boxActivityService.insertBoxReceiver(boxReceiverDTO);
        return new ResponseInfo();
    }

    @RequestMapping("/getAwardsInfo")
    public ResponseInfo getAwardsInfo() {
        return new ResponseInfo(boxActivityService.getAwardsInfo());
    }
}
