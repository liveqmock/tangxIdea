package com.topaiebiz.giftcard.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.topaiebiz.giftcard.entity.GiftcardLog;
import com.topaiebiz.giftcard.entity.GiftcardRefundLog;
import com.topaiebiz.giftcard.vo.GiftcardLogReq;
import com.topaiebiz.giftcard.vo.MyGiftcardLogReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description: 处理老系统退款：礼卡操作日志dao
 * @author: Jeff Chen
 * @date: created in 上午10:53 2018/1/12
 */
@Mapper
public interface GiftcardRefundLogDao extends BaseMapper<GiftcardRefundLog>{


}
