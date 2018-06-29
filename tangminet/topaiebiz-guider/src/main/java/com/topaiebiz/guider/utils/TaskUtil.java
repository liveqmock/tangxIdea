package com.topaiebiz.guider.utils;

import com.topaiebiz.guider.constants.GuiderContants;
import com.topaiebiz.guider.dto.GuiderTaskInfoDto;
import com.topaiebiz.guider.dto.GuiderTaskLevelDto;
import com.topaiebiz.guider.dto.GuiderTaskLevelPrizeDto;
import com.topaiebiz.guider.entity.GuiderTaskInfoEntity;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by ward on 2018-06-05.
 */
public class TaskUtil {

    public static Boolean checkTaskTime(GuiderTaskInfoEntity taskInfo) {
        Date currentDate = new Date();
        Date startTime = taskInfo.getStartTime();
        Date endTime = taskInfo.getEndTime();
        if (null != taskInfo && currentDate.compareTo(startTime) > 0
                && currentDate.compareTo(endTime) < 0) {
            return true;
        }
        return false;
    }

    /**
     * 检测奖品等级 区间是连续 （连续返回true）
     *
     * @param taskLevelDtoList
     * @return
     */
    public static Boolean checkTaskLevelContinuou(List<GuiderTaskLevelDto> taskLevelDtoList) {
        TreeMap<Integer, Integer> intervalMap = new TreeMap<>();
        for (GuiderTaskLevelDto taskLevelDto : taskLevelDtoList) {
            intervalMap.put(taskLevelDto.getLeftInclusiveInterval(), taskLevelDto.getRightOpenInterval());
        }
        Integer lastKey = intervalMap.lastKey();
        Integer tempKey = intervalMap.firstKey();
        while (true) {
            if (lastKey.equals(tempKey)) {
                return true;
            }
            tempKey = intervalMap.get(tempKey);
            if (null == tempKey) {
                return false;
            }
        }
    }

    /**
     * 检测奖品等级 区间是否有 重叠 （重叠返回true）
     *
     * @param taskLevelDtoList
     * @return
     */
    public static Boolean checkTaskLevelOverlap(List<GuiderTaskLevelDto> taskLevelDtoList) {
        TreeMap<Integer, Integer> intervalMap = new TreeMap<>();
        for (GuiderTaskLevelDto taskLevelDto : taskLevelDtoList) {
            intervalMap.put(taskLevelDto.getLeftInclusiveInterval(), taskLevelDto.getRightOpenInterval());
        }
        Iterator iter = intervalMap.entrySet().iterator();
        Integer compareInterval = 1;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Integer leftInclusiveInterval = (Integer) entry.getKey();
            if (compareInterval <= leftInclusiveInterval) {
                compareInterval = (Integer) entry.getValue();
            } else {
                return true;
            }
        }
        return false;
    }


    public static GuiderTaskLevelDto obtainTaskLevel(List<GuiderTaskLevelDto> taskLevelDtoList, Integer sortingId) {
        if (checkTaskLevelOverlap(taskLevelDtoList)) {
            return null;
        }
        for (GuiderTaskLevelDto taskLevelDto : taskLevelDtoList) {
            Integer leftInclusiveInterval = taskLevelDto.getLeftInclusiveInterval();
            Integer rightInterval = taskLevelDto.getRightOpenInterval();
            if (leftInclusiveInterval >= sortingId &&
                    (null == rightInterval || rightInterval < sortingId)) {
                return taskLevelDto;
            }
        }
        return null;
    }


    public static BigDecimal obtainAwardRate(List<GuiderTaskLevelPrizeDto> taskLevelPrizeDtos) {
        if (CollectionUtils.isEmpty(taskLevelPrizeDtos)) {
            return null;
        }
        BigDecimal awardRate = null;
        for (GuiderTaskLevelPrizeDto levelPrizeDto : taskLevelPrizeDtos) {
            if (GuiderContants.PrizeObjType.ORDER_RATIO.equals(levelPrizeDto.getPrizeObjType())) {
                BigDecimal tempAwardRate = new BigDecimal(levelPrizeDto.getPrizeObjValue());
                awardRate = tempAwardRate.subtract(new BigDecimal(100));
                break;
            }
        }
        return awardRate;
    }

    public static Integer  processTaskStatus(GuiderTaskInfoDto guiderTaskInfoDto){
        long startTime=guiderTaskInfoDto.getStartTime().getTime();
        long endTime=guiderTaskInfoDto.getEndTime().getTime();
        long currentTime=System.currentTimeMillis();
        if(currentTime>endTime){
            return GuiderContants.TaskStatus.FINISH;
        }else if(currentTime<startTime){
            return GuiderContants.TaskStatus.NOT_BEGUN;
        }else {
            return GuiderContants.TaskStatus.UNDERWAY;
        }
    }
}
