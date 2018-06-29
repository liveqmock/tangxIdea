package com.topaiebiz.giftcard.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.topaiebiz.giftcard.entity.GiftcardLabel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: 礼卡标签dao
 * @author: Jeff Chen
 * @date: created in 上午10:53 2018/1/12
 */
@Mapper
public interface GiftcardLabelDao extends BaseMapper<GiftcardLabel> {

    /**
     * 分页查询
     * @param page 返回分页信息
     * @param labelName
     * @return
     */
    List<GiftcardLabel> queryGiftcardLabel(Page<GiftcardLabel> page, @Param("labelName") String labelName);
}
