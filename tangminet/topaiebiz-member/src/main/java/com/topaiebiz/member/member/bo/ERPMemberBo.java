package com.topaiebiz.member.member.bo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class ERPMemberBo {
	 /**
     * member_id
     * 会员id
     */
	@JSONField(name = "MemberID")
    private Integer memberId;

    /**
     * member_name
     * 会员名称
     */
	@JSONField(name = "CustomerCode")
    private String memberName;

    /**
     * member_truename
     * 真实姓名
     */
	@JSONField(name = "CustomerName")
    private String memberTruename;

    /**
     * member_sex 1、男，2、女
     * 会员性别
     */
    @JSONField(name = "Sex")
    private Integer memberSex;

    /**
     * member_birthday
     * 生日
     */
    @JSONField(name = "Birthday", format = "yyyy-MM-dd")
    private Date memberBirthday;

    /**
     * member_passwd
     * 会员密码
     */
    @JSONField(name = "CustomerPWD")
    private String memberPasswd;

    /**
     * member_email
     * 会员邮箱
     */
    @JSONField(name = "Email")
    private String memberEmail;


    /**
     * member_mobile
     * 手机号
     */
    @JSONField(name = "Mobile")
    private String memberMobile;


    /**
     * member_qq
     * qq
     */
    @JSONField(name = "QQ")
    private String memberQq;

    /**
     * member_ww
     * 阿里旺旺
     */
    @JSONField(name = "WW")
    private String memberWw;

    /**
     * member_exppoints
     * 会员经验值
     */
    @JSONField(name = "ExpPoint")
    private Integer memberExppoints;


    /**
     * address
     * 地址
     */
    @JSONField(name = "Address")
    private String address;

    /**
     * weixin_info
     * 微信用户相关信息
     */
//    @JSONField(name = "WeiXinLoginInfo")
//    private String weixinInfo;

    /**
     * member_points
     * 会员积分
     */
    @JSONField(name = "AdjIntegral")
    private Integer memberPoints;
   
    
    /**
     * bb_birthday
     * 宝宝生日
     */
//    @JSONField(name = "BabyBirthday", format = "yyyy-MM-dd")
//    private Date bbBirthday;

    /**
     * bb_relation
     * 与宝宝关系
     */
    @JSONField(name = "Relationship")
    private Integer bbRelation;

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberTruename() {
		return memberTruename;
	}

	public void setMemberTruename(String memberTruename) {
		this.memberTruename = memberTruename;
	}

	public Integer getMemberSex() {
		return memberSex;
	}

	public void setMemberSex(Integer memberSex) {
		this.memberSex = memberSex;
	}

	public Date getMemberBirthday() {
		return memberBirthday;
	}

	public void setMemberBirthday(Date memberBirthday) {
		this.memberBirthday = memberBirthday;
	}

	public String getMemberPasswd() {
		return memberPasswd;
	}

	public void setMemberPasswd(String memberPasswd) {
		this.memberPasswd = memberPasswd;
	}

	public String getMemberEmail() {
		return memberEmail;
	}

	public void setMemberEmail(String memberEmail) {
		this.memberEmail = memberEmail;
	}

	public String getMemberMobile() {
		return memberMobile;
	}

	public void setMemberMobile(String memberMobile) {
		this.memberMobile = memberMobile;
	}

	public String getMemberQq() {
		return memberQq;
	}

	public void setMemberQq(String memberQq) {
		this.memberQq = memberQq;
	}

	public String getMemberWw() {
		return memberWw;
	}

	public void setMemberWw(String memberWw) {
		this.memberWw = memberWw;
	}

	public Integer getMemberExppoints() {
		return memberExppoints;
	}

	public void setMemberExppoints(Integer memberExppoints) {
		this.memberExppoints = memberExppoints;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

//	public String getWeixinInfo() {
//		return weixinInfo;
//	}
//
//	public void setWeixinInfo(String weixinInfo) {
//		this.weixinInfo = weixinInfo;
//	}

	public Integer getMemberPoints() {
		return memberPoints;
	}

	public void setMemberPoints(Integer memberPoints) {
		this.memberPoints = memberPoints;
	}

//	public Date getBbBirthday() {
//		return bbBirthday;
//	}
//
//	public void setBbBirthday(Date bbBirthday) {
//		this.bbBirthday = bbBirthday;
//	}

	public Integer getBbRelation() {
		return bbRelation;
	}

	public void setBbRelation(Integer bbRelation) {
		this.bbRelation = bbRelation;
	}
    
}
