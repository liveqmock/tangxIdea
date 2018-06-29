package com.topaiebiz.transport.expressage.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.transport.dto.ExpressageParamDto;
import com.topaiebiz.transport.dto.LogisticsDto;
import com.topaiebiz.transport.expressage.dao.ExpressageInfoDao;
import com.topaiebiz.transport.expressage.dao.ExpressageSubscriptionLogDao;
import com.topaiebiz.transport.expressage.dao.LogisticsCompanyDao;
import com.topaiebiz.transport.expressage.dto.ExpressageDto;
import com.topaiebiz.transport.expressage.dto.LogisticsCompanyDto;
import com.topaiebiz.transport.expressage.entity.ExpressageInfoEntity;
import com.topaiebiz.transport.expressage.entity.ExpressageSubscriptionLogEntity;
import com.topaiebiz.transport.expressage.entity.LogisticsCompanyEntity;
import com.topaiebiz.transport.expressage.exception.ExpressageExceptionEnum;
import com.topaiebiz.transport.expressage.service.ExpressageService;
import com.topaiebiz.transport.expressage.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description 快递处理业务层 实现类
 * <p>
 * Author Aaron.Xue
 * <p>
 * Date 2017年10月17日 下午9:11:02
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
@Transactional
@Slf4j
public class ExpressageServiceImpl implements ExpressageService {

    @Value("${kuaidi100.collback_url}")
    private String baseUrl;

    @Value("${kuaidi100.key_id}")
    private String key_id;

    @Value("${kuaidi100.customer}")
    private String customer;

    @Autowired
    private ExpressageInfoDao expressageInfoDao;

    @Autowired
    private ExpressageSubscriptionLogDao expressageSubscriptionLogDao;

    @Autowired
    private LogisticsCompanyDao logisticsCompanyDao;

    @Override
    public List<LogisticsCompanyDto> getListLogisticsCompany() {
        EntityWrapper<LogisticsCompanyEntity> condition = new EntityWrapper<>();
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condition.eq("status", 0);
        List<LogisticsCompanyEntity> logisticsCompanyEntities = logisticsCompanyDao.selectList(condition);

        List<LogisticsCompanyDto> dtos = new ArrayList<>();
        for (LogisticsCompanyEntity entity : logisticsCompanyEntities) {
            LogisticsCompanyDto dto = new LogisticsCompanyDto();
            BeanCopyUtil.copy(entity, dto);
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public PageInfo<LogisticsCompanyDto> getList(LogisticsCompanyDto logisticsCompanyDto) {
        Page<LogisticsCompanyDto> page = PageDataUtil.buildPageParam(logisticsCompanyDto);
        EntityWrapper<LogisticsCompanyEntity> condition = new EntityWrapper<LogisticsCompanyEntity>();
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        if (null != logisticsCompanyDto.getComName() && !"".equals(logisticsCompanyDto.getComName())) {
            condition.like("comName", logisticsCompanyDto.getComName());
        }
        List<LogisticsCompanyEntity> comEntities = logisticsCompanyDao.selectPage(page, condition);
        if (CollectionUtils.isEmpty(comEntities)) {
            return PageDataUtil.copyPageInfo(page);
        }

        List<LogisticsCompanyDto> comDtos = new ArrayList<>();
        for (LogisticsCompanyEntity entity : comEntities) {
            LogisticsCompanyDto dto = new LogisticsCompanyDto();
            BeanCopyUtil.copy(entity, dto);
            comDtos.add(dto);
        }
        page.setRecords(comDtos);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public void add(LogisticsCompanyDto logisticsCompanyDto) {
        LogisticsCompanyEntity entity = new LogisticsCompanyEntity();
        entity.cleanInit();
        entity.setComName(logisticsCompanyDto.getComName());
        entity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        LogisticsCompanyEntity logisticsCompanyEntity = logisticsCompanyDao.selectOne(entity);
        if (null != logisticsCompanyEntity) {
            throw new GlobalException(ExpressageExceptionEnum.NAME_IS_REPEATED);
        }
        logisticsCompanyEntity = new LogisticsCompanyEntity();
        BeanCopyUtil.copy(logisticsCompanyDto, logisticsCompanyEntity);
        logisticsCompanyEntity.setLastModifiedTime(new Date());
        logisticsCompanyDao.insert(logisticsCompanyEntity);
    }

    @Override
    public void edit(LogisticsCompanyDto logisticsCompanyDto) {
        EntityWrapper<LogisticsCompanyEntity> condition = new EntityWrapper<>();
        condition.ne("id", logisticsCompanyDto.getId());
        condition.eq("comName", logisticsCompanyDto.getComName());
        List<LogisticsCompanyEntity> logisticsCompanyEntities = logisticsCompanyDao.selectList(condition);
        if (!CollectionUtils.isEmpty(logisticsCompanyEntities)) {
            throw new GlobalException(ExpressageExceptionEnum.NAME_IS_REPEATED);
        }

        LogisticsCompanyEntity entity = logisticsCompanyDao.selectById(logisticsCompanyDto.getId());
        BeanCopyUtil.copy(logisticsCompanyDto, entity);
        entity.setLastModifiedTime(new Date());
        logisticsCompanyDao.updateById(entity);

    }

    @Override
    public void remove(List<Long> ids) {
        EntityWrapper<LogisticsCompanyEntity> condition = new EntityWrapper<>();
        condition.in("id", ids);

        LogisticsCompanyEntity entity = new LogisticsCompanyEntity();
        entity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        logisticsCompanyDao.update(entity, condition);
    }

    @Override
    public void editStatus(List<Long> ids, Integer status) {
        EntityWrapper<LogisticsCompanyEntity> condition = new EntityWrapper<>();
        condition.in("id", ids);

        LogisticsCompanyEntity entity = new LogisticsCompanyEntity();
        entity.setStatus(status); //启用，禁用
        logisticsCompanyDao.update(entity, condition);
    }

    @Override
    public LogisticsDto getLogisticsByCode(String expressCompanyCode) {
        LogisticsCompanyEntity conditionEntity = new LogisticsCompanyEntity();
        conditionEntity.cleanInit();
        conditionEntity.setCompany(expressCompanyCode);
        conditionEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);

        LogisticsCompanyEntity logisticsCompanyEntity = logisticsCompanyDao.selectOne(conditionEntity);
        if(null == logisticsCompanyEntity){
            return null;
        }
        LogisticsDto dto = new LogisticsDto();
        dto.setComName(logisticsCompanyEntity.getComName());
        dto.setId(logisticsCompanyEntity.getId());
        dto.setExpressCompanyCode(logisticsCompanyEntity.getCompany());
        return dto;
    }

    @Override
    public String saveExpressageInfo(String param) {
        NoticeResponse resp = new NoticeResponse();
        try {
            NoticeRequest nReq = JacksonHelper.fromJSON(param, NoticeRequest.class);
            Result result = nReq.getLastResult();
            // 处理快递结果
            ExpressageInfoEntity entity = this.convertInfo(result);
            if(null != entity){
                expressageInfoDao.insert(entity);
            }
            resp.setResult(true);
            resp.setMessage("保存成功");
            resp.setReturnCode("200");
            return JacksonHelper.toJSON(resp); //这里必须返回，否则认为失败，过30分钟又会重复推送。
        } catch (Exception e) {
            resp.setResult(false);
            resp.setReturnCode("500");
            resp.setMessage("保存失败");
            resp.setMessage("保存失败" + e.getMessage());
            return JacksonHelper.toJSON(resp);//保存失败，服务端等30分钟会重复推送。
        }
    }

    @Override
    public boolean subscriptionExpressage(ExpressageParamDto expressageParamDto) {
        if(StringUtils.isBlank(expressageParamDto.getNumber())){
            throw new GlobalException(ExpressageExceptionEnum.NUMBER_IS_NULL);
        }
        if(expressageParamDto.getId() == null){
            throw new GlobalException(ExpressageExceptionEnum.ID_IS_NULL);
        }
        LogisticsCompanyEntity logisticsCompanyEntity = logisticsCompanyDao.selectById(expressageParamDto.getId());
        if(null == logisticsCompanyEntity){
            throw new GlobalException(ExpressageExceptionEnum.ID_IS_ERROR);
        }
        //公司编码
        String com = logisticsCompanyEntity.getCompany();
        //拼装请求参数
        TaskRequest req = new TaskRequest();
        req.setCompany(com);
        req.setNumber(expressageParamDto.getNumber());
        req.setKey(key_id);
        req.getParameters().put("callbackurl", baseUrl + "/transport/expressage/addExpressageInfo");
        log.info("快递100推送回调接口地址：{}", baseUrl + "/transport/expressage/addExpressageInfo");
        HashMap<String, String> p = new HashMap<String, String>();
        p.put("schema", "json");
        p.put("param", JacksonHelper.toJSON(req));
        //订阅快递
        log.info("快递100推送回调接口参数：{}", JacksonHelper.toJSON(req));
        return this.subscription(p, com, expressageParamDto.getNumber());
    }

    //订阅快递
    private boolean subscription(HashMap<String, String> param, String com, String number) {
        try {
            //httpclent 调用快递100的接口
            String ret = HttpRequest.postData("http://www.kuaidi100.com/poll", param, "UTF-8");
            TaskResponse resp = JacksonHelper.fromJSON(ret, TaskResponse.class);
            this.saveExpressageSubscriptionLog(resp, com, number);
            return resp.getResult();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ExpressageDto getExpressInfo(ExpressageParamDto expressageParamDto) {
        //是否传了快递编号
        if(StringUtils.isBlank(expressageParamDto.getNumber())){
            throw new GlobalException(ExpressageExceptionEnum.NUMBER_IS_NULL);
        }
        //用编号去快递信息表里查询快递信息，取最新的
        EntityWrapper<ExpressageInfoEntity> condition = new EntityWrapper<>();
        condition.eq("nu", expressageParamDto.getNumber());
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condition.orderBy("lastModifiedTime", false);
        List<ExpressageInfoEntity> expressageInfoEntities = expressageInfoDao.selectList(condition);
        if(!CollectionUtils.isEmpty(expressageInfoEntities)){
            //取出最近一条
            ExpressageInfoEntity expressageInfoEntity = expressageInfoEntities.get(0);
            return this.convertExpressage(expressageInfoEntity);
        }

        if(expressageParamDto.getId() == null){
            throw new GlobalException(ExpressageExceptionEnum.ID_IS_NULL);
        }

        LogisticsCompanyEntity logisticsCompanyEntity = logisticsCompanyDao.selectById(expressageParamDto.getId());
        if(null == logisticsCompanyEntity){
            throw new GlobalException(ExpressageExceptionEnum.ID_IS_ERROR);
        }
        String com = logisticsCompanyEntity.getCompany();
        return this.queryExpressInfo(com, expressageParamDto.getNumber());
    }

    @Override
    public LogisticsDto getLogistics(Long id) {
        LogisticsCompanyEntity logisticsCompanyEntity = logisticsCompanyDao.selectById(id);
        if(logisticsCompanyEntity == null){
            return null;
        }else{
            LogisticsDto dto = new LogisticsDto();
            dto.setComName(logisticsCompanyEntity.getComName());
            dto.setId(logisticsCompanyEntity.getId());
            return dto;
        }
    }

    //记录订阅记录
    private void saveExpressageSubscriptionLog(TaskResponse resp, String com, String number) {
        ExpressageSubscriptionLogEntity entity = new ExpressageSubscriptionLogEntity();
        entity.setCom(com);
        entity.setNu(number);
        entity.setIsSuccess(resp.getResult() ? "1" : "0");//1成功 0失败
        entity.setReturnCode(resp.getReturnCode());
        entity.setLastModifiedTime(new Date());
        expressageSubscriptionLogDao.insert(entity);
    }

    //实时查询快递
    private ExpressageDto queryExpressInfo(String com, String num){
        try {
            String param ="{\"com\":\""+com+"\",\"num\":\""+num+"\"}";
            String sign = MD5.encode(param + key_id + customer);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("param",param);
            params.put("sign",sign);
            params.put("customer",customer);
            String resp = new HttpRequest().postData("http://poll.kuaidi100.com/poll/query.do", params, "utf-8").toString();
            Result result = JacksonHelper.fromJSON(resp, Result.class);
            ExpressageInfoEntity expressageInfoEntity = this.convertInfo(result);
            if(expressageInfoEntity == null) {
                log.info("实时查询物流接口，没有查询到信息：物流公司为:{},单号为：{}" + com, num);
                return null;
            }
            //查询到直接返回，不在放入数据库。
            //expressageInfoDao.insert(expressageInfoEntity);
            return this.convertExpressage(expressageInfoEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //result转entity
    private ExpressageInfoEntity convertInfo(Result result){
        //取出结果
        ArrayList<ResultItem> dataArray = result.getData();
        if(CollectionUtils.isEmpty(dataArray)){
            return null;
        }
        // 处理快递结果
        ExpressageInfoEntity entity = new ExpressageInfoEntity();
        //拷贝到entity
        BeanUtils.copyProperties(result, entity);
        StringBuffer sb = new StringBuffer();
        for (ResultItem resultItem : dataArray) {
            sb.append(resultItem.getFtime());
            sb.append("#");
            sb.append(resultItem.getContext());
            sb.append("$");
        }
        //讲data放入entity
        entity.setData(sb.substring(0, sb.length() - 1));
        entity.setLastModifiedTime(new Date());
        return entity;
    }

    //eneity转成dto
    private ExpressageDto convertExpressage(ExpressageInfoEntity expressageInfoEntity){
        //查出快递名称
        LogisticsCompanyEntity comEntity = new LogisticsCompanyEntity();
        comEntity.cleanInit();
        comEntity.setCompany(expressageInfoEntity.getCom());
        LogisticsCompanyEntity logisticsCompanyEntity = logisticsCompanyDao.selectOne(comEntity);
        //返回结果
        ExpressageDto expressageDto = new ExpressageDto();
        expressageDto.setComName(logisticsCompanyEntity.getComName());
        BeanCopyUtil.copy(expressageInfoEntity, expressageDto);
        //设置返回的信息
        String dataStr = expressageInfoEntity.getData();
        String[] dataArray = dataStr.split("[$]");
        expressageDto.setDataList(Arrays.asList(dataArray));
        return expressageDto;
    }

}
