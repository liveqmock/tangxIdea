package com.topaiebiz.promotion.mgmt.service.impl.init.data;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.api.GoodsSkuApi;
import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import com.topaiebiz.goods.dto.sku.ItemDTO;
import com.topaiebiz.promotion.common.util.ReadExcelUtil;
import com.topaiebiz.promotion.mgmt.component.ExcelRow;
import com.topaiebiz.promotion.mgmt.component.excel.BoxActivityItemRowImpl;
import com.topaiebiz.promotion.mgmt.component.excel.FloorCardRowImpl;
import com.topaiebiz.promotion.mgmt.component.excel.FloorGoodsRowImpl;
import com.topaiebiz.promotion.mgmt.component.excel.PromotionGoodsRowImpl;
import com.topaiebiz.promotion.mgmt.dao.BoxActivityItemDao;
import com.topaiebiz.promotion.mgmt.dao.FloorCardDao;
import com.topaiebiz.promotion.mgmt.dao.FloorGoodsDao;
import com.topaiebiz.promotion.mgmt.dao.PromotionGoodsDao;
import com.topaiebiz.promotion.mgmt.dto.init.data.InitBoxActivityItemDTO;
import com.topaiebiz.promotion.mgmt.dto.init.data.InitFloorCardDTO;
import com.topaiebiz.promotion.mgmt.dto.init.data.InitFloorGoodsDTO;
import com.topaiebiz.promotion.mgmt.dto.init.data.InitPromotionGoodsDTO;
import com.topaiebiz.promotion.mgmt.entity.FloorCardEntity;
import com.topaiebiz.promotion.mgmt.entity.FloorGoodsEntity;
import com.topaiebiz.promotion.mgmt.entity.PromotionGoodsEntity;
import com.topaiebiz.promotion.mgmt.entity.box.BoxActivityItemEntity;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEntry;
import com.topaiebiz.promotion.mgmt.service.InitDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.topaiebiz.promotion.constants.PromotionConstants.InitDataRecord.*;
import static com.topaiebiz.promotion.constants.PromotionConstants.VariableExceptionCode.READ_FILE;

@Slf4j
@Service
public class InitDataServiceImpl implements InitDataService {

    @Autowired
    private BoxActivityItemDao boxActivityItemDao;
    @Autowired
    private FloorGoodsDao floorGoodsDao;
    @Autowired
    private PromotionGoodsDao promotionGoodsDao;
    @Autowired
    private FloorCardDao floorCardDao;
    @Autowired
    private GoodsApi goodsApi;
    @Autowired
    private GoodsSkuApi goodsSkuApi;

    @Override
    public <T> List<T> readExcel(MultipartFile file, Integer type) {
        //获奖信息列表
        List<T> entityList;
        try {
            ExcelRow excelRow;
            if (PROMOTION_GOODS.equals(type)) {
                excelRow = new PromotionGoodsRowImpl();
            } else if (FLOOR_GOODS.equals(type)) {
                excelRow = new FloorGoodsRowImpl();
            } else if (BOX_ACTIVITY_ITEMS.equals(type)) {
                excelRow = new BoxActivityItemRowImpl();
            } else {
                excelRow = new FloorCardRowImpl();
            }
            entityList = ReadExcelUtil.readList(file, excelRow);
        } catch (Exception e) {
            throw new GlobalException(new PromotionExceptionEntry(READ_FILE, e.getMessage()));
        }
        return entityList;
    }

    @Override
    @Transactional
    public Integer addBoxActivityItemRecords(List<InitBoxActivityItemDTO> boxActivityItemList) {
        //新增数量
        Integer count = 0;
        if (CollectionUtils.isEmpty(boxActivityItemList)) {
            return null;
        }

        List<Long> promotionBoxIds = boxActivityItemList.stream().map(item -> item.getPromotionBoxId()).distinct().collect(Collectors.toList());

        //删除原有数据
        BoxActivityItemEntity delData = new BoxActivityItemEntity();
        delData.cleanInit();
        delData.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        delData.setLastModifiedTime(new Date());
        EntityWrapper<BoxActivityItemEntity> cond = new EntityWrapper();
        cond.in("promotionBoxId", promotionBoxIds);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        boxActivityItemDao.update(delData, cond);

        //新增活动商品记录
        for (InitBoxActivityItemDTO dto : boxActivityItemList) {
            BoxActivityItemEntity entity = new BoxActivityItemEntity();
            BeanCopyUtil.copy(dto, entity);
            count += boxActivityItemDao.insert(entity);
        }

        return count;
    }

    @Override
    @Transactional
    public Integer addFloorGoodsRecords(List<InitFloorGoodsDTO> floorGoodsList) {
        //新增数量
        Integer count = 0;
        if (CollectionUtils.isEmpty(floorGoodsList)) {
            return null;
        }

        //所有code
        List<String> floorCodes = floorGoodsList.stream().map(floorGoods -> floorGoods.getFloorCode()).distinct().collect(Collectors.toList());

        //删除原有数据
        FloorGoodsEntity delData = new FloorGoodsEntity();
        delData.cleanInit();
        delData.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        delData.setLastModifiedTime(new Date());
        EntityWrapper<FloorGoodsEntity> cond = new EntityWrapper();
        cond.in("floorCode", floorCodes);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        floorGoodsDao.update(delData, cond);

        //新增活动商品记录
        for (InitFloorGoodsDTO dto : floorGoodsList) {
            FloorGoodsEntity entity = new FloorGoodsEntity();
            BeanCopyUtil.copy(dto, entity);
            count += floorGoodsDao.insert(entity);
        }

        return count;
    }

    @Override
    @Transactional
    public Integer addPromotionGoodsRecords(List<InitPromotionGoodsDTO> promotionGoodsList, Long promotionId) {
        //新增数量
        Integer count = 0;
        if (CollectionUtils.isEmpty(promotionGoodsList)) {
            return null;
        }

        //删除原有数据
        EntityWrapper<PromotionGoodsEntity> cond = new EntityWrapper();
        cond.eq("promotionId", promotionId);
        promotionGoodsDao.delete(cond);

        //新增活动商品记录
        for (InitPromotionGoodsDTO dto : promotionGoodsList) {
            PromotionGoodsEntity entity = new PromotionGoodsEntity();
            BeanCopyUtil.copy(dto, entity);
            entity.setPromotionId(promotionId);
            //从数据库读出的值
            ItemDTO item = goodsApi.getItem(dto.getItemId());
            if (item != null) {
                //商家ID
                entity.setStoreId(item.getBelongStore());
            }

            GoodsSkuDTO goodsSku = goodsSkuApi.getGoodsSku(dto.getGoodsSkuId());
            if (goodsSku != null) {
                //原有库存
                int repertoryNum = goodsSku.getStockNumber().intValue();
                if (goodsSku.getStockNumber() == null) {
                    repertoryNum = 0;
                }
                entity.setRepertoryNum(repertoryNum);
                Integer promotionNum = dto.getPromotionNum();
                //活动库存
                if (repertoryNum < promotionNum.intValue()) {
                    promotionNum = repertoryNum;
                }
                entity.setPromotionNum(promotionNum);
                //商品优惠价格
                BigDecimal goodPrice = goodsSku.getPrice();
                if (goodPrice == null) {
                    goodPrice = BigDecimal.ZERO;
                }
                entity.setDiscountValue(goodPrice.setScale(2, BigDecimal.ROUND_HALF_UP).subtract(dto.getPromotionPrice()));
            }
            count += promotionGoodsDao.insert(entity);
        }

        return count;
    }

    @Override
    public Integer addFloorCardRecords(List<InitFloorCardDTO> floorCardList) {
        //新增数量
        Integer count = 0;
        if (CollectionUtils.isEmpty(floorCardList)) {
            return null;
        }

        //所有code
        List<String> floorCodes = floorCardList.stream().map(floorGoods -> floorGoods.getFloorCode()).distinct().collect(Collectors.toList());

        //删除原有数据
        FloorCardEntity delData = new FloorCardEntity();
        delData.cleanInit();
        delData.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        delData.setLastModifiedTime(new Date());
        EntityWrapper<FloorCardEntity> cond = new EntityWrapper();
        cond.in("floorCode", floorCodes);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        floorCardDao.update(delData, cond);

        //新增活动商品记录
        for (InitFloorCardDTO dto : floorCardList) {
            FloorCardEntity entity = new FloorCardEntity();
            BeanCopyUtil.copy(dto, entity);
            count += floorCardDao.insert(entity);
        }

        return count;
    }

}
