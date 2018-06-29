package com.topaiebiz.promotion.mgmt.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.basic.api.ConfigApi;
import com.topaiebiz.card.api.GiftCardApi;
import com.topaiebiz.card.dto.PrizeCardDTO;
import com.topaiebiz.member.api.MemberApi;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.merchant.api.StoreApi;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.promotion.common.util.DozerUtils;
import com.topaiebiz.promotion.constants.PromotionConstants;
import com.topaiebiz.promotion.mgmt.dao.*;
import com.topaiebiz.promotion.mgmt.dto.PromotionDto;
import com.topaiebiz.promotion.mgmt.dto.box.AwardRecordDTO;
import com.topaiebiz.promotion.mgmt.dto.box.BoxActivityDTO;
import com.topaiebiz.promotion.mgmt.dto.box.BoxReceiverDTO;
import com.topaiebiz.promotion.mgmt.dto.box.MemberBoxDTO;
import com.topaiebiz.promotion.mgmt.dto.box.content.CardBoxDTO;
import com.topaiebiz.promotion.mgmt.dto.box.content.CouponBoxDTO;
import com.topaiebiz.promotion.mgmt.dto.box.content.ResBoxDTO;
import com.topaiebiz.promotion.mgmt.dto.box.json.AwardPoolJsonDTO;
import com.topaiebiz.promotion.mgmt.dto.box.json.FixedNodeJsonDTO;
import com.topaiebiz.promotion.mgmt.dto.box.json.RateJsonDTO;
import com.topaiebiz.promotion.mgmt.dto.box.json.ResBoxJsonDTO;
import com.topaiebiz.promotion.mgmt.entity.PromotionEntity;
import com.topaiebiz.promotion.mgmt.entity.box.*;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEnum;
import com.topaiebiz.promotion.mgmt.service.BoxActivityService;
import com.topaiebiz.promotion.mgmt.service.PromotionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.topaiebiz.promotion.constants.PromotionConstants.AvailState.UNAVAILABLE;
import static com.topaiebiz.promotion.constants.PromotionConstants.AwardType.*;
import static com.topaiebiz.promotion.constants.PromotionConstants.CacheKey.*;
import static com.topaiebiz.promotion.constants.PromotionConstants.NodeType.TIME_NODE;
import static com.topaiebiz.promotion.constants.PromotionConstants.ReceiveState.RECEIVE_NO;
import static com.topaiebiz.promotion.constants.PromotionConstants.ReceiveState.RECEIVE_YES;
import static com.topaiebiz.promotion.constants.PromotionConstants.SeparatorChar.SEPARATOR_COLON;
import static com.topaiebiz.promotion.constants.PromotionConstants.SeparatorChar.SEPARATOR_COMMA;
import static com.topaiebiz.promotion.promotionEnum.PromotionStateEnum.PROMOTION_STATE_ONGOING;
import static com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum.PROMOTION_TYPE_OPEN_BOX;

/**
 * 活动开宝箱
 */
@Slf4j
@Service
public class BoxActivityServiceImpl implements BoxActivityService {
    //未中奖记录 0-记录 1-不记录
    private static final String noAwardRecord = "no_award_record";
    //奖品信息
    private static final String awardRecords = "award_records";
    //记录起始页码
    private Integer awardsCurrent = 0;
    //记录每页条数
    private Integer awardsSize = 20;
    //中奖会员名称展示
    public Integer zeroLength = 0;
    public Integer oneLength = 1;
    public Integer twoLength = 2;
    public String motherBuyMember = "妈妈购会员";
    public String SYMBOL = "***";

    @Autowired
    private AwardRecordDao awardRecordDao;
    @Autowired
    private BoxActivityDao boxActivityDao;
    @Autowired
    private BoxActivityItemDao boxActivityItemDao;
    @Autowired
    private BoxReceiverDao boxReceiverDao;
    @Autowired
    private ConfigApi configApi;
    @Autowired
    private FloorGoodsDao floorGoodsDao;
    @Autowired
    private GiftCardApi giftCardApi;
    @Autowired
    private MemberBoxDao memberBoxDao;
    @Autowired
    private PromotionDao promotionDao;
    @Autowired
    private PromotionGoodsDao promotionGoodsDao;
    @Autowired
    private PromotionService promotionService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private StoreApi storeApi;
    @Autowired
    private MemberApi memberApi;

    /**
     * 产生宝箱,并更新宝箱数量
     *
     * @param memberId 会员ID
     * @return
     */
    @Override
    @Transactional
    public Boolean produceBox(Long memberId, Integer nodeType) {
        BoxActivityDTO boxActivity = getBoxActivity();
        if (boxActivity == null) {
            //开宝箱活动不存在或无效
            throw new GlobalException(PromotionExceptionEnum.OPEN_BOX_ACTIVITY_INVALID);
        }

        //判断该节点是否可以产生宝箱
        Boolean avail = checkAvailNode(boxActivity, memberId, nodeType);
        if (!avail) {
            log.warn("触发产生宝箱的节点无效，节点类型-nodeType:{}, 会员ID-memberId:{}, 活动宝箱配置ID-activityId:{}", nodeType, memberId, boxActivity.getId());
            return false;
        }

        //查询用户是否存在宝箱记录
        MemberBoxEntity memberBoxEntity = queryMemberBox(boxActivity.getPromotionId(), memberId);

        //插入或更新结果
        Integer res = 0;
        //该用户没有宝箱则创建
        if (null == memberBoxEntity) {
            memberBoxEntity = new MemberBoxEntity();
            memberBoxEntity.setMemberId(memberId);
            memberBoxEntity.setAwardCount(1);
            //查询产生宝箱节点所属开宝箱活动的配置宝箱
            memberBoxEntity.setPromotionId(boxActivity.getPromotionId());
            res = memberBoxDao.insert(memberBoxEntity);
        } else {
            //若该用户有宝箱，宝箱的数量+1
            Integer AwardCount = memberBoxEntity.getAwardCount() + 1;
            MemberBoxEntity updateEntity = new MemberBoxEntity();
            updateEntity.cleanInit();
            updateEntity.setId(memberBoxEntity.getId());
            updateEntity.setAwardCount(AwardCount);
            updateEntity.setLastModifiedTime(new Date());
            res = memberBoxDao.updateById(updateEntity);
        }
        return res > 0;
    }

    /**
     * 会员-拥有宝箱数量
     *
     * @param memberId 会员ID
     * @return
     */
    @Override
    public MemberBoxDTO getAwardCount(Long memberId) {
        BoxActivityDTO boxActivity = getBoxActivity();
        if (boxActivity == null) {
            //开宝箱活动不存在或无效
            log.warn("查询拥有宝箱数量，开宝箱活动不存在或无效");
            throw new GlobalException(PromotionExceptionEnum.OPEN_BOX_ACTIVITY_INVALID);
        }

        //查询用户是否存在宝箱记录
        MemberBoxEntity memberBoxEntity = queryMemberBox(boxActivity.getPromotionId(), memberId);

        MemberBoxDTO memberBoxDTO = new MemberBoxDTO();
        memberBoxDTO.setPromotionId(boxActivity.getPromotionId());
        if (memberBoxEntity == null) {
            //会员宝箱不存在，数量默认0
            memberBoxDTO.setAwardCount(0);
        } else {
            memberBoxDTO.setAwardCount(memberBoxEntity.getAwardCount());
        }
        return memberBoxDTO;
    }

    /**
     * 会员-宝箱获奖列表
     *
     * @param memberId    会员ID
     * @param promotionId 活动开宝箱配置ID
     * @return
     */
    @Override
    public List<AwardRecordDTO> getAwardRecordsList(Long memberId, Long promotionId) {
        log.info("获取中奖记录查询条件，会员ID-memberId:{}, 开宝箱活动ID-activityId:{}", memberId, promotionId);
        //查询会员的宝箱奖品列表
        EntityWrapper<AwardRecordEntity> condWrapper = new EntityWrapper<>();
        condWrapper.eq("memberId", memberId);
        if (promotionId != null) {
            condWrapper.eq("promotionId", promotionId);
        }
        condWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        //倒序
        condWrapper.orderBy(true, "createdTime", false);
        List<AwardRecordEntity> awardRecordEntities = awardRecordDao.selectList(condWrapper);
        // 返参DTO集合
        List<AwardRecordDTO> awardRecordDTOs = DozerUtils.maps(awardRecordEntities, AwardRecordDTO.class);
        return awardRecordDTOs;
    }

    /**
     * 实物宝箱-保存收货人信息
     *
     * @param boxReceiverDTO 宝箱领奖人信息
     * @return
     */
    @Override
    @Transactional
    public Boolean insertBoxReceiver(BoxReceiverDTO boxReceiverDTO) {
        //源对象转换为目标对象
        BoxReceiverEntity boxReceiverEntity = DozerUtils.map(boxReceiverDTO, BoxReceiverEntity.class);
        Integer count = boxReceiverDao.insert(boxReceiverEntity);
        if (count > 0) {
            log.info("保存收货人信息成功，手机号-mobile:{}", boxReceiverDTO.getMobile());
            AwardRecordEntity awardRecordEntity = new AwardRecordEntity();
            awardRecordEntity.cleanInit();
            awardRecordEntity.setId(boxReceiverDTO.getBoxRecordId());
            //已领取状态
            awardRecordEntity.setState(RECEIVE_YES);
            count = awardRecordDao.updateById(awardRecordEntity);
        }
        return count > 0;
    }

    @Override
    @Transactional
    public AwardRecordDTO openBox(Long memberId) {
        //查询当前开宝箱的活动
        PromotionEntity promotionEntity = queryPromotion();
        if (promotionEntity == null) {
            log.warn("开宝箱活动不存在或无效");
            //开宝箱活动不存在或无效
            throw new GlobalException(PromotionExceptionEnum.OPEN_BOX_ACTIVITY_INVALID);
        }
        //查询开宝箱活动当前时间所提供的宝箱模板
        BoxActivityEntity boxActivity = queryActivity(promotionEntity.getId());
        if (boxActivity == null) {
            log.warn("开宝箱活动不存在或无效");
            //开宝箱活动不存在或无效
            throw new GlobalException(PromotionExceptionEnum.OPEN_BOX_ACTIVITY_INVALID);
        }

        //校验用户符合开宝箱条件
        MemberBoxEntity memberBoxEntity = queryMemberBox(boxActivity.getPromotionId(), memberId);

        //判断该用户是否存在宝箱记录
        if (memberBoxEntity == null) {
            //没有可开启的宝箱
            log.warn("没有可开启的宝箱，会员ID-memberId:{}, 活动宝箱ID-promotionId:{}", memberId, boxActivity.getPromotionId());
            throw new GlobalException(PromotionExceptionEnum.NO_AVAILABLE_BOX);
        }
        //判断该用户的宝箱是否大于0
        if (memberBoxEntity.getAwardCount().intValue() <= 0) {
            //没有可开启的宝箱
            log.warn("没有可开启的宝箱，会员ID-memberId:{}, 活动宝箱ID-promotionId:{}", memberId, boxActivity.getPromotionId());
            throw new GlobalException(PromotionExceptionEnum.NO_AVAILABLE_BOX);
        }

        //开宝箱结果
        AwardRecordDTO awardRecord = openBox(boxActivity, memberId, null);

//        //是否记录空宝箱
//        Integer flag = UNAVAILABLE;
//        String configValue = configApi.getConfig(noAwardRecord);
//        if (configValue != null) {
//            flag = Integer.parseInt(configValue);
//        }
//        //未中奖添加一条记录
//        if (awardRecord == null && AVAILABLE.equals(flag)){
//            log.info("记录未中奖，会员ID-memberId:{}, 活动宝箱配置ID-activityId:{}", memberId, boxActivity.getId());
//            AwardRecordEntity awardRecordEntity = new AwardRecordEntity();
//            awardRecordEntity.setAwardType(NO_AWARD);
//            awardRecordEntity.setMemberId(memberId);
//            //活动名称
//            awardRecordEntity.setPromotionId(promotionEntity.getId());
//            awardRecordEntity.setPromotionName(promotionEntity.getName());
//            awardRecordEntity.setPromotionBoxId(boxActivity.getId());
//            //设置状态为已领取
//            awardRecordEntity.setState(RECEIVE_YES);
//            awardRecordEntity.setAwardName("谢谢参与！");
//            //新增奖品记录
//            awardRecordDao.insert(awardRecordEntity);
//
//            awardRecord = new AwardRecordDTO();
//            BeanCopyUtil.copy(awardRecordEntity, awardRecord);
//        }
        //抽奖失败
        if (awardRecord != null) {
            //若该用户拥有宝箱的数量-1,开启的宝箱+1
            MemberBoxEntity updateEntity = new MemberBoxEntity();
            updateEntity.cleanInit();
            updateEntity.setId(memberBoxEntity.getId());
            updateEntity.setAwardCount(memberBoxEntity.getAwardCount() - 1);
            updateEntity.setOpenCount(memberBoxEntity.getOpenCount() + 1);
            memberBoxDao.updateById(updateEntity);
        }

        return awardRecord;
    }

    @Override
    @Transactional
    public ResBoxJsonDTO getResBox(Long id) {
        //获取实物宝箱详情
        AwardRecordEntity cond = new AwardRecordEntity();
        cond.cleanInit();
        cond.setId(id);
        //未领取
        cond.setState(RECEIVE_NO);
        cond.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
        AwardRecordEntity awardRecord = awardRecordDao.selectOne(cond);
        if (awardRecord == null) {
            //宝箱奖品领取无效
            log.warn("宝箱奖品领取无效，中奖记录ID：{}", id);
            throw new GlobalException(PromotionExceptionEnum.BOX_AWARD_RECEIVED_INVALID);
        }
        if (awardRecord.getContent() == null) {
            //宝箱奖品领取无效
            log.warn("宝箱奖品领取无效，中奖记录ID：{}", id);
            throw new GlobalException(PromotionExceptionEnum.BOX_AWARD_RECEIVED_INVALID);
        }
        ResBoxJsonDTO resBox = JSON.parseObject(awardRecord.getContent(), ResBoxJsonDTO.class);
        resBox.setAwardName(awardRecord.getAwardName());

        return resBox;
    }

    @Override
    public List<String> getAwardsInfo() {
        //最后展示的中奖记录
        List<AwardRecordDTO> showList;
        String awards = configApi.getConfig(awardRecords);
        //初始获奖记录
        List<AwardRecordDTO> initDataList = null;
        if (StringUtils.isNotBlank(awards)) {
            initDataList = JSON.parseArray(awards, AwardRecordDTO.class);
        }

        //当前有效活动
        PromotionEntity promotion = queryPromotion();
        if (promotion == null) {
            //开宝箱活动不存在或无效
            log.warn("获奖信息弹幕，开宝箱活动不存在或无效");
            throw new GlobalException(PromotionExceptionEnum.OPEN_BOX_ACTIVITY_INVALID);
        }

        //获取展示获奖记录
        List<AwardRecordEntity> awardRecords = getShowRecord(promotion.getId());

        //会员ID集合
        List<Long> memberIds = awardRecords.stream().map(record -> record.getMemberId()).collect(Collectors.toList());
        List<MemberDto> memberList = memberApi.getMemberList(memberIds);
        if (awardRecords == null
                || memberList == null) {
            //展示初始值
            showList = initDataList;
        } else {
            //实际中奖记录
            List<AwardRecordDTO> recordList = new ArrayList<>();
            //整合会员MAP
            Map<Long, MemberDto> memberMap = memberList.stream().collect(Collectors.toMap(MemberDto::getId, member -> member));
            for (AwardRecordEntity entity : awardRecords) {
                AwardRecordDTO record = new AwardRecordDTO();
                record.setAwardName(entity.getAwardName());
                MemberDto member = memberMap.get(entity.getMemberId());
                record.setMemberName(getMemberInfo(member));
                recordList.add(record);
            }

            if (recordList.size() < awardsSize && CollectionUtils.isNotEmpty(initDataList)) {
                recordList.addAll(initDataList.subList(zeroLength, awardsSize - recordList.size()));
            }

            showList = recordList;
        }

        List<String> records = new ArrayList<>();
        for (AwardRecordDTO show : showList) {
            //拼接中奖记录
            records.add(StringUtils.join(show.getMemberName(), "\t获得\t", show.getAwardName()));
        }
        return records;
    }

    /**
     * 获取展示获奖记录
     *
     * @param promotionId 开宝箱活动ID
     * @return
     */
    private List<AwardRecordEntity> getShowRecord(Long promotionId) {
        Page<AwardRecordEntity> page = new Page<>();
        page.setCurrent(awardsCurrent);
        page.setSize(awardsSize);
        //查询会员的宝箱奖品列表
        EntityWrapper<AwardRecordEntity> condWrapper = new EntityWrapper<>();
        condWrapper.eq("promotionId", promotionId);
        condWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        //倒序
        condWrapper.orderBy(true, "createdTime", false);
        return awardRecordDao.selectPage(page, condWrapper);
    }

    /**
     * 获取会员名称
     *
     * @param member 会员
     * @return
     */
    private String getMemberInfo(MemberDto member) {
        if (member == null) {
            return motherBuyMember;
        }
        String nickName = member.getNickName();
        if (StringUtils.isNotBlank(nickName)) {
            if (nickName.length() <= twoLength) {
                String substring = nickName.substring(zeroLength, nickName.length());
                return StringUtils.join(substring, SYMBOL);
            }
            String first = nickName.substring(zeroLength, twoLength);
            String last = nickName.substring(nickName.length() - oneLength);
            return StringUtils.join(first, SYMBOL, last);
        }

        String userName = member.getUserName();
        if (StringUtils.isNotBlank(userName)) {
            if (userName.length() == oneLength) {
                String substring = userName.substring(zeroLength, oneLength);
                return StringUtils.join(substring, SYMBOL);
            }
            String first = userName.substring(zeroLength, oneLength);
            String last = userName.substring(userName.length() - oneLength);
            return StringUtils.join(first, SYMBOL, last);
        }
        return motherBuyMember;
    }

    /**
     * 验证固定节点触发宝箱是否有效
     *
     * @param boxActivity 宝箱活动配置
     * @param memberId    会员ID
     * @param nodeType    节点类型
     * @return
     */
    private Boolean checkAvailNode(BoxActivityDTO boxActivity, Long memberId, Integer nodeType) {
        log.info("验证触发产生宝箱的节点是否有效，节点类型:{}, 会员ID:{}, 活动宝箱配置ID:{}", nodeType, memberId, boxActivity.getId());
        //固定节点JSON串
        String fixedNodeJson = boxActivity.getFixedNode();
        //时间节点JSON串
        String timeNodes = boxActivity.getTimeNode();

        log.info("固定节点-fixedNodeJson:{}", fixedNodeJson);
        log.info("时间节点-timeNodes:{}", timeNodes);
        //当前时间
        LocalTime nowTime = LocalTime.now();

        //时间节点集合
        List<LocalTime> timeNodeList = convertToList(timeNodes);
        if (!CollectionUtils.isEmpty(timeNodeList)) {
            //时间节点
            if (TIME_NODE.equals(nodeType)) {
                for (LocalTime timeNode : timeNodeList) {
                    //产生宝箱时间节点，设定时间在3分钟之内
                    if (timeNode.plusMinutes(-1).isBefore(nowTime)
                            && timeNode.plusMinutes(2).isAfter(nowTime)) {
                        StringBuilder timeKeySb = new StringBuilder();
                        timeKeySb.append(TIME_NODE_PREFIX);
                        timeKeySb.append(memberId).append(SEPARATOR_COLON).append(timeNode);
                        String timeKey = timeKeySb.toString();
                        //判断该时间节点是否已经产生宝箱
                        if (redisCache.getInt(timeKey) == null) {
                            log.info("该时间节点产生宝箱成功，时间节点:{}, 产生宝箱时间:{}", timeNode, nowTime);
                            //设置时间节点缓存-过期时间1分钟
                            redisCache.set(timeKey, 1, 30);
                            return true;
                        }
                        log.warn("该时间节点已产生过宝箱，时间节点:{}", timeNode);
                    }
                }
                return false;
            }
        }

        //固定节点集合
        List<FixedNodeJsonDTO> fixedNodeList = null;
        if (!StringUtils.isBlank(fixedNodeJson)) {
            fixedNodeList = JSON.parseArray(fixedNodeJson, FixedNodeJsonDTO.class);
        }
        if (!CollectionUtils.isEmpty(fixedNodeList)) {
            for (FixedNodeJsonDTO fixedNode : fixedNodeList) {
                if (!nodeType.equals(fixedNode.getNodeType())
                        || UNAVAILABLE.equals(fixedNode.getState())) {
                    continue;
                }

                StringBuilder fixedKeySb = new StringBuilder();
                fixedKeySb.append(FIXED_NODE_PREFIX);
                fixedKeySb.append(memberId).append(SEPARATOR_COLON).append(nodeType);
                String fixedKey = fixedKeySb.toString();
                Integer fixedCache = redisCache.getInt(fixedKey);
                if (fixedCache == null) {
                    log.info("该固定节点产生宝箱成功，节点类型:{}, 产生宝箱时间:{}", fixedNode.getNodeType(), nowTime);
                    redisCache.set(fixedKey, 1, 86400);
                    return true;
                } else {
                    if (fixedCache.compareTo(fixedNode.getLimited()) < 0) {
                        log.info("该固定节点产生宝箱成功，节点类型:{}, 产生宝箱时间:{}", fixedNode.getNodeType(), nowTime);
                        redisCache.delete(fixedKey);
                        redisCache.set(fixedKey, fixedCache + 1, 86400);
                        return true;
                    }
                    log.warn("该固定节点产生宝箱数量已达上限，节点类型:{}", fixedNode.getNodeType());
                }
            }
        }

        return false;
    }

    /**
     * 时间节点集合
     *
     * @param timeNodes
     * @return
     */
    private List<LocalTime> convertToList(String timeNodes) {
        if (StringUtils.isBlank(timeNodes)) {
            return null;
        }
        List<LocalTime> list = Arrays.asList(timeNodes.split(SEPARATOR_COMMA)).stream().map(s -> LocalTime.parse(s.trim())).collect(Collectors.toList());
        return list;
    }

    /**
     * 查询开宝箱活动当前时间所提供的宝箱模板
     *
     * @return
     */
    @Override
    public BoxActivityDTO getBoxActivity() {
        //查询当前开宝箱的活动
        PromotionEntity promotionEntity = queryPromotion();
        if (promotionEntity == null) {
            return null;
        }

        //查询开宝箱活动当前时间所提供的宝箱模板
        BoxActivityEntity entity = queryActivity(promotionEntity.getId());
        if (entity == null) {
            return null;
        }

        BoxActivityDTO boxActivity = new BoxActivityDTO();
        BeanCopyUtil.copy(entity, boxActivity);
        return boxActivity;
    }

    @Override
    public PromotionDto getPromotion() {
        PromotionEntity entity = queryPromotion();
        if (entity == null) {
            return null;
        }

        PromotionDto promotion = new PromotionDto();
        BeanCopyUtil.copy(entity, promotion);
        return promotion;
    }

    /**
     * 查询当前的开宝箱活动
     *
     * @return
     */
    private PromotionEntity queryPromotion() {
        PromotionEntity promotionEntity = redisCache.get(OPEN_BOX, PromotionEntity.class);
        //没有进行中的活动
        if (promotionEntity == null) {
            //查询当前时间是否有可用礼卡秒杀活动
            PromotionEntity promotionCond = new PromotionEntity();
            promotionCond.clearInit();
            //活动类型（10-开宝箱）
            promotionCond.setTypeId(PROMOTION_TYPE_OPEN_BOX.getCode());
            //活动状态（2-进行中）
            promotionCond.setMarketState(PROMOTION_STATE_ONGOING.getCode());
            promotionCond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            promotionEntity = promotionDao.selectOne(promotionCond);
            if (promotionEntity == null) {
                log.warn("queryPromotion-当前时间暂无开宝箱活动！");
                return null;
            }
            //过期时间一天
            redisCache.set(OPEN_BOX, promotionEntity, 3600);
        }
        return promotionEntity;
    }

    /**
     * 查询开宝箱活动当前时间所提供的宝箱模板
     *
     * @param promotionId 开宝箱活动ID
     * @return
     */
    private BoxActivityEntity queryActivity(Long promotionId) {
        BoxActivityEntity boxActivity = redisCache.get(OPEN_BOX_ACTIVITY, BoxActivityEntity.class);
        if (boxActivity == null) {
            EntityWrapper<BoxActivityEntity> activityCond = new EntityWrapper<>();
            activityCond.eq("promotionId", promotionId);
            activityCond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            List<BoxActivityEntity> activityList = boxActivityDao.selectList(activityCond);

            if (CollectionUtils.isEmpty(activityList)) {
                log.warn("queryActivity-开宝箱活动没有配置宝箱！活动ID:{}", promotionId);
                return null;
            }
            LocalDateTime now = LocalDateTime.now();
            for (BoxActivityEntity activity : activityList) {
                if (now.isAfter(activity.getEndTime()) || now.isBefore(activity.getStartTime())) {
                    continue;
                }
                //过期时间1分钟
                redisCache.set(OPEN_BOX_ACTIVITY, activity, 30);
                boxActivity = activity;
            }
        }
        return boxActivity;
    }

    /**
     * 查询用户宝箱记录
     *
     * @param promotionId 开宝箱活动的宝箱模板ID
     * @param memberId    用户ID
     * @return
     */
    private MemberBoxEntity queryMemberBox(Long promotionId, Long memberId) {
        //校验用户是否用户宝箱
        MemberBoxEntity entity = new MemberBoxEntity();
        entity.cleanInit();
        entity.setMemberId(memberId);
        entity.setPromotionId(promotionId);
        entity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
        return memberBoxDao.selectOne(entity);
    }

    /**
     * 开宝箱结果
     *
     * @param boxActivity 开宝箱活动的宝箱模板
     * @param memberId    用户IDR
     * @return
     */
    private AwardRecordDTO openBox(BoxActivityEntity boxActivity, Long memberId, Integer awardType) {
        log.info("开宝箱进行中，活动宝箱配置ID:{}, 会员ID:{}", boxActivity.getId(), memberId);
        if (awardType == null) {
            //获取中奖类型
            awardType = getAwardType(boxActivity.getRate());
        }

        //未中奖
        if (NO_AWARD.equals(awardType)) {
            return null;
        }

        //筛选出符合条件的奖池配置，并更新剩余库存
        BoxActivityItemEntity itemEntity = getAwardRecord(boxActivity.getId(), boxActivity.getAwardPool(), awardType);

        //该类型无可用奖品
        if (itemEntity == null) {
            log.warn("openBox-奖池配置无此类型，awardType:{}", awardType);
            return null;
        }

        //奖品记录
        AwardRecordEntity awardRecordEntity = new AwardRecordEntity();
        //活动名称
        PromotionEntity promotionEntity = queryPromotion();
        if (promotionEntity == null) {
            log.warn("开宝箱活动不存在或无效");
            //开宝箱活动不存在或无效
            throw new GlobalException(PromotionExceptionEnum.OPEN_BOX_ACTIVITY_INVALID);
        }
        awardRecordEntity.setPromotionId(promotionEntity.getId());
        awardRecordEntity.setPromotionName(promotionEntity.getName());
        awardRecordEntity.setAwardType(awardType);
        awardRecordEntity.setMemberId(memberId);
        awardRecordEntity.setPromotionBoxId(boxActivity.getId());
        //进一步封装奖品记录，用户绑定奖品
        AwardRecordDTO awardRecord = packAwardRecord(awardType, itemEntity, awardRecordEntity);
        if (awardRecord == null) {
            return null;
        }
        //新增奖品记录
        Integer count = awardRecordDao.insert(awardRecordEntity);
        if (count > 0) {
            awardRecord.setId(awardRecordEntity.getId());
            return awardRecord;
        }
        return null;
    }

    /**
     * 获取中奖类型
     *
     * @param rateJson 中奖率json串
     * @return
     */
    private Integer getAwardType(String rateJson) {
        List<RateJsonDTO> rateList = new ArrayList<>();
        List<RateJsonDTO> awardRateList = JSON.parseArray(rateJson, RateJsonDTO.class);
        if (CollectionUtils.isEmpty(awardRateList)) {
            //奖品概率配置不可用
            throw new GlobalException(PromotionExceptionEnum.OPEN_BOX_RATE_CONFIG_INVALID);
        }

        rateList.addAll(awardRateList);
//        //中奖总概率
//        Double sumAwardRate = awardRateList.stream().mapToDouble(RateJsonDTO::getRate).sum();
//        //未中奖概率
//        RateJsonDTO noAwardRate = new RateJsonDTO();
//        noAwardRate.setRate(100d - sumAwardRate);
//        noAwardRate.setAwardType(NO_AWARD);
//        rateList.add(noAwardRate);

        //筛选宝箱开出的奖品类型
        List<Double> rates = rateList.stream().map(rate -> rate.getRate()).collect(Collectors.toList());
        int selected = pickSelectedIndex(rates);
        return rateList.get(selected).getAwardType();
    }

    /**
     * 筛选出中奖的下标
     *
     * @param rates 中奖爆率
     * @return
     */
    private int pickSelectedIndex(List<Double> rates) {
        int selected = 0;
        try {
            //计算总权重
            Double sumWeight = 0d;
            for (Double rate : rates) {
                sumWeight += rate;
            }

            //产生随机数
            double randomNumber = ThreadLocalRandom.current().nextDouble();

            //根据随机数在所有奖品分布的区域并确定所抽奖品
            double d1 = 0;
            double d2 = 0;
            for (int i = 0; i < rates.size(); i++) {
                d2 += Double.parseDouble(String.valueOf(rates.get(i))) / sumWeight;
                if (i == 0) {
                    d1 = 0;
                } else {
                    d1 += Double.parseDouble(String.valueOf(rates.get(i - 1))) / sumWeight;
                }
                //判断每个奖品出现的概率
                if (randomNumber >= d1 && randomNumber <= d2) {
                    selected = i;
                    break;
                }
            }
        } catch (Exception e) {
            log.error("生成抽奖随机数出错，出错原因:{}", e.getMessage());
        }
        return selected;
    }

    /**
     * 筛选奖品，并更新该奖品库存
     *
     * @param boxActivityId 开宝箱活动的宝箱模板ID
     * @param awardPoolJson 奖池配置
     * @param awardType     奖品类型
     * @return
     */
    private BoxActivityItemEntity getAwardRecord(Long boxActivityId, String awardPoolJson, Integer awardType) {
        //筛选出符合条件的奖池配合
        List<AwardPoolJsonDTO> awardPoolList = JSON.parseArray(awardPoolJson, AwardPoolJsonDTO.class);
        if (CollectionUtils.isEmpty(awardPoolList)) {
            //奖池配置不可用
            throw new GlobalException(PromotionExceptionEnum.AWARD_POOL_CONFIG_INVALID);
        }

        AwardPoolJsonDTO availPool = null;
        for (AwardPoolJsonDTO awardPool : awardPoolList) {
            if (awardType.equals(awardPool.getAwardType())) {
                availPool = awardPool;
                break;
            }
        }

        if (availPool == null) {
            log.warn("openBox-奖池配置无此类型，awardType:{}", awardType);
            return null;
        }

        if (UNAVAILABLE.equals(availPool.getState())) {
            log.warn("openBox-奖池未选中此类型，awardType:{}", awardType);
            return null;
        }

        //查询奖品配置
        EntityWrapper<BoxActivityItemEntity> condWrapper = new EntityWrapper<>();
        condWrapper.eq("awardType", awardType);
        condWrapper.eq("promotionBoxId", boxActivityId);
        condWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

        String awardIds = availPool.getAwardIds();
        if (StringUtils.isBlank(awardIds)) {
            //非实物奖品，awardIds不能为空
            if (!RES_AWARD.equals(awardType)) {
                log.warn("openBox-未配置此类型的奖品，awardType:{}", awardType);
                return null;
            }
        } else {
            //获取奖品ID集合
            List<Long> awardIdList = Arrays.asList(awardIds.split(SEPARATOR_COMMA)).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
            condWrapper.in("awardId", awardIdList);
        }

        List<BoxActivityItemEntity> itemList = boxActivityItemDao.selectList(condWrapper);

        //筛选出奖品剩余库存大于0的记录
        List<BoxActivityItemEntity> availList = itemList.stream()
                .filter(avail -> avail.getDayStorageRest().intValue() > 0)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(availList)) {
            log.info("openBox-此类型的奖品库存不足，awardType:{}", awardType);
            return null;
        }

//        //获取随机数
//        int useIndex = ThreadLocalRandom.current().nextInt(0, availList.size());
        List<Double> rates = availList.stream().map(avail -> avail.getAwardRate()).collect(Collectors.toList());
        int useIndex = pickSelectedIndex(rates);
        BoxActivityItemEntity useAwardRecord = availList.get(useIndex);
        //更新库存
        Integer count = boxActivityItemDao.reduceStock(useAwardRecord.getId(), 1);
        if (count == 0) {
            //库存不足，重新选取商品
            useAwardRecord = getAwardRecord(boxActivityId, awardPoolJson, awardType);
        }

        return useAwardRecord;
    }

    /**
     * 封装奖品记录，用户绑定奖品
     *
     * @param awardType  奖品类型
     * @param itemEntity 宝箱奖品配置
     * @return
     */
    private AwardRecordDTO packAwardRecord(Integer awardType, BoxActivityItemEntity itemEntity, AwardRecordEntity awardRecordEntity) {
        //奖品记录
        AwardRecordDTO awardRecord;
        //优惠券
        if (COUPON_AWARD.equals(awardType)) {
            //查询优惠券详情
            PromotionEntity promotionEntity = promotionDao.selectById(itemEntity.getAwardId());
            if (promotionEntity == null) {
                log.warn("openBox-优惠券不存在，优惠券ID:{}", itemEntity.getAwardId());
                return null;
            }
            //优惠券名称
            awardRecordEntity.setAwardName(promotionEntity.getName());
            //设置状态为已领取
            awardRecordEntity.setState(RECEIVE_YES);

            try {
                //用户绑定优惠券
                promotionService.getCoupon(awardRecordEntity.getMemberId(), promotionEntity.getId());
            } catch (GlobalException e) {
                log.error("用户绑定优惠券失败:{}, 会员ID:{}, 优惠券ID-couponId:{}"
                        , e.getExceptionInfo().getDefaultMessage()
                        , awardRecordEntity.getMemberId()
                        , itemEntity.getAwardId());
                return null;
            }

            //awardRecordEntity封装完成后，复制到奖品返回记录
            awardRecord = DozerUtils.map(awardRecordEntity, AwardRecordDTO.class);
            //优惠券宝箱
            CouponBoxDTO couponBoxDTO = DozerUtils.map(promotionEntity, CouponBoxDTO.class);
            couponBoxDTO.setCouponId(promotionEntity.getId());
            couponBoxDTO.setStoreId(promotionEntity.getSponsorType());
            //店铺优惠券
            if (promotionEntity.getSponsorType() != null) {
                //店铺名称
                StoreInfoDetailDTO storeInfo = storeApi.getStore(promotionEntity.getSponsorType());
                if (storeInfo != null) {
                    couponBoxDTO.setStoreName(storeInfo.getName());
                }
            }
            awardRecord.setCouponBox(couponBoxDTO);
        } else if (CARD_AWARD.equals(awardType)) {//美礼卡
            try {
                //用户绑定美礼卡
                PrizeCardDTO prizeCard = giftCardApi.bindCardFromGiftcardBatch(itemEntity.getAwardId(), awardRecordEntity.getMemberId());

                if (prizeCard == null) {
                    log.info("openBox-美礼卡绑定失败，礼卡发行批次ID:{}", itemEntity.getAwardId());
                    return null;
                }

                //礼卡名称
                awardRecordEntity.setAwardName(prizeCard.getCardName());
                //设置状态为已领取
                awardRecordEntity.setState(RECEIVE_YES);

                //awardRecordEntity封装完成后，复制到奖品返回记录
                awardRecord = DozerUtils.map(awardRecordEntity, AwardRecordDTO.class);
                //礼卡宝箱
                CardBoxDTO cardBox = new CardBoxDTO();
                cardBox.setBatchId(itemEntity.getAwardId());
                cardBox.setCardCover(prizeCard.getCover());
                //礼卡面值
                cardBox.setCardValue(prizeCard.getFaceValue());
                awardRecord.setCardBox(cardBox);
            } catch (GlobalException e) {
                log.error("用户绑定礼卡失败:{}, 会员ID:{}, 礼卡发行批次ID:{}"
                        , e.getExceptionInfo().getDefaultMessage()
                        , awardRecordEntity.getMemberId()
                        , itemEntity.getAwardId());
                return null;
            }
        } else {//实物奖
            ResBoxJsonDTO resBoxJson = JSON.parseObject(itemEntity.getResContent(), ResBoxJsonDTO.class);
            if (resBoxJson == null) {
                log.warn("openBox-实物奖品配置为空，活动配置itemId:{}", itemEntity.getId());
                return null;
            }
            //实物奖品名称
            awardRecordEntity.setAwardName(resBoxJson.getAwardName());
            //设置状态为未领取
            awardRecordEntity.setState(RECEIVE_NO);

            //awardRecordEntity封装完成后，复制到奖品返回记录
            awardRecord = DozerUtils.map(awardRecordEntity, AwardRecordDTO.class);
            //实物宝箱
            ResBoxDTO resBox = new ResBoxDTO();
            //实物奖品封面
            resBox.setResCover(resBoxJson.getResCover());
            awardRecordEntity.setContent(JSON.toJSONString(resBox));
            awardRecord.setResBox(resBox);
        }

        return awardRecord;
    }
}


