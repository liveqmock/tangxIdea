package com.topaiebiz.member.point.crm.ws;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
//XML文件中的根标识  
@XmlRootElement(name = "Contact")
//控制JAXB 绑定类中属性和字段的排序  
@XmlType(propOrder = {
     "BusinessType",   
     "HasBoughtMilkFlag",   
     "MotivatePointsBalance",   
     "MemberId",   
     "FeedType", 
     "LastUseBrand",   
     "LastUseProduct",   
     "OpinionLeaderFlag",   
     "SourceCode",
     "SubSource",
     "MedicalRepresentNo",
     "HospitalId",
     "EnrollBySelf",
     "ExternalInfo",
     "PrimaryHouseholdId",
     "ExtMemberNo",
     "RedeemPointsBalance",
     "MemberStartDate",
     "ContinuallyUse",
     "ContactId",
     "BirthDate",
     "CellularPhone",
     "ContactName",
     "Gender",
     "PersonalCountry",
     "PersonalAddress",
     "PersonalState",
     "PersonalCity",
     "RegisterSource",
     "SellerTerminalCode",
     "ShoppingGuideNo",
     "Status"
})  
public class CrmContact implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7205982562380319165L;
	
	
	private String BusinessType;
	private String HasBoughtMilkFlag;
	private Double MotivatePointsBalance;
	private String MemberId;
	private String FeedType;
	     
	private String LastUseBrand;
	     private String LastUseProduct;
	     private String OpinionLeaderFlag;
	     private String SourceCode;
	     private String SubSource;
	     private String MedicalRepresentNo;
	     private String HospitalId;
	     private String EnrollBySelf;
	     private String ExternalInfo;
	     private String PrimaryHouseholdId;
	     private String ExtMemberNo;
	     private Double RedeemPointsBalance;
	     private String MemberStartDate;
	     private String ContinuallyUse;
	     private String ContactId;
	     private String BirthDate;
	     private String CellularPhone;
	     private String ContactName;
	     private String Gender;
	     private String PersonalCountry;
	     private String PersonalAddress;
	     private String PersonalState;
	     private String PersonalCity;
	     private String RegisterSource;
	     private String SellerTerminalCode;
	     private String ShoppingGuideNo;
	     private String Status;
		public String getBusinessType() {
			return BusinessType;
		}
		public void setBusinessType(String businessType) {
			BusinessType = businessType;
		}
		public String getHasBoughtMilkFlag() {
			return HasBoughtMilkFlag;
		}
		public void setHasBoughtMilkFlag(String hasBoughtMilkFlag) {
			HasBoughtMilkFlag = hasBoughtMilkFlag;
		}
		public Double getMotivatePointsBalance() {
			return MotivatePointsBalance;
		}
		public void setMotivatePointsBalance(Double motivatePointsBalance) {
			MotivatePointsBalance = motivatePointsBalance;
		}
		public String getMemberId() {
			return MemberId;
		}
		public void setMemberId(String memberId) {
			MemberId = memberId;
		}
		public String getFeedType() {
			return FeedType;
		}
		public void setFeedType(String feedType) {
			FeedType = feedType;
		}
		public String getLastUseBrand() {
			return LastUseBrand;
		}
		public void setLastUseBrand(String lastUseBrand) {
			LastUseBrand = lastUseBrand;
		}
		public String getLastUseProduct() {
			return LastUseProduct;
		}
		public void setLastUseProduct(String lastUseProduct) {
			LastUseProduct = lastUseProduct;
		}
		public String getOpinionLeaderFlag() {
			return OpinionLeaderFlag;
		}
		public void setOpinionLeaderFlag(String opinionLeaderFlag) {
			OpinionLeaderFlag = opinionLeaderFlag;
		}
		public String getSourceCode() {
			return SourceCode;
		}
		public void setSourceCode(String sourceCode) {
			SourceCode = sourceCode;
		}
		public String getSubSource() {
			return SubSource;
		}
		public void setSubSource(String subSource) {
			SubSource = subSource;
		}
		public String getMedicalRepresentNo() {
			return MedicalRepresentNo;
		}
		public void setMedicalRepresentNo(String medicalRepresentNo) {
			MedicalRepresentNo = medicalRepresentNo;
		}
		public String getHospitalId() {
			return HospitalId;
		}
		public void setHospitalId(String hospitalId) {
			HospitalId = hospitalId;
		}
		public String getEnrollBySelf() {
			return EnrollBySelf;
		}
		public void setEnrollBySelf(String enrollBySelf) {
			EnrollBySelf = enrollBySelf;
		}
		public String getExternalInfo() {
			return ExternalInfo;
		}
		public void setExternalInfo(String externalInfo) {
			ExternalInfo = externalInfo;
		}
		public String getPrimaryHouseholdId() {
			return PrimaryHouseholdId;
		}
		public void setPrimaryHouseholdId(String primaryHouseholdId) {
			PrimaryHouseholdId = primaryHouseholdId;
		}
		public String getExtMemberNo() {
			return ExtMemberNo;
		}
		public void setExtMemberNo(String extMemberNo) {
			ExtMemberNo = extMemberNo;
		}
		public Double getRedeemPointsBalance() {
			return RedeemPointsBalance;
		}
		public void setRedeemPointsBalance(Double redeemPointsBalance) {
			RedeemPointsBalance = redeemPointsBalance;
		}
		public String getMemberStartDate() {
			return MemberStartDate;
		}
		public void setMemberStartDate(String memberStartDate) {
			MemberStartDate = memberStartDate;
		}
		public String getContinuallyUse() {
			return ContinuallyUse;
		}
		public void setContinuallyUse(String continuallyUse) {
			ContinuallyUse = continuallyUse;
		}
		public String getContactId() {
			return ContactId;
		}
		public void setContactId(String contactId) {
			ContactId = contactId;
		}
		public String getBirthDate() {
			return BirthDate;
		}
		public void setBirthDate(String birthDate) {
			BirthDate = birthDate;
		}
		public String getCellularPhone() {
			return CellularPhone;
		}
		public void setCellularPhone(String cellularPhone) {
			CellularPhone = cellularPhone;
		}
		public String getContactName() {
			return ContactName;
		}
		public void setContactName(String contactName) {
			ContactName = contactName;
		}
		public String getGender() {
			return Gender;
		}
		public void setGender(String gender) {
			Gender = gender;
		}
		public String getPersonalCountry() {
			return PersonalCountry;
		}
		public void setPersonalCountry(String personalCountry) {
			PersonalCountry = personalCountry;
		}
		public String getPersonalAddress() {
			return PersonalAddress;
		}
		public void setPersonalAddress(String personalAddress) {
			PersonalAddress = personalAddress;
		}
		public String getPersonalState() {
			return PersonalState;
		}
		public void setPersonalState(String personalState) {
			PersonalState = personalState;
		}
		
		
		
		public String getPersonalCity() {
			return PersonalCity;
		}
		public void setPersonalCity(String personalCity) {
			PersonalCity = personalCity;
		}
		public String getRegisterSource() {
			return RegisterSource;
		}
		public void setRegisterSource(String registerSource) {
			RegisterSource = registerSource;
		}
		public String getSellerTerminalCode() {
			return SellerTerminalCode;
		}
		public void setSellerTerminalCode(String sellerTerminalCode) {
			SellerTerminalCode = sellerTerminalCode;
		}
		public String getShoppingGuideNo() {
			return ShoppingGuideNo;
		}
		public void setShoppingGuideNo(String shoppingGuideNo) {
			ShoppingGuideNo = shoppingGuideNo;
		}
		public String getStatus() {
			return Status;
		}
		public void setStatus(String status) {
			Status = status;
		}
		
	

}
