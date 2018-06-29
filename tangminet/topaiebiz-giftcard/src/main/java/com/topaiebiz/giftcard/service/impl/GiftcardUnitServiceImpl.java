package com.topaiebiz.giftcard.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.card.constant.ApplyScopeEnum;
import com.topaiebiz.card.dto.PayCard;
import com.topaiebiz.card.dto.PayInfoDTO;
import com.topaiebiz.card.dto.PaySubOrder;
import com.topaiebiz.card.dto.RefundOrderDTO;
import com.topaiebiz.giftcard.BizConstants;
import com.topaiebiz.giftcard.dao.GiftcardBatchDao;
import com.topaiebiz.giftcard.dao.GiftcardLogDao;
import com.topaiebiz.giftcard.dao.GiftcardOpLogDao;
import com.topaiebiz.giftcard.dao.GiftcardUnitDao;
import com.topaiebiz.giftcard.entity.GiftcardBatch;
import com.topaiebiz.giftcard.entity.GiftcardLog;
import com.topaiebiz.giftcard.entity.GiftcardOpLog;
import com.topaiebiz.giftcard.entity.GiftcardUnit;
import com.topaiebiz.giftcard.enums.*;
import com.topaiebiz.giftcard.service.GiftcardOpLogService;
import com.topaiebiz.giftcard.service.GiftcardUnitService;
import com.topaiebiz.giftcard.util.AESUtil;
import com.topaiebiz.giftcard.util.DateUtil;
import com.topaiebiz.giftcard.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @description:
 * @author: Jeff Chen
 * @date: created in 下午3:45 2018/1/12
 */
@Service
@Slf4j
public class GiftcardUnitServiceImpl extends ServiceImpl<GiftcardUnitDao, GiftcardUnit> implements GiftcardUnitService {

    @Autowired
    private GiftcardUnitDao giftcardUnitDao;
    @Autowired
    private GiftcardLogDao giftcardLogDao;
    @Autowired
    private GiftcardBatchDao giftcardBatchDao;
    @Autowired
    private GiftcardOpLogService giftcardOpLogService;
    @Autowired
    private RedisCache redisCache;
    @Override
    public List<GiftcardExportVO> export(Long batchId) {
        EntityWrapper<GiftcardUnit> wrapper = new EntityWrapper<>();
        wrapper.eq("batchId", batchId);
        List<GiftcardUnit> giftcardUnits = selectList(wrapper);
        if (CollectionUtils.isEmpty(giftcardUnits)) {
            throw new GlobalException(GiftcardExceptionEnum.NEED_TO_PRODUCE);
        }
        List<GiftcardExportVO> giftcardExportVOList = new ArrayList<>(giftcardUnits.size());
        giftcardUnits.forEach(giftcardUnit -> {
            GiftcardExportVO giftcardExportVO = new GiftcardExportVO();
            giftcardExportVO.setCardNo(giftcardUnit.getCardNo());
            giftcardExportVO.setPassword(AESUtil.decrypt(giftcardUnit.getPassword(), AESUtil.CARD_ENCRYPT_KEY));
            giftcardExportVOList.add(giftcardExportVO);
        });
        return giftcardExportVOList;
    }

    @Override
    public PageInfo<GiftcardUnitVO> queryGiftcard(GiftcardUnitReq giftcardEntityReq) {
        Page page = PageDataUtil.buildPageParam(giftcardEntityReq);
        List<GiftcardUnit> giftcardEntityList = giftcardUnitDao.queryGiftcard(page, giftcardEntityReq);
        if (!CollectionUtils.isEmpty(giftcardEntityList)) {
            List<GiftcardUnitVO> giftcardEntityVOList = new ArrayList<>(giftcardEntityReq.getPageSize());
            giftcardEntityList.forEach(giftcardEntity -> {
                GiftcardUnitVO giftcardEntityVO = new GiftcardUnitVO();
                BeanCopyUtil.copy(giftcardEntity, giftcardEntityVO);
                giftcardEntityVO.setUnitId(giftcardEntity.getId());
                //如果有失效时间，则有效天数=deadTime-now
                if (null != giftcardEntity.getDeadTime()) {
                    giftcardEntityVO.setValidDays(DateUtil.diffDays(giftcardEntity.getDeadTime(), new Date()));
                }
                giftcardEntityVOList.add(giftcardEntityVO);
            });
            page.setRecords(giftcardEntityVOList);
        }
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean renewal(UnitHandleReq unitHandleReq) {
        GiftcardUnit giftcardUnit = giftcardUnitDao.selectById(unitHandleReq.getUnitId());
        if (null == giftcardUnit) {
            throw new GlobalException(GiftcardExceptionEnum.UNIT_NOT_EXIST);
        }
        if (CardUnitStatusEnum.EXPIRE.getStatusCode() != giftcardUnit.getCardStatus()) {
            //未绑定的卡无需续期
            throw new GlobalException(GiftcardExceptionEnum.NOT_TO_RENEWAL);
        }
        if (null == unitHandleReq.getRenewalDays() || unitHandleReq.getRenewalDays().intValue() < 1) {
            throw new GlobalException(GiftcardExceptionEnum.RENEWAL_LIMIT);
        }
        //续期是对dead_time进行续期
        GiftcardUnit unitUpdate = new GiftcardUnit();
        unitUpdate.setId(unitHandleReq.getUnitId());
        unitUpdate.setModifiedTime(unitHandleReq.getModifiedTime());
        unitUpdate.setModifier(unitHandleReq.getModifier());
        unitUpdate.setDeadTime(DateUtil.renewalDays(giftcardUnit.getDeadTime(), unitHandleReq.getRenewalDays()));
        //续期后变成绑定状态
        unitUpdate.setCardStatus(CardUnitStatusEnum.BOUND.getStatusCode());
        unitUpdate.setLastStatus(giftcardUnit.getCardStatus());
        if (giftcardUnitDao.updateById(unitUpdate) > 0) {
            GiftcardOpLog giftcardOpLog = new GiftcardOpLog();
            giftcardOpLog.setBizId(unitHandleReq.getUnitId());
            giftcardOpLog.setOperator(unitHandleReq.getModifier());
            giftcardOpLog.setNote(unitHandleReq.getNote());
            giftcardOpLog.setOpTime(new Date());
            giftcardOpLog.setOpSrc(OpSrcEnum.OP_CARD.getSrcId());
            giftcardOpLog.setOpType(CardOpTypeEnum.RENEW.getOpType());
            return giftcardOpLogService.insert(giftcardOpLog);
        }
        return false;
    }

    @Override
    public GiftcardUnitVO getGiftcardInfoById(Long unitId) {
        GiftcardUnit selectUnit = new GiftcardUnit();
        selectUnit.setId(unitId);
        GiftcardUnit giftcardUnit = giftcardUnitDao.getGiftcardInfo(selectUnit);
        if (null == giftcardUnit) {
            throw new GlobalException(GiftcardExceptionEnum.UNIT_NOT_EXIST);
        }
        GiftcardUnitVO giftcardUnitVO = new GiftcardUnitVO();
        BeanCopyUtil.copy(giftcardUnit, giftcardUnitVO);
        giftcardUnitVO.setUnitId(giftcardUnit.getId());
        //batchNo准备废弃
        giftcardUnitVO.setBatchNo(giftcardUnit.getBatchId()+"");
        //如果有失效时间，则有效天数=deadTime-now
        if (null != giftcardUnit.getDeadTime()) {
            giftcardUnitVO.setValidDays(DateUtil.diffDays(giftcardUnit.getDeadTime(), new Date()));
        }
        //贴现：数据库保存的是比例，输出金额
        BigDecimal discount = giftcardUnit.getFaceValue().subtract(giftcardUnit.getSalePrice());
        giftcardUnitVO.setPlatformDiscount(discount.multiply(giftcardUnit.getPlatformDiscount()).divide(BigDecimal.valueOf(100)));
        giftcardUnitVO.setStoreDiscount(discount.multiply(giftcardUnit.getStoreDiscount()).divide(BigDecimal.valueOf(100)));
        giftcardUnitVO.setMediumStr(CardMediumEnum.getByMediumId(giftcardUnit.getMedium()).getMediumName());
        //操作日志
        GiftcardOpLog giftcardOpLog = new GiftcardOpLog();
        giftcardOpLog.setBizId(unitId);
        giftcardOpLog.setOpSrc(OpSrcEnum.OP_CARD.getSrcId());
        List<CardOpLogVO> opLogVOList = giftcardOpLogService.selectByBizId(giftcardOpLog);
        giftcardUnitVO.setOpLogList(opLogVOList);
        return giftcardUnitVO;
    }

    @Override
    public Boolean active(List<String> ids, String modifier) {
        //为了兼容激活老数据，暂时无法批量操作
        int total = 0;
        if (!CollectionUtils.isEmpty(ids)) {
            for (String id : ids) {
                GiftcardUnit unit = selectById(Long.valueOf(id));
                if (null != unit) {
                    //老数据中存在未激活未绑定余额为0的数据，激活时从t_giftcard_batch获取面值
                    if (null == unit.getBindingMember() && unit.getBalance().compareTo(BigDecimal.ZERO) == 0
                            && unit.getCardStatus().equals(CardUnitStatusEnum.INACTIVED.getStatusCode())) {
                        GiftcardBatch batch = giftcardBatchDao.selectById(unit.getBatchId());
                        if (null != batch) {
                            unit.setBalance(batch.getFaceValue());
                        }
                    }
                    unit.setModifier(modifier);
                    unit.setModifiedTime(new Date());
                    unit.setCardStatus(CardUnitStatusEnum.ACTIVED.getStatusCode());
                    unit.setActiveTime(new Date());
                    if (updateById(unit)) {
                        total++;
                    }
                }

            }
            log.info("本次激活卡片数量：{}", total);
        }
        return total > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean freeze(List<Long> ids, String modifier) {
        int count = 0;
        if (!CollectionUtils.isEmpty(ids)) {
            for (Long unitId : ids) {
                GiftcardUnit unit = selectById(unitId);
                if (null != unit) {
                    unit.setLastStatus(unit.getCardStatus());
                    unit.setCardStatus(CardUnitStatusEnum.FREEZED.getStatusCode());
                    unit.setModifiedTime(new Date());
                    unit.setModifier(modifier);
                    updateById(unit);
                    count++;
                }
            }
        }
        return count > 0;
    }

    @Override
    public Boolean unfreeze(Long unitId, String modifier) {
        GiftcardUnit unit = selectById(unitId);
        if (null == unit || CardUnitStatusEnum.FREEZED.getStatusCode() != unit.getCardStatus()) {
            throw new GlobalException(GiftcardExceptionEnum.NOT_TO_UNFREEZE);
        }
        unit.setCardStatus(unit.getLastStatus());
        unit.setModifier(modifier);
        unit.setModifiedTime(new Date());
        if (giftcardUnitDao.updateById(unit) > 0) {
            GiftcardOpLog giftcardOpLog = new GiftcardOpLog();
            giftcardOpLog.setBizId(unitId);
            giftcardOpLog.setOperator(modifier);
            giftcardOpLog.setNote("");
            giftcardOpLog.setOpTime(new Date());
            giftcardOpLog.setOpSrc(OpSrcEnum.OP_CARD.getSrcId());
            giftcardOpLog.setOpType(CardOpTypeEnum.UNFREEZE.getOpType());
            return giftcardOpLogService.insert(giftcardOpLog);
        }
        return false;
    }

    @Override
    public Boolean unfreeze(Long unitId, String modifier, String note) {
        GiftcardUnit unit = selectById(unitId);
        if (null == unit || CardUnitStatusEnum.FREEZED.getStatusCode() != unit.getCardStatus()) {
            throw new GlobalException(GiftcardExceptionEnum.NOT_TO_UNFREEZE);
        }
        unit.setCardStatus(unit.getLastStatus());
        unit.setLastStatus(CardUnitStatusEnum.FREEZED.getStatusCode());
        unit.setModifier(modifier);
        unit.setModifiedTime(new Date());
        if (giftcardUnitDao.updateById(unit) > 0) {
            GiftcardOpLog giftcardOpLog = new GiftcardOpLog();
            giftcardOpLog.setBizId(unitId);
            giftcardOpLog.setOperator(modifier);
            giftcardOpLog.setNote(note);
            giftcardOpLog.setOpTime(new Date());
            giftcardOpLog.setOpSrc(OpSrcEnum.OP_CARD.getSrcId());
            giftcardOpLog.setOpType(CardOpTypeEnum.UNFREEZE.getOpType());
            return giftcardOpLogService.insert(giftcardOpLog);
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchActive(CardOpReq cardOpReq) {
        int count = 0;
        if (!CollectionUtils.isEmpty(cardOpReq.getIdList())) {
            for (Long uid : cardOpReq.getIdList()) {
                GiftcardUnit unit = selectById(uid);
                if (null != unit) {
                    GiftcardUnit updUnit = new GiftcardUnit();
                    updUnit.setId(uid);
                    updUnit.setLastStatus(unit.getCardStatus());
                    updUnit.setCardStatus(CardUnitStatusEnum.ACTIVED.getStatusCode());
                    updUnit.setModifiedTime(new Date());
                    updUnit.setModifier(cardOpReq.getOperator());
                    updUnit.setActiveTime(new Date());
                    //老数据中存在未激活未绑定余额为0的数据，激活时从t_giftcard_batch获取面值
                    if (null == unit.getBindingMember() && unit.getBalance().compareTo(BigDecimal.ZERO) == 0
                            && unit.getCardStatus().equals(CardUnitStatusEnum.INACTIVED.getStatusCode())) {
                        GiftcardBatch batch = giftcardBatchDao.selectById(unit.getBatchId());
                        if (null != batch) {
                            updUnit.setBalance(batch.getFaceValue());
                        }
                    }
                    if (giftcardUnitDao.updateById(updUnit) > 0) {
                        GiftcardOpLog giftcardOpLog = new GiftcardOpLog();
                        giftcardOpLog.setBizId(uid);
                        giftcardOpLog.setNote(cardOpReq.getNote());
                        giftcardOpLog.setOperator(cardOpReq.getOperator());
                        giftcardOpLog.setOpTime(new Date());
                        giftcardOpLog.setOpSrc(OpSrcEnum.OP_CARD.getSrcId());
                        giftcardOpLog.setOpType(CardOpTypeEnum.ACTIVE.getOpType());
                        if (giftcardOpLogService.insert(giftcardOpLog)) {
                            count++;
                        }
                    }
                }
            }
        }
        log.info("{}批量激活礼卡数量:{}",cardOpReq.getOperator(),count);
        return count>0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchFreeze(CardOpReq cardOpReq) {
        int count = 0;
        if (!CollectionUtils.isEmpty(cardOpReq.getIdList())) {
            for (Long uid : cardOpReq.getIdList()) {
                GiftcardUnit unit = selectById(uid);
                if (null != uid) {
                    //只有已激活和已绑定可以冻结
                    if (CardUnitStatusEnum.ACTIVED.getStatusCode() != unit.getCardStatus()
                            && CardUnitStatusEnum.BOUND.getStatusCode() != unit.getCardStatus()) {
                        continue;
                    }
                    GiftcardUnit updUnit = new GiftcardUnit();
                    updUnit.setId(uid);
                    updUnit.setLastStatus(unit.getCardStatus());
                    updUnit.setCardStatus(CardUnitStatusEnum.FREEZED.getStatusCode());
                    updUnit.setModifiedTime(new Date());
                    updUnit.setModifier(cardOpReq.getOperator());
                    if (giftcardUnitDao.updateById(updUnit) > 0) {
                        GiftcardOpLog giftcardOpLog = new GiftcardOpLog();
                        giftcardOpLog.setBizId(uid);
                        giftcardOpLog.setNote(cardOpReq.getNote());
                        giftcardOpLog.setOperator(cardOpReq.getOperator());
                        giftcardOpLog.setOpTime(new Date());
                        giftcardOpLog.setOpSrc(OpSrcEnum.OP_CARD.getSrcId());
                        giftcardOpLog.setOpType(CardOpTypeEnum.FREEZE.getOpType());
                        if (giftcardOpLogService.insert(giftcardOpLog)) {
                            count++;
                        }
                    }
                }
            }
        }
        log.info("{}批量冻结礼卡数量:{}",cardOpReq.getOperator(),count);
        return count>0;
    }

    @Override
    public List<GiftcardUnit> selectMemberBoundCards(Long memberId) {
        return giftcardUnitDao.selectMemberBoundCards(memberId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean payByCards(PayInfoDTO payInfoDTO) {
        log.info("礼卡支付信息：{}", JSONObject.toJSON(payInfoDTO));
        if (null == payInfoDTO.getTotalAmount()) {
            log.error("礼卡支付金额不能为空");
            throw new GlobalException(GiftcardExceptionEnum.CARD_PAID_PARAM_ERROR);
        }
        //1.添加日志 2.更新余额
        List<PaySubOrder> subOrderList = payInfoDTO.getSubOrderList();
        if (!CollectionUtils.isEmpty(subOrderList)) {
            BigDecimal total = BigDecimal.ZERO;
            for (PaySubOrder paySubOrder : subOrderList) {
                List<PayCard> payCardList = paySubOrder.getCardList();
                if (!CollectionUtils.isEmpty(payCardList)) {
                    for (PayCard payCard : payCardList) {
                        GiftcardUnit unit = selectByCardNo(payCard.getCardNo(), payInfoDTO.getMemberId());
                        if (null == unit||!unit.getCardStatus().equals(CardUnitStatusEnum.BOUND.getStatusCode())) {
                            log.error("礼卡不可用：{}", payCard.getCardNo());
                            throw new GlobalException(GiftcardExceptionEnum.UNIT_NOT_EXIST);
                        }
                        total = total.add(payCard.getAmount());
                        GiftcardLog giftcardLog = new GiftcardLog();
                        giftcardLog.setLogType(GiftcardLogTypeEnum.CONSUME.getType());
                        giftcardLog.setAmount(BigDecimal.ZERO.subtract(payCard.getAmount()));
                        giftcardLog.setCardNo(payCard.getCardNo());
                        giftcardLog.setMemberId(payInfoDTO.getMemberId());
                        giftcardLog.setMemberName(payInfoDTO.getMemberName());
                        giftcardLog.setCreatedTime(new Date());
                        giftcardLog.setGoodsId(payCard.getGoodsId());
                        giftcardLog.setGoodsName(payCard.getGoodsName());
                        giftcardLog.setBalance(unit.getBalance().subtract(payCard.getAmount()));
                        giftcardLog.setUnitId(unit.getId());
                        giftcardLog.setStoreId(paySubOrder.getStoreId().longValue());
                        giftcardLog.setStoreName(paySubOrder.getStoreName());
                        giftcardLog.setPaySn(payInfoDTO.getPaySn());
                        giftcardLog.setOrderSn(paySubOrder.getOrderSn());
                        if (giftcardLogDao.insert(giftcardLog) > 0) {
                            GiftcardUnit updUnit = new GiftcardUnit();
                            updUnit.setId(unit.getId());
                            updUnit.setBalance(giftcardLog.getBalance());
                            EntityWrapper<GiftcardUnit> wrapper = new EntityWrapper<>();
                            wrapper.eq("id", unit.getId());
                            //乐观锁
                            wrapper.eq("balance", unit.getBalance());
                            if (giftcardUnitDao.update(updUnit, wrapper) < 1) {
                                throw new GlobalException(GiftcardExceptionEnum.CARD_PAID_ERROR);
                            }
                        }
                    }
                }
            }
            if (total.compareTo(payInfoDTO.getTotalAmount()) != 0) {
                log.error("支付明细汇总{}不等于支付总金额{}", total, payInfoDTO.getTotalAmount());
                throw new GlobalException(GiftcardExceptionEnum.CARD_PAID_PARAM_ERROR);
            }

        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean refundCards(RefundOrderDTO refundOrderDTO) {
        log.info("礼卡退款信息：{}", JSONObject.toJSON(refundOrderDTO));
        if (null == refundOrderDTO.getTotalAmount()) {
            log.error("退款金额不能为空");
            throw new GlobalException(GiftcardExceptionEnum.REFUND_PARAM_ERROR);
        }
        //退单号查询
        if (null != refundOrderDTO.getOrderNo()) {
            EntityWrapper<GiftcardLog> wrapper = new EntityWrapper<>();
            wrapper.eq("orderSn", refundOrderDTO.getOrderNo());
            wrapper.eq("logType", 2);
            Integer one = giftcardLogDao.selectCount(wrapper);
            if (one>0) {
                log.info("重复退款发生：{}",JSONObject.toJSON(refundOrderDTO));
                return true;
            }
        }
        List<PayCard> payCardList = refundOrderDTO.getPayCardList();
        if (!CollectionUtils.isEmpty(payCardList)) {
            BigDecimal total = BigDecimal.ZERO;
            for (PayCard payCard : payCardList) {
                GiftcardUnit unit = selectByCardNo(payCard.getCardNo(), refundOrderDTO.getMemberId());
                if (null == unit) {
                    log.error("礼卡不存在：{}", payCard.getCardNo());
                    throw new GlobalException(GiftcardExceptionEnum.UNIT_NOT_EXIST);
                }
                total = total.add(payCard.getAmount());
                GiftcardLog giftcardLog = new GiftcardLog();
                giftcardLog.setLogType(GiftcardLogTypeEnum.REFUND.getType());
                giftcardLog.setAmount(payCard.getAmount());
                giftcardLog.setCardNo(payCard.getCardNo());
                giftcardLog.setStoreName(payCard.getStoreName());
                giftcardLog.setStoreId(payCard.getStoreId());
                giftcardLog.setMemberId(refundOrderDTO.getMemberId());
                giftcardLog.setMemberName(refundOrderDTO.getMemberName());
                giftcardLog.setCreatedTime(new Date());
                giftcardLog.setGoodsId(payCard.getGoodsId());
                giftcardLog.setGoodsName(payCard.getGoodsName());
                giftcardLog.setBalance(unit.getBalance().add(payCard.getAmount()));
                giftcardLog.setUnitId(unit.getId());
                giftcardLog.setPaySn(refundOrderDTO.getPaySn());
                giftcardLog.setOrderSn(refundOrderDTO.getOrderNo());
                if (giftcardLogDao.insert(giftcardLog) > 0) {
                    GiftcardUnit updUnit = new GiftcardUnit();
                    //如果是已用完状态，改为绑定状态
                    updUnit.setCardStatus(unit.getCardStatus().equals(CardUnitStatusEnum.CLEAR.getStatusCode())?
                            CardUnitStatusEnum.BOUND.getStatusCode():unit.getCardStatus());
                    updUnit.setBalance(giftcardLog.getBalance());
                    //乐观锁
                    EntityWrapper<GiftcardUnit> wrapper = new EntityWrapper<>();
                    wrapper.eq("balance", unit.getBalance());
                    wrapper.eq("id", unit.getId());
                    if (giftcardUnitDao.update(updUnit, wrapper) < 1) {
                        throw new GlobalException(GiftcardExceptionEnum.REFUND_ERROR);
                    }
                }
            }
            if (total.compareTo(refundOrderDTO.getTotalAmount()) != 0) {
                log.error("退款明细汇总{}不等于退款总额{}",total,refundOrderDTO.getTotalAmount());
                throw new GlobalException(GiftcardExceptionEnum.REFUND_PARAM_ERROR);
            }
        }

        return true;
    }

    @Override
    public GiftcardUnit selectByCardNo(String cardNo, Long memberId) {
        EntityWrapper<GiftcardUnit> wrapper = new EntityWrapper<>();
        wrapper.eq("cardNo", cardNo);
        wrapper.eq("bindingMember", memberId);
        wrapper.eq("delFlag", 0);
        wrapper.last("limit 1");
        List<GiftcardUnit> giftcardUnits = selectList(wrapper);
        if (!CollectionUtils.isEmpty(giftcardUnits)) {
            return giftcardUnits.get(0);
        }
        return null;
    }

    @Override
    public BigDecimal totalBalance(Long memberId) {
        List<GiftcardUnit> units = selectMemberBoundCards(memberId);
        BigDecimal total = BigDecimal.ZERO;
        if (!CollectionUtils.isEmpty(units)) {
            for (GiftcardUnit giftcardUnit : units) {
                if (giftcardUnit.getCardStatus().equals(CardUnitStatusEnum.BOUND.getStatusCode())) {
                    total = total.add(giftcardUnit.getBalance());
                }
            }
        }
        return total;
    }

    @Override
    public MyGiftcardListVO selectMyGiftcardList(Long memberId) {
        List<GiftcardUnit> units = selectMemberBoundCards(memberId);
        MyGiftcardListVO myGiftcardListVO = new MyGiftcardListVO();
        if (!CollectionUtils.isEmpty(units)) {
            List<MyGiftcardVO> validList = new ArrayList<>();
            List<MyGiftcardVO> invalidList = new ArrayList<>();
            units.forEach(giftcardUnit -> {
                //只有绑定状态才可用
                if (giftcardUnit.getCardStatus().equals(CardUnitStatusEnum.BOUND.getStatusCode())) {
                    MyGiftcardVO myGiftcardVO = new MyGiftcardVO();
                    BeanCopyUtil.copy(giftcardUnit, myGiftcardVO);
                    myGiftcardVO.setUnitId(giftcardUnit.getId());
                    myGiftcardVO.setScope(ApplyScopeEnum.getById(giftcardUnit.getApplyScope()).getScopeDesc());
                    validList.add(myGiftcardVO);
                } else {
                    MyGiftcardVO myGiftcardVO = new MyGiftcardVO();
                    BeanCopyUtil.copy(giftcardUnit, myGiftcardVO);
                    myGiftcardVO.setUnitId(giftcardUnit.getId());
                    myGiftcardVO.setScope(ApplyScopeEnum.getById(giftcardUnit.getApplyScope()).getScopeDesc());
                    invalidList.add(myGiftcardVO);
                }

            });
            myGiftcardListVO.setInvalidCardList(invalidList);
            myGiftcardListVO.setValidCardList(validList);
        }
        return myGiftcardListVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean bindCard(CardBindVO cardBindVO) {
        GiftcardUnit selectUnit = new GiftcardUnit();
        selectUnit.setCardNo(cardBindVO.getCardNo());
        GiftcardUnit giftcardUnit = giftcardUnitDao.getGiftcardInfo(selectUnit);
        if (null == giftcardUnit) {
            throw new GlobalException(GiftcardExceptionEnum.BIND_ERROR);
        }
        //1.卡密绑定
        if (cardBindVO.getBindWay().equals(BindWayEnum.CARD_PWD.getWayId())) {
            if (giftcardUnit.getCardStatus().equals(CardUnitStatusEnum.BOUND.getStatusCode())) {
                throw new GlobalException(GiftcardExceptionEnum.HAD_BOUND);
            }
            if (giftcardUnit.getCardStatus().equals(CardUnitStatusEnum.INACTIVED.getStatusCode())) {
                throw new GlobalException(GiftcardExceptionEnum.INACTIVED_NOT_BOUND);
            }
            //老数据中存在自定义面值的卡，余额为0的情况，待查
            if (giftcardUnit.getBalance().equals(BigDecimal.ZERO)) {
                throw new GlobalException(GiftcardExceptionEnum.CARD_BALANCE_EXCEPTION);
            }
            //冻结 ，清零
            if (!giftcardUnit.getCardStatus().equals(CardUnitStatusEnum.ACTIVED.getStatusCode())) {
                throw new GlobalException(GiftcardExceptionEnum.INACTIVED_NOT_BOUND);
            }
            //老数据：存在md5和明文
            //新系统：AES加密
            Integer tryNum = redisCache.getInt(BizConstants.BIND_CARD_KEY + cardBindVO.getCardNo());
            //三小时限制五次
            if (null != tryNum && tryNum >= 5) {
                throw new GlobalException(GiftcardExceptionEnum.BIND_TRY_LIMIT);
            }
            if (!giftcardUnit.getPassword().equalsIgnoreCase(DigestUtils.md5Hex(cardBindVO.getPwd()))) {
                if (!giftcardUnit.getPassword().equals(cardBindVO.getPwd())) {
                    if (!AESUtil.decrypt(giftcardUnit.getPassword(), AESUtil.CARD_ENCRYPT_KEY).equals(cardBindVO.getPwd())) {
                        //三小时限制五次
                        redisCache.set(BizConstants.BIND_CARD_KEY + cardBindVO.getCardNo(), (tryNum == null ? 1 : ++tryNum),3*60*60);
                        throw new GlobalException(GiftcardExceptionEnum.BIND_ERROR);
                    }
                }
            }
            giftcardUnit.setModifier(BizConstants.CARD_PWD_USER);
            giftcardUnit.setDeadTime(DateUtil.renewalDays(new Date(), giftcardUnit.getValidDays()));
        } else if (cardBindVO.getBindWay().equals(BindWayEnum.GET_GIVEN.getWayId())) {
            //2.转赠领取
            if (giftcardUnit.getGivenStatus() != 1) {
                throw new GlobalException(GiftcardExceptionEnum.NOT_GET_GIVEN);
            }
            if (giftcardUnit.getBalance().compareTo(giftcardUnit.getFaceValue()) != 0) {
                throw new GlobalException(GiftcardExceptionEnum.USED_NOT_GET);
            }
            //转赠领取绑定
            giftcardUnit.setGivenStatus(GivenStatusEnum.HAD_GIVEN.getCode());
            giftcardUnit.setModifier(BizConstants.GIVEN_GET_USER);
        } else if (cardBindVO.getBindWay().equals(BindWayEnum.ONE_KEY.getWayId())) {
            //3.一键绑定
            giftcardUnit.setModifier(BizConstants.BINDING_DIRECT);
            giftcardUnit.setDeadTime(DateUtil.renewalDays(new Date(), giftcardUnit.getValidDays()));
        } else {
            throw new GlobalException(GiftcardExceptionEnum.INVALID_REQ);
        }
        giftcardUnit.setBindingMember(cardBindVO.getMemberId());
        giftcardUnit.setBindingTime(new Date());
        giftcardUnit.setCardStatus(CardUnitStatusEnum.BOUND.getStatusCode());
        return updateById(giftcardUnit);
    }

    @Override
    public PageInfo<MyGiftcardVO> getMyGiftcardBycategory(MyGiftcardReq myGiftcardReq) {
        Page page = PageDataUtil.buildPageParam(myGiftcardReq);
        List<GiftcardUnit> giftcardUnitList = giftcardUnitDao.selectMyGiftcardByCategory(page, myGiftcardReq);
        if (!CollectionUtils.isEmpty(giftcardUnitList)) {
            List<MyGiftcardVO> myGiftcardVOList = new ArrayList<>();
            giftcardUnitList.forEach(giftcardUnit -> {
                MyGiftcardVO myGiftcardVO = new MyGiftcardVO();
                //兼容老数据
                if (null == giftcardUnit.getDeadTime()) {
                    giftcardUnit.setDeadTime(new Date(1835280000000L));
                }
                if (giftcardUnit.getBalance().compareTo(BigDecimal.ZERO) == 0) {
                    giftcardUnit.setCardStatus(CardUnitStatusEnum.CLEAR.getStatusCode());
                }
                BeanCopyUtil.copy(giftcardUnit, myGiftcardVO);
                myGiftcardVO.setUnitId(giftcardUnit.getId());
                myGiftcardVO.setScope(ApplyScopeEnum.getById(giftcardUnit.getApplyScope()).getScopeDesc());
                //是否转赠 :0-不可转赠，1-可转赠
                if (giftcardUnit.getGivenStatus() != 1
                        || giftcardUnit.getFaceValue().compareTo(giftcardUnit.getBalance()) != 0
                        || giftcardUnit.getCardStatus() != CardUnitStatusEnum.BOUND.getStatusCode()
                        || giftcardUnit.getMedium()==CardMediumEnum.SOLID_CARD.getMediumId()) {
                    myGiftcardVO.setGivenStatus(0);
                }
                myGiftcardVOList.add(myGiftcardVO);
            });
            page.setRecords(myGiftcardVOList);
        }
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public Integer countByMemberAndBatch(Long batchId, Long owner) {
        return giftcardUnitDao.countByMemberAndBatch(batchId, owner);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updGiftcardUnitStatus() {
        //1.已用完
        EntityWrapper<GiftcardUnit> outWrapper = new EntityWrapper<>();
        outWrapper.eq("balance", BigDecimal.ZERO);
        outWrapper.eq("cardStatus", CardUnitStatusEnum.BOUND.getStatusCode());
        GiftcardUnit giftcardUnit = new GiftcardUnit();
        giftcardUnit.setCardStatus(CardUnitStatusEnum.CLEAR.getStatusCode());
        giftcardUnit.setModifiedTime(new Date());
        giftcardUnit.setModifier(BizConstants.TIME_TASK_USER);
        Integer outTotal = giftcardUnitDao.update(giftcardUnit, outWrapper);
        log.info("本次用完礼卡数量：{}", outTotal);
        //2.已过期
        EntityWrapper<GiftcardUnit> expireWrapper = new EntityWrapper<>();
        expireWrapper.le("deadTime", new Date());
        expireWrapper.eq("cardStatus", CardUnitStatusEnum.BOUND.getStatusCode());
        giftcardUnit.setCardStatus(CardUnitStatusEnum.EXPIRE.getStatusCode());
        Integer expireTotal = giftcardUnitDao.update(giftcardUnit, expireWrapper);
        log.info("本次过期礼卡数量:{}", expireTotal);
        return outTotal + expireTotal;

    }

    @Override
    public Map<String, Object> getBalanceByMemberIds(List<Long> memberIdList,Integer useType) {
        if (!CollectionUtils.isEmpty(memberIdList)) {
            List<Map<String, Object>> balanceList = giftcardUnitDao.selectBalanceByMemberList(memberIdList,useType);
            if (!CollectionUtils.isEmpty(balanceList)) {
                Map<String, Object> balanceMap = new HashMap<>(16);
                balanceList.forEach(objectMap->{
                    balanceMap.put(objectMap.get("memberId").toString(), objectMap.get("balance"));
                });
                return balanceMap;
            }
        }
        return null;
    }
}
