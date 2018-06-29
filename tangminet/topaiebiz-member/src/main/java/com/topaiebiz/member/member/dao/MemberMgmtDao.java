package com.topaiebiz.member.member.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.member.dto.member.MemberMgmtDto;
import com.topaiebiz.member.dto.member.MemberStatisticsDto;
import com.topaiebiz.member.member.entity.MemberEntity;
import com.topaiebiz.member.member.entity.MemberStoreBindLogEntity;
import com.topaiebiz.member.po.MemberFilterPo;
import com.topaiebiz.member.reserve.entity.MemberTypeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description： 描述的会员管理接口，并向会员管理控制层提供相关的方法。
 * <p>
 * Author Scott.Yang
 * <p>
 * Date 2017年11月28日 下午1:37:49
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Mapper
public interface MemberMgmtDao extends BaseDao<MemberEntity> {
    /**
     * Description：  会员等级分页加列表加查询
     * <p>
     * Author Scott.Yang
     *
     * @param page           分页参数
     * @param memberFilterPo 会员信息Dto
     * @return
     */
    List<MemberMgmtDto> selectMemberMgmt(Page<MemberMgmtDto> page, MemberFilterPo memberFilterPo);

    /**
     * Description：获取会员类型列表
     * <p>
     * Author Scott.Yang
     *
     * @return
     */
    List<MemberTypeEntity> selectMemberTypeList();

    /**
     * Description：对会员编号进行重复验证   （memberCode 字段已经删除）
     * <p>
     * Author Scott.Yang
     *
     * @param memberCode 会员编号
     * @return
     */
    @Deprecated
    MemberEntity selectMemberByCode(String memberCode);

    /**
     * Description： 对会员用户名进行是否冻结验证
     * <p>
     * Author Scott.Yang
     *
     * @param userName 会员用户名
     * @return
     */
    @Deprecated
    MemberEntity selectMemberByuserName(String userName);

    /**
     * Description： 根据id删除会员信息(修改状态)
     * <p>
     * Author Scott.Yang
     *
     * @param ids 会员编号ID
     */
    void deleteMemberMgmts(Long[] ids);

    /**
     * Description： 根据id冻结会员状态
     * <p>
     * Author Scott.Yang
     *
     * @param id 会员编号ID
     */
    @Deprecated
    void congelationMemberMgmt(Long id);

    /**
     * Description：添加会员与店铺绑定记录
     * <p>
     * Author Scott.Yang
     *
     * @param memberStoreBindLogEntity
     * @return
     */
    Integer insertMemberStoreBindLogEntity(MemberStoreBindLogEntity memberStoreBindLogEntity);

    /**
     * Description： 根据手机号查询用户对象信息是否冻结
     * <p>
     * Author Scott.Yang
     * 根据手机号查询会员是否冻结验证
     *
     * @param telephone
     * @return
     */
    @Deprecated
    MemberEntity selectMemberByTelephone(String telephone);

    /**
     * Description： 会员管理分页加列表加查询(商家端)
     * <p>
     * Author Scott.Yang
     *
     * @param page
     * @param memberDto
     * @return
     */
    List<MemberDto> selectMemberMgmtForSeller(Page<MemberDto> page, MemberDto memberDto);

    /**
     * Description： 根据手机号查询用户对象信息
     * <p>
     * Author Scott.Yang
     *
     * @param telephone
     * @return
     */
    @Deprecated
    MemberEntity getMemberByuserTelephone(String telephone);

    /**
     * Description： 根据id解除冻结会员状态
     * <p>
     * Author Scott.Yang
     *
     * @param id 会员编号ID
     */
    @Deprecated
    void relieveMemberMgmt(Long id);

    /**
     * Description：查询{beforDate-afterDate}之间的新注册的会员ID集合
     * <p>
     * Author hxpeng
     *
     * @param beforDate
     * @param afterDate
     * @return
     */
    List<Long> getNewRegisterByTimeFrame(@Param(value = "beforDate") String beforDate, @Param(value = "afterDate") String afterDate);

    /**
     * Description： 会员增值情况视图(按年展示)
     * <p>
     * Author Scott.Yang
     *
     * @param memberStatisticsDto
     * @return
     */
    List<MemberStatisticsDto> selectMemberRecordByYear(MemberStatisticsDto memberStatisticsDto);

    /**
     * Description： 会员增值情况视图(按月展示)
     * <p>
     * Author Scott.Yang
     *
     * @param memberStatisticsDto
     * @return
     */
    List<MemberStatisticsDto> selectMemberRecordByMonths(MemberStatisticsDto memberStatisticsDto);

    /**
     * Description： 会员增值情况视图(按年展示商家端)
     * <p>
     * Author Scott.Yang
     *
     * @param memberStatisticsDto
     * @return
     */
    List<MemberStatisticsDto> selectMemberRecordByYearOfBusiness(MemberStatisticsDto memberStatisticsDto);

    /**
     * Description： 会员增值情况视图(按月展示商家端)
     * <p>
     * Author Scott.Yang
     *
     * @param memberStatisticsDto
     * @return
     */
    List<MemberStatisticsDto> selectMemberRecordByMonthsOfBusiness(MemberStatisticsDto memberStatisticsDto);

    /**
     * Description：根据手机号或用户名查询会员
     * <p>
     * Author: Anthony
     *
     * @param nameOrMobile
     * @return
     */
    MemberEntity getMemberByMobileOrName(String nameOrMobile);

    /**
     * Description： 根据id查询会员可用积分
     * <p>
     * Author Scott.Yang
     *
     * @param memberId
     * @return
     */
    @Deprecated
    Long selectMemberMgmtByIdSum(Long memberId);

    /**
     * Description： 根据商家ID查询会员 取最早的一条(该表已经 删除 merchantId)
     * <p>
     * Author Scott.Yang
     *
     * @param merchantId
     * @return
     */
    @Deprecated
    MemberEntity selectMemberByMerchantId(Long merchantId);


} 
