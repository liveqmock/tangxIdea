package com.topaiebiz.giftcard.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.topaiebiz.giftcard.entity.GiftcardSelect;
import com.topaiebiz.giftcard.vo.GiftcardSelectReq;
import com.topaiebiz.giftcard.vo.GiftcardSelectVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: 礼卡精选dao
 * @author: Jeff Chen
 * @date: created in 上午10:53 2018/1/12
 */
@Mapper
public interface GiftcardSelectDao extends BaseMapper<GiftcardSelect>{

    /**
     * 分页查询
     * @param page
     * @param giftcardSelectReq
     * @return
     */
    List<GiftcardSelectVO> querySelect(Page page, GiftcardSelectReq giftcardSelectReq);

    /**
     * 查找seq下级的一条数据
     * @param seq
     * @return
     */
    GiftcardSelect nextBySeq(@Param("seq") Integer seq);

    /**
     * 查找seq上级的一条数据
     * @param seq
     * @return
     */
    GiftcardSelect uponBySeq(@Param("seq") Integer seq);
}
