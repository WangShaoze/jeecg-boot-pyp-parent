package org.jeecg.modules.pengyipeng.utils;

/*
 * ClassName: HttpLinkMatcherUtil
 * Package: org.jeecg.modules.pengyipeng.utils
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/28 - 13:11
 * @Version: v1.0
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpLinkMatcherUtil {
    /**
     * 提取字符串中第一个符合URL规范的HTTP/HTTPS链接
     * 支持大多数合法格式：包含用户名密码、端口、路径、查询参数、锚点等
     *
     * @param input 包含链接的字符串
     * @return 第一个匹配的链接，无匹配则返回null
     */
    public static String extractFirstHttpLink(String input) {
        // 更完善的URL正则表达式（基于RFC 3986简化）
        String regex = "https?://" +  // 协议部分
                "(?:[a-zA-Z0-9$-_@.&+]+|%[0-9a-fA-F]{2})+" +  // 用户名:密码@（可选）
                "(?::\\d+)?" +  // 端口号（可选，如:8080）
                "(?:/" +  // 路径部分（可选）
                "(?:[a-zA-Z0-9$-_@.&+]+|%[0-9a-fA-F]{2})*" +
                ")*" +
                "(?:\\?" +  // 查询参数（可选，如?key=value）
                "(?:[a-zA-Z0-9$-_@.&+]+|%[0-9a-fA-F]{2})*" +
                ")*" +
                "(?:#" +  // 锚点（可选，如#section1）
                "(?:[a-zA-Z0-9$-_@.&+]+|%[0-9a-fA-F]{2})*" +
                ")*";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
