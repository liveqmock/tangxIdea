package com.topaiebiz.guider.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.guider.bo.*;
import com.topaiebiz.guider.constants.GuiderContants;
import com.topaiebiz.guider.dao.*;
import com.topaiebiz.guider.dto.GuiderTaskInfoDto;
import com.topaiebiz.guider.dto.GuiderTaskLevelDto;
import com.topaiebiz.guider.dto.GuiderTaskLevelPrizeDto;
import com.topaiebiz.guider.entity.*;
import com.topaiebiz.guider.exception.GuiderExceptionEnum;
import com.topaiebiz.guider.service.AchievementService;
import com.topaiebiz.guider.service.TaskService;
import com.topaiebiz.guider.utils.AchievementUtil;
import com.topaiebiz.guider.utils.TaskUtil;
import com.topaiebiz.member.constants.LoginType;
import com.topaiebiz.trade.api.order.OrderServiceApi;
import com.topaiebiz.trade.constants.OrderStatusEnum;
import com.topaiebiz.trade.dto.order.GuiderOrderDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by admin on 2018/5/31.
 */
@Slf4j
@Service
public class AchievementServiceImpl implements AchievementService {

    @Autowired
    private GuiderTaskNewUserDao taskNewUserDao;

    @Autowired
    private GuiderTaskOrderDao taskOrderDao;

    @Autowired
    private GuiderTaskPayDao taskPayDao;

    @Autowired
    private GuiderTaskAchievementDao taskAchievementDao;

    @Autowired
    private GuiderTotalAchievementDao totalAchievementDao;

    @Autowired
    private OrderServiceApi orderServiceApi;

    private GuiderTaskNewUserEntity getTaskNewUser(Long memberId) {
        GuiderTaskNewUserEntity param = new GuiderTaskNewUserEntity();
        param.cleanInit();
        param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        param.setMemberId(memberId);
        return taskNewUserDao.selectOne(param);
    }

    private Integer getTaskPayCount(Long orderId) {
        GuiderTaskOrderEntity param = new GuiderTaskOrderEntity();
        param.cleanInit();
        param.setOrderId(orderId);
        param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        return taskOrderDao.selectCount(new EntityWrapper<>(param));
    }

    private List<GuiderTaskOrderEntity> getTaskOrderList(Long orderId) {
        GuiderTaskOrderEntity param = new GuiderTaskOrderEntity();
        param.cleanInit();
        param.setOrderId(orderId);
        param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        return taskOrderDao.selectList(new EntityWrapper<>(param));
    }

    private Integer getTaskPayCount(Long taskId, Long memberId) {
        Wrapper<GuiderTaskOrderEntity> param = Condition.create().setSqlSelect("count(DISTINCT payId)")
                .eq("memberId", memberId)
                .eq("taskId", taskId)
                .eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        log.info("--------{}", param);
        return taskOrderDao.selectCount(param);
//        GuiderTaskOrderEntity param = new GuiderTaskOrderEntity();
//        param.cleanInit();
//        param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
//        param.setMemberId(memberId);
//        param.setTaskId(taskId);
//        return taskOrderDao.selectCount(new EntityWrapper<>(param).setSqlSelect("COUNT(DISTINCT payId)"));
    }

    private Boolean updateTaskOrderStatus(Long taskId, Long orderId, Integer orderStatus, Long lastModifierId) {
        GuiderTaskOrderEntity update = new GuiderTaskOrderEntity();
        update.cleanInit();
        update.setOrderStatus(orderStatus);
        update.setLastModifierId(lastModifierId);
        update.setLastModifiedTime(new Date());
        Wrapper<GuiderTaskOrderEntity> where = new EntityWrapper<>();
        where.eq("orderId", orderId);
        where.eq("taskId", taskId);
        return taskOrderDao.update(update, where) > 0;
    }

    private Boolean updateTaskOrderComplete(OrderCompleteAchievementBo param) {
        GuiderTaskOrderEntity update = new GuiderTaskOrderEntity();
        update.cleanInit();
        update.setOrderStatus(OrderStatusEnum.ORDER_COMPLETION.getCode());
        update.setLastModifierId(param.getMemberId());
        update.setLastModifiedTime(new Date());
        update.setAwardMoney(param.getAwardMoney());
        update.setRefundMoney(param.getRefundMoney());
        update.setPayMoney(param.getPayMoney());
        update.setFreightMoney(param.getFreightMoney());
        Wrapper<GuiderTaskOrderEntity> where = new EntityWrapper<>();
        where.eq("orderId", param.getOrderId());
        where.eq("taskId", param.getTaskId());
        return taskOrderDao.update(update, where) > 0;
    }


    private GuiderTaskAchievementEntity getTaskAchievement(Long taskId, Long srcMemberId) {
        GuiderTaskAchievementEntity where = new GuiderTaskAchievementEntity();
        where.cleanInit();
        where.setTaskId(taskId);
        where.setSrcMemberId(srcMemberId);
        where.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        return taskAchievementDao.selectOne(where);
    }

    private GuiderTotalAchievementEntity getTotalAchievement(Long srcMemberId) {
        GuiderTotalAchievementEntity where = new GuiderTotalAchievementEntity();
        where.cleanInit();
        where.setSrcMemberId(srcMemberId);
        where.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        return totalAchievementDao.selectOne(where);
    }


    private List<GuiderTaskOrderEntity> getTaskOrderListByPayId(Long payId) {
        GuiderTaskOrderEntity param = new GuiderTaskOrderEntity();
        param.cleanInit();
        param.setPayId(payId);
        param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        return taskOrderDao.selectList(new EntityWrapper<>(param));
    }
    private List<GuiderTaskPayEntity> getTaskPayList(Long payId) {
        GuiderTaskPayEntity where = new GuiderTaskPayEntity();
        where.cleanInit();
        where.setPayId(payId);
        return taskPayDao.selectList(new EntityWrapper<>(where));
    }





    

    /**
     * @desc 订单关闭 更新业绩统计信息信息
     */
    private void updateAchievement(PayLoseAchievementBo payLoseAchievementBo) {
        Long srcMemberId = payLoseAchievementBo.getSrcMemberId();
        Long taskId = payLoseAchievementBo.getTaskId();
        Long memberId = payLoseAchievementBo.getMemberId();
        BigDecimal awardRate = payLoseAchievementBo.getAwardRate();
        BigDecimal payMoney = payLoseAchievementBo.getPayMomey();
        BigDecimal freightMoney = payLoseAchievementBo.getFreightMoney();
        BigDecimal reduceMoeny = payMoney.subtract(freightMoney);
        BigDecimal awardMoeny = reduceMoeny.multiply(awardRate);


        GuiderTotalAchievementEntity totalAchievement = getTotalAchievement(srcMemberId);
        GuiderTaskAchievementEntity taskAchievement = getTaskAchievement(taskId, srcMemberId);
        if (null == totalAchievement || null == taskAchievement) {
            return;
        }
        totalAchievement.setLastModifierId(memberId);
        totalAchievement.setLastModifiedTime(new Date());
        totalAchievement.setOrderUncompleteNum(totalAchievement.getOrderUncompleteNum() - 1);
        totalAchievement.setOrderUncompleteTotalMoney(totalAchievement.getOrderUncompleteTotalMoney()
                .subtract(reduceMoeny));
        totalAchievement.setOrderExpectAwardTotalMoney(totalAchievement.getOrderExpectAwardTotalMoney()
                .subtract(awardMoeny));
        //updateTotalAchievement(totalAchievement);
        totalAchievementDao.updateById(totalAchievement);


        taskAchievement.setLastModifierId(memberId);
        taskAchievement.setLastModifiedTime(new Date());
        taskAchievement.setOrderUncompleteNum(taskAchievement.getOrderUncompleteNum() - 1);
        taskAchievement.setOrderUncompleteTotalMoney(taskAchievement.getOrderUncompleteTotalMoney()
                .subtract(reduceMoeny));
        taskAchievement.setOrderExpectAwardTotalMoney(taskAchievement.getOrderExpectAwardTotalMoney()
                .subtract(awardMoeny));
        //updateTaskAchievement(taskAchievement);
        taskAchievementDao.updateById(taskAchievement);
    }


    private void updateAchievement(PaySuccessAchievementBo paySuccessAchievementBo) {
        Long srcMemberId = paySuccessAchievementBo.getSrcMemberId();
        Long taskId = paySuccessAchievementBo.getTaskId();
        Long memberId = paySuccessAchievementBo.getMemberId();
        BigDecimal payMoney = paySuccessAchievementBo.getPayMomey();
        BigDecimal freightMoney = paySuccessAchievementBo.getFreightMoney();
        BigDecimal awardRate = paySuccessAchievementBo.getAwardRate();
        BigDecimal addMoney = payMoney.subtract(freightMoney);
        BigDecimal awardMoney = addMoney.multiply(awardRate);

        GuiderTotalAchievementEntity totalAchievement = getTotalAchievement(srcMemberId);
        GuiderTaskAchievementEntity taskAchievement = getTaskAchievement(taskId, srcMemberId);
        if (null == totalAchievement) {
            totalAchievement = new GuiderTotalAchievementEntity();
            totalAchievement.init();
            totalAchievement.setSrcMemberId(srcMemberId);
            totalAchievement.setCreatorId(memberId);
            totalAchievement.setCreatedTime(new Date());
            totalAchievement.setOrderUncompleteNum(1);
            totalAchievement.setOrderUncompleteTotalMoney(addMoney);
            totalAchievement.setOrderExpectAwardTotalMoney(awardMoney);
            //insertTotalAchievement(totalAchievement);
            totalAchievementDao.insert(totalAchievement);
        } else {
            totalAchievement.setLastModifierId(memberId);
            totalAchievement.setLastModifiedTime(new Date());
            totalAchievement.setOrderUncompleteNum(totalAchievement.getOrderUncompleteNum() + 1);
            totalAchievement.setOrderUncompleteTotalMoney(addMoney.add(taskAchievement.getOrderUncompleteTotalMoney()));
            totalAchievement.setOrderExpectAwardTotalMoney(awardMoney.add(totalAchievement.getOrderExpectAwardTotalMoney()));
            // updateTotalAchievement(totalAchievement);
            totalAchievementDao.updateById(totalAchievement);
        }
        if (null == taskAchievement) {
            taskAchievement = new GuiderTaskAchievementEntity();
            taskAchievement.init();
            taskAchievement.setSrcMemberId(srcMemberId);
            taskAchievement.setCreatorId(memberId);
            taskAchievement.setTaskId(taskId);
            taskAchievement.setCreatedTime(new Date());
            taskAchievement.setOrderExpectAwardTotalMoney(awardMoney);
            taskAchievement.setOrderUncompleteTotalMoney(addMoney);
            taskAchievement.setOrderUncompleteNum(1);
            //insertTaskAchievement(taskAchievement);
            taskAchievementDao.insert(taskAchievement);
        } else {
            taskAchievement.setLastModifierId(memberId);
            taskAchievement.setCreatedTime(new Date());
            taskAchievement.setOrderUncompleteNum(taskAchievement.getOrderUncompleteNum() + 1);
            taskAchievement.setOrderExpectAwardTotalMoney(awardMoney.add(taskAchievement.getOrderExpectAwardTotalMoney()));
            taskAchievement.setOrderUncompleteTotalMoney(addMoney.add(taskAchievement.getOrderUncompleteTotalMoney()));
            // updateTaskAchievement(taskAchievement);

            taskAchievementDao.updateById(taskAchievement);
        }
    }



    private void updateAchievement(RegisterAchievementBo registerAchievementBo) {
        Long srcMemberId = registerAchievementBo.getSrcMemberId();
        Long taskId = registerAchievementBo.getTaskId();
        Long memberId = registerAchievementBo.getMemberId();

        GuiderTotalAchievementEntity totalAchievement = getTotalAchievement(srcMemberId);
        GuiderTaskAchievementEntity taskAchievement = getTaskAchievement(taskId, srcMemberId);
        if (null == totalAchievement || null == taskAchievement) {
            return;
        }
        GuiderTotalAchievementEntity updateTotalAchieve = new GuiderTotalAchievementEntity();
        updateTotalAchieve.cleanInit();
        updateTotalAchieve.setId(totalAchievement.getId());
        updateTotalAchieve.setLastModifierId(memberId);
        updateTotalAchieve.setCreatedTime(new Date());
        updateTotalAchieve.setOrderUncompleteNum(totalAchievement.getDevelopingUserNum() + 1);
        // updateTotalAchievement(totalAchievement);
        totalAchievementDao.updateById(updateTotalAchieve);
        //=================================
        GuiderTaskAchievementEntity updateTaskAchieve = new GuiderTaskAchievementEntity();
        updateTaskAchieve.setId(taskAchievement.getId());
        updateTaskAchieve.setLastModifierId(memberId);
        updateTaskAchieve.setCreatedTime(new Date());
        updateTaskAchieve.setOrderUncompleteNum(taskAchievement.getDevelopingUserNum() + 1);
        //updateTaskAchievement(taskAchievement);
        taskAchievementDao.updateById(updateTaskAchieve);
    }

    /**
     * @desc 更新业绩统计信息信息
     */
    private void updateAchievement(PayCompleteAchievementBo orderCompleteAchievementBo) {
        Long srcMemberId = orderCompleteAchievementBo.getSrcMemberId();
        Long taskId = orderCompleteAchievementBo.getTaskId();
        Long memberId = orderCompleteAchievementBo.getMemberId();
        BigDecimal awardMoney = orderCompleteAchievementBo.getAwardMoney();
        BigDecimal awardBaseMoney = orderCompleteAchievementBo.getAwardBaseMoney();

        GuiderTotalAchievementEntity totalAchievement = getTotalAchievement(srcMemberId);
        GuiderTaskAchievementEntity taskAchievement = getTaskAchievement(taskId, srcMemberId);
        if (null == totalAchievement || null == taskAchievement) {
            return;
        }
        totalAchievement.setLastModifierId(memberId);
        totalAchievement.setCreatedTime(new Date());
        //预期奖励金额 转化为  奖励金额;
        //未完成订单  转化为 已完成订单
        totalAchievement.setOrderExpectAwardTotalMoney(totalAchievement.getOrderExpectAwardTotalMoney()
                .subtract(awardMoney));
        totalAchievement.setOrderUncompleteTotalMoney(totalAchievement.getOrderUncompleteTotalMoney()
                .subtract(awardBaseMoney));
        totalAchievement.setOrderAwardTotalMoney(totalAchievement.getOrderAwardTotalMoney()
                .add(awardMoney));
        totalAchievement.setOrderUncompleteNum(totalAchievement.getOrderUncompleteNum() - 1);
        totalAchievement.setOrderCompleteNum(totalAchievement.getOrderUncompleteNum() + 1);

        totalAchievementDao.updateById(totalAchievement);
        // updateTotalAchievement(totalAchievement);
        //=================================

        taskAchievement.setLastModifierId(memberId);
        taskAchievement.setCreatedTime(new Date());
        taskAchievement.setOrderAwardTotalMoney(taskAchievement.getOrderAwardTotalMoney()
                .add(awardMoney));
        taskAchievement.setOrderExpectAwardTotalMoney(totalAchievement.getOrderExpectAwardTotalMoney()
                .subtract(awardMoney));
        taskAchievement.setOrderUncompleteTotalMoney(totalAchievement.getOrderUncompleteTotalMoney()
                .subtract(awardBaseMoney));
        taskAchievement.setOrderUncompleteNum(taskAchievement.getOrderUncompleteNum() - 1);
        taskAchievement.setOrderCompleteNum(taskAchievement.getOrderCompleteNum() + 1);
        //updateTaskAchievement(taskAchievement);
        taskAchievementDao.updateById(taskAchievement);
    }


    private void updateAchievement(NewUserAchievementBo newUserAchievementBo) {
        Long srcMemberId = newUserAchievementBo.getSrcMemberId();
        Long taskId = newUserAchievementBo.getTaskId();
        Long memberId = newUserAchievementBo.getMemberId();
        BigDecimal awardMoney = newUserAchievementBo.getAwardMoney();

        GuiderTotalAchievementEntity totalAchievement = getTotalAchievement(srcMemberId);
        GuiderTaskAchievementEntity taskAchievement = getTaskAchievement(taskId, srcMemberId);
        if (null == totalAchievement || null == taskAchievement) {
            return;
        }
        GuiderTotalAchievementEntity updateTotalAchieve = new GuiderTotalAchievementEntity();
        updateTotalAchieve.cleanInit();
        updateTotalAchieve.setId(totalAchievement.getId());
        updateTotalAchieve.setLastModifierId(memberId);
        updateTotalAchieve.setCreatedTime(new Date());
        updateTotalAchieve.setDevelopingUserNum(totalAchievement.getDevelopingUserNum() - 1);
        updateTotalAchieve.setDevelopedUserNum(totalAchievement.getDevelopedUserNum() + 1);
        updateTotalAchieve.setDevelopedAwardMoney(awardMoney.add(totalAchievement.getDevelopedAwardMoney()));
        // updateTotalAchievement(totalAchievement);
        totalAchievementDao.updateById(updateTotalAchieve);

        //=================================
        GuiderTaskAchievementEntity updateTaskAchieve = new GuiderTaskAchievementEntity();
        updateTaskAchieve.setId(taskAchievement.getId());
        updateTaskAchieve.setLastModifierId(memberId);
        updateTaskAchieve.setCreatedTime(new Date());
        updateTaskAchieve.setDevelopingUserNum(taskAchievement.getDevelopingUserNum() - 1);
        updateTaskAchieve.setDevelopedUserNum(taskAchievement.getDevelopedUserNum() + 1);
        updateTaskAchieve.setDevelopedAwardMoney(awardMoney.add(taskAchievement.getDevelopedAwardMoney()));
        //updateTaskAchievement(taskAchievement);
        taskAchievementDao.updateById(updateTaskAchieve);
    }


    @Autowired
    private TaskService taskService;

    @Override
    public void processMemberLogin(Long memberId, LoginType loginType) {
        GuiderTaskNewUserEntity taskNewUser = getTaskNewUser(memberId);
        Long taskId = taskNewUser.getTaskId();
        Long srcMemberId = taskNewUser.getSrcMemberId();
        GuiderTaskInfoDto taskInfo = taskService.selectGuiderTaskInfoDetailById(taskId);
        if (null == taskNewUser) {
            return;
        }
        GuiderTaskNewUserEntity updateTaskNewUser = new GuiderTaskNewUserEntity();
        updateTaskNewUser.setId(taskNewUser.getId());
        updateTaskNewUser.setLastModifiedTime(new Date());
        updateTaskNewUser.setLastModifierId(memberId);
        if (LoginType.ANDROID.equals(loginType)) {
            updateTaskNewUser.setIsAndroidLogin(1);
        } else if (LoginType.IOS.equals(loginType)) {
            updateTaskNewUser.setIsIosLogin(1);
        } else if (LoginType.WECHAT.equals(loginType)) {
            updateTaskNewUser.setIsSubscribeWeixin(1);
        } else {
            return;
        }
        taskNewUserDao.updateById(updateTaskNewUser);
        BigDecimal awardMoney = new BigDecimal("1.00");
        List<GuiderTaskLevelDto> taskLevelDtoList = taskService.getTaskLevelList(taskId, 1);

        if (!TaskUtil.checkTaskLevelContinuou(taskLevelDtoList)) {
            throw new GlobalException(GuiderExceptionEnum.TASK_LEVEL_NOT_CONTINUOU);
        }

        NewUserAchievementBo newUserAchievementBo = new NewUserAchievementBo(srcMemberId, taskId, memberId, awardMoney);
        updateAchievement(newUserAchievementBo);
    }

    @Override
    public void processMemberRegister(Long memberId, String srcCode) {
        //1.验证分享码（是否存在、是否在有效期）,获得对应的scrMemberId
        GuiderTaskShareEntity taskShare = new GuiderTaskShareEntity();
        GuiderTaskInfoEntity taskInfo = new GuiderTaskInfoEntity();
        if (null == taskShare || null == taskInfo || !TaskUtil.checkTaskTime(taskInfo)) {
            return;
        }

        //2.判断是否可以参加拉新的活动（一个新注册用户最多能参与一次）
        GuiderTaskNewUserEntity taskNewUser = getTaskNewUser(memberId);
        if (null != taskNewUser) {
            log.error("导购模块--processMemberRegister memberId:{}，srcCode:{}", memberId, srcCode);
            return;
        }
        //3.记录拉新记录,并更新统计数据
        Long srcMemberId = taskShare.getMemberId();
        Long taskId = taskShare.getTaskId();

        GuiderTaskNewUserEntity insertNewUser = new GuiderTaskNewUserEntity();
        insertNewUser.setTaskId(taskShare.getTaskId());
        insertNewUser.setMemberId(memberId);
        insertNewUser.setSrcMemberId(srcMemberId);
        insertNewUser.setShareId(taskShare.getId());
        insertNewUser.setShareCode(taskShare.getShareCode());
        insertNewUser.setCreatedTime(new Date());
        insertNewUser.setCreatorId(memberId);
        taskNewUserDao.insert(insertNewUser);
        RegisterAchievementBo registerAchievementBo = new RegisterAchievementBo(srcMemberId, taskId, memberId);
        updateAchievement(registerAchievementBo);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processPaySuccess(Long memberId, Long payId, List<Long> orderIds, Boolean isPromotionOrder) {
        //一.优先判断是否包含退关订单
        if (isPromotionOrder) {

        }
        //1.判断用户能否计入导购活动
        GuiderTaskNewUserEntity taskNewUserEntity = getTaskNewUser(memberId);
        if (!AchievementUtil.checkQualification(taskNewUserEntity)) {
            return;
        }

        //3.判断是订单 所属的奖励阶梯区间中
        Long taskId = taskNewUserEntity.getTaskId();
        Long srcMemberId = taskNewUserEntity.getSrcMemberId();
        Integer taskOrderCount = getTaskPayCount(memberId, taskId);
        Integer sortingId = taskOrderCount + 1;
        List<GuiderTaskLevelDto> taskLevelDtoList = taskService.getTaskLevelList(taskId, GuiderContants.LevelType.ORDER);
        GuiderTaskLevelDto taskLevelDto = TaskUtil.obtainTaskLevel(taskLevelDtoList, sortingId);
        if (null == taskLevelDto) {
            return;
        }
        Long prizeLevelId = taskLevelDto.getLevelId();
        List<GuiderTaskLevelPrizeDto> taskLevelPrizeDtos = taskService.getTaskLevelPrizeList(prizeLevelId);
        BigDecimal awardRate = TaskUtil.obtainAwardRate(taskLevelPrizeDtos);
        if (null == awardRate) {
            return;
        }

        Map<Long, GuiderOrderDetailDTO> orderDetailDTOMap = orderServiceApi.guiderOrderDetailGroupById(orderIds);
        Iterator<Map.Entry<Long, GuiderOrderDetailDTO>> entries = orderDetailDTOMap.entrySet().iterator();
        BigDecimal orderExpectAwardTotalMoney = BigDecimal.ZERO;
        BigDecimal orderUncompleteTotalMoney = BigDecimal.ZERO;
        BigDecimal payTotalMoney = BigDecimal.ZERO;
        BigDecimal refundTotalMoney = BigDecimal.ZERO;
        BigDecimal freightTotalMoney = BigDecimal.ZERO;

        //计算预期奖励及 插入 订单信息
        while (entries.hasNext()) {
            Map.Entry<Long, GuiderOrderDetailDTO> entry = entries.next();
            GuiderOrderDetailDTO orderDetailDTO = entry.getValue();
            //插入  订单记录
            taskOrderDao.insert(AchievementUtil.packageTaskOrder(orderDetailDTO, awardRate, taskNewUserEntity));

            payTotalMoney.add(orderDetailDTO.getPayPrice());
            //refundTotalMoney.add(orderDetailDTO.getRefundPrice());
            freightTotalMoney.add(orderDetailDTO.getActualFreight());

            BigDecimal awardBaseMoney = AchievementUtil.calcAwardBaseMoney(orderDetailDTO);
            orderUncompleteTotalMoney = orderUncompleteTotalMoney.add(awardBaseMoney);
            orderExpectAwardTotalMoney = orderExpectAwardTotalMoney.
                    add(AchievementUtil.calcAwardMoney(awardRate, awardBaseMoney));
        }

        //插入支付单信息
        GuiderTaskPayEntity taskPayEntity = new GuiderTaskPayEntity();
        taskPayEntity.setMemberId(memberId);
        taskPayEntity.setSrcMemberId(srcMemberId);
        taskPayEntity.setPayId(payId);
        taskPayEntity.setPayStatus(GuiderContants.GuiderPayStatus.WAITING);
        taskPayEntity.setPayMoney(payTotalMoney);
        taskPayEntity.setRefundMoney(refundTotalMoney);
        taskPayEntity.setFreightMoney(freightTotalMoney);
        taskPayEntity.setAwardRate(awardRate);
        taskPayEntity.setAwardMoney(orderExpectAwardTotalMoney);
        taskPayEntity.setSortingId(sortingId);
        taskPayDao.insert(taskPayEntity);

        //更新预期收入
        PaySuccessAchievementBo paySuccessAchievementBo = new PaySuccessAchievementBo();
        paySuccessAchievementBo.setSrcMemberId(srcMemberId);
        paySuccessAchievementBo.setTaskId(taskId);
        paySuccessAchievementBo.setMemberId(memberId);
        paySuccessAchievementBo.setPayMomey(payTotalMoney);
        paySuccessAchievementBo.setFreightMoney(freightTotalMoney);
        paySuccessAchievementBo.setSortingId(sortingId);
        updateAchievement(paySuccessAchievementBo);
    }



    /**
     * @param memberId
     * @param orderId
     * @desc: 订单关闭事件：  1.当前订单所属支付下其他订单的状态查询。     对于订单的关闭 和对 支付单的关闭
     */
    @Override
    public void processOrderClose(Long memberId, Long orderId) {
        Long lastModifierId = memberId;

        //订单关闭 （发货之前进行退款）
        //1.关闭任务中的订单状态  (未完成订单减1  未完成订单金额减少  预期奖金总额减少)
        List<GuiderTaskOrderEntity> taskOrderEntityList = getTaskOrderList(orderId);
        if (taskOrderEntityList.size() <= 0) {
            return;
        }

        Long payId = taskOrderEntityList.get(0).getPayId();
        List<GuiderTaskPayEntity> allTaskPayList = getTaskPayList(payId);
        List<GuiderTaskOrderEntity> allTaskOrderList = getTaskOrderListByPayId(payId);
        HashMap<String, PayOrderBo> aggregateMap = AchievementUtil.aggregateTaskPayMap(orderId, allTaskOrderList, allTaskPayList);
        Iterator iterator = aggregateMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            PayOrderBo payOrderBo = (PayOrderBo) entry.getValue();
            Long taskId = payOrderBo.getTaskId();
            updateTaskOrderStatus(taskId, orderId, OrderStatusEnum.ORDER_CLOSE.getCode(), lastModifierId);

            GuiderTaskOrderEntity taskOrderEntity = payOrderBo.getCurrentTaskOrder();
            GuiderTaskPayEntity taskPayEntity = payOrderBo.getCurrentTaskPay();

            Integer soldierOrderStatus = payOrderBo.getSoldierOrderStatus();
            if (OrderStatusEnum.ORDER_CLOSE.getCode().equals(soldierOrderStatus)) {
                //1.支付单状态--关闭
                PayLoseAchievementBo payLoseAchievementBo = new PayLoseAchievementBo();
                payLoseAchievementBo.setSrcMemberId(taskOrderEntity.getSrcMemberId());
                payLoseAchievementBo.setTaskId(taskId);
                payLoseAchievementBo.setPayMomey(taskPayEntity.getPayMoney());
                payLoseAchievementBo.setFreightMoney(taskPayEntity.getFreightMoney());
                payLoseAchievementBo.setAwardRate(taskOrderEntity.getAwardRate());
                updateAchievement(payLoseAchievementBo);

            } else if (OrderStatusEnum.ORDER_COMPLETION.getCode().equals(soldierOrderStatus)) {
                //2.支付单状态--完成

            } else {
                //3.支付单状态--等待

            }


        }


        //2.后续有可能 进行消息通知
    }


    @Override
    public void processOrderComplete(Long memberId, Long orderId) {
        //订单完成 （发货之后的状态，只能是完成）存在 两种情况 1.部分退货 后  2.未发生任何售后 完成
        List<GuiderTaskOrderEntity> taskOrderEntityList = getTaskOrderList(orderId);
        if (taskOrderEntityList.size() < 1) {
            return;
        }

        List orderIds = new ArrayList<>();
        orderIds.add(orderId);
        Map<Long, GuiderOrderDetailDTO> orderDetailDTOMap = orderServiceApi.guiderOrderDetailGroupById(orderIds);
        if (null == orderDetailDTOMap) {
            return;
        }
        GuiderOrderDetailDTO orderDetailDTO = orderDetailDTOMap.get(orderId);
        if (null == orderDetailDTO) {
            return;
        }


        Long payId = taskOrderEntityList.get(0).getPayId();
        List<GuiderTaskPayEntity> allTaskPayList = getTaskPayList(payId);
        List<GuiderTaskOrderEntity> allTaskOrderList = getTaskOrderListByPayId(payId);
        HashMap<String, PayOrderBo> aggregateMap = AchievementUtil.aggregateTaskPayMap(orderId, allTaskOrderList, allTaskPayList);
        Iterator iterator = aggregateMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            PayOrderBo payOrderBo = (PayOrderBo) entry.getValue();
            Long taskId = payOrderBo.getTaskId();
            GuiderTaskOrderEntity currentTaskOrder = payOrderBo.getCurrentTaskOrder();
            //更新订单信息
            OrderCompleteAchievementBo orderCompleteAchievementBo = AchievementUtil.packageOrderCompleteAchievement(currentTaskOrder, orderDetailDTO);
            updateTaskOrderComplete(orderCompleteAchievementBo);

            // 更新支付单信息（同时也更新 业绩统计信息）
            Integer soldierOrderStatus = payOrderBo.getSoldierOrderStatus();
            if (OrderStatusEnum.ORDER_CLOSE.getCode().equals(soldierOrderStatus)
                    || OrderStatusEnum.ORDER_COMPLETION.getCode().equals(soldierOrderStatus)) {
                //2.支付单状态--完成(订单完成的通知后，支付订单的状态一定不存在“关闭”的状态)
                PayCompleteAchievementBo payCompleteAchievementBo = AchievementUtil.calcPayCompleteAchievement(OrderStatusEnum.ORDER_COMPLETION,
                        currentTaskOrder, payOrderBo.getOtherTaskOrders());

                updateAchievement(payCompleteAchievementBo);
            }

        }

    }



}
