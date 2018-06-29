package com.topaiebiz.trade.order.core.order.handler.common;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import com.topaiebiz.member.dto.address.MemberAddressDto;
import com.topaiebiz.merchant.dto.template.FreightTemplateDTO;
import com.topaiebiz.merchant.dto.template.FreightTemplateDetailDTO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderGoodsBO;
import com.topaiebiz.trade.order.core.order.context.AddressContext;
import com.topaiebiz.trade.order.core.order.context.SkuContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.facade.FreightTemplateServiceFacade;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import com.topaiebiz.trade.order.util.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.topaiebiz.merchant.constants.MerchantConstants.FreightPriceType.*;
import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.ADDRESS_NOT_SUPPORT;

/***
 * @author yfeng
 * @date 2018-01-15 19:04
 */
@Component("goodsFreightHandler")
@Slf4j
public class GoodsFreightHandler implements OrderSubmitHandler {

    @Autowired
    private FreightTemplateServiceFacade freightTemplateServiceFacade;

    @Override
    public void handle(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        Map<Long, GoodsSkuDTO> skuMap = SkuContext.get();
        MemberAddressDto addressDto = AddressContext.get();

        //请求无地址参数
        if (addressDto == null) {
            return;
        }

        //step 1 : 批量查询每个商品的运费模板信息
        List<Long> templateIds = skuMap.values().stream().map(item -> item.getItem().getLogisticsId()).collect(Collectors.toList());
        Map<Long, FreightTemplateDTO> freightDatas = freightTemplateServiceFacade.getFreightTemplete(templateIds);

        //step 2 : 计算每个商品的运费信息
        for (StoreOrderBO storeOrderBO : submitContext.getStoreOrderMap().values()) {
            for (StoreOrderGoodsBO orderGoodsBO : storeOrderBO.getGoodsList()) {
                GoodsSkuDTO sku = orderGoodsBO.getGoods();
                FreightTemplateDTO freightTemplateDTO = freightDatas.get(sku.getItem().getLogisticsId());

                //校验商品在地址区域是否可以卖，计算商品的运费
                caculateFreightPrice(addressDto, orderGoodsBO, freightTemplateDTO);
            }
            storeOrderBO.updatePrice();
        }
    }

    private void caculateFreightPrice(MemberAddressDto addressDto, StoreOrderGoodsBO orderGoodsBO, FreightTemplateDTO template) {
        if (template == null) {
            if (needValidateAddresSupport()) {
                log.error("Item {} 无运费模板", orderGoodsBO.getGoods().getItemId());
                throw new GlobalException(ADDRESS_NOT_SUPPORT);
            } else {
                //不计算运费，直接返回
                return;
            }
        }
        Long cityId = addressDto.getCityId();
        GoodsSkuDTO sku = orderGoodsBO.getGoods();

        //step 1 : 计算目标运费模板详情
        FreightTemplateDetailDTO templateDetail = getMatchTemplateDetail(orderGoodsBO.getGoods(), cityId, template);
        if (templateDetail == null) {
            if (needValidateAddresSupport()) {
                log.warn("sku {} 不支持 cityId: {} 运费模板: {}", sku.getId(), cityId, template.getId());
                throw new GlobalException(ADDRESS_NOT_SUPPORT);
            } else {
                //不计算运费，直接返回
                return;
            }
        }

        //step 2 : 计算商品运费
        Integer priceType = template.getPricing();
        BigDecimal goodsFreight = getPriceFieldVal(templateDetail, priceType, sku, orderGoodsBO.getGoodsNum());

        //step 3 : 设置商品运费
        orderGoodsBO.setFreight(goodsFreight);
        orderGoodsBO.updatePrice();
    }

    protected boolean needValidateAddresSupport() {
        return false;
    }

    private FreightTemplateDetailDTO getMatchTemplateDetail(GoodsSkuDTO sku, Long cityId, FreightTemplateDTO template) {
        if (CollectionUtils.isEmpty(template.getFreightTempleteDetailList())) {
            log.error("Item:{}没有运费模板", sku.getItemId());
            return null;
        }

        FreightTemplateDetailDTO defaultTemplateDetail = null;
        for (FreightTemplateDetailDTO templateDetail : template.getFreightTempleteDetailList()) {
            if (templateDetail.isDefaultFreight()) {
                defaultTemplateDetail = templateDetail;
            }
            List<Long> cityIds = templateDetail.getSupportCityIds();
            if (CollectionUtils.isEmpty(cityIds)) {
                continue;
            }
            if (cityIds.contains(cityId)) {
                return templateDetail;
            }
        }

        //若是仅限指定区域购买，不能允许使用默认模板详情
        if (template.hasSaleAreaLimit()) {
            return null;
        }

        //若找不到运费模板，则使用默认运费模板
        return defaultTemplateDetail;
    }

    private BigDecimal getPriceFieldVal(FreightTemplateDetailDTO tplDetail, Integer priceType, GoodsSkuDTO sku, Long goodsNum) {
        BigDecimal totalVolume = BigDecimal.ZERO;
        if (VOLUME.getValue().equals(priceType) || WEIGHT.getValue().equals(priceType)) {
            totalVolume = MathUtil.mutliply(sku.getItem().getWeightBulk(), goodsNum);
        } else if (GOODS_NUM.getValue().equals(priceType)) {
            totalVolume = new BigDecimal(goodsNum);
        }
        // 基础运费
        BigDecimal basicFreight = tplDetail.getFirstPrice();

        //不满足增量运费条件,只有基础运费
        if (MathUtil.greateEq(tplDetail.getFirstNum(), totalVolume)) {
            log.warn("运费模板详情：{}", JSON.toJSONString(tplDetail));
            return basicFreight;
        }
        if (!MathUtil.greaterThanZero(tplDetail.getAddNum())) {
            //没有配置结贴增长规则
            log.warn("运费模板详情：{}", JSON.toJSONString(tplDetail));
            return basicFreight;
        }

        //增量运费 =  (总量 - 基础) * 步长数
        BigDecimal extVolume = totalVolume.subtract(tplDetail.getFirstNum());
        BigDecimal extStep = extVolume.divide(tplDetail.getAddNum(), 0, BigDecimal.ROUND_DOWN);
        BigDecimal extFreight = extStep.multiply(tplDetail.getAddPrice());

        //基础运费 + 增量运费
        return basicFreight.add(extFreight);
    }
}