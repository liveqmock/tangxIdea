package com.topaiebiz.member.point.crm.ws;

import com.topaiebiz.member.point.utils.XmlParserTool;

import javax.xml.ws.Holder;

public class TestWebService {
	
	public static void  main(String[] arg){
		
		/*   String xml = "<?xml version='1.0' encoding='UTF-8'?>"
    		       + "<RedeemReq>"
    		       + "    	<MemberType>单个</MemberType>"
    		       + "    	<MemberId>1-F3N6WK8</MemberId>"
    		       + "    	<Channel>积分商城</Channel>"
    		       + "    	<RedeemType>邮寄兑换</RedeemType>"
    		       + "    	<UseJLPointsFlag>N</UseJLPointsFlag>"
    		       + "    	<PointType>购物积分</PointType>"
    		       + "    	<OrderToTxnFlag>Y</OrderToTxnFlag>"
    		       + "    	<Comments>OK</Comments>"
    		       + "    	<ProdList>"
    		       + "    		<ProdInfo>"
    		       + "    			<ProdNum>1-DKKCQGP</ProdNum>"
    		       + "                <ItemId>1</ItemId>"
    		       + "    			<Points>1</Points>"
    		       + "    			<Quantity>2</Quantity>"
    		       + "    			<OverWriteFlag>Y</OverWriteFlag>"
    		       + "    		</ProdInfo>"
    		       + "    	</ProdList>"
    		       + "   </RedeemReq>";
	

  
    Holder<String> errCode = new Holder<String>();
	Holder<String> errDesc = new Holder<String>();	
	Holder<String> outXml = new Holder<String>();
	Holder<String> redeemPointsBal= new Holder<String>();
	Holder<String> needPoints = new Holder<String>();
	CRMRedemptionWS cRMRedemptionWS =  new CRMRedemptionWS();
	 NewRedemptionOrder newRedemptionOrder = cRMRedemptionWS.getNewRedemptionOrder();
	 newRedemptionOrder.newRedemptionOrder(xml, "积分商城", errCode, errDesc, needPoints, outXml, redeemPointsBal);*/
		
		
		 Holder<String> errCode = new Holder<String>();
		 Holder<String> errDesc = new Holder<String>();
		 Holder<String> lastPage = new Holder<String>();
		 Holder<String> outXml = new Holder<String>();
		 CRMContactWS cRMContactWS = new CRMContactWS();		
		 ContactQuery contactQuery = cRMContactWS.getContactQuery();
		 //13798193091
		 contactQuery.contactQuery("10", "", "All", "积分商城", "0", "[Contact.BYM Contact Business Type] ='贝因美' AND [Contact.Status] ='活动' AND [Contact.BYM Baby Flag]='N' AND [Contact.Cellular Phone #]='18057116066'", errCode, errDesc, lastPage, outXml);
		
		
		 String result = outXml.value;
		
		 CrmContact crmContact = XmlParserTool.convertXmlStrToObject(CrmContact.class, result.replace("<ListOfContact>", "").replace("</ListOfContact>", ""));
		
		
	}

}
