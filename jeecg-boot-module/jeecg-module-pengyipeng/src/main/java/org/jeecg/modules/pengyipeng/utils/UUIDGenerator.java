package org.jeecg.modules.pengyipeng.utils;

/*
 * ClassName: UUIDGenerator
 * Package: org.jeecg.modules.pengyipeng.utils
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/9/8 - 15:46
 * @Version: v1.0
 */

import java.util.UUID;

public class UUIDGenerator {
    public static String generate32BitUUID() {
        // 生成标准UUID（带连字符，共36位）
        UUID uuid = UUID.randomUUID();

        // 去除连字符，得到32位UUID
        return uuid.toString().replaceAll("-", "");
    }

    public static void main(String[] args) {
        String uuid = generate32BitUUID();
        System.out.println("32位UUID: " + uuid);
        System.out.println("长度: " + uuid.length()); // 输出32
    }
}