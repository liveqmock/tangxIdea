
package com.topaiebiz.member.point.crm.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.ws.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "CRMContactWS", targetNamespace = "http://siebel.com/asi/BYM", wsdlLocation = "http://cardc.motherbuy.com/CRMContactWS.WSDL")
public class CRMContactWS extends Service {

	private static Logger LOG = LoggerFactory.getLogger(CRMContactWS.class);
	private final static URL CRMCONTACTWS_WSDL_LOCATION;
	private final static WebServiceException CRMCONTACTWS_EXCEPTION;
	private final static QName CRMCONTACTWS_QNAME = new QName("http://siebel.com/asi/BYM", "CRMContactWS");

	static {
		URL url = null;
		WebServiceException e = null;
		try {
			url = new URL("http://cardc.motherbuy.com/CRMContactWS.WSDL");
		} catch (MalformedURLException ex) {
			LOG.error(ex.getMessage(), ex);
			e = new WebServiceException(ex);
		}
		CRMCONTACTWS_WSDL_LOCATION = url;
		CRMCONTACTWS_EXCEPTION = e;
	}

	public CRMContactWS() {
		super(__getWsdlLocation(), CRMCONTACTWS_QNAME);
	}

	public CRMContactWS(WebServiceFeature... features) {
		super(__getWsdlLocation(), CRMCONTACTWS_QNAME, features);
	}

	public CRMContactWS(URL wsdlLocation) {
		super(wsdlLocation, CRMCONTACTWS_QNAME);
	}

	public CRMContactWS(URL wsdlLocation, WebServiceFeature... features) {
		super(wsdlLocation, CRMCONTACTWS_QNAME, features);
	}

	public CRMContactWS(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public CRMContactWS(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
		super(wsdlLocation, serviceName, features);
	}

	/**
	 * 
	 * @return returns ContactUpsert
	 */
	@WebEndpoint(name = "ContactUpsert")
	public ContactUpsert getContactUpsert() {
		return super.getPort(new QName("http://siebel.com/asi/BYM", "ContactUpsert"), ContactUpsert.class);
	}

	/**
	 * 
	 * @param features
	 *            A list of {@link javax.xml.ws.WebServiceFeature} to configure
	 *            on the proxy. Supported features not in the
	 *            <code>features</code> parameter will have their default
	 *            values.
	 * @return returns ContactUpsert
	 */
	@WebEndpoint(name = "ContactUpsert")
	public ContactUpsert getContactUpsert(WebServiceFeature... features) {
		return super.getPort(new QName("http://siebel.com/asi/BYM", "ContactUpsert"), ContactUpsert.class, features);
	}

	/**
	 *
	 * @return returns ContactQuery
	 */
	@WebEndpoint(name = "ContactQuery")
	public ContactQuery getContactQuery() {
		return super.getPort(new QName("http://siebel.com/asi/BYM", "ContactQuery"), ContactQuery.class);
	}

	/**
	 *
	 * @param features
	 *            A list of {@link javax.xml.ws.WebServiceFeature} to configure
	 *            on the proxy. Supported features not in the
	 *            <code>features</code> parameter will have their default
	 *            values.
	 * @return returns ContactQuery
	 */
	@WebEndpoint(name = "ContactQuery")
	public ContactQuery getContactQuery(WebServiceFeature... features) {
		return super.getPort(new QName("http://siebel.com/asi/BYM", "ContactQuery"), ContactQuery.class, features);
	}

	private static URL __getWsdlLocation() {
		if (CRMCONTACTWS_EXCEPTION != null) {
			throw CRMCONTACTWS_EXCEPTION;
		}
		return CRMCONTACTWS_WSDL_LOCATION;
	}

}
