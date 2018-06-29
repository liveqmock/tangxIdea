package com.topaiebiz.giftcard.controller.mis;

import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.SystemExceptionEnum;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.giftcard.controller.AbstractController;
import com.topaiebiz.giftcard.entity.GiftcardLabel;
import com.topaiebiz.giftcard.service.GiftcardLabelService;
import com.topaiebiz.giftcard.vo.DataMap;
import com.topaiebiz.giftcard.vo.GiftcardLabelVO;
import com.topaiebiz.giftcard.vo.LabelPageReq;
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
 * @description: 礼卡标签
 * @author: Jeff Chen
 * @date: created in 下午1:36 2018/1/15
 */
@RestController
@RequestMapping(value = "/giftcard/label",method = RequestMethod.POST)
public class GiftcardLabelController extends AbstractController{
    @Autowired
    private GiftcardLabelService giftcardLabelService;

    /**
     * 添加
     * @param giftcardLabelVO
     * @param result
     * @return
     */
    @RequestMapping("/add")
    @PermissionController(value = PermitType.PLATFORM,operationName = "添加标签")
    public ResponseInfo add(@Valid @RequestBody GiftcardLabelVO giftcardLabelVO, BindingResult result) {
        ResponseInfo responseInfo = validParam(result);
        if (null != responseInfo) {
            return responseInfo;
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        GiftcardLabel giftcardLabel = new GiftcardLabel();
        giftcardLabel.setLabelName(giftcardLabelVO.getLabelName());
        giftcardLabel.setSamplePic(giftcardLabelVO.getSamplePic());
        giftcardLabel.setRemark(giftcardLabelVO.getRemark());
        giftcardLabel.setCreator(currentUserDto.getUsername());
        giftcardLabel.setCreatedTime(new Date());
        giftcardLabel.setModifier(currentUserDto.getUsername());
        giftcardLabel.setModifiedTime(giftcardLabel.getCreatedTime());
        return new ResponseInfo(giftcardLabelService.insert(giftcardLabel));
    }

    /**
     * 编辑
     * @param giftcardLabelVO
     * @param result
     * @return
     */
    @RequestMapping("/edit")
    @PermissionController(value = PermitType.PLATFORM,operationName = "编辑标签")
    public ResponseInfo edit(@Valid @RequestBody GiftcardLabelVO giftcardLabelVO, BindingResult result) {
        ResponseInfo responseInfo = validParam(result);
        if (null != responseInfo) {
            return responseInfo;
        }
        if (null == giftcardLabelVO.getLabelId()) {
            return paramError();
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        GiftcardLabel giftcardLabel = new GiftcardLabel();
        giftcardLabel.setId(giftcardLabelVO.getLabelId());
        giftcardLabel.setLabelName(giftcardLabelVO.getLabelName());
        giftcardLabel.setSamplePic(giftcardLabelVO.getSamplePic());
        giftcardLabel.setRemark(giftcardLabelVO.getRemark());
        giftcardLabel.setModifier(currentUserDto.getUsername());
        giftcardLabel.setModifiedTime(new Date());
        return new ResponseInfo(giftcardLabelService.updateById(giftcardLabel));
    }

    /**
     * 删除
     * @return
     */
    @RequestMapping("/delete/{labelId}")
    @PermissionController(value = PermitType.PLATFORM,operationName = "删除标签")
    public ResponseInfo delete(@PathVariable Long labelId) {
        if (null == labelId) {
            return paramError();
        }
        GiftcardLabel giftcardLabel = new GiftcardLabel();
        giftcardLabel.setId(labelId);
        giftcardLabel.setDelFlag(1);
        return new ResponseInfo(giftcardLabelService.updateById(giftcardLabel));
    }


    /**
     * 分区查询
     * @param req
     * @return
     */
    @RequestMapping("/query")
    @PermissionController(value = PermitType.PLATFORM,operationName = "查询标签")
    public ResponseInfo query(@RequestBody LabelPageReq req) {
        PagePO pagePO = new PagePO();
        pagePO.setPageSize(req.getPageSize());
        pagePO.setPageNo(req.getPageNo());
        return new ResponseInfo(giftcardLabelService.queryGiftcardLabel(pagePO, req.getLabelName()));
    }

    /**
     * 标签下拉框数据
     * @return
     */
    @RequestMapping("/all")
    public ResponseInfo all() {
        return new ResponseInfo(giftcardLabelService.allLabelList());
    }
}
