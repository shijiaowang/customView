package com.wangyang.divviewlibrary.utils;

/**
 * Created by wangyang on 2017/5/10.
 * email:1440214507@qq.com
 * 检查手机号码
 */


import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PhoneUtils {
    public static boolean isMobileNO(String mobiles) {
        String REGEX_MOBILE = "^1[3,4,5,7,8]\\d{9}$";
        if (mobiles == null) {
            return false;
        }
        Pattern p = Pattern.compile(REGEX_MOBILE);
        Matcher m = p.matcher(mobiles);
        boolean matches = m.matches();
        return matches;

    }

    /**
     * 检查手机号码
     *
     * @param phone
     * @return
     */
    public static boolean checkPhoneNumber(String phone) {
        if (StringUtils.isEmpty(phone)) {
            ToastUtils.showToast("请输入手机号码。");
            return true;
        }
        if (!PhoneUtils.isMobileNO(phone)) {
            ToastUtils.showToast("请输入正确的手机号码");
            return true;
        }
        return false;
    }

}