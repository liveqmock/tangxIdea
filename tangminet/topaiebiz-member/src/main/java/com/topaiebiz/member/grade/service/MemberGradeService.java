package com.topaiebiz.member.grade.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.dto.grade.MemberGradeDto;
import com.topaiebiz.member.grade.entity.MemberGradeEntity;
import com.topaiebiz.member.grade.entity.MemberGradePrivilegeEntity;

import java.util.List;
import java.util.Map;

public interface MemberGradeService {
	
	/**
	 * Description： 添加会员等级   
	 * 
	 * Author Scott.Yang   
	 * 
	 * @param memberGrade
	 * 			会员等级Entity
	 * @return
	 * @throws GlobalException
	 */
	Integer saveMemberGrade(MemberGradeDto memberGrade) throws GlobalException;
	
	/**
	 * Description：  修改会员等级 
	 * 
	 * Author Scott.Yang   
	 * 
	 * @param memberGradeDto
	 * 			会员等级Entity
	 * @return
	 * @throws GlobalException
	 */
	Integer modifyMemberGrade(MemberGradeDto memberGradeDto) throws GlobalException;
	
	/**
	 * Description： 会员等级分页加列表加查询    
	 * 
	 * Author Scott.Yang   
	 * 
	 * @param pagePo
	 * 			分页参数
	 * @param memberGradeDto
	 * 			会员等级Dto
	 * @return
	 */
	Page<MemberGradeDto> listMemberGrade(PagePO pagePo, MemberGradeDto memberGradeDto);

	/**
	 * Description：  批量删除会员等级 (修改状态)  
	 * 
	 * Author Scott.Yang   
	 * 
	 * @param id
	 * 		会员等级编号
	 * @throws GlobalException
	 */
	void removeMemberGrades(Long[] id) throws GlobalException;
	/**
	 * Description： 会员等级配置特权   
	 * 
	 * Author Scott.Yang   
	 * 
	 * 		会员等级特权Entity
	 * @return
	 * @throws GlobalException
	 */
	Integer saveMemberGradePrivilege(MemberGradePrivilegeEntity memberGradePrivilegeDto) throws GlobalException;
	/**
	 * Description：根据id查询等级信息 
	 * 
	 * Author Scott.Yang   
	 * 
	 * @param id
	 * 		会员等级id
	 * @return
	 * @throws GlobalException 
	 */
	MemberGradeEntity findMemberGradesById(Long id) throws GlobalException;

	/**
	 * Description：  获取会员等级列表  
	 * 
	 * Author Scott.Yang   
	 * 
	 * @return
	 */
	List<MemberGradeEntity> getAllGrade();


    Map<Long, MemberGradeDto> getMemberGradeMap(List<Long> gradeIdList);

    MemberGradeDto getMemberGrade(Long gradeId);
}
