package com.topaiebiz.goods.repair.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.topaiebiz.goods.repair.dto.ItemPicDTO;
import com.topaiebiz.goods.repair.service.ItemImageRepairService;
import com.topaiebiz.goods.sku.dao.ItemDao;
import com.topaiebiz.goods.sku.dao.ItemPictureDao;
import com.topaiebiz.goods.sku.entity.ItemEntity;
import com.topaiebiz.goods.sku.entity.ItemPictureEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-03-10 16:27
 */
@Slf4j
@Service
public class ImageRepairServiceImpl implements ItemImageRepairService {

    private int maxImgCount = 5;

    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemPictureDao pictureDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void repairItemImages(Long itemId, List<ItemPicDTO> pics) {
        deleteItemPic(itemId);

        List<ItemPicDTO> targetPics = getTargetImages(pics);
        String mainImgUrl = setMainImg(pics);

        // log.info("---  itemId {} mainImg {} --> pic size: {}", itemId, mainImgUrl, JSON.toJSONString(targetPics, true));
        log.info("---  itemId {} mainImg {} --> pic size: {}", itemId, mainImgUrl, targetPics.size());
        updateItemMainImg(itemId, mainImgUrl);
        for (ItemPicDTO picDTO : targetPics) {
            ItemPictureEntity pic = new ItemPictureEntity();
            pic.setItemId(itemId);
            pic.setType(1);
            pic.setName(picDTO.getImgUrl());
            pic.setIsMain(picDTO.getIsMain() ? 1 : 0);
            pic.setCreatedTime(new Date());
            pic.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            pictureDao.insert(pic);
        }
    }

    private String setMainImg(List<ItemPicDTO> pics) {
        ItemPicDTO firstPic = pics.get(0);
        firstPic.setIsMain(true);
        return pics.get(0).getImgUrl();
    }

    private List<ItemPicDTO> getTargetImages(List<ItemPicDTO> pics) {
        List<ItemPicDTO> res = new ArrayList<>();
        int count = 0;
        //优先加载default的图片
        for (ItemPicDTO picDTO : pics) {
            if (picDTO.getIsDef() && count <= maxImgCount) {
                res.add(picDTO);
                count++;
            }
            if (count >= maxImgCount) {
                break;
            }
        }

        //补充到5条
        if (count < maxImgCount) {
            for (ItemPicDTO picDTO : pics) {
                if (!picDTO.getIsDef() && count <= maxImgCount) {
                    res.add(picDTO);
                    count++;
                }
                if (count >= maxImgCount) {
                    break;
                }
            }
        }
        return res;
    }


    private void deleteItemPic(Long itemId) {
        ItemPictureEntity cond = new ItemPictureEntity();
        cond.cleanInit();
        cond.setItemId(itemId);
        cond.setVersion(1L);
        pictureDao.delete(new EntityWrapper<>(cond));
        log.info("delete ItemPictureEntity where itemId = {}", itemId);
    }

    private void updateItemMainImg(Long itemId, String imgUrl) {
        ItemEntity update = new ItemEntity();
        update.cleanInit();
        update.setId(itemId);
        update.setPictureName(imgUrl);
        itemDao.updateById(update);
    }
}