package com.topaiebiz.goods.repair.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.repair.SkuCommentRepairService;
import com.topaiebiz.goods.repair.service.CommentRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hecaifeng on 2018/3/26.
 */
@RestController
@RequestMapping(value = "/goods/repair/comment/", method = RequestMethod.POST)
public class CommentController {

    @Autowired
    private CommentRepairService commentRepairService;

    @Autowired
    private SkuCommentRepairService skuCommentRepairService;


    /**
     * 给商品评价添加itemId
     *
     * @return
     */
    @RequestMapping(path = "/addCommentItemId")
    public ResponseInfo addCommentItemId() {
        commentRepairService.addCommentItemId();
        return new ResponseInfo();
    }

    @RequestMapping(path = "/fixDate")
    public ResponseInfo fixData() {
        skuCommentRepairService.start();
        return new ResponseInfo();
    }

    /**
     * 删除评价缓存
     *
     * @return
     */
    @RequestMapping(path = "/removeCommentRedis")
    public ResponseInfo removeCommentRedis() {
        return new ResponseInfo(commentRepairService.removeCommentRedis());
    }
}
;