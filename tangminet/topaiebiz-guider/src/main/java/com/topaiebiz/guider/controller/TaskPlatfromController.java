package com.topaiebiz.guider.controller;

import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.guider.dto.GuiderTaskInfoDto;
import com.topaiebiz.guider.dto.GuiderTaskLevelDto;
import com.topaiebiz.guider.dto.GuiderTaskShowDto;
import com.topaiebiz.guider.dto.UpdateOnLineDto;
import com.topaiebiz.guider.service.TaskService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by admin on 2018/5/30.
 */
@RestController
@RequestMapping(path = "/guider/task", method = RequestMethod.POST)
public class TaskPlatfromController {

    @Autowired
    private TaskService taskService;



    @RequestMapping(path = "/selectGuiderTaskInfoList")
    @PermissionController(value = PermitType.PLATFORM,operationName = "任务分页列表")
    public ResponseInfo selectGuiderTaskInfoList(@RequestBody GuiderTaskInfoDto guiderTaskInfoDto){
        int pageNo = guiderTaskInfoDto.getPageNo();
        int pageSize = guiderTaskInfoDto.getPageSize();
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(pageNo);
        pagePO.setPageSize(pageSize);
        return new ResponseInfo(taskService.getGuiderTaskInfoList(pagePO,guiderTaskInfoDto));
    }

    /**
     * 添加任务基本信息
     *
     * @param guiderTaskInfoDto
     * @return
     */
    @RequestMapping(path = "/insertGuiderTaskInfo")
    @PermissionController(value = PermitType.PLATFORM, operationName = "添加任务基本信息")
    public ResponseInfo insertGuiderTaskInfo(@RequestBody GuiderTaskInfoDto guiderTaskInfoDto) {
        return new ResponseInfo(taskService.insertGuiderTaskInfo(guiderTaskInfoDto));
    }

    /**
     * 回显任务基本信息(上一步)
     *
     * @param guiderTaskInfoDto
     * @return
     */
    @RequestMapping(path = "/selectGuiderTaskInfoById")
    @PermissionController(value = PermitType.PLATFORM, operationName = "回显任务基本信息")
    public ResponseInfo selectGuiderTaskInfoById(@RequestBody GuiderTaskInfoDto guiderTaskInfoDto) {
        return new ResponseInfo(taskService.selectGuiderTaskInfo(guiderTaskInfoDto.getTaskId()));
    }

    /**
     * 修改任务基本信息（下一步）
     *
     * @param guiderTaskInfoDto
     * @return
     */
    @RequestMapping(path = "/updateGuiderTaskInfo")
    @PermissionController(value = PermitType.PLATFORM, operationName = "修改任务基本信息")
    public ResponseInfo updateGuiderTaskInfo(@RequestBody GuiderTaskInfoDto guiderTaskInfoDto) {
        return new ResponseInfo(taskService.updateGuiderTaskInfoById(guiderTaskInfoDto));
    }

    /**
     * 添加任务阶梯与奖励配置
     * @param guiderTaskLevelDto
     * @return
     */
    @RequestMapping(path = "/insertGuiderTaskLevelInfo")
    @PermissionController(value = PermitType.PLATFORM, operationName = "添加任务阶梯")
    public ResponseInfo insertGuiderTaskLevelInfo(@RequestBody GuiderTaskLevelDto guiderTaskLevelDto) {
    return new ResponseInfo(taskService.insertGuiderTaskLevelInfo(guiderTaskLevelDto));
    }

    /**
     * 根据阶梯id删除任务阶梯
     * @param guiderTaskLevelDto
     * @return
     */
    @RequestMapping(path = "/deleteGuiderLevelInfoByLevelId")
    @PermissionController(value = PermitType.PLATFORM,operationName = "删除任务阶梯")
    public ResponseInfo deleteGuiderLevelInfoByLevelId(@RequestBody GuiderTaskLevelDto guiderTaskLevelDto){
        return new ResponseInfo(taskService.deleteGuiderLevelInfoByLevelId(guiderTaskLevelDto));
    }

    /**
     * 回显任务阶梯
     * @param guiderTaskLevelDto
     * @return
     */
    @RequestMapping(path = "/selectGuiderTaskLevelById")
    @PermissionController(value = PermitType.PLATFORM,operationName = "回显任务阶梯")
    public ResponseInfo selectGuiderTaskLevelById(@RequestBody GuiderTaskLevelDto guiderTaskLevelDto){
        return new ResponseInfo(taskService.selectGuiderTaskLevelInfoByLevelId(guiderTaskLevelDto.getLevelId()));
    }

    /**
     * 任务阶梯奖励列表
     * @param guiderTaskLevelDto
     * @return
     */
    @RequestMapping(path = "/selectGuiderTaskLevelInfoList")
    @PermissionController(value = PermitType.PLATFORM,operationName = "任务阶梯奖励列表")
    public ResponseInfo selectGuiderTaskLevelInfoList(@RequestBody  GuiderTaskLevelDto guiderTaskLevelDto){
        return new ResponseInfo(taskService.selectGuiderTaskLevelInfoList(guiderTaskLevelDto));
    }

    /**
     * 修改任务阶梯奖励信息
     * @param guiderTaskLevelDto
     * @return
     */
    @RequestMapping(path = "/updateGuiderTaskLevelInfo")
    @PermissionController(value = PermitType.PLATFORM,operationName = "修改阶梯奖励")
    public ResponseInfo updateGuiderTaskLevelInfo(@RequestBody GuiderTaskLevelDto guiderTaskLevelDto){
        return new ResponseInfo(taskService.updateGuiderTaskLevelInfo(guiderTaskLevelDto));
    }

    /**
     * 添加任务详细信息
     * @param guiderTaskShowDto
     * @return
     */
    @RequestMapping(path = "/insertGuiderTaskShowInfo")
    @PermissionController(value = PermitType.PLATFORM,operationName = "添加任务详情")
    public ResponseInfo insertGuiderTaskShowInfo(@RequestBody GuiderTaskShowDto guiderTaskShowDto){
        return new ResponseInfo(taskService.insertGuiderTaskShowInfo(guiderTaskShowDto));
    }

    /**
     * 回显任务详情信息
     * @param guiderTaskShowDto
     * @return
     */
    @RequestMapping(path = "/selectGuiderTaskShowInfoById")
    @PermissionController(value = PermitType.PLATFORM,operationName = "回显任务详情")
    public ResponseInfo selectGuiderTaskShowInfoById(@RequestBody GuiderTaskShowDto guiderTaskShowDto){
        return new ResponseInfo(taskService.selectGuiderTaskShowInfo(guiderTaskShowDto.getTaskId()));
    }

    /**
     * 修改任务详情信息
     * @param guiderTaskShowDto
     * @return
     */
    @RequestMapping(path = "/updateGuiderTaskShowInfo")
    @PermissionController(value = PermitType.PLATFORM,operationName = "修改任务详情")
    public ResponseInfo updateGuiderTaskShowInfo(@RequestBody GuiderTaskShowDto guiderTaskShowDto){
        return  new ResponseInfo(taskService.updateGuiderTaskShowInfo(guiderTaskShowDto));
    }

    /**
     * 修改上线下线状态
     * @param updateOnLineDto
     * @return
     */
   @RequestMapping(path = "/updateOnLine")
    @PermissionController(value = PermitType.PLATFORM,operationName = "判断上线下线")
   public ResponseInfo updateOnLine(@RequestBody UpdateOnLineDto updateOnLineDto){
        return new ResponseInfo(taskService.updateOnLine(updateOnLineDto.getTaskId(),updateOnLineDto.getIsOnLine()));
    }


}
