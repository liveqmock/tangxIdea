package com.topaiebiz.decorate.transformer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.decorate.component.ItemListVO;
import com.topaiebiz.decorate.component.ItemVO;
import com.topaiebiz.decorate.constant.Constant;
import com.topaiebiz.decorate.dao.ComponentItemDao;
import com.topaiebiz.decorate.dto.ComponentContentDto;
import com.topaiebiz.decorate.dto.ItemDetailDto;
import com.topaiebiz.decorate.entity.ComponentItemEntity;
import com.topaiebiz.decorate.exception.DecorateExcepionEnum;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.dto.sku.GoodsDecorateDTO;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ItemListTransformer extends ComponentTransformer<ItemListVO> {

    @Autowired
    private GoodsApi goodsApi;

    @Autowired
    private ComponentItemDao componentItemDao;

    @Override
    public ItemListVO transform(String content) {
        return JSON.parseObject(content, ItemListVO.class);
    }

    @Override
    public void validate(ItemListVO value) {
        //商品不可为空
        if (CollectionUtils.isEmpty(value.getItemVOS())) {
            throw new GlobalException(DecorateExcepionEnum.ITEM_NOT_NULL);
        }
    }

    @Override
    public String componentType() {
        return Constant.ITEM;
    }

    @Override
    public String getPageContent(String dbConent) {
        ItemListVO itemListVO = this.transform(dbConent);
        List<ItemVO> itemVOS = itemListVO.getItemVOS();
        List<GoodsDecorateDTO> params = new ArrayList<>();
        for (ItemVO itemVO : itemVOS) {
            GoodsDecorateDTO goodsDecorateDTO = new GoodsDecorateDTO();
            goodsDecorateDTO.setSortNo(itemVO.getSortNo());
            goodsDecorateDTO.setGoodsId(itemVO.getItemId());
            params.add(goodsDecorateDTO);
        }
        List<GoodsDecorateDTO> goodsDecorateDTOS = goodsApi.getGoodsDecorate(params);
        ItemDetailDto itemDetailDto = new ItemDetailDto();
        itemDetailDto.setGoodsDecorateDTOS(goodsDecorateDTOS);
        itemDetailDto.setPrice(itemListVO.getPrice());
        itemDetailDto.setEvaluations(itemListVO.getEvaluations());
        itemDetailDto.setIntegralDiscount(itemListVO.getIntegralDiscount());
        itemDetailDto.setOriginalPrice(itemListVO.getOriginalPrice());
        itemDetailDto.setSales(itemListVO.getSales());
        itemDetailDto.setTitle(itemListVO.getTitle());
        return JSON.toJSONString(itemDetailDto);
    }

    @Override
    public void dealItem(ComponentContentDto componentContentDto) {
        //先删除该组件下所有的商品
        EntityWrapper<ComponentItemEntity> componentItemCondition = new EntityWrapper<>();
        componentItemCondition.eq("componentId", componentContentDto.getComponentId());
        ComponentItemEntity modifyEntity = new ComponentItemEntity();
        modifyEntity.cleanInit();
        modifyEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        modifyEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        modifyEntity.setLastModifiedTime(new Date());
        componentItemDao.update(modifyEntity, componentItemCondition);

        ItemListVO itemListVO = this.transform(componentContentDto.getContent());
        List<ItemVO> itemVOS = itemListVO.getItemVOS();
        Long componentId = componentContentDto.getComponentId();
        List<ComponentItemEntity> componentItemEntities = new ArrayList<>();
        for (ItemVO itemVO : itemVOS) {
            ComponentItemEntity componentItemEntity = new ComponentItemEntity();
            componentItemEntity.setComponentId(componentId);
            componentItemEntity.setItemId(itemVO.getItemId());
            componentItemEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
            componentItemEntity.setCreatedTime(new Date());
            componentItemEntities.add(componentItemEntity);
        }
        componentItemDao.insertBatch(componentItemEntities);
    }
}
