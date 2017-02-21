package com.bc.ywjphone.readily.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/12/16 0016.
 */
public class RegexTools {

    public static boolean RegexName(String str) {
        if (str.matches("^(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$")) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isMoney(String trim) {
        String regEx = "^[0-9]+(.[0-9]{0,2})?$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(trim);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.matches();

        return rs;
    }

    public static boolean isNull(Object obj) {
        return obj==null?true:false;
    }

}