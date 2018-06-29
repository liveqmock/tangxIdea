package com.topaiebiz.promotion.mgmt.controller;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.promotion.mgmt.dto.PromotionGoodsDto;
import com.topaiebiz.promotion.mgmt.service.SecKillService;
import com.topaiebiz.promotion.mgmt.vo.PromotionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/secKill/mgmt", method = RequestMethod.POST)
public class SecKillController {

    @Autowired
    private SecKillService secKillService;

    /**
     * 导入秒杀活动商品（导入功能暂时无法判断平台端）
     *
     * @param file        导入文件
     * @param promotionId
     * @return
     */
    @RequestMapping("/importGoods/{promotionId}")
//    @PermissionController(value = PermitType.PLATFORM, operationName = "导入秒杀活动商品")
    public ResponseInfo importGoods(@RequestParam("file") MultipartFile file, @PathVariable("promotionId") Long promotionId) {
        String res = secKillService.importGoods(file, promotionId);
        return new ResponseInfo(res);
    }

    /**
     * 预览秒杀商品
     *
     * @param promotionVO 活动请求参数
     * @return
     */
    @RequestMapping("/previewGoods")
    public ResponseInfo previewGoods(@RequestBody PromotionVO promotionVO) {
        PageInfo<PromotionGoodsDto> pageInfo = secKillService.previewGoods(promotionVO);
        return new ResponseInfo(pageInfo);
    }

}
