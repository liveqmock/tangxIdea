package com.topaiebiz.giftcard.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.topaiebiz.giftcard.entity.GiftcardUnit;
import com.topaiebiz.giftcard.vo.GiftcardUnitReq;
import com.topaiebiz.giftcard.vo.MyGiftcardReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @description: 礼卡dao
 * @author: Jeff Chen
 * @date: created in 上午10:53 2018/1/12
 */
@Mapper
public interface GiftcardUnitDao extends BaseMapper<GiftcardUnit>{

    /**
     * 分页查询卡片
     * @param page
     * @param giftcardEntityReq
     * @return
     */
    List<GiftcardUnit> queryGiftcard(Page page, GiftcardUnitReq giftcardEntityReq);

    /**
     * 分页查询我的卡片
     * @param page
     * @param myGiftcardReq
     * @return
     */
    List<GiftcardUnit> selectMyGiftcardByCategory(Page page, MyGiftcardReq myGiftcardReq);

    /**
     * 按指定属性查询
     * @param giftcardUnit
     * @return
     */
    GiftcardUnit getGiftcardInfo(GiftcardUnit giftcardUnit);

    /**
     * 查询用户绑定的卡
     * @param bindingMember
     * @return
     */
    List<GiftcardUnit> selectMemberBoundCards(@Param("bindingMember") Long bindingMember);

    /**
     * 查询用户购买指定批次礼卡的数量
     * @param batchId
     * @param owner
     * @return
     */
    int countByMemberAndBatch(@Param("batchId") Long batchId,@Param("owner") Long owner);

    /**
     * 批量查询用户余额
     * @param memberIdList
     * @return
     */
    List<Map<String, Object>> selectBalanceByMemberList(@Param("memberIdList") List<Long> memberIdList,@Param("useType") Integer useType);

    /**
     * 查询某批次各状态数量
     * @param batchId
     * @return
     */
    List<Map<String, Object>> selectCardNumByStatus(@Param("batchId") Long batchId);
}
