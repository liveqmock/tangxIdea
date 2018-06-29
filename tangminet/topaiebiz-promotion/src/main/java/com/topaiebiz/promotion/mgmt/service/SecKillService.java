package com.topaiebiz.promotion.mgmt.service;

import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.promotion.mgmt.dto.PromotionGoodsDto;
import com.topaiebiz.promotion.mgmt.vo.PromotionVO;
import org.springframework.web.multipart.MultipartFile;

public interface SecKillService {

    /**
     * 导入秒杀活动商品
     *
     * @param file        导入文件
     * @param promotionId
     * @return
     */
    String importGoods(MultipartFile file, Long promotionId);

    /**
     * 预览秒杀商品
     *
     * @param promotionVO
     * @return
     */
    PageInfo<PromotionGoodsDto> previewGoods(PromotionVO promotionVO);
}
