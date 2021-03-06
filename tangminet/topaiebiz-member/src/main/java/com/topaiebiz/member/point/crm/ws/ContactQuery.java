
package com.topaiebiz.member.point.crm.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Holder;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "ContactQuery", targetNamespace = "http://siebel.com/asi/BYM")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ContactQuery {


    /**
     * 
     * @param errDesc
     * @param sortSpec
     * @param lastPage
     * @param startRowNum
     * @param errCode
     * @param pageSize
     * @param outXml
     * @param source
     * @param viewMode
     * @param searchExpr
     */
    @WebMethod(operationName = "ContactQuery", action = "rpc/http://siebel.com/asi/BYM:ContactQuery")
    public void contactQuery(
            @WebParam(name = "PageSize", partName = "PageSize")
                    String pageSize,
            @WebParam(name = "SortSpec", partName = "SortSpec")
                    String sortSpec,
            @WebParam(name = "ViewMode", partName = "ViewMode")
                    String viewMode,
            @WebParam(name = "Source", partName = "Source")
                    String source,
            @WebParam(name = "StartRowNum", partName = "StartRowNum")
                    String startRowNum,
            @WebParam(name = "SearchExpr", partName = "SearchExpr")
                    String searchExpr,
            @WebParam(name = "ErrCode", mode = WebParam.Mode.OUT, partName = "ErrCode")
                    Holder<String> errCode,
            @WebParam(name = "ErrDesc", mode = WebParam.Mode.OUT, partName = "ErrDesc")
                    Holder<String> errDesc,
            @WebParam(name = "LastPage", mode = WebParam.Mode.OUT, partName = "LastPage")
                    Holder<String> lastPage,
            @WebParam(name = "OutXml", mode = WebParam.Mode.OUT, partName = "OutXml")
                    Holder<String> outXml);

}
