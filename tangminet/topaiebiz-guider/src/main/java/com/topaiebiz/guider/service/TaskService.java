package com.topaiebiz.guider.service;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.guider.dto.GuiderTaskInfoDto;
import com.topaiebiz.guider.dto.GuiderTaskLevelDto;
import com.topaiebiz.guider.dto.GuiderTaskLevelPrizeDto;
import com.topaiebiz.guider.dto.GuiderTaskShowDto;

import java.util.List;

/**
 * Created by ward on 2018-05-29.
 */
public interface TaskService {


    /**
     * 根据任务id，查询任务详细信息
     *
     * @param taskId
     * @return
     */
    GuiderTaskInfoDto selectGuiderTaskInfoDetailById(Long taskId);

    /**
     * 任务中心列表
     *
     * @param pagePO
     * @param guiderTaskInfoDto
     * @return
     */
    PageInfo<GuiderTaskInfoDto> getGuiderTaskInfoList(PagePO pagePO, GuiderTaskInfoDto guiderTaskInfoDto);

    /**
     * 添加任务基本信息
     *
     * @param guiderTaskInfoDto
     * @return
     */
    GuiderTaskInfoDto insertGuiderTaskInfo(GuiderTaskInfoDto guiderTaskInfoDto);

    /**
     * 回显任务基本信息（上一步）
     *
     * @param taskId
     * @return
     */
    GuiderTaskInfoDto selectGuiderTaskInfo(Long taskId);

    /**
     * 修改任务基本信息
     *
     * @param guiderTaskInfoDto
     * @return
     */
    Integer updateGuiderTaskInfoById(GuiderTaskInfoDto guiderTaskInfoDto);

    /**
     * 添加阶梯信息，奖励配置信息 修改
     *
     * @param guiderTaskLevelDto
     * @return
     */
    Integer insertGuiderTaskLevelInfo(GuiderTaskLevelDto guiderTaskLevelDto);

    /**
     * 根据levelId删除对应的阶梯
     * @param guiderTaskLevelDto
     * @return
     */
    Integer deleteGuiderLevelInfoByLevelId(GuiderTaskLevelDto guiderTaskLevelDto);

    /**
     * 根据阶梯id回显阶梯奖励信息
     * @param levelId
     * @return
     */
    GuiderTaskLevelDto selectGuiderTaskLevelInfoByLevelId(Long levelId);


    /**
     * 根据levelId修改阶梯奖励信息
     * @param guiderTaskLevelDto
     * @return
     */
    Integer updateGuiderTaskLevelInfo(GuiderTaskLevelDto guiderTaskLevelDto);


    /**
     * 根据任务id展示任务阶梯
     *
     * @param guiderTaskLevelDto
     * @return
     */
    List<GuiderTaskLevelDto> selectGuiderTaskLevelInfoList(GuiderTaskLevelDto guiderTaskLevelDto);


    /**
     * 添加任务分享详情
     *
     * @param guiderTaskShowDto
     * @return
     */
    Integer insertGuiderTaskShowInfo(GuiderTaskShowDto guiderTaskShowDto);

    /**
     * 根据任务id查询任务详情
     * @param taskId
     * @return
     */
    GuiderTaskShowDto selectGuiderTaskShowInfo(Long taskId);

    /**
     * 根据任务id修改任务分享详情
     * @param guiderTaskShowDto
     * @return
     */
    Integer updateGuiderTaskShowInfo(GuiderTaskShowDto guiderTaskShowDto);

    /**
     *根据任务id，阶梯奖励类型 查询任务阶梯集合
     * @param taskId
     * @param levelType
     * @return
     */
    List<GuiderTaskLevelDto> getTaskLevelList(Long taskId, Integer levelType);

    /**
     *根据阶梯id查询奖励集合
     * @param levelId
     * @return
     */
    List<GuiderTaskLevelPrizeDto> getTaskLevelPrizeList(Long levelId);


    /**
     * 修改上线下线的状态
     * @param taskId
     * @param isOnline
     * @return
     */
    Boolean updateOnLine(Long taskId,Integer isOnline);



}
