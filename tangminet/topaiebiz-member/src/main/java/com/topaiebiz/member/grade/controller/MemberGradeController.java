package com.topaiebiz.member.grade.controller;

import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.member.dto.grade.MemberGradeDto;
import com.topaiebiz.member.dto.grade.MemberGradePrivilegeDto;
import com.topaiebiz.member.exception.MemberExceptionEnum;
import com.topaiebiz.member.grade.entity.MemberGradeEntity;
import com.topaiebiz.member.grade.entity.MemberGradePrivilegeEntity;
import com.topaiebiz.member.grade.service.MemberGradeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Description：会员等级控制层。
 * <p>
 * Author Scott.Yang
 * <p>
 * Date 2017年9月23日 下午9:03:38
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping("/member/grade")
public class MemberGradeController {

    @Autowired
    private MemberGradeService memberGradeService;

    /**
     * Description：添加会员等级
     * <p>
     * Author Scott.Yang
     *
     * @param memberGradeDto 会员等级Dto
     * @param result         错误结果
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/saveMemberGrade", method = RequestMethod.POST)
    public ResponseInfo addMemberGrade(@Valid MemberGradeDto memberGradeDto, BindingResult result)
            throws GlobalException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        // 成功时，直接分发数据并调用业务逻辑方法，并返回响应信息对象。
        Integer saveInteger = memberGradeService.saveMemberGrade(memberGradeDto);
        return new ResponseInfo(saveInteger);
    }

    /**
     * Description：修改会员等级
     * <p>
     * Author Scott.Yang
     *
     * @param memberGradeDto 会员等级Dto
     * @param result         错误结果
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/updateMemberGrade", method = RequestMethod.POST)
    public ResponseInfo editMemberGrade(MemberGradeDto memberGradeDto, BindingResult result) throws GlobalException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        // 成功时，直接分发数据并调用业务逻辑方法，并返回响应信息对象。
        Integer updateInteger = memberGradeService.modifyMemberGrade(memberGradeDto);
        return new ResponseInfo(updateInteger);
    }

    /**
     * Description： 会员等级分页加列表加查询
     * <p>
     * Author Scott.Yang
     *
     * @param page           分页参数
     * @param memberGradeDto 会员等级Dto
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/getMemberGradeList")
    public ResponseInfo getMemberGradeList(PagePO page, MemberGradeDto memberGradeDto)
            throws GlobalException {
        //Page<MemberGradeDto> list = memberGradeService.getMemberGradeList(page, memberGradeDto);
        return new ResponseInfo(memberGradeService.listMemberGrade(page, memberGradeDto));
    }


    public ResponseInfo getAllGrade() {
        return new ResponseInfo(memberGradeService.getAllGrade());
    }


    /**
     * Description： 批量删除会员等级 (修改状态)
     * <p>
     * Author Scott.Yang
     *
     * @param gradeIds 会员等级编号
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/cancelMemberGrades", method = RequestMethod.POST)
    public ResponseInfo cancelMemberGrades(Long[] gradeIds) throws GlobalException {
        if (null == gradeIds) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_ID_NOT_NULL);
        }
        memberGradeService.removeMemberGrades(gradeIds);
        return new ResponseInfo();
    }

    /**
     * Description： 会员等级配置特权
     * <p>
     * Author Scott.Yang
     *
     * @param memberGradePrivilegeDto 会员等级权限Dto
     * @param result
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/addMemberGradePrivilege", method = RequestMethod.POST)
    public ResponseInfo addMemberGradePrivilege(@Valid MemberGradePrivilegeDto memberGradePrivilegeDto, BindingResult result)
            throws GlobalException {
        if (result.hasErrors()) {
            // 初始化非法参数的提示信息。
            IllegalParamValidationUtils.initIllegalParamMsg(result);
            // 获取非法参数异常信息对象，并抛出异常。
            throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
        }
        // 成功时，直接分发数据并调用业务逻辑方法，并返回响应信息对象。
        MemberGradePrivilegeEntity entity = new MemberGradePrivilegeEntity();
        BeanUtils.copyProperties(memberGradePrivilegeDto, entity);
        Integer saveInteger = memberGradeService.saveMemberGradePrivilege(entity);
        return new ResponseInfo(saveInteger);
    }

    /**
     * Description： 根据id查询等级信息
     * <p>
     * Author Scott.Yang
     *
     * @param id 会员等级编号ID
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/findMemberGradesById")
    @ResponseBody
    public ResponseInfo findMemberGradesById(Long id) throws GlobalException {
        MemberGradeDto memberGradeDto = new MemberGradeDto();
        if (null != id) {
            MemberGradeEntity memberGrade = memberGradeService.findMemberGradesById(id);
            BeanCopyUtil.copy(memberGrade, memberGradeDto);
        }
        return new ResponseInfo(memberGradeDto);
    }

    /**
     * Description： 获取会员的等级列表.
     * <p>
     * Author Scott.Yang
     *
     * @return
     * @throws GlobalException
     */

    @RequestMapping(path = "/all", method = RequestMethod.POST)
    public ResponseInfo getAllGrades() {
        List<MemberGradeEntity> memberGradeList = memberGradeService.getAllGrade();
        List<MemberGradeDto> memberGradeDtoList = new ArrayList<>();
        for (MemberGradeEntity memberGradeEntity : memberGradeList) {
            MemberGradeDto memberGradeDto = new MemberGradeDto();
            BeanCopyUtil.copy(memberGradeEntity, memberGradeDto);
            memberGradeDtoList.add(memberGradeDto);
        }
        return new ResponseInfo(memberGradeDtoList);
    }

}