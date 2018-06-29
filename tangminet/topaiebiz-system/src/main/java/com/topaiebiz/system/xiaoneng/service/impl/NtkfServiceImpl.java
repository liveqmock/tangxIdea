package com.topaiebiz.system.xiaoneng.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.dto.sku.ItemAppDTO;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.exception.MemberExceptionEnum;
import com.topaiebiz.system.xiaoneng.dao.XiaonengConfigDao;
import com.topaiebiz.system.xiaoneng.dto.NtkfConfigDto;
import com.topaiebiz.system.xiaoneng.dto.NtkfParamCommonDto;
import com.topaiebiz.system.xiaoneng.dto.NtkfParamGoodsDto;
import com.topaiebiz.system.xiaoneng.dto.XiaonengGoodsInfoDto;
import com.topaiebiz.system.xiaoneng.entity.XiaonengConfigEntity;
import com.topaiebiz.system.xiaoneng.po.NtkfGoodsPo;
import com.topaiebiz.system.xiaoneng.service.NtkfService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Joe on 2018/3/29.
 */
@Slf4j
@Service
public class NtkfServiceImpl implements NtkfService {


    @Autowired
    private XiaonengConfigDao xiaonengConfigDao;

    private XiaonengConfigEntity getXiaonengConfig(Long storeId) {
        XiaonengConfigEntity xiaonengConfigEntity = null;
        try {
            XiaonengConfigEntity param = new XiaonengConfigEntity();
            param.cleanInit();
            param.setStoreId(storeId);
            param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            xiaonengConfigEntity = xiaonengConfigDao.selectOne(param);
        } catch (Exception e) {
            log.error("会员信息异常,该手机号存在多条会员信息mobile={}", storeId);
            //TODO 待处理异常
            throw new GlobalException(MemberExceptionEnum.MEMBER_INFO_MULTI);
        }
        return xiaonengConfigEntity;
    }


    private static final String PLATFORM_RECEPTION = "mg_1000_9999";

    @Override
    public String getGoodsNtkf(MemberTokenDto memberTokenDto, NtkfGoodsPo ntkfGoodsPo) {
        Long storeId = ntkfGoodsPo.getStoreId();
        Long goodsId = ntkfGoodsPo.getItemId();
        String goodsType = ntkfGoodsPo.getGoodsType();
        ItemAppDTO itemDTO = goodsApi.getGoods(goodsId);
        if (null != itemDTO) {
            if (itemDTO.getBelongStore() != null) {
                storeId = itemDTO.getBelongStore();
            }
        } else {
            log.warn("xiaoneng getItemById 返回商品为空：itemId={}", goodsId);
        }

        String paramStr = "";
        String ntkfSellerId;
        String ntkfStoreNum;
        XiaonengConfigEntity configEntity = getXiaonengConfig(storeId);
        if (null != configEntity) {
            ntkfSellerId = configEntity.getNtkfSellerId();
            List<NtkfConfigDto> preSalesConfigDtoList = parseSalesConfig(configEntity.getPreSalesConfig());
            String preSalesStr = processPreSalesConfig(storeId, preSalesConfigDtoList);

            List<NtkfConfigDto> afterSalesConfigDtoList = parseSalesConfig(configEntity.getAfterSalesConfig());
            String afterSalesStr = processAfterSalesConfig(storeId, afterSalesConfigDtoList);

            ntkfStoreNum = extractNtkfStoreNum(preSalesConfigDtoList, afterSalesConfigDtoList);

            NtkfParamCommonDto ntkfParamCommonDto = packageNtkfComonParam(memberTokenDto, ntkfSellerId, ntkfStoreNum);
            String paramJson;
            if (null != itemDTO) {
                paramJson = JSON.toJSONString(packageNtkfGoodsParam(goodsId, goodsType, ntkfParamCommonDto));
            } else {
                paramJson = JSON.toJSONString(ntkfParamCommonDto);
            }
            paramStr = packageNtkfStr(preSalesStr, afterSalesStr, paramJson);
            return paramStr;
        } else {
            String preSalesStr = processPreSalesConfig(storeId, null);
            String afterSalesStr = processAfterSalesConfig(storeId, null);
            NtkfParamCommonDto ntkfParamCommonDto = packageNtkfComonParam(memberTokenDto, null, PLATFORM_RECEPTION);
            paramStr = packageNtkfStr(preSalesStr, afterSalesStr, JSON.toJSONString(ntkfParamCommonDto, SerializerFeature.WriteNonStringValueAsString));
            return paramStr;
        }


    }


    private String packageNtkfStr(String preSalesStr, String afterSalesStr, String ntkfParamJson) {
        // return String.join(preSalesStr, afterSalesStr, "NTKF_PARAM =", ntkfParamJson);
        return preSalesStr + afterSalesStr + "NTKF_PARAM =" + ntkfParamJson;
    }


    private NtkfParamGoodsDto packageNtkfGoodsParam(Long goodsId, String goodsType, NtkfParamCommonDto ntkfCommonParamDto) {
        NtkfParamGoodsDto ntkfGoodsParamDto = new NtkfParamGoodsDto();
        BeanCopyUtil.copy(ntkfCommonParamDto, ntkfGoodsParamDto);
        ntkfGoodsParamDto.setItemId(goodsId);
        ntkfGoodsParamDto.setItemParam(goodsType);
        return ntkfGoodsParamDto;
    }

    private NtkfParamCommonDto packageNtkfComonParam(MemberTokenDto memberTokenDto, String ntkfSellerId, String ntkfStoreNum) {
        NtkfParamCommonDto paramDto = new NtkfParamCommonDto();
        paramDto.setSiteId("mg_1000");
        paramDto.setSellerId(ntkfSellerId);
        paramDto.setSettingId(ntkfStoreNum);
        if (null == memberTokenDto) {
            paramDto.setUId("");
            paramDto.setUName("");
            paramDto.setIsVip(0);
        } else {
            paramDto.setUId(null == memberTokenDto.getMemberId() ? "" : memberTokenDto.getMemberId().toString());
            paramDto.setUName(null == memberTokenDto.getUserName() ? "" : memberTokenDto.getUserName());
            paramDto.setIsVip(1);
        }
        paramDto.setUserLevel(1);
        paramDto.setErpParam("abc");
        return paramDto;
    }

    private String processPreSalesConfig(Long storeId, List<NtkfConfigDto> preSalesConfigDtoList) {
        Integer i = 0;
        String varStr = "";
        if (CollectionUtils.isEmpty(preSalesConfigDtoList)) {
            varStr = "var xi_b1_" + storeId + "=\"" + PLATFORM_RECEPTION + "\";";
        } else {
            for (NtkfConfigDto ntkfConfigDto : preSalesConfigDtoList) {
                i = i + 1;
                varStr = "var xi_b" + i + "_" + storeId + "=\"" + ntkfConfigDto.getNum() + "\";";
            }
        }
        return varStr;
    }

    private String extractNtkfStoreNum(List<NtkfConfigDto> preSalesConfigDtoList, List<NtkfConfigDto> afterSalesConfigDtoList) {
        String ntkfStoreNum = null;
        for (NtkfConfigDto ntkfConfigDto : preSalesConfigDtoList) {
            if (null == ntkfStoreNum) {
                ntkfStoreNum = ntkfConfigDto.getNum();
            }
        }
        for (NtkfConfigDto ntkfConfigDto : afterSalesConfigDtoList) {
            if (null == ntkfStoreNum) {
                ntkfStoreNum = ntkfConfigDto.getNum();
            }
        }
        return ntkfStoreNum;
    }


    private List<NtkfConfigDto> parseSalesConfig(String salesConfig) {
        if (StringUtils.isBlank(salesConfig)) {
            return new ArrayList<NtkfConfigDto>();
        }
        List<NtkfConfigDto> salesConfigDtoList = JSON.parseArray(salesConfig.trim(), NtkfConfigDto.class);
        ListIterator<NtkfConfigDto> it = salesConfigDtoList.listIterator();
        while (it.hasNext()) {
            NtkfConfigDto item = it.next();
            if (!"4".equals(item.getType().toString().trim())) {
                it.remove();
            }
        }
        return salesConfigDtoList;
    }


    private String processAfterSalesConfig(Long storeId, List<NtkfConfigDto> afterSalesConfigDtoList) {
        Integer i = 0;
        String varStr = "";
        if (CollectionUtils.isEmpty(afterSalesConfigDtoList)) {
            varStr = "var xi_f1" + storeId + "=\"" + PLATFORM_RECEPTION + "\";";
        } else {
            for (NtkfConfigDto ntkfConfigDto : afterSalesConfigDtoList) {
                i = i + 1;
                varStr = "var xi_f" + i + "_" + storeId + "=\"" + ntkfConfigDto.getNum() + "\";";
            }
        }
        return varStr;
    }


    @Autowired
    private GoodsApi goodsApi;

    /**
     * 商品详情页前缀
     */
    @Value("${xiaoneng.goods.url.prefix}")
    private String xiaonengGoodsUrlPrefix = "http://eshop.mamago.com/motherbuy/#/goodsDetails/";

    /**
     * 商品图片路径前缀
     */
    @Value("${xiaoneng.goods.image.url.prefix}")
    private String xiaonengGoodsImageUrlPrefix = "http://oss.motherbuy.com/";


    @Override
    public XiaonengGoodsInfoDto getItemById(Long itemId) {
        if (null == itemId || itemId < 0) {
            log.warn("xiaoneng getItemById itemId 异常：", itemId);
            return null;
        }
        XiaonengGoodsInfoDto goodsInfoDto = new XiaonengGoodsInfoDto();
        ItemAppDTO itemDTO = goodsApi.getGoods(itemId);
        if (null == itemDTO) {
            log.warn("xiaoneng getItemById 为空：", itemDTO);
            return null;
        }
        if (null == itemDTO.getId()) {
            log.warn("xiaoneng getItemById 商品返回id为空：", itemDTO.getId());
            return null;
        }
        goodsInfoDto.setId(itemDTO.getId().toString());
        goodsInfoDto.setName(null == itemDTO.getName() ? "" : itemDTO.getName());
        goodsInfoDto.setImageurl(null == itemDTO.getPictureName() ? "" : xiaonengGoodsImageUrlPrefix + itemDTO.getPictureName());
        goodsInfoDto.setUrl(xiaonengGoodsUrlPrefix + itemDTO.getId());
        goodsInfoDto.setCurrency("¥");
        goodsInfoDto.setSiteprice(itemDTO.getDefaultPrice().toString());
        goodsInfoDto.setMarketprice(String.valueOf(itemDTO.getMarketPrice()));
        goodsInfoDto.setCategory(null == itemDTO.getCategoryName() ? "" : itemDTO.getCategoryName());
        goodsInfoDto.setBrand(null == itemDTO.getBrandName() ? "" : itemDTO.getBrandName());
        log.warn("xiaoneng getItemById 结果：", JSON.toJSONString(goodsInfoDto));
        return goodsInfoDto;
    }
}
