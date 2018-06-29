package com.topaiebiz.giftcard.controller.app;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.giftcard.controller.AbstractController;
import com.topaiebiz.giftcard.enums.CardAttrEnum;
import com.topaiebiz.giftcard.service.*;
import com.topaiebiz.giftcard.vo.GiftcardSelectReq;
import com.topaiebiz.giftcard.vo.GiftcardShowReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: C端礼卡展示
 * @author: Jeff Chen
 * @date: created in 下午5:29 2018/1/24
 */
@RestController
@RequestMapping("/app/giftcard/show")
public class GiftcardShowController extends AbstractController {
    @Autowired
    private GiftcardLabelService giftcardLabelService;

    @Autowired
    private GiftcardSelectService giftcardSelectService;

    @Autowired
    private GiftcardBatchService giftcardBatchService;

    @Autowired
    private GiftcardCarouselService giftcardCarouselService;

    /**
     * 查询可用的标签
     * @return
     */
    @RequestMapping("/labelList")
    public ResponseInfo labelList() {
        return new ResponseInfo(giftcardLabelService.showLabelList());
    }

    /**
     * 按标签查询:默认普通卡
     * @param giftcardShowReq
     * @return
     */
    @RequestMapping("/listByLabel")
    public ResponseInfo listByLabel(@RequestBody GiftcardShowReq giftcardShowReq) {
        if (null == giftcardShowReq || null == giftcardShowReq.getLabelId()) {
            return paramError();
        }
        giftcardShowReq.setCardAttr(CardAttrEnum.COMMON.getId());
        return new ResponseInfo(giftcardBatchService.selectGiftcardShow(giftcardShowReq));
    }

    /**
     * 按属性查询:默认联名卡
     * @param giftcardShowReq
     * @return
     */
    @RequestMapping("/listByAttr")
    public ResponseInfo listByAttr(@RequestBody GiftcardShowReq giftcardShowReq) {
        if (null == giftcardShowReq) {
            return paramError();
        }
        if (null == giftcardShowReq.getCardAttr()) {
            giftcardShowReq.setCardAttr(CardAttrEnum.JOINT.getId());
        }
        return new ResponseInfo(giftcardBatchService.selectGiftcardShow(giftcardShowReq));
    }

    /**
     * 精选列表
     *
     * @return
     */
    @RequestMapping("/selectList")
    public ResponseInfo selectList(@RequestBody GiftcardSelectReq giftcardSelectReq) {
        return new ResponseInfo(giftcardSelectService.querySelectShow(giftcardSelectReq));
    }

    /**
     * 卡片详情
     *
     * @return
     */
    @RequestMapping("/detail")
    public ResponseInfo detail(@RequestBody GiftcardShowReq giftcardShowReq) {
        if (null == giftcardShowReq || null == giftcardShowReq.getBatchId()) {
            return paramError();
        }
        return new ResponseInfo(giftcardBatchService.detailGiftcardShow(giftcardShowReq));
    }

    /**
     * 轮播列表
     * @return
     */
    @RequestMapping("/carousel")
    public ResponseInfo carousel() {
        return new ResponseInfo(giftcardCarouselService.queryAll());
    }

    /**
     * 查询指定批次的圈定商品信息
     * @return
     */
    @RequestMapping("/goods/{batchId}")
    public ResponseInfo getGiftcardGoods(@PathVariable Long batchId) {
        return new ResponseInfo(giftcardBatchService.getGiftcardGoodsByGoodsIds(giftcardBatchService.getGiftcardGoodsByBatchId(batchId)));
    }
}
