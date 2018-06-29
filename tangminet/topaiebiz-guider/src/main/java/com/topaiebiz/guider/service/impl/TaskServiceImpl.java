package com.topaiebiz.guider.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.guider.constants.GuiderContants;
import com.topaiebiz.guider.dao.GuiderTaskInfoDao;
import com.topaiebiz.guider.dao.GuiderTaskLevelDao;
import com.topaiebiz.guider.dao.GuiderTaskLevelPrizeDao;
import com.topaiebiz.guider.dao.GuiderTaskShowDao;
import com.topaiebiz.guider.entity.GuiderTaskInfoEntity;
import com.topaiebiz.guider.entity.GuiderTaskLevelEntity;
import com.topaiebiz.guider.entity.GuiderTaskLevelPrizeEntity;
import com.topaiebiz.guider.entity.GuiderTaskShowEntity;
import com.topaiebiz.guider.dto.GuiderTaskInfoDto;
import com.topaiebiz.guider.dto.GuiderTaskLevelDto;
import com.topaiebiz.guider.dto.GuiderTaskLevelPrizeDto;
import com.topaiebiz.guider.dto.GuiderTaskShowDto;
import com.topaiebiz.guider.exception.GuiderExceptionEnum;
import com.topaiebiz.guider.service.TaskService;
import com.topaiebiz.guider.utils.TaskUtil;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2018/5/31.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private GuiderTaskInfoDao guiderTaskInfoDao;

    @Autowired
    private GuiderTaskLevelDao guiderTaskLevelDao;

    @Autowired
    private GuiderTaskLevelPrizeDao guiderTaskLevelPrizeDao;

    @Autowired
    private GuiderTaskShowDao guiderTaskShowDao;

    @Autowired
    private GuiderTaskLevelDao taskLevelDao;

    @Autowired
    protected GuiderTaskLevelPrizeDao taskLevelPrizeDao;

    @Override
    public GuiderTaskInfoDto selectGuiderTaskInfoDetailById(Long taskId) {
        return null;
    }

    @Override
    public PageInfo<GuiderTaskInfoDto> getGuiderTaskInfoList(PagePO pagePO, GuiderTaskInfoDto guiderTaskInfoDto) {
        Page<GuiderTaskInfoDto> page = PageDataUtil.buildPageParam(pagePO);
        EntityWrapper<GuiderTaskInfoEntity> cood = new EntityWrapper<>();
        cood.eq("deletedFlag",Constants.DeletedFlag.DELETED_NO);
        if (null != guiderTaskInfoDto.getTaskName() && !"".equals(guiderTaskInfoDto.getTaskName())) {
            cood.like("taskName", guiderTaskInfoDto.getTaskName());
        }
        if (null != guiderTaskInfoDto.getTaskStatus() && !"".equals(guiderTaskInfoDto.getTaskStatus())) {
            cood.like("status", String.valueOf(guiderTaskInfoDto.getTaskStatus()));
        }
        List<GuiderTaskInfoEntity> guiderTaskInfoEntities = guiderTaskInfoDao.selectPage(page, cood);
        List<GuiderTaskInfoDto> guiderTaskInfoDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(guiderTaskInfoEntities)){
        for (GuiderTaskInfoEntity guiderTaskShowEntity :guiderTaskInfoEntities){
            GuiderTaskInfoDto tempTaskInfoDto = new GuiderTaskInfoDto();
            BeanCopyUtil.copy(guiderTaskShowEntity,tempTaskInfoDto);
            tempTaskInfoDto.setTaskId(guiderTaskShowEntity.getId());
            tempTaskInfoDto.setTaskStatus(TaskUtil.processTaskStatus(tempTaskInfoDto));
            guiderTaskInfoDtoList.add(tempTaskInfoDto);
        }
        }
        page.setRecords(guiderTaskInfoDtoList);
        return PageDataUtil.copyPageInfo(page);
    }


    @Override
    public GuiderTaskInfoDto insertGuiderTaskInfo(GuiderTaskInfoDto guiderTaskInfoDto) {
        GuiderTaskInfoEntity guiderTaskInfoEntity = new GuiderTaskInfoEntity();
        BeanCopyUtil.copy(guiderTaskInfoDto, guiderTaskInfoEntity);
        guiderTaskInfoDao.insert(guiderTaskInfoEntity);
        GuiderTaskInfoDto guiderTaskInfo = new GuiderTaskInfoDto();
        BeanCopyUtil.copy(guiderTaskInfoEntity, guiderTaskInfo);
        guiderTaskInfo.setTaskId(guiderTaskInfoEntity.getId());
        return guiderTaskInfo;
    }

    @Override
    public GuiderTaskInfoDto selectGuiderTaskInfo(Long taskId) {
        //判断taskId是否为空
        GuiderTaskInfoEntity guiderTaskInfoEntity = guiderTaskInfoDao.selectById(taskId);
        GuiderTaskInfoDto guiderTaskInfoDto = new GuiderTaskInfoDto();
        if (guiderTaskInfoEntity != null) {
            BeanCopyUtil.copy(guiderTaskInfoEntity, guiderTaskInfoDto);
        }
        return guiderTaskInfoDto;
    }

    @Override
    public Integer updateGuiderTaskInfoById(GuiderTaskInfoDto guiderTaskInfoDto) {
        Integer i = 0;
        GuiderTaskInfoEntity guiderTaskInfoEntity = new GuiderTaskInfoEntity();
        guiderTaskInfoEntity.setId(guiderTaskInfoDto.getTaskId());
        GuiderTaskInfoEntity taskInfoEntity = guiderTaskInfoDao.selectById(guiderTaskInfoEntity);
        if (taskInfoEntity !=null){
            BeanCopyUtil.copy(guiderTaskInfoDto, taskInfoEntity);
            taskInfoEntity.setLastModifiedTime(new Date());
            taskInfoEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
            i = guiderTaskInfoDao.updateById(taskInfoEntity);
        }

        return i;
    }

    @Override
    public Integer insertGuiderTaskLevelInfo(GuiderTaskLevelDto guiderTaskLevelDto) {
        GuiderTaskLevelEntity guiderTaskLevelEntity = new GuiderTaskLevelEntity();
        //阶梯类型
        BeanCopyUtil.copy(guiderTaskLevelDto, guiderTaskLevelEntity);
        guiderTaskLevelDao.insert(guiderTaskLevelEntity);
        Integer i = 0;
        List<GuiderTaskLevelPrizeDto> guiderTaskLevelPrizeDtos = guiderTaskLevelDto.getGuiderTaskLevelPrizeDtos();
        if (CollectionUtils.isNotEmpty(guiderTaskLevelPrizeDtos)) {
            for (GuiderTaskLevelPrizeDto guiderTaskLevelPrizeDto : guiderTaskLevelPrizeDtos) {
                GuiderTaskLevelPrizeEntity guiderTaskLevelPrizeEntity = new GuiderTaskLevelPrizeEntity();
                BeanCopyUtil.copy(guiderTaskLevelPrizeDto, guiderTaskLevelPrizeEntity);
                guiderTaskLevelPrizeEntity.setTaskId(guiderTaskLevelEntity.getTaskId());
                guiderTaskLevelPrizeEntity.setLevelId(guiderTaskLevelEntity.getId());
                i = guiderTaskLevelPrizeDao.insert(guiderTaskLevelPrizeEntity);
            }
        }
        return i;
    }

    @Override
    public Integer deleteGuiderLevelInfoByLevelId(GuiderTaskLevelDto guiderTaskLevelDto) {
      Integer i=0;
       if (guiderTaskLevelDto.getLevelId()!=null){
           GuiderTaskLevelEntity coon= new GuiderTaskLevelEntity();
           coon.cleanInit();
           coon.setId(guiderTaskLevelDto.getLevelId());
           coon.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
           guiderTaskLevelDao.updateById(coon);
           EntityWrapper<GuiderTaskLevelPrizeEntity> cond= new EntityWrapper<>();
           cond.eq("levelId",guiderTaskLevelDto.getLevelId());
           List<GuiderTaskLevelPrizeEntity> guiderTaskLevelPrizeEntities = guiderTaskLevelPrizeDao.selectList(cond);
            if (CollectionUtils.isNotEmpty(guiderTaskLevelPrizeEntities)){
                for (GuiderTaskLevelPrizeEntity guiderTaskLevelPrizeEntity :guiderTaskLevelPrizeEntities){
                    guiderTaskLevelPrizeEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
                    i = guiderTaskLevelPrizeDao.updateById(guiderTaskLevelPrizeEntity);
                }
            }
       }
        return i;
    }

    @Override
    public GuiderTaskLevelDto selectGuiderTaskLevelInfoByLevelId(Long levelId) {
        GuiderTaskLevelEntity coon = new GuiderTaskLevelEntity();
        coon.cleanInit();
        coon.setId(levelId);
        coon.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        GuiderTaskLevelEntity guiderTaskLevelEntity = guiderTaskLevelDao.selectOne(coon);
        GuiderTaskLevelDto guiderTaskLevelDto = new GuiderTaskLevelDto();
        BeanCopyUtil.copy(guiderTaskLevelEntity,guiderTaskLevelDto);
        EntityWrapper<GuiderTaskLevelPrizeEntity> cond = new EntityWrapper<>();
        cond.eq("levelId",levelId);
        cond.eq("deletedFlag",Constants.DeletedFlag.DELETED_NO);
        List<GuiderTaskLevelPrizeEntity> guiderTaskLevelPrizeEntities = guiderTaskLevelPrizeDao.selectList(cond);
        List<GuiderTaskLevelPrizeDto> guiderTaskLevelPrizeDtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(guiderTaskLevelPrizeEntities)){
            for (GuiderTaskLevelPrizeEntity guiderTaskLevelPrizeEntity :guiderTaskLevelPrizeEntities){
                GuiderTaskLevelPrizeDto guiderTaskLevelPrizeDto = new GuiderTaskLevelPrizeDto();
                BeanCopyUtil.copy(guiderTaskLevelPrizeEntity,guiderTaskLevelPrizeDto);
                guiderTaskLevelPrizeDto.setPrizeId(guiderTaskLevelPrizeEntity.getId());
                guiderTaskLevelPrizeDtos.add(guiderTaskLevelPrizeDto);
            }
        }
        if (CollectionUtils.isNotEmpty(guiderTaskLevelPrizeDtos)){
            guiderTaskLevelDto.setGuiderTaskLevelPrizeDtos(guiderTaskLevelPrizeDtos);
        }
        return guiderTaskLevelDto;
    }

    @Override
    public Integer updateGuiderTaskLevelInfo(GuiderTaskLevelDto guiderTaskLevelDto) {
        Integer i =0;
        GuiderTaskLevelEntity coon = new GuiderTaskLevelEntity();
        coon.cleanInit();
        coon.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        coon.setId(guiderTaskLevelDto.getLevelId());
        GuiderTaskLevelEntity guiderTaskLevelEntity = guiderTaskLevelDao.selectById(coon);
        if (guiderTaskLevelEntity !=null){
            guiderTaskLevelEntity.setLeftInclusiveInterval(guiderTaskLevelDto.getLeftInclusiveInterval());
            guiderTaskLevelEntity.setRightOpenInterval(guiderTaskLevelDto.getRightOpenInterval());
            guiderTaskLevelEntity.setLevelType(guiderTaskLevelDto.getLevelType());
            guiderTaskLevelEntity.setId(guiderTaskLevelDto.getLevelId());
            guiderTaskLevelDao.updateById(guiderTaskLevelEntity);
        }
        //删除原有的奖励
        EntityWrapper<GuiderTaskLevelPrizeEntity> cond = new EntityWrapper<>();
        cond.eq("levelId",guiderTaskLevelDto.getLevelId());
        cond.eq("deletedFlag",Constants.DeletedFlag.DELETED_NO);
        List<GuiderTaskLevelPrizeEntity> guiderTaskLevelPrizeEntities = guiderTaskLevelPrizeDao.selectList(cond);
        if (CollectionUtils.isNotEmpty(guiderTaskLevelPrizeEntities)){
            for (GuiderTaskLevelPrizeEntity guiderTaskLevelPrizeEntity : guiderTaskLevelPrizeEntities){
                guiderTaskLevelPrizeEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
                guiderTaskLevelPrizeDao.updateById(guiderTaskLevelPrizeEntity);
            }
        }
        if (CollectionUtils.isNotEmpty(guiderTaskLevelDto.getGuiderTaskLevelPrizeDtos())){
            List<GuiderTaskLevelPrizeDto> guiderTaskLevelPrizeDtos = guiderTaskLevelDto.getGuiderTaskLevelPrizeDtos();
            for (GuiderTaskLevelPrizeDto guiderTaskLevelPrizeDto : guiderTaskLevelPrizeDtos) {
                GuiderTaskLevelPrizeEntity guiderTaskLevelPrizeEntity = new GuiderTaskLevelPrizeEntity();
                BeanCopyUtil.copy(guiderTaskLevelPrizeDto, guiderTaskLevelPrizeEntity);
                guiderTaskLevelPrizeEntity.setTaskId(guiderTaskLevelEntity.getTaskId());
                guiderTaskLevelPrizeEntity.setLevelId(guiderTaskLevelEntity.getId());
                guiderTaskLevelPrizeEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
                guiderTaskLevelPrizeEntity.setLastModifiedTime(new Date());
                i = guiderTaskLevelPrizeDao.insert(guiderTaskLevelPrizeEntity);
            }
        }
        return i;
    }

    @Override
    public List<GuiderTaskLevelDto> selectGuiderTaskLevelInfoList(GuiderTaskLevelDto guiderTaskLevelDto) {
        EntityWrapper<GuiderTaskLevelEntity> conn = new EntityWrapper();
        conn.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        conn.eq("taskId", guiderTaskLevelDto.getTaskId());
        List<GuiderTaskLevelEntity> guiderTaskLevelEntities = guiderTaskLevelDao.selectList(conn);
        if (CollectionUtils.isEmpty(guiderTaskLevelEntities)) {
            return Collections.EMPTY_LIST;
        }
        List<GuiderTaskLevelDto> guiderTaskLevelDtoss = new ArrayList();
        for (GuiderTaskLevelEntity guiderTaskLevelEntity : guiderTaskLevelEntities){
            GuiderTaskLevelDto guiderTaskLevelDto1 = new GuiderTaskLevelDto();
            BeanCopyUtil.copy(guiderTaskLevelEntity,guiderTaskLevelDto1);
            guiderTaskLevelDto1.setLevelId(guiderTaskLevelEntity.getId());
            guiderTaskLevelDtoss.add(guiderTaskLevelDto1);
            EntityWrapper<GuiderTaskLevelPrizeEntity> cond = new EntityWrapper();
            cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            cond.eq("levelId", guiderTaskLevelEntity.getId());
            List<GuiderTaskLevelPrizeEntity> guiderTaskLevelPrizeEntities = guiderTaskLevelPrizeDao.selectList(cond);
            if (CollectionUtils.isNotEmpty(guiderTaskLevelPrizeEntities)) {
                List<GuiderTaskLevelPrizeDto> guiderTaskLevelPrizeDtos = BeanCopyUtil.copyList(guiderTaskLevelPrizeEntities, GuiderTaskLevelPrizeDto.class);
                guiderTaskLevelDto1.setGuiderTaskLevelPrizeDtos(guiderTaskLevelPrizeDtos);
            }
        }
        return guiderTaskLevelDtoss;
    }

    @Override
    public Integer insertGuiderTaskShowInfo(GuiderTaskShowDto guiderTaskShowDto) {
        Integer i = 0;
        GuiderTaskShowEntity guiderTaskShowEntity = new GuiderTaskShowEntity();
        BeanCopyUtil.copy(guiderTaskShowDto, guiderTaskShowEntity);
        i = guiderTaskShowDao.insert(guiderTaskShowEntity);
        return i;
    }

    @Override
    public GuiderTaskShowDto selectGuiderTaskShowInfo(Long taskId) {
        GuiderTaskShowDto guiderTaskShowDto = new GuiderTaskShowDto();
        if (taskId !=null){
            GuiderTaskShowEntity conn = new GuiderTaskShowEntity();
            conn.cleanInit();
            conn.setTaskId(taskId);
            conn.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            GuiderTaskShowEntity guiderTaskShowEntity = guiderTaskShowDao.selectOne(conn);
            if (guiderTaskShowEntity !=null){
                BeanCopyUtil.copy(guiderTaskShowEntity,guiderTaskShowDto);
                guiderTaskShowDto.setShowId(guiderTaskShowEntity.getId());
            }
        }
        return guiderTaskShowDto;
    }

    @Override
    public Integer updateGuiderTaskShowInfo(GuiderTaskShowDto guiderTaskShowDto) {
        Integer i =0;
        GuiderTaskShowEntity cond = new GuiderTaskShowEntity();
        cond.cleanInit();
        cond.setTaskId(guiderTaskShowDto.getTaskId());
        cond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        GuiderTaskShowEntity guiderTaskShowEntity = guiderTaskShowDao.selectOne(cond);
        if (guiderTaskShowEntity !=null){
            BeanCopyUtil.copy(guiderTaskShowDto,guiderTaskShowEntity);
            guiderTaskShowEntity.setLastModifiedTime(new Date());
            guiderTaskShowEntity.setId(guiderTaskShowDto.getShowId());
            guiderTaskShowEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
             i = guiderTaskShowDao.updateById(guiderTaskShowEntity);
        }
        return i;
    }

    @Override
    public List<GuiderTaskLevelDto> getTaskLevelList(Long taskId, Integer levelType) {
        EntityWrapper<GuiderTaskLevelEntity> cond = new EntityWrapper<>();
        cond.eq("id",taskId);
        cond.eq("levelType",levelType);
        List<GuiderTaskLevelEntity> guiderTaskLevelEntities = guiderTaskLevelDao.selectList(cond);
         List<GuiderTaskLevelDto> guiderTaskInfoDtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(guiderTaskLevelEntities)){
            for (GuiderTaskLevelEntity guiderTaskLevelEntity :guiderTaskLevelEntities){
                GuiderTaskLevelDto guiderTaskLevelDto = new GuiderTaskLevelDto();
                BeanCopyUtil.copy(guiderTaskLevelEntity,guiderTaskLevelDto);
                guiderTaskLevelDto.setLevelId(guiderTaskLevelEntity.getId());
                guiderTaskInfoDtos.add(guiderTaskLevelDto);
            }
        }
        return guiderTaskInfoDtos;
    }

    @Override
    public List<GuiderTaskLevelPrizeDto> getTaskLevelPrizeList(Long levelId) {
        EntityWrapper<GuiderTaskLevelPrizeEntity> coon = new EntityWrapper<>();
        coon.eq("deletedFlag",Constants.DeletedFlag.DELETED_NO);
        coon.eq("levelId",levelId);
        List<GuiderTaskLevelPrizeEntity> guiderTaskLevelPrizeEntities = guiderTaskLevelPrizeDao.selectList(coon);
        if (CollectionUtils.isEmpty(guiderTaskLevelPrizeEntities)){
            return null;
        }
        List<GuiderTaskLevelPrizeDto> guiderTaskLevelPrizeDaos = BeanCopyUtil.copyList(guiderTaskLevelPrizeEntities, GuiderTaskLevelPrizeDto.class);
        return guiderTaskLevelPrizeDaos;
    }

    @Override
    public Boolean updateOnLine(Long taskId,Integer isOnline) {
        GuiderTaskInfoEntity conn = new GuiderTaskInfoEntity();
        conn.setId(taskId);
        conn.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        GuiderTaskInfoEntity guiderTaskInfoEntity = guiderTaskInfoDao.selectById(conn);
        if(guiderTaskInfoEntity !=null){
            guiderTaskInfoEntity.setIsOnLine(isOnline);
            guiderTaskInfoEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
            guiderTaskInfoEntity.setLastModifiedTime(new Date());
            guiderTaskInfoDao.updateById(guiderTaskInfoEntity);
            return true;
        }
        if (guiderTaskInfoEntity == null){
            throw new GlobalException(GuiderExceptionEnum.GUIDERTASKINFO_NOT_ENTER);
        }
        return false;
    }


}
