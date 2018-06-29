package com.topaiebiz.member.point.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class XmlParserTool {
	
	  @SuppressWarnings("unchecked")
	  public static <T> T convertXmlStrToObject(Class<T> clazz, String xmlStr) {
	        T obj = null;  
	        try {  
	            JAXBContext context = JAXBContext.newInstance(clazz);
	            //将xml转成对象的核心接口  
	            Unmarshaller unmarshaller = context.createUnmarshaller();
	            StringReader sr = new StringReader(xmlStr);
	            obj = (T)unmarshaller.unmarshal(sr);  
	        } catch (JAXBException e) {
	            e.printStackTrace();  
	        }  
	        return obj;  
	    }  

}
