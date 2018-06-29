package com.topaiebiz.member.member.utils;

/**
 * Created by ward on 2018-01-12.
 */

import org.apache.commons.lang3.StringUtils;

import java.lang.Character.UnicodeBlock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    public static final Pattern MOBILE_PATTERN = Pattern.compile("1\\d{10}");
    public static final Pattern PAYPWD_PATTERN = Pattern.compile("\\w{6,32}");
   // public static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_]+$)(?![a-z0-9]+$)(?![a-z\\W_]+$)(?![0-9\\W_]+$)[a-zA-Z0-9\\W_]{8,32}$");
    public static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9].*)(?=.*[a-zA-Z].*).{8,20}$");


    public static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+");

    public RegexUtil() {
    }

    public static boolean checkMobile(String mobile) {
        if(StringUtils.isBlank(mobile)) {
            return false;
        } else {
            Matcher matcher = MOBILE_PATTERN.matcher(mobile);
            return matcher.find();
        }
    }

    public static boolean checkPassword(String password) {
        if(StringUtils.isBlank(password)) {
            return false;
        } else {
            Matcher matcher = PASSWORD_PATTERN.matcher(password);
            return matcher.find();
        }
    }

    public static boolean checkPaypwd(String paypwd) {
        if(StringUtils.isBlank(paypwd)) {
            return false;
        } else {
            Matcher matcher = PAYPWD_PATTERN.matcher(paypwd);
            return matcher.find();
        }
    }

    public static boolean checkEmail(String email) {
        if(StringUtils.isBlank(email)) {
            return false;
        } else {
            Matcher matcher = EMAIL_PATTERN.matcher(email);
            return matcher.find();
        }
    }

    public static boolean isChinese(char c) {
        UnicodeBlock ub = UnicodeBlock.of(c);
        return ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == UnicodeBlock.GENERAL_PUNCTUATION || ub == UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|t*|r*|n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = (float)ch.length;
        float count = 0.0F;

        for(int result = 0; result < ch.length; ++result) {
            char c = ch[result];
            if(!Character.isLetterOrDigit(c) && !isChinese(c)) {
                ++count;
            }
        }

        float var10 = count / chLength;
        if((double)var10 > 0.4D) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isChineseByREG(String str) {
        if(str == null) {
            return false;
        } else {
            Pattern pattern = Pattern.compile("[\\u4E00-\\u9FBF]+");
            return pattern.matcher(str.trim()).find();
        }
    }

    public static boolean isChineseByName(String str) {
        if(str == null) {
            return false;
        } else {
            String reg = "\\p{InCJK Unified Ideographs}&&\\P{Cn}";
            Pattern pattern = Pattern.compile(reg);
            return pattern.matcher(str.trim()).find();
        }
    }
}