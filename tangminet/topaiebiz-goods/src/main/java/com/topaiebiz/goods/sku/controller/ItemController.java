package com.topaiebiz.goods.sku.controller;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.goods.category.backend.dto.BackendCategorysDto;
import com.topaiebiz.goods.sku.dto.*;
import com.topaiebiz.goods.sku.service.GoodsSkuService;
import com.topaiebiz.goods.sku.service.ItemService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Description 商品sku控制层
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年10月3日 下午2:35:46
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping(value = "/goods/item",method = RequestMethod.POST)
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private GoodsSkuService goodsSkuService;

    /**
     * Description 商家商品信息列表分页检索出售中的商品
     * <p>
     * Author Hedda
     *
     * @param itemDto 商品信息dto
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家商品出售中列表分页检索")
    @RequestMapping(value = "/getMerchantListItemDto")
    public ResponseInfo getMerchantListItemDto(@RequestBody ItemDto itemDto) throws GlobalException {
        PageInfo<ItemDto> listItemDto = itemService.getMerchantListItemDto(itemDto);
        return new ResponseInfo(listItemDto);
    }

    /**
     * Description 商家商品信息列表分页检索仓库中的商品
     * <p>
     * Author Hedda
     *
     * @param itemDto 商品信息dto
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家商品仓库中列表分页检索")
    @RequestMapping(value = "/getMerchantListStoreItemDto")
    public ResponseInfo getMerchantListStoreItemDto(@RequestBody ItemDto itemDto) throws GlobalException {
        PageInfo<ItemDto> listItemDto = itemService.getMerchantListStoreItemDto(itemDto);
        return new ResponseInfo(listItemDto);
    }

    /**
     * Description 平台端商家商品信息列表分页检索
     * <p>
     * Author Hedda
     *
     * @param itemDto 商品信息dto
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台商家商品列表分页检索")
    @RequestMapping(value = "/getListMerchantItemDto")
    public ResponseInfo getListMerchantItemDto(@RequestBody ItemDto itemDto) throws GlobalException {
        PageInfo<ItemDto> listItemDto = itemService.getListMerchantItemDto(itemDto);
        return new ResponseInfo(listItemDto);
    }

    /**
     * Description 商家站点管理商品列表
     * <p>
     * Author Hedda
     *
     * @param itemDto 商品信息dto
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家商品出售中列表分页检索")
    @RequestMapping(value = "/getDecorateItem")
    public ResponseInfo getDecorateItem(@RequestBody ItemDto itemDto) throws GlobalException {
        PageInfo<ItemDto> listItemDto = itemService.getDecorateItem(itemDto);
        return new ResponseInfo(listItemDto);
    }

    /**
     * Description 商家营销活动需要展示的商品
     * <p>
     * Author Hedda
     *
     * @param itemDto 商品信息dto
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家营销活动需要展示的商品")
    @RequestMapping(value = "/getPromotionItem")
    public ResponseInfo getPromotionItem(@RequestBody ItemDto itemDto) throws GlobalException {
        PageInfo<ItemDto> listItemDto = itemService.getPromotionItem(itemDto);
        return new ResponseInfo(listItemDto);
    }



    /**
     * Description 平台端查询最近使用类目
     * <p>
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台查询最近使用类目")
    @RequestMapping(value = "/getItemRecentlyCategoryList")
    public ResponseInfo getItemRecentlyCategoryList() throws GlobalException {
        List<BackendCategorysDto> backendCategorysDto = itemService.getItemRecentlyCategoryList();
        return new ResponseInfo(backendCategorysDto);
    }

    /**
     * Description 商家端查询最近使用类目
     * <p>
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家查询最近使用类目")
    @RequestMapping(value = "/getMerchantItemRecentlyCategoryList")
    public ResponseInfo getMerchantItemRecentlyCategoryList() throws GlobalException {
        List<BackendCategorysDto> backendCategorysDto = itemService.getMerchantItemRecentlyCategoryList();
        return new ResponseInfo(backendCategorysDto);
    }

    /**
     * Description 商品item批量逻辑删除
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家商品批量删除")
    @RequestMapping(value = "/cancelItems")
    public ResponseInfo cancelItems(@RequestBody Long[] id) throws GlobalException {
        itemService.removeItems(id);
        return new ResponseInfo("删除成功！");
    }

    /**
     * Description 商品sku批量逻辑删除
     * <p>
     * Author Hedda
     *
     * @param id 商品sku的id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "商品sku批量删除")
    @RequestMapping(value = "/cancelGoodsSkus")
    public ResponseInfo cancelGoodsSkus(@RequestBody Long[] id) throws GlobalException {
        boolean b = goodsSkuService.removeGoodsSkus(id);
        return new ResponseInfo(b);
    }

    /**
     * Description 商品item批量上架
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家商品批量上架")
    @RequestMapping(value = "/putItems")
    public ResponseInfo putItems(@RequestBody Long[] id) throws GlobalException {
        itemService.putItems(id);
        return new ResponseInfo("上架成功！");
    }

    /**
     * Description 商品item批量下架
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商家商品批量下架")
    @RequestMapping(value = "/outItems")
    public ResponseInfo outItems(@RequestBody Long[] id) throws GlobalException {
        itemService.outItems(id);
        return new ResponseInfo("下架成功！");
    }

    /**
     * Description 商品item冻结
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商家商品冻结")
    @RequestMapping(value = "/freezeItem")
    public ResponseInfo freezeItem(@RequestBody Long[] id) throws GlobalException {
        itemService.freezeItem(id);
        return new ResponseInfo("冻结成功！");
    }

    /**
     * Description 商品item解冻
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商家商品解冻")
    @RequestMapping(value = "/unFreezeItem")
    public ResponseInfo unFreezeItem(@RequestBody Long[] id) throws GlobalException {
        itemService.unFreezeItem(id);
        return new ResponseInfo("解冻成功！");
    }

    /**
     * Description 商家商品item信息添加
     * <p>
     * Author Hedda
     *
     * @param itemDto 商品item信息
     * @param result  错误结果
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家商品添加")
    @RequestMapping(path = "/addMerchantItem")
    public ResponseInfo addMerchantItem(@Valid @RequestBody ItemDto itemDto, BindingResult result)
            throws GlobalException {
        /** 对商品字段进行校验 */
        if (result.hasErrors()) {
            /** 初始化非法参数的提示信息。 */
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            /** 获取非法参数异常信息对象，并抛出异常。 */
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        List<ItemPictureDto> itemPictureDtos = itemDto.getItemPictureDtos();
        List<GoodsSkuDto> goodsSkuDtos = itemDto.getGoodsSkuDtos();
        return new ResponseInfo(itemService.saveMerchantItem(itemDto, itemPictureDtos, goodsSkuDtos));
    }

    /**
     * Description 商品item信息修改
     * <p>
     * Author Hedda
     *
     * @param itemDto 商品item信息
     * @param result  错误结果
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家商品修改")
    @RequestMapping(path = "/editItem")
    public ResponseInfo editItem(@Valid @RequestBody ItemDto itemDto, BindingResult result) throws GlobalException {
        /** 对商品字段进行校验 */
        if (result.hasErrors()) {
            /** 初始化非法参数的提示信息。 */
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            /** 获取非法参数异常信息对象，并抛出异常。 */
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        List<ItemPictureDto> itemPictureDtos = itemDto.getItemPictureDtos();
        List<GoodsSkuDto> goodsSkuDtos = itemDto.getGoodsSkuDtos();
        return new ResponseInfo(itemService.modifyItem(itemDto, itemPictureDtos, goodsSkuDtos));
    }

    /**
     * Description 根据id查询商品信息回显
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商家商品回显")
    @RequestMapping(path = "/findItemById/{id}")
    public ResponseInfo findItemById(@PathVariable Long id) throws GlobalException {
        ItemDto itemDto = itemService.findItemById(id);
        List<GoodsSkuDto> goodsSkuDtos = itemService.findGoodsSkuById(id);
        List<ItemPictureDto> itemPictureDtos = itemService.findItemPictureById(id);
        if (!(goodsSkuDtos == null || goodsSkuDtos.size() == 0)) {
            itemDto.setGoodsSkuDtos(goodsSkuDtos);
        }
        if (!(itemPictureDtos == null || itemPictureDtos.size() == 0)) {
            itemDto.setItemPictureDtos(itemPictureDtos);
        }
        return new ResponseInfo(itemDto);
    }

    /**
     * Description 根据itemId查询商品sku信息
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "根据itemId查询商品sku信息")
    @RequestMapping(path = "/findSkuById/{id}")
    public ResponseInfo findSkuById(@PathVariable  Long id) throws GlobalException {
        List<GoodsSkuDto> goodsSkuDtos = itemService.findGoodsSkuById(id);
        return new ResponseInfo(goodsSkuDtos);
    }

    /**
     * Description 根据itemId查询商品sku信息
     * <p>
     * Author Hedda
     *
     * @param itemId 商品item的id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "根据itemId查询商品sku信息")
    @RequestMapping(path = "/findSku/{itemId}")
    public ResponseInfo findSku(@PathVariable Long itemId) throws GlobalException {
        List<GoodsSkusDto> goodsSkuDtos = itemService.findGoodsSku(itemId);
        return new ResponseInfo(goodsSkuDtos);
    }



    /**
     * Description 商家根据id查询商品信息
     * <p>
     * Author Hedda
     *
     * @param id 商品item的id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商家根据id查询商品信息")
    @RequestMapping(path = "/findMerchantItemById/{id}")
    public ResponseInfo findMerchantItemById(@PathVariable  Long id) throws GlobalException {
        ItemDto itemDto = itemService.findMerchantItemById(id);
        List<GoodsSkuDto> goodsSkuDtos = itemService.findGoodsSkuById(id);
        List<ItemPictureDto> itemPictureDtos = itemService.findItemPictureById(id);
        if (!(goodsSkuDtos == null || goodsSkuDtos.size() == 0)) {
            itemDto.setGoodsSkuDtos(goodsSkuDtos);
        }
        if (!(itemPictureDtos == null || itemPictureDtos.size() == 0)) {
            itemDto.setItemPictureDtos(itemPictureDtos);
        }
        return new ResponseInfo(itemDto);
    }

    /**
     * Description  根据商品id查询商品信息
     * <p>
     * Author Hedda
     * @param id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商家根据id查询商品信息")
    @RequestMapping(path = "/findItem/{id}")
    public ResponseInfo findGoodsById(@PathVariable  Long id) throws GlobalException {
        GoodsDto goodsDto = itemService.findGoodsById(id);
        return new ResponseInfo(goodsDto);
    }

    /**
     * Description  根据商品ids查询商品信息
     * <p>
     * Author Hedda
     *
     * @param id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "根据商品ids查询商品信息")
    @RequestMapping(path = "/findGoodsList")
    public ResponseInfo findGoodsList(@RequestBody Long[] id) throws GlobalException {
        List<GoodsDto> goodsDto = itemService.findGoodsList(id);
        return new ResponseInfo(goodsDto);
    }

    /**
     * Description  根据商品id查询商品信息
     * <p>
     * Author Hedda
     * @param id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商家根据id查询商品信息")
    @RequestMapping(path = "/findItems")
    public ResponseInfo findItemsById(@RequestBody Long[] id) throws GlobalException {
        List<GoodsDto> goodsDtos = itemService.findItemsById(id);
        return new ResponseInfo(goodsDtos);
    }

    /**
     * Description 平台端给商家商品配置佣金比例
     * <p>
     * Author Hedda
     *
     * @param commissionRateDto
     * @param result
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台端给商家商品配置佣金比例")
    @RequestMapping(path = "/addGoodsCommissionRate")
    public ResponseInfo addGoodsCommissionRate(@RequestBody @Valid CommissionRateDto commissionRateDto, BindingResult result) throws GlobalException {
        /** 对商品添加佣金比例字段进行校验 */
        if (result.hasErrors()) {
            /** 初始化非法参数的提示信息。 */
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            /** 获取非法参数异常信息对象，并抛出异常。 */
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        return new ResponseInfo(itemService.saveGoodsCommissionRate(commissionRateDto));
    }

    /**
     * Description 平台端商家类目佣金比例回显
     * <p>
     * Author Hedda
     *
     * @param commissionRate
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台端商家类目佣金比例回显")
    @RequestMapping(path = "/findCommissionRate")
    public ResponseInfo findCommissionRate(@RequestBody CommissionRateDto commissionRate) throws GlobalException {
        CommissionRateDto commissionRateDto = itemService.findCommissionRate(commissionRate);
        return new ResponseInfo(commissionRateDto);
    }

    /**
     * Description 平台端给商家商品配置积分比例
     * <p>
     * Author Hedda
     *
     * @param integralRatioDto
     * @param result
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台端给商家商品配置积分比例")
    @RequestMapping(path = "/addIntegralRatio")
    public ResponseInfo addIntegralRatio(@RequestBody @Valid List<IntegralRatioDto> integralRatioDto, BindingResult result) throws GlobalException {
        /** 对商品添加积分比例字段进行校验 */
        if (result.hasErrors()) {
            /** 初始化非法参数的提示信息。 */
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            /** 获取非法参数异常信息对象，并抛出异常。 */
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        return new ResponseInfo(itemService.saveIntegralRatio(integralRatioDto));
    }

    /**
     * Description 平台端商家商品积分比例回显
     * <p>
     * Author Hedda
     *
     * @param id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台端商家商品积分比例回显")
    @RequestMapping(path = "/findIntegralRatio/{id}")
    public ResponseInfo findIdIntegralRatio(@PathVariable Long id) throws GlobalException {
        IntegralRatioDto integralRatioDto = itemService.findIdIntegralRatio(id);
        return new ResponseInfo(integralRatioDto);
    }

    /**
     * Description 平台端统计管理商品销售情况
     * <p>
     * Author Hedda
     *
     * @param itemSellDto 商品销售dto
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台端统计管理商品销售情况")
    @RequestMapping(path = "/getSellGoodsList")
    public ResponseInfo getSellGoodsList(@RequestBody ItemSellDto itemSellDto) throws GlobalException {
        PageInfo<ItemSellDto> itemSellDtos = itemService.getSellGoodsList(itemSellDto);
        return new ResponseInfo(itemSellDtos);
    }

    /**
     * Description 商家端统计管理商品销售情况
     * <p>
     * Author Hedda
     *
     * @param itemSellDto 商品销售dto
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商家端统计管理商品销售情况")
    @RequestMapping(path = "/getMerchantSellGoodsList")
    public ResponseInfo getMerchantSellGoodsList(@RequestBody ItemSellDto itemSellDto)
            throws GlobalException {
        PageInfo<ItemSellDto> itemSellDtos = itemService.getMerchantSellGoodsList(itemSellDto);
        return new ResponseInfo(itemSellDtos);
    }

    /**
     * Description 平台端统计管理商品类目销售分析
     * <p>
     * Author Hedda
     *
     * @param itemSellDto 商品销售dto
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台端统计管理商品类目销售情况")
    @RequestMapping(path = "/getSellGoodsCategoryList")
    public ResponseInfo getSellGoodsCategoryList(@RequestBody ItemSellDto itemSellDto)
            throws GlobalException {
        PageInfo<ItemSellDto> itemSellDtos = itemService.getSellGoodsCategoryList(itemSellDto);
        return new ResponseInfo(itemSellDtos);
    }

    /**
     * Description 商家端统计管理商品类目销售分析
     * <p>
     * Author Hedda
     *
     * @param itemSellDto 商品销售dto
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "商家端统计管理商品类目销售分析")
    @RequestMapping(path = "/getStoreSellGoodsCategoryList")
    public ResponseInfo getStoreSellGoodsCategoryList(@RequestBody ItemSellDto itemSellDto)
            throws GlobalException {
        PageInfo<ItemSellDto> itemSellDtos = itemService.getStoreSellGoodsCategoryList(itemSellDto);
        return new ResponseInfo(itemSellDtos);
    }

}
