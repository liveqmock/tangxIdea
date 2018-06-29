package com.topaiebiz.giftcard.controller.mis;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.giftcard.controller.AbstractController;
import com.topaiebiz.giftcard.entity.GiftcardSelect;
import com.topaiebiz.giftcard.service.GiftcardSelectService;
import com.topaiebiz.giftcard.vo.CommonData;
import com.topaiebiz.giftcard.vo.DataMap;
import com.topaiebiz.giftcard.vo.GiftcardSelectReq;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @description: 卡精选控制
 * @author: Jeff Chen
 * @date: created in 下午2:51 2018/1/18
 */
@RestController
@RequestMapping("/giftcard/select")
public class GiftcardSelectController extends AbstractController{

    @Autowired
    private GiftcardSelectService giftcardSelectService;


    /**
     * 查询精选列表
     * @param giftcardSelectReq
     * @return
     */
    @RequestMapping("/query")
    @PermissionController(value = PermitType.PLATFORM,operationName = "精选列表")
    public ResponseInfo query(@RequestBody GiftcardSelectReq giftcardSelectReq) {

        return new ResponseInfo(giftcardSelectService.querySelect(giftcardSelectReq));
    }

    /**
     * 添加
     * @param data
     * @return
     */
    @RequestMapping("/add")
    @PermissionController(value = PermitType.PLATFORM,operationName = "添加精选")
    public ResponseInfo add(@RequestBody CommonData data) {
        if (null == data || data.getBatchId() < 1) {
            return paramError();
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        GiftcardSelect giftcardSelect = new GiftcardSelect();
        giftcardSelect.setBatchId(data.getBatchId());
        giftcardSelect.setCreator(currentUserDto.getUsername());
        giftcardSelect.setCreatedTime(new Date());
        giftcardSelect.setModifier(currentUserDto.getUsername());
        giftcardSelect.setModifiedTime(new Date());
        return new ResponseInfo(giftcardSelectService.add(giftcardSelect));
    }

    @RequestMapping("/delete/{selectId}")
    @PermissionController(value = PermitType.PLATFORM,operationName = "删除精选")
    public ResponseInfo delete(@PathVariable Long selectId) {
        if (null == selectId) {
            return paramError();
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        GiftcardSelect giftcardSelect = new GiftcardSelect();
        giftcardSelect.setId(selectId);
        giftcardSelect.setDelFlag(1);
        giftcardSelect.setModifiedTime(new Date());
        giftcardSelect.setModifier(currentUserDto.getUsername());
        return new ResponseInfo(giftcardSelectService.updateById(giftcardSelect));
    }

    /**
     * 上移
     * @param selectId
     * @return
     */
    @RequestMapping("/moveUp/{selectId}")
    @PermissionController(value = PermitType.PLATFORM,operationName = "上移精选")
    public ResponseInfo moveUp(@PathVariable Long selectId) {
        if (null == selectId) {
            return paramError();
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        GiftcardSelect giftcardSelect = new GiftcardSelect();
        giftcardSelect.setId(selectId);
        giftcardSelect.setModifier(currentUserDto.getUsername());
        return new ResponseInfo(giftcardSelectService.moveUp(giftcardSelect));
    }

    /**
     * 下移
     * @param selectId
     * @return
     */
    @RequestMapping("/moveDown/{selectId}")
    @PermissionController(value = PermitType.PLATFORM,operationName = "下移精选")
    public ResponseInfo moveDown(@PathVariable Long selectId) {
        if (null == selectId) {
            return paramError();
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        GiftcardSelect giftcardSelect = new GiftcardSelect();
        giftcardSelect.setId(selectId);
        giftcardSelect.setModifier(currentUserDto.getUsername());
        return new ResponseInfo(giftcardSelectService.moveDown(giftcardSelect));
    }
}
