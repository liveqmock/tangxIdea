package com.topaiebiz.giftcard.controller.mis;

import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.exception.SystemExceptionEnum;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.card.constant.ApplyScopeEnum;
import com.topaiebiz.giftcard.controller.AbstractController;
import com.topaiebiz.giftcard.dao.GiftcardUnitDao;
import com.topaiebiz.giftcard.entity.GiftcardBatch;
import com.topaiebiz.giftcard.entity.GiftcardOpLog;
import com.topaiebiz.giftcard.enums.*;
import com.topaiebiz.giftcard.service.GiftcardBatchService;
import com.topaiebiz.giftcard.service.GiftcardLabelService;
import com.topaiebiz.giftcard.service.GiftcardOpLogService;
import com.topaiebiz.giftcard.service.GiftcardUnitService;
import com.topaiebiz.giftcard.util.BizSerialUtil;
import com.topaiebiz.giftcard.util.ExportUtil;
import com.topaiebiz.giftcard.vo.*;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

/**
 * @description: 礼卡批次
 * @author: Jeff Chen
 * @date: created in 上午9:17 2018/1/16
 */
@RestController
@RequestMapping(value = "/giftcard/batch")
public class GiftcardBatchController extends AbstractController {

    @Autowired
    private GiftcardBatchService giftcardBatchService;

    @Autowired
    private GiftcardUnitService giftcardUnitService;
    @Autowired
    private GiftcardOpLogService giftcardOpLogService;
    /**
     * 添加
     * @param giftcardIssueVO
     * @param result
     * @return
     */
    @RequestMapping("/add")
    @PermissionController(value = PermitType.PLATFORM ,operationName = "添加")
    @Deprecated
    public ResponseInfo add(@Valid @RequestBody GiftcardIssueVO giftcardIssueVO, BindingResult result) {
        ResponseInfo responseInfo = validParam(result);
        //个别参数校验
        StringBuffer sb = new StringBuffer();
        if (giftcardIssueVO.getCardAttr().equals(CardAttrEnum.COMMON.getId())) {
            if (giftcardIssueVO.getLabelId() == null) {
                sb.append("标签不能为空").append(",");
            }
        }
        if (ApplyScopeEnum.APPLY_EXCLUDE.getScopeId().equals(giftcardIssueVO.getApplyScope())
                || ApplyScopeEnum.APPLY_INCLUDE.getScopeId().equals(giftcardIssueVO.getApplyScope())) {
            if (StringUtils.isEmpty(giftcardIssueVO.getStoreIds())) {
                sb.append("支持或不支持的店铺不能为空").append(",");
            }
        }
        if (ApplyScopeEnum.APPLY_GOODS.getScopeId().equals(giftcardIssueVO.getApplyScope())) {
            if (CollectionUtils.isEmpty(giftcardIssueVO.getGoodsIds())) {
                sb.append("请圈定至少一个商品").append(",");
            }
        }
        if (sb.length() > 0) {
            return new ResponseInfo(SystemExceptionEnum.ILLEGAL_PARAM.getCode(),sb.toString());
        }
        if (null != responseInfo) {
            return responseInfo;
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        GiftcardBatch giftcardBatch = new GiftcardBatch();
        BeanCopyUtil.copy(giftcardIssueVO, giftcardBatch);
        giftcardBatch.setCreator(currentUserDto.getUsername());
        giftcardBatch.setCreatedTime(new Date());
        giftcardBatch.setModifier(currentUserDto.getUsername());
        giftcardBatch.setModifiedTime(new Date());
        return new ResponseInfo(giftcardBatchService.save(giftcardBatch));
    }
    /**
     * 保存草稿
     * @param giftcardIssueVO
     * @return
     */
    @RequestMapping("/saveDraft")
    @PermissionController(value = PermitType.PLATFORM ,operationName = "保存草稿")
    public ResponseInfo saveDraft(@RequestBody GiftcardIssueVO giftcardIssueVO) {
        if (StringUtils.isEmpty(giftcardIssueVO.getCardName())) {
            throw new GlobalException(GiftcardExceptionEnum.LEAST_BATCH_PARAM);
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        GiftcardBatch giftcardBatch = new GiftcardBatch();
        BeanCopyUtil.copy(giftcardIssueVO, giftcardBatch);
        giftcardBatch.setIssueStatus(IssueStatusEnum.DRAFT.getStatusId());
        if (null != giftcardIssueVO.getBatchId()) {
            giftcardBatch.setId(giftcardIssueVO.getBatchId());
        } else {
            giftcardBatch.setCreator(currentUserDto.getUsername());
            giftcardBatch.setCreatedTime(new Date());
        }
        giftcardBatch.setModifier(currentUserDto.getUsername());
        giftcardBatch.setModifiedTime(new Date());
        return new ResponseInfo(giftcardBatchService.saveOrUpd(giftcardBatch));
    }

    @RequestMapping("/commitAudit")
    @PermissionController(value = PermitType.PLATFORM ,operationName = "提交审核")
    public ResponseInfo commitAudit(@RequestBody @Valid GiftcardIssueVO giftcardIssueVO, BindingResult result) {
        ResponseInfo responseInfo = validParam(result);
        if (null != responseInfo) {
            return responseInfo;
        }
        //个别参数校验
        StringBuffer sb = new StringBuffer();
        if (giftcardIssueVO.getCardAttr().equals(CardAttrEnum.COMMON.getId())) {
            if (giftcardIssueVO.getLabelId() == null) {
                sb.append("标签不能为空").append(",");
            }
        }
        if (ApplyScopeEnum.APPLY_EXCLUDE.getScopeId().equals(giftcardIssueVO.getApplyScope())
                || ApplyScopeEnum.APPLY_INCLUDE.getScopeId().equals(giftcardIssueVO.getApplyScope())) {
            if (StringUtils.isEmpty(giftcardIssueVO.getStoreIds())) {
                sb.append("支持或不支持的店铺不能为空").append(",");
            }
        }
        if (ApplyScopeEnum.APPLY_GOODS.getScopeId().equals(giftcardIssueVO.getApplyScope())) {
            if (CollectionUtils.isEmpty(giftcardIssueVO.getGoodsIds())) {
                sb.append("请圈定至少一个商品").append(",");
            }
        }
        if (sb.length() > 0) {
            return new ResponseInfo(SystemExceptionEnum.ILLEGAL_PARAM.getCode(),sb.toString());
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        GiftcardBatch giftcardBatch = new GiftcardBatch();
        BeanCopyUtil.copy(giftcardIssueVO, giftcardBatch);
        giftcardBatch.setIssueStatus(IssueStatusEnum.AUDIT_WAIT.getStatusId());
        if (null != giftcardIssueVO.getBatchId()) {
            giftcardBatch.setId(giftcardIssueVO.getBatchId());
        } else {
            giftcardBatch.setCreator(currentUserDto.getUsername());
            giftcardBatch.setCreatedTime(new Date());
        }
        giftcardBatch.setModifier(currentUserDto.getUsername());
        giftcardBatch.setModifiedTime(new Date());
        return new ResponseInfo(giftcardBatchService.saveOrUpd(giftcardBatch));
    }

    /**
     * 查询
     * @param giftcardIssueReq
     * @return
     */
    @RequestMapping("/query")
    @PermissionController(value = PermitType.PLATFORM,operationName = "礼卡批次列表")
    public ResponseInfo query(@RequestBody GiftcardIssueReq giftcardIssueReq) {
        if (null == giftcardIssueReq || null == giftcardIssueReq.getMedium()) {
            throw new GlobalException(GiftcardExceptionEnum.SOLID_OR_ELECT);
        }
        return new ResponseInfo(giftcardBatchService.queryGiftcardIssue(giftcardIssueReq));
    }

    /**
     * 删除
     * @param batchId
     * @return
     */
    @RequestMapping("/delete/{batchId}")
    @PermissionController(value = PermitType.PLATFORM,operationName = "删除批次")
    public ResponseInfo delete(@PathVariable Long batchId) {
        if (null == batchId) {
            return paramError();
        }
        GiftcardBatch giftcardIssue = new GiftcardBatch();
        giftcardIssue.setId(batchId);
        giftcardIssue.setDelFlag(1);
        return new ResponseInfo(giftcardBatchService.updateById(giftcardIssue));
    }

    /**
     * 查看详情
     * @param batchId
     * @return
     */
    @RequestMapping("/detail/{batchId}")
    @PermissionController(value = PermitType.PLATFORM,operationName = "批次详情")
    public ResponseInfo detail(@PathVariable Long batchId) {
        if (null == batchId) {
            return paramError();
        }
        return new ResponseInfo(giftcardBatchService.getById(batchId));
    }

    /**
     * 编辑发行信息
     * @param giftcardIssueVO
     * @return
     */
    @RequestMapping("/edit")
    @PermissionController(value = PermitType.PLATFORM,operationName = "编辑")
    public ResponseInfo edit(@RequestBody GiftcardIssueVO giftcardIssueVO) {
        if (null == giftcardIssueVO || null == giftcardIssueVO.getBatchId()) {
            return paramError();
        }

        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        GiftcardBatch giftcardIssue = new GiftcardBatch();
        BeanCopyUtil.copy(giftcardIssueVO, giftcardIssue);
        giftcardIssue.setId(giftcardIssueVO.getBatchId());
        giftcardIssue.setModifiedTime(new Date());
        giftcardIssue.setModifier(currentUserDto.getUsername());
        return new ResponseInfo(giftcardBatchService.editGiftcardIssue(giftcardIssue));
    }

    /**
     * 修改礼卡批次状态：0-待审核，1-审核通过（未上架/未生产），2-未通过，3-已上架/未入库，4-已入库
     * @param issueAuditReq
     * @return
     */
    @RequestMapping("/handle")
    @PermissionController(value = PermitType.PLATFORM,operationName = "操作批次")
    public ResponseInfo handle(@Valid @RequestBody IssueAuditReq issueAuditReq,BindingResult result) {
        ResponseInfo responseInfo = validParam(result);
        if (null != responseInfo) {
            return responseInfo;
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        GiftcardBatch giftcardBatch = new GiftcardBatch();
        giftcardBatch.setId(issueAuditReq.getBatchId());
        giftcardBatch.setIssueStatus(issueAuditReq.getIssueStatus());
        giftcardBatch.setRemark(issueAuditReq.getNote());
        giftcardBatch.setModifier(currentUserDto.getUsername());
        giftcardBatch.setModifiedTime(new Date());
        return new ResponseInfo(giftcardBatchService.changeIssueStatus(giftcardBatch));
    }

    /**
     * 修改优先级
     * @param giftcardIssueVO
     * @return
     */
    @RequestMapping("/updatePriority")
    @PermissionController(value = PermitType.PLATFORM,operationName = "更新优先级")
    public ResponseInfo updatePriority(@RequestBody GiftcardIssueVO giftcardIssueVO) {
        if (null == giftcardIssueVO || null == giftcardIssueVO.getBatchId() || null == giftcardIssueVO.getPriority()) {
            return paramError();
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        GiftcardBatch giftcardIssue = new GiftcardBatch();
        giftcardIssue.setId(giftcardIssueVO.getBatchId());
        giftcardIssue.setPriority(giftcardIssueVO.getPriority());
        giftcardIssue.setModifier(currentUserDto.getUsername());
        giftcardIssue.setModifiedTime(new Date());
        return new ResponseInfo(giftcardBatchService.updatePriority(giftcardIssue));
    }

    @RequestMapping("/export/{batchId}")
    @PermissionController(value = PermitType.PLATFORM,operationName = "导出卡密")
    public ResponseInfo export(HttpServletResponse response, @PathVariable Long batchId) {
        List<GiftcardExportVO> giftcardExportVOList = giftcardUnitService.export(batchId);
        try {
            CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
            //操作日志
            GiftcardOpLog giftcardOpLog = new GiftcardOpLog();
            giftcardOpLog.setBizId(batchId);
            giftcardOpLog.setOpSrc(OpSrcEnum.OP_BATCH.getSrcId());
            giftcardOpLog.setOpType(CardOpTypeEnum.EXPORT_CARD.getOpType());
            giftcardOpLog.setOpTime(new Date());
            giftcardOpLog.setOperator(currentUserDto.getUsername());
            giftcardOpLogService.insert(giftcardOpLog);
            if (!CollectionUtils.isEmpty(giftcardExportVOList)) {
                List<Map<String, Object>> dataList = new ArrayList<>(giftcardExportVOList.size());
                giftcardExportVOList.forEach(giftcardExportVO -> {
                    Map<String, Object> data = new HashMap<>(2);
                    data.put("cardNo", giftcardExportVO.getCardNo()+"\t");
                    data.put("password", giftcardExportVO.getPassword()+"\t");
                    dataList.add(data);
                });
                ExportUtil.setRespProperties("卡密导出", response);
                ExportUtil.doExport(dataList, "卡号,密码", "cardNo,password", response.getOutputStream());
                return null;
            }
        } catch (Exception e) {
            logger.error("卡密导出异常",e);
        }

        return new ResponseInfo();
    }

    /**
     * 生产实体卡
     * @param batchId
     * @return
     */
    @RequestMapping("/produce/{batchId}")
    @PermissionController(value = PermitType.PLATFORM,operationName = "生产")
    public ResponseInfo produce(@PathVariable Long batchId) {
        if (null == batchId) {
            return paramError();
        }
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        GiftcardBatch giftcardIssue = new GiftcardBatch();
        giftcardIssue.setId(batchId);
        giftcardIssue.setCreator(currentUserDto.getUsername());
        giftcardIssue.setCreatedTime(new Date());
        giftcardIssue.setModifier(currentUserDto.getUsername());
        giftcardIssue.setModifiedTime(new Date());

        return new ResponseInfo(giftcardBatchService.produceSolidCards(giftcardIssue));
    }

    /**
     * 获取卡号跨度
     * @param giftcardIssueVO
     * @return
     */
    @RequestMapping("/span")
    public ResponseInfo cardNoSpan(@RequestBody GiftcardIssueVO giftcardIssueVO) {
        if (null == giftcardIssueVO || null == giftcardIssueVO.getPrefix() || null == giftcardIssueVO.getIssueNum()) {
            return paramError();
        }
        return new ResponseInfo(giftcardBatchService.getCardNoSpan(giftcardIssueVO.getPrefix(),giftcardIssueVO.getIssueNum()));
    }

    /**
     * test
     * @return
     */
    public ResponseInfo updateCover(Integer useType) {
        List<Long> memberIds = new ArrayList<>();
        memberIds.add(960418558400327682L);
        memberIds.add(951392514894483457L);
        memberIds.add(961076844040622081L);
        return new ResponseInfo(giftcardUnitService.getBalanceByMemberIds(memberIds,useType));
    }
}
