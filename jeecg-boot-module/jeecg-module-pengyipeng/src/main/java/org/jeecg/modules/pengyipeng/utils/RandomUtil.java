package org.jeecg.modules.pengyipeng.utils;

/*
 * ClassName: RandomUtil
 * Package: org.jeecg.modules.jxcmanage.utils
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/9/3 - 18:23
 * @Version: v1.0
 */

import java.security.SecureRandom;

public final class RandomUtil {

    /**
     * 安全级别随机源，可复用
     */
    private static final SecureRandom SR = new SecureRandom();

    /**
     * 字符池：0-9 A-Z a-z
     */
    private static final String CHAR_POOL =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * 数字-字符池：0-9
     */
    private static final String DIGITAL_CHAR_POOL =
            "012345678901234567890123456789012345678901234567890123456789";

    /**
     * 生成 6 位大小写字母+数字随机串
     */
    public static String randomAlphanumeric6() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(CHAR_POOL.charAt(SR.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }

    /**
     * 生成5位数字随机串
     */
    public static String randomNumeric6() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(DIGITAL_CHAR_POOL.charAt(SR.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }

    /* ---------------- 示例调用 ---------------- */
    public static void main(String[] args) {
        System.out.println(randomAlphanumeric6()); // 每次输出如：k4Zq9B
        System.out.println(randomNumeric6()); // 每次输出如：370279
    }
}
