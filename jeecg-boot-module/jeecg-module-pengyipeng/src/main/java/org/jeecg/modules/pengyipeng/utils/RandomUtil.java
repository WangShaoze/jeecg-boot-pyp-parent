package org.jeecg.modules.pengyipeng.utils;

/*
 * ClassName: RandomUtil
 * Package: org.jeecg.modules.jxcmanage.utils
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/9/3 - 18:23
 * @Version: v1.0
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.List;

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


    public static List<String> processJsonList(String jsonStr) {
        ObjectMapper objectMapper = new ObjectMapper();


        try {
            // 将JSON字符串转换为List<String>
            List<String> list = objectMapper.readValue(jsonStr, new TypeReference<List<String>>() {
            });

            return randomSelectTwoElement(list);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // 发生异常时返回空列表
        }
    }

    public static List<String> randomSelectTwoElement(List<String> list) {
        if (list == null || list.isEmpty()) {
            return List.of();
        }
        SecureRandom random = new SecureRandom();
        // 如果列表长度大于2，随机选择2个元素
        if (list.size() > 2) {
            // 生成两个不同的随机索引
            int index1 = random.nextInt(list.size());
            int index2;
            do {
                index2 = random.nextInt(list.size());
            } while (index2 == index1);

            // 返回包含两个随机元素的新列表
            return List.of(list.get(index1), list.get(index2));
        } else {
            // 长度小于等于2，直接返回原列表
            return list;
        }
    }

    public static String randomSelectOneElement(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        SecureRandom random = new SecureRandom();
        if (list.size() > 1) {
            // 生成两个不同的随机索引
            int index = random.nextInt(list.size());
            // 返回包含两个随机元素的新列表
            return list.get(index);
        } else {
            // 长度小于等于2，直接返回原列表
            return list.get(0);
        }
    }


    /* ---------------- 示例调用 ---------------- */
    public static void main(String[] args) {
        System.out.println(randomAlphanumeric6()); // 每次输出如：k4Zq9B
        System.out.println(randomNumeric6()); // 每次输出如：370279
    }
}
