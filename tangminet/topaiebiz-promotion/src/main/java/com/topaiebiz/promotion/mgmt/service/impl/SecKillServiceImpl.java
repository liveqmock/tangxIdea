package com.topaiebiz.promotion.mgmt.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.topaiebiz.goods.api.BackendCategoryApi;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.api.GoodsSkuApi;
import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import com.topaiebiz.goods.dto.sku.ItemDTO;
import com.topaiebiz.promotion.constants.PromotionConstants;
import com.topaiebiz.promotion.mgmt.dao.PromotionGoodsDao;
import com.topaiebiz.promotion.mgmt.dto.PromotionGoodsDto;
import com.topaiebiz.promotion.mgmt.dto.init.data.InitPromotionGoodsDTO;
import com.topaiebiz.promotion.mgmt.entity.PromotionGoodsEntity;
import com.topaiebiz.promotion.mgmt.service.InitDataService;
import com.topaiebiz.promotion.mgmt.service.SecKillService;
import com.topaiebiz.promotion.mgmt.vo.PromotionVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.topaiebiz.promotion.constants.PromotionConstants.InitDataRecord.PROMOTION_GOODS;

@Slf4j
@Service
public class SecKillServiceImpl implements SecKillService {
    @Autowired
    private InitDataService initDataService;
    @Autowired
    private PromotionGoodsDao promotionGoodsDao;
    @Autowired
    private GoodsSkuApi goodsSkuApi;
    @Autowired
    private GoodsApi goodsApi;
    @Autowired
    private BackendCategoryApi backendCategoryApi;

    @Override
    public String importGoods(MultipartFile file, Long promotionId) {
        List<InitPromotionGoodsDTO> list = initDataService.readExcel(file, PROMOTION_GOODS);
        Integer count = initDataService.addPromotionGoodsRecords(list, promotionId);
        return StringUtils.join("成功导入", count, "条数据");
    }

    @Override
    public PageInfo<PromotionGoodsDto> previewGoods(PromotionVO promotionVO) {
        Page<PromotionGoodsDto> page = PageDataUtil.buildPageParam(promotionVO);
        EntityWrapper<PromotionGoodsEntity> cond = new EntityWrapper<>();
        cond.eq("promotionId", promotionVO.getPromotionId());
        cond.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionGoodsEntity> goodsEntityList = promotionGoodsDao.selectPage(page, cond);
        List<PromotionGoodsDto> goodsList = BeanCopyUtil.copyList(goodsEntityList, PromotionGoodsDto.class);
        for (PromotionGoodsDto goods : goodsList) {
            ItemDTO item = goodsApi.getItem(goods.getItemId());
            if (item != null) {
                goods.setGoodsName(item.getName());
            }

            GoodsSkuDTO goodsSku = goodsSkuApi.getGoodsSku(goods.getGoodsSkuId());
            if (goodsSku != null) {
                if (!StringUtils.isBlank(goodsSku.getSaleFieldValue())) {
                    String saleFieldValue = backendCategoryApi.jointSaleFieldValue(goodsSku.getSaleFieldValue());
                    if (!StringUtils.isBlank(saleFieldValue)) {
                        goods.setSaleFieldValue(saleFieldValue);
                    }
                }
                goods.setGoodsPrice(goodsSku.getPrice());
                goods.setRepertoryNum(goodsSku.getStockNumber().intValue());
            }
        }
        page.setRecords(goodsList);
        return PageDataUtil.copyPageInfo(page);
    }

}
