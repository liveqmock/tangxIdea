package com.topaiebiz.giftcard.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.common.BeanCopyUtil;
import com.topaiebiz.giftcard.dao.GiftcardLogDao;
import com.topaiebiz.giftcard.dao.GiftcardRefundLogDao;
import com.topaiebiz.giftcard.dao.GiftcardUnitDao;
import com.topaiebiz.giftcard.entity.GiftcardLog;
import com.topaiebiz.giftcard.entity.GiftcardRefundLog;
import com.topaiebiz.giftcard.entity.GiftcardUnit;
import com.topaiebiz.giftcard.enums.CardUnitStatusEnum;
import com.topaiebiz.giftcard.enums.GiftcardLogTypeEnum;
import com.topaiebiz.giftcard.service.GiftcardRefundLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @description: 处理老系统退款
 * @author: Jeff Chen
 * @date: created in 上午9:06 2018/5/7
 */
@Service
@Slf4j
public class GiftcardRefundLogServiceImpl extends ServiceImpl<GiftcardRefundLogDao, GiftcardRefundLog> implements GiftcardRefundLogService {
    @Autowired
    private GiftcardLogDao giftcardLogDao;
    @Autowired
    private GiftcardRefundLogDao giftcardRefundLogDao;

    @Autowired
    private GiftcardUnitDao giftcardUnitDao;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String refundByCard(Date date) {
        EntityWrapper<GiftcardRefundLog> wrapper = new EntityWrapper<>();
        wrapper.ge("createdTime", date);
        List<GiftcardRefundLog> refundLogList = giftcardRefundLogDao.selectList(wrapper);
        if (CollectionUtils.isEmpty(refundLogList)) {
            return "no refund data";
        }
        refundLogList.forEach(giftcardRefundLog -> {
            //不退款的用户：7364013，7364007
            if (!giftcardRefundLog.getMemberId().equals(7364013L) && !giftcardRefundLog.getMemberId().equals(7364007L))  {

                //1.先查paySn是否存在
                //2.再查礼卡是否存在
                //3.补偿再写日志
                if (!StringUtils.isEmpty(giftcardRefundLog.getCardNo()) &&
                        !StringUtils.isEmpty(giftcardRefundLog.getPaySn())) {
                    GiftcardLog giftcardLog = new GiftcardLog();
                    giftcardLog.setCardNo(giftcardRefundLog.getCardNo());
                    giftcardLog.setPaySn(giftcardRefundLog.getPaySn());
                    //未记录退款
                    if (null == giftcardLogDao.selectOne(giftcardLog)) {
                        GiftcardUnit unit = new GiftcardUnit();
                        unit.setCardNo(giftcardRefundLog.getCardNo());
                        GiftcardUnit giftcardUnit = giftcardUnitDao.selectOne(unit);
                        if (null != giftcardUnit) {
                            EntityWrapper<GiftcardUnit> updWrapper = new EntityWrapper<>();
                            updWrapper.eq("cardNo", giftcardUnit.getCardNo());
                            updWrapper.eq("balance", giftcardUnit.getBalance());
                            GiftcardUnit updUnit = new GiftcardUnit();
                            updUnit.setBalance(giftcardUnit.getBalance().add(giftcardRefundLog.getAmount()));
                            updUnit.setModifier("老系统退款");
                            updUnit.setModifiedTime(new Date());
                            //非冻结的卡都转为已绑定
                            if (!giftcardUnit.getCardStatus().equals(CardUnitStatusEnum.FREEZED.getStatusCode())) {
                                updUnit.setCardStatus(CardUnitStatusEnum.BOUND.getStatusCode());
                            }
                            if (giftcardUnitDao.update(updUnit, updWrapper) > 0) {
                                GiftcardLog insertLog = new GiftcardLog();
                                BeanCopyUtil.copy(giftcardRefundLog, insertLog);
                                insertLog.setRemark("老系统退款补偿");
                                insertLog.setLogType(GiftcardLogTypeEnum.REFUND.getType());
                                insertLog.setId(null);
                                insertLog.setBalance(updUnit.getBalance());
                                if (giftcardLogDao.insert(insertLog) > 0) {
                                    log.info("手动补偿退款成功：{}", giftcardRefundLog.toString());
                                }
                            }
                        } else {
                            log.info("退款礼卡不存在：{}", giftcardUnit.toString());
                        }
                    }
                } else {
                    log.info("退款异常数据：{}", giftcardRefundLog.toString());
                }

            }
        });

        return "处理数据："+refundLogList.size();
    }
}
