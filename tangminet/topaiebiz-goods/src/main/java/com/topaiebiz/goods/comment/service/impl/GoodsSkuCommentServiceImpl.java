package com.topaiebiz.goods.comment.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryAttrDao;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryAttrEntity;
import com.topaiebiz.goods.comment.dao.GoodsSkuCommentDao;
import com.topaiebiz.goods.comment.dao.GoodsSkuCommentPictureDao;
import com.topaiebiz.goods.comment.dto.GoodsSkuCommentDto;
import com.topaiebiz.goods.comment.dto.GoodsSkuCommentPictureDto;
import com.topaiebiz.goods.comment.entity.GoodsSkuCommentEntity;
import com.topaiebiz.goods.comment.entity.GoodsSkuCommentPictureEntity;
import com.topaiebiz.goods.comment.exception.GoodsSkuCommentExceptionEnum;
import com.topaiebiz.goods.comment.service.GoodsSkuCommentService;
import com.topaiebiz.goods.goodsenum.GoodsRedisKey;
import com.topaiebiz.goods.sku.dao.GoodsSkuDao;
import com.topaiebiz.goods.sku.dao.ItemDao;
import com.topaiebiz.goods.sku.entity.GoodsSkuEntity;
import com.topaiebiz.goods.sku.entity.ItemEntity;
import com.topaiebiz.goods.sku.exception.GoodsSkuExceptionEnum;
import com.topaiebiz.member.api.PointApi;
import com.topaiebiz.member.constants.PointOperateType;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.dto.point.PointChangeDto;
import com.topaiebiz.merchant.api.StoreApi;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.system.util.SecurityContextUtils;
import com.topaiebiz.trade.api.order.OrderServiceApi;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.topaiebiz.goods.constants.GoodsConstants.GoodsCommentLevel.*;
import static com.topaiebiz.goods.constants.GoodsConstants.GoodsCommentType.*;

/**
 * Description 商品评价实现类
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年10月2日 下午8:10:06
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
public class GoodsSkuCommentServiceImpl implements GoodsSkuCommentService {

    @Value("${point.add.comment}")
    private Integer addCommentPoint = 1;

    @Autowired
    private GoodsSkuCommentDao goodsSkuComentDao;

    @Autowired
    private GoodsSkuCommentPictureDao goodsSkuCommentPictureDao;

    @Autowired
    private GoodsSkuDao goodsSkuDao;

    @Autowired
    private OrderServiceApi orderServiceApi;

    @Autowired
    private PointApi pointApi;

    @Autowired
    private BackendCategoryAttrDao backendCategoryAttrDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private StoreApi storeApi;

    @Autowired
    private RedisCache redisCache;

    @Override
    public PageInfo<GoodsSkuCommentDto> getMerchentListGoodsSkuComment(
            GoodsSkuCommentDto goodsSkuCommentDto) throws GlobalException {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(goodsSkuCommentDto.getPageNo());
        pagePO.setPageSize(goodsSkuCommentDto.getPageSize());
        Page<GoodsSkuCommentDto> page = PageDataUtil.buildPageParam(pagePO);
        //获得到店铺的id
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        goodsSkuCommentDto.setBelongStore(storeId);
        List<GoodsSkuCommentDto> goodsSkuComments = goodsSkuComentDao.selectMerchentListGoodsSkuComment(page, goodsSkuCommentDto);
        if (CollectionUtils.isEmpty(goodsSkuComments)) {
            return PageDataUtil.copyPageInfo(page);
        }
        goodsSkuComments = getPictureName(goodsSkuComments);
        List<Long> commentIds = goodsSkuComments.stream().map(goodsSkuComment -> goodsSkuComment.getId()).collect(Collectors.toList());
        EntityWrapper<GoodsSkuCommentPictureEntity> cond = new EntityWrapper<>();
        cond.in("commentId", commentIds);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        //批量查询评价图片
        List<GoodsSkuCommentPictureEntity> goodsSkuCommentPictureEntities = goodsSkuCommentPictureDao.selectList(cond);

        //商品评价图片dto  copy  entity
        List<GoodsSkuCommentPictureDto> goodsSkuCommentPictureDtos = getGoodsSkuCommentPics(goodsSkuCommentPictureEntities);
        getGoodsSkuCommentDto(goodsSkuComments, goodsSkuCommentPictureDtos);
        page.setRecords(goodsSkuComments);
        return PageDataUtil.copyPageInfo(page);
    }

    private void getGoodsSkuCommentDto(List<GoodsSkuCommentDto> goodsSkuComments, List<GoodsSkuCommentPictureDto> goodsSkuCommentPictureDtos) {
        for (GoodsSkuCommentDto goodsSkuComment : goodsSkuComments) {
            if (CollectionUtils.isNotEmpty(goodsSkuCommentPictureDtos)) {
                List<GoodsSkuCommentPictureDto> goodsSkuCommentPictureDtoList = new ArrayList<>();
                for (GoodsSkuCommentPictureDto goodsSkuCommentPictureDto : goodsSkuCommentPictureDtos) {
                    if (goodsSkuComment.getId().equals(goodsSkuCommentPictureDto.getCommentId())) {
                        goodsSkuCommentPictureDtoList.add(goodsSkuCommentPictureDto);
                        goodsSkuComment.setGoodsSkuCommentPictureDtos(goodsSkuCommentPictureDtoList);
                    }
                }
            }
        }
    }

    private List<GoodsSkuCommentPictureDto> getGoodsSkuCommentPics(List<GoodsSkuCommentPictureEntity> goodsSkuCommentPictureEntities) {
        List<GoodsSkuCommentPictureDto> goodsSkuCommentPictureDtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(goodsSkuCommentPictureEntities)) {
            for (GoodsSkuCommentPictureEntity goodsSkuCommentPictureEntity : goodsSkuCommentPictureEntities) {
                GoodsSkuCommentPictureDto goodsSkuCommentPictureDto1 = new GoodsSkuCommentPictureDto();
                BeanCopyUtil.copy(goodsSkuCommentPictureEntity, goodsSkuCommentPictureDto1);
                goodsSkuCommentPictureDtos.add(goodsSkuCommentPictureDto1);
            }
        }
        return goodsSkuCommentPictureDtos;
    }

    @Override
    public Integer removeGoodsSkuComment(Long id) throws GlobalException {
        Integer i = 0;
        /**判断id是非空*/
        if (id == null) {
            throw new GlobalException(GoodsSkuCommentExceptionEnum.GOODSSKUCOMMENT_ID_NOT_NULL);
        }
        GoodsSkuCommentEntity goodsSkuComment = goodsSkuComentDao.selectById(id);
        /**判断id是非存在*/
        if (goodsSkuComment == null) {
            throw new GlobalException(GoodsSkuCommentExceptionEnum.GOODSSKUCOMMENT_ID_NOT_EXIST);
        }
        List<GoodsSkuCommentPictureEntity> goodsSkuCommentPictures = goodsSkuCommentPictureDao.selectGoodsSkuCommentPicture(id);
        /**删除评价下面对应的图片*/
        if (goodsSkuCommentPictures != null) {
            for (GoodsSkuCommentPictureEntity goodsSkuCommentPictureEntity : goodsSkuCommentPictures) {
                goodsSkuCommentPictureEntity.setDeletedFlag(Constants.DeletedFlag.DELETED_YES);
                i = goodsSkuCommentPictureDao.updateById(goodsSkuCommentPictureEntity);
            }
        }
        goodsSkuComment.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        i = goodsSkuComentDao.updateById(goodsSkuComment);
        return i;
    }

    @Override
    public Integer saveGoodsSkuCommentReply(GoodsSkuCommentDto goodsSkuCommentDto) throws GlobalException {
        /**判断id是非为空*/
        if (goodsSkuCommentDto.getId() == null) {
            throw new GlobalException(GoodsSkuCommentExceptionEnum.GOODSSKUCOMMENT_ID_NOT_NULL);
        }
        GoodsSkuCommentEntity goodsSkuComment = goodsSkuComentDao.selectById(goodsSkuCommentDto.getId());
        /**判断id是否存在*/
        if (goodsSkuComment == null) {
            throw new GlobalException(GoodsSkuCommentExceptionEnum.GOODSSKUCOMMENT_ID_NOT_EXIST);
        }
        goodsSkuComment.setReplyText(goodsSkuCommentDto.getReplyText());
        goodsSkuComment.setReplyTime(new Date());
        goodsSkuComment.setLastModifiedTime(new Date());
        goodsSkuComment.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        return goodsSkuComentDao.updateById(goodsSkuComment);
    }

    @Override
    public Integer saveGoodsSkuCommentDto(List<GoodsSkuCommentDto> goodsSkuCommentDtos, Long memberId, MemberTokenDto memberTokenDto) {
        Integer i = 0;
        if (CollectionUtils.isEmpty(goodsSkuCommentDtos)) {
            return 0;
        }
        Long orderId = goodsSkuCommentDtos.get(0).getOrderId();
        List<String> descriptions = goodsSkuCommentDtos.stream().map(goodsSkuCommentDto -> goodsSkuCommentDto.getDescription()).filter(comment -> StringUtils.isNotBlank(comment)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(descriptions)) {
            PointChangeDto pointChangeDto = new PointChangeDto();
            String operateSn = "COMMENT" + orderId;
            String telephone = memberTokenDto.getTelephone();
            String userName = memberTokenDto.getUserName();
            pointChangeDto.setMemberId(memberId);
            pointChangeDto.setTelephone(telephone);
            pointChangeDto.setUserName(userName);
            pointChangeDto.setOperateType(PointOperateType.COMMENTS);
            pointChangeDto.setPoint(addCommentPoint);
            pointChangeDto.setOperateSn(operateSn);
            boolean pointFlag = pointApi.addPoint(pointChangeDto);
        }
        for (GoodsSkuCommentDto goodsSkuCommentDto : goodsSkuCommentDtos) {
            List<GoodsSkuCommentPictureDto> goodsSkuCommentPictureDtos = goodsSkuCommentDto.getGoodsSkuCommentPictureDtos();
            GoodsSkuCommentEntity goodsSkuComment = new GoodsSkuCommentEntity();
            BeanCopyUtil.copy(goodsSkuCommentDto, goodsSkuComment);
            List<GoodsSkuCommentPictureEntity> goodsSkuCommentPictureEntities = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(goodsSkuCommentPictureDtos)) {
                for (GoodsSkuCommentPictureDto goodsSkuCommentPictureDto : goodsSkuCommentPictureDtos) {
                    GoodsSkuCommentPictureEntity goodsSkuCommentPicture = new GoodsSkuCommentPictureEntity();
                    BeanCopyUtil.copy(goodsSkuCommentPictureDto, goodsSkuCommentPicture);
                    goodsSkuCommentPictureEntities.add(goodsSkuCommentPicture);
                }
            }
            GoodsSkuEntity goodsSkuEntity = goodsSkuDao.selectById(goodsSkuComment.getSkuId());
            if (goodsSkuEntity == null) {
                throw new GlobalException(GoodsSkuExceptionEnum.GOODSKU_ID_NOT_EXIST);
            }

            //判断是好评中评还是差评
            Integer goodsReputation = goodsSkuComment.getGoodsReputation();
            Integer type = loadGoodsReputation(goodsReputation);
            /**添加商品评价信息*/
            goodsSkuComment.setCreatedTime(new Date());
            goodsSkuComment.setType(type);
            goodsSkuComment.setCreatorId(memberId);
            goodsSkuComment.setMemberId(memberId);
            goodsSkuComment.setUserName(memberTokenDto.getUserName());
            goodsSkuComment.setItemId(goodsSkuEntity.getItemId());
            if (goodsSkuEntity.getSaleFieldValue() != null) {
                String saleFieldValue = loadSaleFieidValue(goodsSkuEntity.getSaleFieldValue());
                goodsSkuComment.setSaleFieldValue(saleFieldValue);
            }
            if (goodsSkuComment.getDescription() == null || goodsSkuComment.getDescription() == "") {
                goodsSkuComment.setDescription("买家未填写评价内容");
            }
            i = goodsSkuComentDao.insert(goodsSkuComment);
            orderServiceApi.orderEvaluated(orderId);
            redisCache.delKeys(GoodsRedisKey.GOODS_DETAILS_COMMENT + goodsSkuEntity.getItemId());
            updateItemCommentCount(goodsSkuEntity.getItemId());
            Long commentId = goodsSkuComment.getId();
            /**添加商品评价图片信息*/
            if (CollectionUtils.isNotEmpty(goodsSkuCommentPictureEntities)) {
                for (GoodsSkuCommentPictureEntity goodsSkuCommentPictureEntity : goodsSkuCommentPictureEntities) {
                    goodsSkuCommentPictureEntity.setCommentId(commentId);
                    goodsSkuCommentPictureEntity.setCreatedTime(new Date());
                    goodsSkuCommentPictureEntity.setCreatorId(memberId);
                    i = goodsSkuCommentPictureDao.insert(goodsSkuCommentPictureEntity);
                }
            }
        }
        return i;
    }

    private void updateItemCommentCount(Long itemId) {
        ItemEntity cond = new ItemEntity();
        cond.clearInit();
        cond.setId(itemId);
        cond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        ItemEntity itemEntity = itemDao.selectOne(cond);
        if (itemEntity != null) {
            itemEntity.setCommentCount(itemEntity.getCommentCount() + 1);
            itemDao.updateById(itemEntity);
        }
    }

    private Integer loadGoodsReputation(Integer goodsReputation) {
        if (Objects.equals(goodsReputation, ONE_LEVEL) || Objects.equals(goodsReputation, TWO_LEVEL)) {
            return THREE_TYPE.getCode();
        } else if (Objects.equals(goodsReputation, THREE_LEVEL)) {
            return TWO_TYPE.getCode();
        } else {
            return ONE_TYPE.getCode();
        }
    }

    @Override
    public GoodsSkuCommentDto findGoodsSkuCommentById(Long id) throws GlobalException {
        /**判断id是非为空*/
        if (id == null) {
            throw new GlobalException(GoodsSkuCommentExceptionEnum.GOODSSKUCOMMENT_ID_NOT_NULL);
        }
        GoodsSkuCommentDto goodsSkuCommentDto = goodsSkuComentDao.selectGoodsSkuComentById(id);
        /**判断id是否存在*/
        if (goodsSkuCommentDto == null) {
            throw new GlobalException(GoodsSkuCommentExceptionEnum.GOODSSKUCOMMENT_ID_NOT_EXIST);
        }
        if (goodsSkuCommentDto != null) {
            //查询评价列表中的评价图片
            List<GoodsSkuCommentPictureDto> selectGoodsSkuCommentPicture = goodsSkuCommentPictureDao.selectGoodsSkuCommentPictureDto(goodsSkuCommentDto.getId());
            if (!(selectGoodsSkuCommentPicture == null || selectGoodsSkuCommentPicture.size() == 0)) {
                goodsSkuCommentDto.setGoodsSkuCommentPictureDtos(selectGoodsSkuCommentPicture);
            }
        }
        return goodsSkuCommentDto;
    }

    @Override
    public PageInfo<GoodsSkuCommentDto> getListGoodsSkuComment(GoodsSkuCommentDto goodsSkuCommentDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(goodsSkuCommentDto.getPageNo());
        pagePO.setPageSize(goodsSkuCommentDto.getPageSize());
        if (StringUtils.isNotBlank(goodsSkuCommentDto.getStoreName())) {
            List<StoreInfoDetailDTO> storeInfoList = storeApi.queryStores(goodsSkuCommentDto.getStoreName());
            List<Long> stroreIds = storeInfoList.stream().map(s -> s.getId()).collect(Collectors.toList());

            goodsSkuCommentDto.setStoreIds(stroreIds);
        }
        Page<GoodsSkuCommentDto> page = PageDataUtil.buildPageParam(pagePO);
        List<GoodsSkuCommentDto> goodsSkuComments = goodsSkuComentDao.selectListGoodsSkuComment(page, goodsSkuCommentDto);
        if (CollectionUtils.isEmpty(goodsSkuComments)) {
            return PageDataUtil.copyPageInfo(page);
        }
        goodsSkuComments = getStoreName(goodsSkuComments);//获取店铺名称
        goodsSkuComments = getPictureName(goodsSkuComments);//获取图片名
        List<Long> commentIds = goodsSkuComments.stream().map(goodsSkuComment -> goodsSkuComment.getId()).collect(Collectors.toList());
        EntityWrapper<GoodsSkuCommentPictureEntity> cond = new EntityWrapper<>();
        cond.in("commentId", commentIds);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        //批量查询评价图片
        List<GoodsSkuCommentPictureEntity> goodsSkuCommentPictureEntities = goodsSkuCommentPictureDao.selectList(cond);
        //商品评价图片dto  copy  entity
        List<GoodsSkuCommentPictureDto> goodsSkuCommentPictureDtos = getGoodsSkuCommentPics(goodsSkuCommentPictureEntities);
        getGoodsSkuCommentDto(goodsSkuComments, goodsSkuCommentPictureDtos);
        page.setRecords(goodsSkuComments);
        return PageDataUtil.copyPageInfo(page);
    }

    public List<GoodsSkuCommentDto> getPictureName(List<GoodsSkuCommentDto> list) {
        return list.stream().map(goodsSkuCommentDto -> {
            GoodsSkuEntity goodsSku = goodsSkuDao.selectById(goodsSkuCommentDto.getSkuId());
            if (goodsSku != null) {
                goodsSkuCommentDto.setPictureName(goodsSku.getSaleImage());
            }
            return goodsSkuCommentDto;
        }).collect(Collectors.toList());
    }

    public List<GoodsSkuCommentDto> getStoreName(List<GoodsSkuCommentDto> list) {
        return list.stream().map(goodsSkuCommentDto -> {
            StoreInfoDetailDTO storeInfoDetail = storeApi.getStore(goodsSkuCommentDto.getBelongStore());
            if (storeInfoDetail != null) {
                goodsSkuCommentDto.setStoreName(storeInfoDetail.getName());
            }
            return goodsSkuCommentDto;
        }).collect(Collectors.toList());
    }


    private String loadSaleFieidValue(String saleFieldValue2) {
        if (StringUtils.isBlank(saleFieldValue2)) {
            return "";
        }
        String saleFieldValue1 = "";
        String[] strss = saleFieldValue2.split(",");
        for (int i = 0; i < strss.length; i++) {
            String[] attrArray = strss[i].split(":");
            if (attrArray.length != 2
                    || StringUtils.isBlank(attrArray[0])
                    || StringUtils.isBlank(attrArray[1])) {
                continue;
            }
            BackendCategoryAttrEntity selectById = backendCategoryAttrDao.selectById(attrArray[0]);
            String name = selectById.getName();
            String value = attrArray[1];
            saleFieldValue1 = name + ":" + value + "  " + saleFieldValue1;
        }
        return saleFieldValue1;
    }

    @Override
    public List<GoodsSkuCommentDto> getGoodsSkuCommentListBySkuId(Long skuId) {
        List<GoodsSkuCommentDto> goodsSkuCommentDtos = goodsSkuComentDao.selectGoodsSkuCommentListBySkuId(skuId);
        for (GoodsSkuCommentDto goodsSkuCommentDto2 : goodsSkuCommentDtos) {
            //查询评价列表中的评价图片
            List<GoodsSkuCommentPictureDto> selectGoodsSkuCommentPicture = goodsSkuCommentPictureDao.selectGoodsSkuCommentPictureDto(goodsSkuCommentDto2.getId());
            if (!(selectGoodsSkuCommentPicture == null || selectGoodsSkuCommentPicture.size() == 0)) {
                goodsSkuCommentDto2.setGoodsSkuCommentPictureDtos(selectGoodsSkuCommentPicture);
            }
        }
        return goodsSkuCommentDtos;
    }

    @Override
    public GoodsSkuCommentDto getGoodsSkuCommentBySkuIdAndOrderId(Long skuId, Long orderId) {
        return goodsSkuComentDao.selectGoodsSkuCommentBySkuIdAndOrderId(skuId, orderId);
    }

}
