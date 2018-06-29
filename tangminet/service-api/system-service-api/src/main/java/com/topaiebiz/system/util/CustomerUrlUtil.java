package com.topaiebiz.system.util;


import java.util.ArrayList;
import java.util.List;

/**
 * 搜集后台用户url
 */
public class CustomerUrlUtil {

    //商家端URL
    private static List<String> merchantURLList = new ArrayList<String>();

    //平台端URL
    private static List<String> platformURLList = new ArrayList<String>();

    //入驻端URL
    private static List<String> enterURLList = new ArrayList<String>();

    //用户端URL
//    private static List<String> customerURLList = new ArrayList<String>();

    //后台不需权限访问的URL
    private static List<String> permissionURLList = new ArrayList<String>();

    protected static void addMerchantURLList(String url) {
        merchantURLList.add(url);
    }

    protected static void addPlatformURLList(String url) {
        platformURLList.add(url);
    }

    protected static void addEnterURLList(String url) {
        enterURLList.add(url);
    }

//    protected static void addCustomerURLList(String url) {
//        customerURLList.add(url);
//    }

    protected static void addPermissionURLList(String url) {
        permissionURLList.add(url);
    }

    public static List<String> getMerchantURLList() {
        return CustomerUrlUtil.merchantURLList;
    }

    public static List<String> getPlatformURLList() {
        return CustomerUrlUtil.platformURLList;
    }

    public static List<String> getEnterURLList() {
        return CustomerUrlUtil.enterURLList;
    }
//    public static List<String> getCustomerURLList() {
//        return CustomerUrlUtil.customerURLList;
//    }

    public static List<String> getPermissionURLList() {
        return permissionURLList;
    }
}
