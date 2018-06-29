package com.topaiebiz.promotion.mgmt.controller;

import com.alibaba.druid.util.StringUtils;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.goods.dto.sku.ItemDTO;
import com.topaiebiz.promotion.mgmt.dto.*;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEnum;
import com.topaiebiz.promotion.mgmt.service.PromotionGoodsService;
import com.topaiebiz.promotion.mgmt.service.PromotionService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Description： 营销活动
 * <p>
 * <p>
 * Author Joe
 * <p>
 * Date 2017年9月22日 下午1:20:40
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping(path = "/promotion/mgmt", method = RequestMethod.POST)
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private PromotionGoodsService promotionGoodsService;

    /**
     * Description 获取营销活动列表
     * <p>
     * Author Joe
     *
     * @return
     */
    @RequestMapping(path = "/getPromotionList")
    @PermissionController(value = PermitType.PLATFORM, operationName = "查询营销活动列表")
    public ResponseInfo getPromotionList(@RequestBody PromotionDto promotionDto) throws GlobalException {
        PageInfo<PromotionDto> promotionDtoList = promotionService.getPromotionList(promotionDto);
        return new ResponseInfo(promotionDtoList);
    }

    /**
     * Description 添加单品折扣
     * <p>
     * Author Joe
     *
     * @param promotionSingleDto
     * @return
     * @throws GlobalException
     * @throws ParseException
     */
    @RequestMapping(path = "/addPromotionSingle")
    @PermissionController(value = PermitType.MERCHANT, operationName = "新增单品折扣")
    public ResponseInfo addPromotionSingle(@RequestBody @Valid PromotionSingleDto promotionSingleDto, BindingResult result) throws GlobalException, ParseException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        Long promotionId = promotionService.addPromotionSingle(promotionSingleDto);
        return new ResponseInfo(promotionId);
    }

    /**
     * Description 添加/修改单品折扣商品
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/editPromotionSingleGoods")
    @PermissionController(value = PermitType.MERCHANT, operationName = "修改单品折扣商品")
    public ResponseInfo editPromotionSingleGoods(@RequestBody List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        validationPromotionGoodsList(promotionGoodsList);
        promotionService.modifyPromotionSingleGoods(promotionGoodsList);
        return new ResponseInfo();
    }

    /**
     * 检验活动商品集合
     * usage Method（editPromotionSingleGoods，addPromotionSingleGoods，addPromotionSingleGoods，
     * editPromotionPriceGoods，addPromotionPriceGoods，editPromotionSeckillGoods，
     * addPromotionSeckillGoods，editPromotionGoods，addPromotion）
     *
     * @param promotionGoodsList
     */
    private void validationPromotionGoodsList(List<PromotionGoodsDto> promotionGoodsList) {
        /** 商品列表不得为空*/
        if (CollectionUtils.isEmpty(promotionGoodsList)) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTIONGOODS_NOT_NULL);
        }
        /** 判断id是否为空 */
        if (null == promotionGoodsList.get(0).getPromotionId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
    }

    /**
     * Description 保存/发布单品折扣活动
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/addPromotionSingleGoods")
    @PermissionController(value = PermitType.MERCHANT, operationName = "保存/发布单品折扣活动")
    public ResponseInfo addPromotionSingleGoods(@RequestBody List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        validationPromotionGoodsList(promotionGoodsList);
        promotionService.savePromotionSingle(promotionGoodsList);
        return new ResponseInfo();
    }

    /**
     * Description 添加一口价活动
     * <p>
     * Author Joe
     *
     * @param promotionSingleDto
     * @param result
     * @return
     * @throws GlobalException
     * @throws ParseException
     */
    @RequestMapping(path = "/addPromotionPrice")
    @PermissionController(value = PermitType.MERCHANT, operationName = "新增一口价活动")
    public ResponseInfo addPromotionPrice(@RequestBody @Valid PromotionSingleDto promotionSingleDto, BindingResult result) throws GlobalException, ParseException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        Long promotionId = promotionService.savePromotionPrice(promotionSingleDto);
        return new ResponseInfo(promotionId);
    }

    /**
     * Description 添加/修改一口价商品
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/editPromotionPriceGoods")
    @PermissionController(value = PermitType.MERCHANT, operationName = "修改一口价商品")
    public ResponseInfo editPromotionPriceGoods(@RequestBody List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        validationPromotionGoodsList(promotionGoodsList);
        promotionService.modifyPromotionPriceGoods(promotionGoodsList);
        return new ResponseInfo();
    }

    /**
     * Description 保存/发布一口价活动
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/addPromotionPriceGoods")
    @PermissionController(value = PermitType.MERCHANT, operationName = "保存/发布一口价活动")
    public ResponseInfo addPromotionPriceGoods(@RequestBody List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        validationPromotionGoodsList(promotionGoodsList);
        promotionService.savePromotionPriceGoods(promotionGoodsList);
        return new ResponseInfo();
    }

    /**
     * Description 添加秒杀活动
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     * @throws ParseException
     */
    @RequestMapping(path = "/addPromotionSeckill")
    @PermissionController(value = PermitType.PLATFORM, operationName = "新增秒杀活动")
    public ResponseInfo addPromotionSeckill(@RequestBody @Valid PromotionSingleDto promotionSingleDto, BindingResult result) throws GlobalException, ParseException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        Long promotionId = promotionService.savePromotionSeckill(promotionSingleDto);
        return new ResponseInfo(promotionId);

    }

    /**
     * Description 添加/修改秒杀活动商品
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/editPromotionSeckillGoods")
    @PermissionController(value = PermitType.PLATFORM, operationName = "修改秒杀活动商品")
    public ResponseInfo editPromotionSeckillGoods(@RequestBody List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        validationPromotionGoodsList(promotionGoodsList);
        promotionService.modifyPromotionSeckillGoods(promotionGoodsList);
        return new ResponseInfo();
    }

    /**
     * Description 保存/发布秒杀活动
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/addPromotionSeckillGoods")
    @PermissionController(value = PermitType.PLATFORM, operationName = "保存/发布秒杀活动")
    public ResponseInfo addPromotionSeckillGoods(@RequestBody List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        validationPromotionGoodsList(promotionGoodsList);
        promotionService.savePromotionSeckillGoods(promotionGoodsList);
        return new ResponseInfo();
    }

    /**
     * Description 活动选择保存商品
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/addPromotionGoods")
    @PermissionController(value = PermitType.PLATFORM, operationName = "活动选择保存商品")
    public ResponseInfo addPromotionGoods(@RequestBody List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        if (CollectionUtils.isNotEmpty(promotionGoodsList)){
            if (null == promotionGoodsList.get(0).getPromotionId()){
                throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
            }
        }
        promotionService.savePromotionGoods(promotionGoodsList);
        return new ResponseInfo();
    }

    /**
     * Description 修改单品级活动(单品折扣,一口价,秒杀)
     * <p>
     * Author Joe
     *
     * @param promotionSingleDto
     * @param result
     * @return
     * @throws GlobalException
     * @throws ParseException
     */
    @RequestMapping(path = "/editPromotionSingle")
    @PermissionController(value = PermitType.PLATFORM, operationName = "修改单品级活动")
    public ResponseInfo editPromotionSingle(@RequestBody @Valid PromotionSingleDto promotionSingleDto, BindingResult result) throws GlobalException, ParseException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        /** 判断id是否为空 */
        if (null == promotionSingleDto.getId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        Long promotionId = promotionService.modifyPromotionSingle(promotionSingleDto);
        return new ResponseInfo(promotionId);

    }

    /**
     * Description 添加满减活动
     * <p>
     * Author Joe
     *
     * @param result
     * @return
     * @throws GlobalException
     * @throws ParseException
     */
    @RequestMapping(path = "/addPromotionReducePrice")
    @PermissionController(value = PermitType.MERCHANT, operationName = "新增满减活动")
    public ResponseInfo addPromotionReducePrice(@RequestBody @Valid PromotionDto promotionDto, BindingResult result) throws GlobalException, ParseException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        Long promotionId = promotionService.savePromotionReducePrice(promotionDto);
        return new ResponseInfo(promotionId);

    }

    /**
     * Description 修改满减活动
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @param result
     * @return
     * @throws GlobalException
     * @throws ParseException
     */
    @RequestMapping(path = "/editPromotionReducePrice")
    @PermissionController(value = PermitType.MERCHANT, operationName = "修改满减活动")
    public ResponseInfo editPromotionReducePrice(@RequestBody @Valid PromotionDto promotionDto, BindingResult result) throws GlobalException, ParseException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        /** 判断id是否为空 */
        if (null == promotionDto.getId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        Long promotionId = promotionService.modifyPromotionReducePrice(promotionDto);
        return new ResponseInfo(promotionId);
    }

    /**
     * Description 添加店铺优惠券
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @param result
     * @return
     * @throws GlobalException
     * @throws ParseException
     */
    @RequestMapping(path = "/addPromotionStoreCoupon")
    @PermissionController(value = PermitType.MERCHANT, operationName = "添加店铺优惠券")
    public ResponseInfo addPromotionStoreCoupon(@RequestBody @Valid PromotionDto promotionDto, BindingResult result) throws GlobalException, ParseException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        Long promotionId = promotionService.savePromotionStoreCoupon(promotionDto);
        return new ResponseInfo(promotionId);
    }

    /**
     * Description 修改店铺优惠券
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @param result
     * @return
     * @throws GlobalException
     * @throws ParseException
     */
    @RequestMapping(path = "/editPromotionStoreCoupon")
    @PermissionController(value = PermitType.MERCHANT, operationName = "修改店铺优惠券")
    public ResponseInfo editPromotionStoreCoupon(@RequestBody @Valid PromotionDto promotionDto, BindingResult result) throws GlobalException, ParseException {

        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        /** 判断id是否为空 */
        if (null == promotionDto.getId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        Long promotionId = promotionService.modifyPromotionStoreCoupon(promotionDto);
        return new ResponseInfo(promotionId);

    }

    /**
     * Description 添加/修改活动商品(满减,包邮,店铺优惠券,平台优惠券,平台优惠码)
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/editPromotionGoods")
    @PermissionController(value = PermitType.PLATFORM, operationName = "修改活动商品")
    public ResponseInfo editPromotionGoods(@RequestBody List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        validationPromotionGoodsList(promotionGoodsList);
        promotionService.modifyPromotionGoods(promotionGoodsList);
        return new ResponseInfo();
    }


    /**
     * Description 添加包邮活动
     * <p>
     * Author Joe
     *
     * @param result
     * @return
     * @throws GlobalException
     * @throws ParseException
     */
    @RequestMapping(path = "/addPromotionFreeShipping")
    @PermissionController(value = PermitType.MERCHANT, operationName = "新增包邮活动")
    public ResponseInfo addPromotionFreeShipping(@RequestBody @Valid PromotionSingleDto promotionSingleDto, BindingResult result) throws GlobalException, ParseException {

        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        /** 包邮条件值不得为空*/
        if (null == promotionSingleDto.getCondValue()) {
            throw new GlobalException(PromotionExceptionEnum.CONDVALUE_NOT_NULL);
        }
        Long promotionId = promotionService.savePromotionFreeShipping(promotionSingleDto);
        return new ResponseInfo(promotionId);
    }

    /**
     * Description 修改包邮活动
     * <p>
     * Author Joe
     *
     * @param result
     * @return
     * @throws GlobalException
     * @throws ParseException
     */
    @RequestMapping(path = "/editPromotionFreeShipping")
    @PermissionController(value = PermitType.MERCHANT, operationName = "修改包邮活动")
    public ResponseInfo editPromotionFreeShipping(@RequestBody @Valid PromotionSingleDto promotionSingleDto, BindingResult result) throws GlobalException, ParseException {

        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        /** 判断id是否为空 */
        if (null == promotionSingleDto.getId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        /** 包邮条件值不得为空*/
        if (null == promotionSingleDto.getCondValue()) {
            throw new GlobalException(PromotionExceptionEnum.CONDVALUE_NOT_NULL);
        }
        Long promotionId = promotionService.modifyPromotionFreeShipping(promotionSingleDto);
        return new ResponseInfo(promotionId);
    }

    /**
     * Description： 添加平台优惠劵
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     * @throws ParseException
     */
    @RequestMapping(path = "/addPromotionCoupon")
    @PermissionController(value = PermitType.PLATFORM, operationName = "新增平台优惠券")
    public ResponseInfo addPromotionCoupon(@RequestBody @Valid PromotionDto promotionDto, BindingResult result) throws GlobalException, ParseException {

        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        Long promotionId = promotionService.savePromotionCoupon(promotionDto);
        return new ResponseInfo(promotionId);

    }

    /**
     * Description： 修改平台优惠券
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     * @throws ParseException
     */
    @RequestMapping(path = "/editCoupon")
    @PermissionController(value = PermitType.PLATFORM, operationName = "修改平台优惠券")
    public ResponseInfo editPromotionCoupon(@RequestBody @Valid PromotionDto promotionDto, BindingResult result) throws GlobalException, ParseException {

        /** 对营销活动字段进行校验 */
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        /** 判断id是否为空 */
        if (null == promotionDto.getId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        Long promotionId = promotionService.modifyPromotionCoupon(promotionDto);
        return new ResponseInfo(promotionId);

    }

    /**
     * Description 添加平台优惠码
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     * @throws ParseException
     */
    @RequestMapping(path = "/addPromotionCouponCode")
    @PermissionController(value = PermitType.PLATFORM, operationName = "新增平台优惠码")
    public ResponseInfo addPromotionCouponCode(@RequestBody @Valid PromotionDto promotionDto, BindingResult result) throws GlobalException, ParseException {

        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        // 验证发放数量
        if (null == promotionDto.getAmount()) {
            throw new GlobalException(PromotionExceptionEnum.QUANTITY_ISSUED_SHALL_NOT_NULL);
        }
        Long promotionId = promotionService.savePromotionCouponCode(promotionDto);
        return new ResponseInfo(promotionId);
    }

    /**
     * Description 修改平台优惠码
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @param result
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/editPromotionCouponCode")
    @PermissionController(value = PermitType.PLATFORM, operationName = "修改平台优惠码")
    public ResponseInfo editPromotionCouponCode(@RequestBody @Valid PromotionDto promotionDto, BindingResult result) throws GlobalException, ParseException {

        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }

        if (null == promotionDto.getAmount()) {// 验证发放数量
            throw new GlobalException(PromotionExceptionEnum.QUANTITY_ISSUED_SHALL_NOT_NULL);
        }
        /** 判断id是否为空 */
        if (null == promotionDto.getId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        Long promotionId = promotionService.modifyPromotionCouponCode(promotionDto);
        return new ResponseInfo(promotionId);
    }

    /**
     * Description 保存/发布平台优惠码活动
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/addPromotionCouponCodeGoods")
    @PermissionController(value = PermitType.PLATFORM, operationName = "保存/发布平台优惠码")
    public ResponseInfo addPromotionCouponCodeGoods(@RequestBody List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        validationPromotionGoodsList(promotionGoodsList);
        promotionService.savePromotionCouponCodeGoods(promotionGoodsList);
        return new ResponseInfo();
    }

    /**
     * Description 停止优惠活动
     * <p>
     * Author Joe
     *
     * @param id
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/editStop/{id}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "停止优惠活动")
    public ResponseInfo editStopPromotion(@PathVariable Long id) throws GlobalException {

        /** 判断id是否为空 */
        if (null == id) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        Integer modifyStopPromotionCouponCode = promotionService.modifyStopPromotion(id);
        return new ResponseInfo(modifyStopPromotionCouponCode);
    }

    /**
     * Description： 删除营销活动
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/cancel/{ids}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "删除营销活动")
    public ResponseInfo cancelPromotion(@PathVariable String ids) throws GlobalException {

        /** 判断id是否为空 */
        if (null == ids) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        promotionService.removePromotion(ids);
        return new ResponseInfo();
    }

    /**
     * Description 修改营销活动回显
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/findPromotionById/{id}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "营销活动回显")
    public ResponseInfo findPromotionById(@PathVariable Long id) throws GlobalException {
        /** 判断id是否为空 */
        if (null == id) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        return new ResponseInfo(promotionService.findPromotionById(id));
    }

    /**
     * Description 获取营销活动所选商品
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/getPromotionGoods")
    @PermissionController(value = PermitType.PLATFORM, operationName = "查询活动所选商品")
    public ResponseInfo getPromotionGoods(@RequestBody ItemDTO itemDTO) throws GlobalException {
        /** 判断id是否为空 */
        if (null == itemDTO.getPromotionId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        PageInfo<PromotionGoodsDto> promotionGoodsDtoList = promotionService.getPromotionGoods(itemDTO);
        return new ResponseInfo(promotionGoodsDtoList);
    }

    /**
     * 查询所选item商品下所选sku商品
     *
     * @param promotionGoodsDto
     * @return
     */
    @RequestMapping(path = "/getPromotionSkuGoodsList")
    @PermissionController(value = PermitType.PLATFORM, operationName = "查询活动所选SKU商品")
    public ResponseInfo getPromotionSkuGoodsList(@RequestBody PromotionGoodsDto promotionGoodsDto) {
        /** 判断id是否为空 */
        if (null == promotionGoodsDto.getPromotionId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        /** 判断itemId是否为空 */
        if (null == promotionGoodsDto.getItemId()) {
            throw new GlobalException(PromotionExceptionEnum.PRODUCT_ITEM_ID_NOT_NULL);
        }
        List<PromotionGoodsDto> promotionGoodsDtos = promotionGoodsService.getPromotionSkuGoodsList(promotionGoodsDto.getPromotionId(), promotionGoodsDto.getItemId());
        return new ResponseInfo(promotionGoodsDtos);
    }


    /**
     * Description 发布活动
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/releasePromotion")
    @PermissionController(value = PermitType.PLATFORM, operationName = "发布活动")
    public ResponseInfo releasePromotion(@RequestBody PromotionDto promotionDto) throws GlobalException {

        /** 判断id是否为空 */
        if (null == promotionDto.getId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        /** 判断活动类型是否为空 */
        if (null == promotionDto.getTypeId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_TYPE_NOT_NULL);
        }
        promotionService.releasePromotion(promotionDto.getId(), promotionDto.getTypeId());
        return new ResponseInfo();
    }

    /**
     * Description 取消活动商品
     * <p>
     * Author Joe
     *
     * @param promotionGoodsDto
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/cancelGoods")
    @PermissionController(value = PermitType.PLATFORM, operationName = "取消活动商品")
    public ResponseInfo cancelGoods(@RequestBody PromotionGoodsDto promotionGoodsDto) throws GlobalException {

        /** 判断id是否为空 */
        if (null == promotionGoodsDto.getPromotionId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        promotionService.removeGoods(promotionGoodsDto.getPromotionId(), promotionGoodsDto.getItemId());
        return new ResponseInfo();
    }

    /**
     * Description 平台报名活动列表
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     * @throws ParseException
     */
    @RequestMapping(path = "/getPlatformEnrolPromotionList")
    @PermissionController(value = PermitType.PLATFORM, operationName = "查询平台报名活动列表")
    public ResponseInfo getPlatformEnrolPromotionList(@RequestBody PromotionDto promotionDto) throws ParseException {
        PageInfo<PromotionDto> promotionDtoList = promotionService.getPlatformEnrolPromotionList(promotionDto);
        return new ResponseInfo(promotionDtoList);
    }

    /**
     * Description 发起报名
     * <p>
     * Author Joe
     *
     * @param promotionEnrolDto
     * @param result
     * @return
     * @throws ParseException
     */
    @RequestMapping(path = "/editInitiateEnrol")
    @PermissionController(value = PermitType.PLATFORM, operationName = "发起报名")
    public ResponseInfo editInitiateEnrol(@RequestBody @Valid PromotionEnrolDto promotionEnrolDto, BindingResult result) throws ParseException {
        /** 判断id是否为空 */
        if (null == promotionEnrolDto.getId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        promotionService.modifyInitiateEnrol(promotionEnrolDto);
        return new ResponseInfo();
    }

    /**
     * Description 发起报名获取活动列表
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     */
    @RequestMapping(path = "/getEnrolPromotionList")
    @PermissionController(value = PermitType.PLATFORM, operationName = "发起报名获取活动列表")
    public ResponseInfo getEnrolPromotionList(@RequestBody PromotionDto promotionDto) {
        PageInfo<PromotionDto> promotionList = promotionService.getEnrolPromotionList(promotionDto);
        return new ResponseInfo(promotionList);
    }

    /**
     * Description 营销活动报名商家列表
     * <p>
     * Author Joe
     *
     * @return
     * @throws ParseException
     */
    @RequestMapping(path = "/getPromotionEnrolStoreList")
    @PermissionController(value = PermitType.PLATFORM, operationName = "查询活动报名商家列表")
    public ResponseInfo getPromotionEnrolStoreList(@RequestBody PromotionEntryDto promotionEntryDto) throws ParseException {
        PageInfo<PromotionEntryDto> promotionEntryList = promotionService.getPromotionEnrolStoreList(promotionEntryDto);
        return new ResponseInfo(promotionEntryList);
    }

    /**
     * Description 查看店铺报名详情
     * <p>
     * Author Joe
     *
     * @param id
     * @return
     */
    @RequestMapping(path = "/getPromotionEnrolStore/{id}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "查询店铺报名详情")
    public ResponseInfo getPromotionEnrolStore(@PathVariable Long id) {
        if (null == id) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        PromotionEntryDto promotionEntryDto = promotionService.getPromotionEnrolStore(id);
        return new ResponseInfo(promotionEntryDto);
    }

    /**
     * Description 报名活动商家不通过
     * <p>
     * Author Joe
     *
     * @return
     */
    @RequestMapping(path = "/editStoreAuditNonconformity/{id}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "报名商家不通过")
    public ResponseInfo editStoreAuditNonconformity(@PathVariable Long id) {
        if (null == id) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        promotionService.modifyStoreAuditNonconformity(id);
        return new ResponseInfo();
    }

    /**
     * Description 商家报名商品
     * <p>
     * Author Joe
     *
     * @param promotionGoodsDto
     * @return
     */
    @RequestMapping(path = "/getStoreEnrolGoodsList")
    @PermissionController(value = PermitType.PLATFORM, operationName = "查询商家报名商品")
    public ResponseInfo getStoreEnrolGoodsList(@RequestBody PromotionGoodsDto promotionGoodsDto) {
        if (promotionGoodsDto.getPromotionId() == null || promotionGoodsDto.getStoreId() == null) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        PageInfo<PromotionGoodsDto> promotionGoodsList = promotionGoodsService.getStoreEnrolGoodsList(promotionGoodsDto);
        return new ResponseInfo(promotionGoodsList);
    }


    /**
     * Description 商家营销活动报名列表(全部活动)
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     */
    @RequestMapping(path = "/getStoreAllPromotionList")
    @PermissionController(value = PermitType.MERCHANT, operationName = "查询商家营销活动报名列表-全部活动")
    public ResponseInfo getStoreAllPromotionList(@RequestBody PromotionDto promotionDto) throws ParseException {
        PageInfo<PromotionDto> promotionDtoList = promotionService.selectStoreAllPromotionList(promotionDto);
        return new ResponseInfo(promotionDtoList);
    }

    /**
     * Description 商家营销活动报名列表(已报名活动)
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     */
    @RequestMapping(path = "/getStorePromotionList")
    @PermissionController(value = PermitType.MERCHANT, operationName = "查询商家已报名活动")
    public ResponseInfo getStoreEnrolPromotionList(@RequestBody PromotionDto promotionDto) throws ParseException {
        PageInfo<PromotionDto> promotionDtoList = promotionService.getStoreEnrolPromotionList(promotionDto);
        return new ResponseInfo(promotionDtoList);
    }

    /**
     * Description 报名商家商品审核列表
     * <p>
     * Author Joe
     *
     * @param promotionGoodsDto
     * @return
     */
    @RequestMapping(path = "/getStoreGoodsAuditList")
    @PermissionController(value = PermitType.PLATFORM, operationName = "查询报名商家商品审核列表")
    public ResponseInfo getStoreGoodsAuditList(@RequestBody PromotionGoodsDto promotionGoodsDto) {
        /** 活动id不可为空*/
        if (null == promotionGoodsDto.getPromotionId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        /** 店铺id不可为空*/
        if (null == promotionGoodsDto.getStoreId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTIONGOODS_STORE_OWNED_NOT_NULL);
        }
        PageInfo<PromotionGoodsDto> promotionGoodsDtoList = promotionGoodsService.getStoreGoodsAuditList(promotionGoodsDto);
        return new ResponseInfo(promotionGoodsDtoList);
    }

    /**
     * Description 审核单个sku商品
     * <p>
     * Author Joe
     *
     * @param promotionGoodsDto
     * @return
     */
    @RequestMapping(path = "/editAuditSingleSkuGoods")
    @PermissionController(value = PermitType.PLATFORM, operationName = "审核单个sku商品")
    public ResponseInfo editAuditSingleSkuGoods(@RequestBody PromotionGoodsDto promotionGoodsDto) {
        if (null == promotionGoodsDto.getId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTIONGOODS_NOT_NULL);
        }
        if (null == promotionGoodsDto.getState()) {
            throw new GlobalException(PromotionExceptionEnum.PRODUCT_AUDIT_STATE_CANNOT_BE_EMPTY);
        }
        promotionGoodsService.modifyAuditSingleSkuGoods(promotionGoodsDto.getId(), promotionGoodsDto.getState());
        return new ResponseInfo();
    }

    /**
     * Description sku商品审核完成
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @return
     */
    @RequestMapping(path = "/editAuditSkuGoods")
    @PermissionController(value = PermitType.PLATFORM, operationName = "sku商品呢审核完后")
    public ResponseInfo editAuditSkuGoods(@RequestBody List<PromotionGoodsDto> promotionGoodsList) {
        promotionGoodsService.modifyAuditSkuGoods(promotionGoodsList);
        return new ResponseInfo();
    }

    /**
     * Description 审核item商品通过/不通过
     * <p>
     * Author Joe
     *
     * @param promotionGoodsDto
     * @return
     */
    @RequestMapping(path = "/editAuditItemGoods")
    @PermissionController(value = PermitType.PLATFORM, operationName = "审核item商品")
    public ResponseInfo editAuditItemGoods(@RequestBody PromotionGoodsDto promotionGoodsDto) {
        if (null == promotionGoodsDto.getPromotionId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTIONGOODS_NOT_NULL);
        }
        if (null == promotionGoodsDto.getState()) {
            throw new GlobalException(PromotionExceptionEnum.PRODUCT_AUDIT_STATE_CANNOT_BE_EMPTY);
        }
        if (null == promotionGoodsDto.getStoreId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTIONGOODS_STORE_OWNED_NOT_NULL);
        }
        if (null == promotionGoodsDto.getItemId()) {
            throw new GlobalException(PromotionExceptionEnum.PRODUCT_ITEM_ID_NOT_NULL);
        }
        promotionGoodsService.modifyAuditItemGoods(promotionGoodsDto);
        return new ResponseInfo();
    }

    /**
     * Description 商家商品审核完成
     * <p>
     * Author Joe
     *
     * @return
     */
    @RequestMapping(path = "/editAuditGoods")
    @PermissionController(value = PermitType.PLATFORM, operationName = "商家商品审核完成")
    public ResponseInfo editAuditGoods(@RequestBody PromotionGoodsDto promotionGoodsDto) {
        if (null == promotionGoodsDto.getPromotionId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTIONGOODS_NOT_NULL);
        }
        if (null == promotionGoodsDto.getStoreId()) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTIONGOODS_STORE_OWNED_NOT_NULL);
        }
        promotionGoodsService.modifyAuditGoods(promotionGoodsDto);
        return new ResponseInfo();
    }

    /**
     * @param map
     * @Author: tangx.w
     * @Description: 插入优惠券使用/排除商品
     * @Date: 2018/4/27 10:53
     */
    @RequestMapping(path = "/insertPromotionGoods")
    @PermissionController(value = PermitType.MERCHANT, operationName = "插入优惠券使用/排除商品")
    public ResponseInfo insertPromotionGoods(@RequestBody Map<String, Object> map) {
        String itemIds = map.get("itemIds").toString();
        Long promotionId = Long.valueOf(map.get("promotionId").toString());
        /** 判断id是否为空 */
        if (null == promotionId) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        if(StringUtils.isEmpty(itemIds)){
            throw new GlobalException(PromotionExceptionEnum.PARAMETER_TYPE_ERROR);
        }
        promotionService.saveGoods(itemIds, promotionId);
        return new ResponseInfo();
    }


    /**
     * @param id
     * @Author: tangx.w
     * @Description: 商家端商品优惠券保存/发布
     * @Date: 2018/4/27 10:54
     */
    @RequestMapping(path = "/editPromotionStatus/{id}")
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家端商品优惠券保存/发布")
    public ResponseInfo editPromotion(@PathVariable Long id) throws ParseException {
        if (null == id) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        promotionService.editPromotionStatus(id);
        return new ResponseInfo();
    }

    /**
     * @param id
     * @Author: tangx.w
     * @Description: 复制商家优惠券
     * @Date: 2018/5/2 13:39
     */
    @RequestMapping(path = "/copyOfCoupons/{id}")
    @PermissionController(value = PermitType.MERCHANT, operationName = "复制商家优惠券")
    public ResponseInfo copyOfCoupons(@PathVariable Long id) {
        if (null == id) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        return new ResponseInfo(promotionService.copyOfCoupons(id));
    }

    /**
     * @param map
     * @Author: tangx.w
     * @Description: 停止商家优惠券
     * @Date: 2018/5/2 13:39
     */
    @RequestMapping(path = "/stopCoupon")
    @PermissionController(value = PermitType.MERCHANT, operationName = "停止商家优惠券")
    public ResponseInfo stopCoupon(@RequestBody Map<String, Object> map) {
        Long promotionId = Long.valueOf(map.get("promotionId").toString());
        String memo = map.get("memo").toString();
        if (null == promotionId) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        promotionService.stopCoupon(promotionId, memo);
        return new ResponseInfo();
    }

    /**
     * @param id
     * @Author: tangx.w
     * @Description: 查看优惠券操作日志
     * @Date: 2018/5/2 13:39
     */
    @RequestMapping(path = "/getPromotionLogLists/{id}")
    @PermissionController(value = PermitType.MERCHANT, operationName = "查看优惠券操作日志")
    public ResponseInfo getPromotionLogLists(@PathVariable Long id) {
        if (null == id) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        return new ResponseInfo(promotionService.getPromotionLogLists(id));
    }

    /**
     * @param map
     * @Author: tangx.w
     * @Description: 获取圈中商品item的sku
     * @Date: 2018/5/2 13:39
     */
    @RequestMapping(path = "/getItemSkuList")
    @PermissionController(value = PermitType.MERCHANT, operationName = "获取圈中商品item的sku")
    public ResponseInfo getItemSkuList(@RequestBody Map<String, Object> map) {
        String skuIds = map.get("skuIds").toString();
        Long promotionId = Long.valueOf(map.get("promotionId").toString());
        if (null == promotionId) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        return new ResponseInfo(promotionService.getItemSkuList(skuIds, promotionId));
    }

    /**
     * @param map
     * @Author: tangx.w
     * @Description: 更改sku选择状态
     * @Date: 2018/5/2 13:39
     */
    @RequestMapping(path = "/changeCouponState")
    @PermissionController(value = PermitType.MERCHANT, operationName = "更改sku选择状态")
    public ResponseInfo changeCouponState(@RequestBody Map<String, Object> map) {
        Long skuId = Long.valueOf(map.get("skuId").toString());
        Long promotionId = Long.valueOf(map.get("promotionId").toString());
        if (null == promotionId) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        promotionService.changeCouponState(skuId, promotionId);
        return new ResponseInfo();
    }

    /**
     * @param map
     * @Author: tangx.w
     * @Description: 获取sku状态
     * @Date: 2018/5/2 13:39
     */
    @RequestMapping(path = "/getSkuState")
    @PermissionController(value = PermitType.MERCHANT, operationName = "获取sku状态")
    public ResponseInfo getSkuState(@RequestBody Map<String, Object> map) {
        String skuIds = map.get("skuIds").toString();
        Long promotionId = Long.valueOf(map.get("promotionId").toString());
        if (null == promotionId) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        return new ResponseInfo(promotionService.getSkuState(skuIds, promotionId));
    }

    /**
     * @param id
     * @Author: tangx.w
     * @Description: 发布中的商家优惠券，修改完商品之后的保存
     * @Date: 2018/5/7 19:25
     */
    @RequestMapping(path = "/save/{id}")
    @PermissionController(value = PermitType.MERCHANT, operationName = "发布中的商家优惠券，修改完商品之后的保存")
    public ResponseInfo save(@PathVariable Long id) {
        if (id == null) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        promotionService.save(id);
        return new ResponseInfo();
    }

    /**
     * @param promotionDto
     * @Author: tangx.w
     * @Description: 获取优惠券列表 商家端typeId=7  平台端typeId=4
     * @Date: 2018/5/7 19:25
     */
    @RequestMapping(path = "/getCouponsList")
    @PermissionController(value = PermitType.PLATFORM, operationName = "查询营销活动列表")
    public ResponseInfo getCouponsList(@RequestBody PromotionDto promotionDto) throws GlobalException, ParseException {
        PageInfo<PromotionDto> promotionDtoList = promotionService.getCouponsList(promotionDto);
        return new ResponseInfo(promotionDtoList);
    }

    /**
     * Description 保存/发布活动(满减,包邮,店铺优惠券,平台优惠券)
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/addPromotion")
    @PermissionController(value = PermitType.PLATFORM, operationName = "保存发布活动")
    public ResponseInfo addPromotion(@RequestBody List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        validationPromotionGoodsList(promotionGoodsList);
        promotionService.savePromotion(promotionGoodsList);
        return new ResponseInfo();
    }
}
