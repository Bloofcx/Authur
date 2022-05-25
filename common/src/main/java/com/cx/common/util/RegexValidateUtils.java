package com.cx.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ChenXu
 * @create 2022-02-17-17:25
 */
public class RegexValidateUtils {
    static boolean flag = false;
    static String regex = "";

    public static boolean check(String str, String regex) {
        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证手机号码
     *
     * 移动号码段:139、138、137、136、135、134、150、151、152、157、158、159、182、183、187、188、147
     * 联通号码段:130、131、132、136、185、186、145
     * 电信号码段:133、153、180、189
     *
     * @param cellphone
     * @return
     */
    public static boolean checkCellphone(String cellphone) {
        String regex = "^[1][3,4,5,7,8][0-9]{9}$";
        return check(cellphone, regex);
    }
}
