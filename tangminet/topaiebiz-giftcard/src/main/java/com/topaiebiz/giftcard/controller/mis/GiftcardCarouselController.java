package com.topaiebiz.giftcard.controller.mis;

import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.sun.org.apache.regexp.internal.RE;
import com.topaiebiz.giftcard.controller.AbstractController;
import com.topaiebiz.giftcard.entity.GiftcardCarousel;
import com.topaiebiz.giftcard.enums.GiftcardExceptionEnum;
import com.topaiebiz.giftcard.service.GiftcardCarouselService;
import com.topaiebiz.giftcard.vo.GiftcardCarouselVO;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * @description: 轮播图
 * @author: Jeff Chen
 * @date: created in 下午8:25 2018/1/17
 */
@RestController
@RequestMapping(value = "/giftcard/carousel",method = RequestMethod.POST)
public class GiftcardCarouselController extends AbstractController{

    @Autowired
    private GiftcardCarouselService giftcardCarouselService;

    /**
     * 添加
     * @param giftcardCarouselVO
     * @return
     */
    @RequestMapping("/add")
    @PermissionController(value = PermitType.PLATFORM,operationName = "添加轮播")
    public ResponseInfo add(@RequestBody @Valid GiftcardCarouselVO giftcardCarouselVO, BindingResult result) {
        ResponseInfo responseInfo = validParam(result);
        if (null != responseInfo) {
            return responseInfo;
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        GiftcardCarousel giftcardCarousel = new GiftcardCarousel();
        giftcardCarousel.setImgUrl(giftcardCarouselVO.getImgUrl());
        giftcardCarousel.setLinkUrl(giftcardCarouselVO.getLinkUrl());
        giftcardCarousel.setTitle(giftcardCarouselVO.getTitle());
        giftcardCarousel.setType(giftcardCarouselVO.getType());
        giftcardCarousel.setCreator(currentUserDto.getUsername());
        giftcardCarousel.setCreatedTime(new Date());
        giftcardCarousel.setModifiedTime(new Date());
        giftcardCarousel.setModifier(currentUserDto.getUsername());
        return new ResponseInfo(giftcardCarouselService.insert(giftcardCarousel));
    }

    /**
     * 查询
     * @return
     */
    @RequestMapping("/query")
    @PermissionController(value = PermitType.PLATFORM,operationName = "查询轮播")
    public ResponseInfo query() {
        List<GiftcardCarouselVO> giftcardCarouselVOList = new ArrayList<>();
        List<GiftcardCarousel> giftcardCarouselList = giftcardCarouselService.selectList(null);
        if (!CollectionUtils.isEmpty(giftcardCarouselList)) {
            giftcardCarouselList.forEach(giftcardCarousel -> {
                GiftcardCarouselVO giftcardCarouselVO = new GiftcardCarouselVO();
                BeanCopyUtil.copy(giftcardCarousel, giftcardCarouselVO);
                giftcardCarouselVO.setCId(giftcardCarousel.getId());
                giftcardCarouselVOList.add(giftcardCarouselVO);
            });
        }
        Map<String, Object> res = new HashMap<>();
        res.put("records", giftcardCarouselVOList);
        return new ResponseInfo(res);
    }
    /**
     * 添加
     * @param giftcardCarouselVO
     * @return
     */
    @RequestMapping("/edit")
    @PermissionController(value = PermitType.PLATFORM,operationName = "编辑轮播")
    public ResponseInfo edit(@RequestBody @Valid GiftcardCarouselVO giftcardCarouselVO, BindingResult result) {
        ResponseInfo responseInfo = validParam(result);
        if (null != responseInfo) {
            return responseInfo;
        }
        if (null == giftcardCarouselVO.getCId()) {
            return paramError();
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        GiftcardCarousel giftcardCarousel = new GiftcardCarousel();
        giftcardCarousel.setId(giftcardCarouselVO.getCId());
        giftcardCarousel.setImgUrl(giftcardCarouselVO.getImgUrl());
        giftcardCarousel.setLinkUrl(giftcardCarouselVO.getLinkUrl());
        giftcardCarousel.setTitle(giftcardCarouselVO.getTitle());
        giftcardCarousel.setType(giftcardCarouselVO.getType());
        giftcardCarousel.setModifiedTime(new Date());
        giftcardCarousel.setModifier(currentUserDto.getUsername());
        return new ResponseInfo(giftcardCarouselService.updateById(giftcardCarousel));
    }

    /**
     * 轮播详情
     * @param cid
     * @return
     */
    @RequestMapping("/detail/{cid}")
    @PermissionController(value = PermitType.PLATFORM,operationName = "轮播详情")
    public ResponseInfo detail(@PathVariable Long cid) {
        return new ResponseInfo(giftcardCarouselService.selectById(cid));
    }
}
