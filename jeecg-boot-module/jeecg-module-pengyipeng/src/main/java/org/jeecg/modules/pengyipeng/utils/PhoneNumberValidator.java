package org.jeecg.modules.pengyipeng.utils;

/*
 * ClassName: PhoneNumberValidator
 * Package: org.jeecg.modules.pengyipeng.utils
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/9/8 - 14:35
 * @Version: v1.0
 */

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PhoneNumberValidator {
    // 定义手机号的正则表达式
    private static final String PHONE_NUMBER_PATTERN = "^1[3-9]\\d{9}$";
    // 编译正则表达式
    private static final Pattern pattern = Pattern.compile(PHONE_NUMBER_PATTERN);

    /**
     * 判断字符串是否是有效的手机号
     *
     * @param phoneNumber 待验证的手机号字符串
     * @return 如果是有效的手机号，返回true；否则返回false
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public static void main(String[] args) {
// 测试用例
        String[] testNumbers = {
                "13800138000", // 有效
                "12345678901", // 无效，第二位不是3-9之间的数字
                "1380013800", // 无效，长度不足11位
                "138001380000",// 无效，长度超过11位
                "1380013800a", // 无效，包含非数字字符
                "13800138000 " // 无效，包含空格
        };
        for (String number : testNumbers) {
            System.out.println("Is " + number + " a valid phone number? " + isValidPhoneNumber(number));
        }
    }
}