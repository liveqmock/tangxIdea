package com.nebulapaas.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import java.io.FileInputStream;
import java.util.*;
/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/16 15:58
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
public class XmlUtils {
    private static Map<String, Object> Dom2Map(Document doc) {
        Map<String, Object> map = new HashMap<>();
        if (doc == null)
            return map;
        Element root = doc.getRootElement();
        for (Iterator iterator = root.elementIterator(); iterator.hasNext(); ) {
            Element e = (Element) iterator.next();
            List list = e.elements();
            if (list.size() > 0) {
                map.put(e.getName(), Dom2Map(e));
            } else
                map.put(e.getName(), e.getText());
        }
        return map;
    }
    @SuppressWarnings("unchecked")
    private static Map Dom2Map(Element e) {
        Map map = new HashMap();
        List list = e.elements();
        if (list.size() > 0) {
            for (Object aList : list) {
                Element iter = (Element) aList;
                List mapList = new ArrayList();
                if (iter.elements().size() > 0) {
                    Map m = Dom2Map(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = (List) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), m);
                } else {
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(iter.getText());
                        }
                        if (obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = (List) obj;
                            mapList.add(iter.getText());
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), iter.getText());
                }
            }
        } else
            map.put(e.getName(), e.getText());
        return map;
    }
    public static Map<String, Object> xmlDocumentFile(String url){
        Document doc = null;
        try {
            FileInputStream fis = new FileInputStream(url);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            String str = new String(b);
            doc = DocumentHelper.parseText(str);
        } catch (Exception e) {
            log.error(">>>>>>>>>>xml file to map fail！", e);
        }
        return XmlUtils.Dom2Map(doc);
    }
    public static Map<String, Object> xmlDocumentStr(String xml){
        if (StringUtils.isBlank(xml)){
            return Collections.emptyMap();
        }
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            log.error(">>>>>>>>>>xml to map fail！", e);
        }
        return XmlUtils.Dom2Map(doc);
    }
}