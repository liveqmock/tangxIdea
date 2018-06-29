package com.topaiebiz.giftcard.controller.mis;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.giftcard.controller.AbstractController;
import com.topaiebiz.giftcard.entity.GiftcardUnit;
import com.topaiebiz.giftcard.enums.GiftcardExceptionEnum;
import com.topaiebiz.giftcard.service.GiftcardLogService;
import com.topaiebiz.giftcard.service.GiftcardUnitService;
import com.topaiebiz.giftcard.util.DateUtil;
import com.topaiebiz.giftcard.vo.*;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * @description: 卡单元的控制
 * @author: Jeff Chen
 * @date: created in 下午8:56 2018/1/16
 */
@RestController
@RequestMapping(value = "/giftcard/unit",method = RequestMethod.POST)
public class GiftcardUnitController extends AbstractController{
    @Autowired
    private GiftcardUnitService giftcardUnitService;
    @Autowired
    private GiftcardLogService giftcardLogService;
    /**
     * 查询
     * @param giftcardUnitReq
     * @return
     */
    @RequestMapping("/query")
    @PermissionController(value = PermitType.PLATFORM,operationName = "礼卡单元列表")
    public ResponseInfo query(@RequestBody GiftcardUnitReq giftcardUnitReq) {
        if (null == giftcardUnitReq || null == giftcardUnitReq.getMedium()) {
            throw new GlobalException(GiftcardExceptionEnum.SOLID_OR_ELECT);
        }
        //卡号起止如果只输入一项则起止相同
        if (null == giftcardUnitReq.getCardNoStart() && null != giftcardUnitReq.getCardNoEnd()) {
            giftcardUnitReq.setCardNoStart(giftcardUnitReq.getCardNoEnd());
        }
        if (null != giftcardUnitReq.getCardNoStart() && null == giftcardUnitReq.getCardNoEnd()) {
            giftcardUnitReq.setCardNoEnd(giftcardUnitReq.getCardNoStart());
        }
        if (null == giftcardUnitReq.getStartTime() && null == giftcardUnitReq.getEndTime()) {
            //默认最近三个月的
            giftcardUnitReq.setStartTime(DateUtil.someDay(new Date(), -90));
        }

        return new ResponseInfo(giftcardUnitService.queryGiftcard(giftcardUnitReq));
    }

    @RequestMapping("/active")
    @PermissionController(value = PermitType.PLATFORM,operationName = "礼卡激活")
    public ResponseInfo active(@RequestBody DataMap dataMap) {
        List<String> ids = dataMap.getList("ids");
        if (CollectionUtils.isEmpty(ids)) {
            return paramError();
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        return new ResponseInfo(giftcardUnitService.active(ids, currentUserDto.getUsername()));
    }
    /**
     * 支持批量冻结
     * @param commonData
     * @return
     */
    @RequestMapping("/freeze")
    @PermissionController(value = PermitType.PLATFORM,operationName = "礼卡冻结")
    public ResponseInfo freeze(@RequestBody CommonData commonData) {
        List<Long> ids = commonData.getIds();
        if (CollectionUtils.isEmpty(ids)) {
            return paramError();
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        return new ResponseInfo(giftcardUnitService.freeze(ids,currentUserDto.getUsername()));
    }

    /**
     * 批量激活
     * @param cardOpReq
     * @return
     */
    @RequestMapping("/batchActive")
    @PermissionController(value = PermitType.PLATFORM,operationName = "批量激活")
    public ResponseInfo batchActive(@RequestBody CardOpReq cardOpReq) {
        if (null == cardOpReq || CollectionUtils.isEmpty(cardOpReq.getIdList())) {
            return paramError();
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        cardOpReq.setOperator(currentUserDto.getUsername());
        return new ResponseInfo(giftcardUnitService.batchActive(cardOpReq));
    }


    /**
     * 支持批量冻结
     * @return
     */
    @RequestMapping("/batchFreeze")
    @PermissionController(value = PermitType.PLATFORM,operationName = "批量冻结")
    public ResponseInfo batchFreeze(@RequestBody CardOpReq cardOpReq) {
        if (null == cardOpReq || CollectionUtils.isEmpty(cardOpReq.getIdList())) {
            return paramError();
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        cardOpReq.setOperator(currentUserDto.getUsername());
        return new ResponseInfo(giftcardUnitService.batchFreeze(cardOpReq));
    }

    /**
     * 解冻
     * @return
     */
    @RequestMapping("/unfreeze")
    @PermissionController(value = PermitType.PLATFORM, operationName = "礼卡解冻")
    public ResponseInfo unfreeze(@RequestBody CardOpReq cardOpReq){
        if (null == cardOpReq || null == cardOpReq.getBizId() ) {
            return paramError();
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        return new ResponseInfo(giftcardUnitService.unfreeze(cardOpReq.getBizId(),currentUserDto.getUsername(),cardOpReq.getNote()));
    }
    /**
     * 操作：冻结，解冻
     * @param unitHandleReq
     * @param result
     * @return
     */
    @RequestMapping("/handle")
    @PermissionController(value = PermitType.PLATFORM,operationName = "礼卡操作")
    @Deprecated
    public ResponseInfo handle(@RequestBody @Valid UnitHandleReq unitHandleReq, BindingResult result) {
        ResponseInfo responseInfo = validParam(result);
        if (null != responseInfo) {
            return responseInfo;
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        GiftcardUnit giftcardUnit = new GiftcardUnit();
        giftcardUnit.setId(unitHandleReq.getUnitId());
        giftcardUnit.setCardStatus(unitHandleReq.getCardStatus());
        giftcardUnit.setModifier(currentUserDto.getUsername());
        giftcardUnit.setModifiedTime(new Date());
        return new ResponseInfo(giftcardUnitService.updateById(giftcardUnit));
    }

    /**
     * 续期
     * @param unitHandleReq
     * @param result
     * @return
     */
    @RequestMapping("/renewal")
    @PermissionController(value = PermitType.PLATFORM,operationName = "礼卡续期")
    @Deprecated
    public ResponseInfo renewal(@RequestBody @Valid UnitHandleReq unitHandleReq, BindingResult result) {
        ResponseInfo responseInfo = validParam(result);
        if (null != responseInfo) {
            return responseInfo;
        }
        if (null == unitHandleReq.getRenewalDays() || unitHandleReq.getRenewalDays().intValue() < 0) {
            throw new GlobalException(GiftcardExceptionEnum.INVALID_RENEWAL);
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        unitHandleReq.setModifier(currentUserDto.getUsername());
        unitHandleReq.setModifiedTime(new Date());
        return new ResponseInfo(giftcardUnitService.renewal(unitHandleReq));
    }

    /**
     * 续期
     * @param cardOpReq
     * @return
     */
    @RequestMapping("/renew")
    @PermissionController(value = PermitType.PLATFORM, operationName = "礼卡续期")
    public ResponseInfo renew(@RequestBody CardOpReq cardOpReq){
        if (null == cardOpReq || null == cardOpReq.getBizId()|| null == cardOpReq.getIntParam()) {
            return paramError();
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        UnitHandleReq unitHandleReq = new UnitHandleReq();
        unitHandleReq.setUnitId(cardOpReq.getBizId());
        unitHandleReq.setNote(cardOpReq.getNote());
        unitHandleReq.setModifier(currentUserDto.getUsername());
        unitHandleReq.setModifiedTime(new Date());
        unitHandleReq.setRenewalDays(cardOpReq.getIntParam());
        return new ResponseInfo(giftcardUnitService.renewal(unitHandleReq));
    }

    /**
     * 卡片详情
     * @return
     */
    @RequestMapping("/detail/{unitId}")
    @PermissionController(value = PermitType.PLATFORM,operationName = "礼卡详情")
    public ResponseInfo detail(@PathVariable Long unitId) {
        return new ResponseInfo(giftcardUnitService.getGiftcardInfoById(unitId));
    }

    /**
     * 消费记录
     * @return
     */
    @RequestMapping("/consumeLog")
    @PermissionController(value = PermitType.PLATFORM,operationName = "消费日志")
    public ResponseInfo consumeLog(@Valid @RequestBody GiftcardLogReq giftcardLogReq,BindingResult result) {
        ResponseInfo responseInfo = validParam(result);
        if (null != responseInfo) {
            return responseInfo;
        }
        return new ResponseInfo(giftcardLogService.queryLog(giftcardLogReq));
    }
}
