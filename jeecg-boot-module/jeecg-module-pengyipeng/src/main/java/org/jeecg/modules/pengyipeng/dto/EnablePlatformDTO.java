package org.jeecg.modules.pengyipeng.dto;

/*
 * ClassName: ds
 * Package: org.jeecg.modules.pengyipeng.dto
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/24 - 11:34
 * @Version: v1.0
 */

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class EnablePlatformDTO {
    private Integer merchantId;
    private Integer dazhong;
    private Integer meituan;
    private Integer douyin;
    private Integer xiaohongshu;
    private Integer meituan_tuan;
    private Integer wechat_friend;
    private Integer wechat;
    private Integer xiecheng;
    private Integer dazhong_tuan;
    private Integer douyin_tuan;
    private Integer gaode;

    public static Map<String, Integer> convert(EnablePlatformDTO dto) {
        Map<String, Integer> map = new HashMap<>();
        map.put("dazhong", dto.getDazhong());
        map.put("meituan", dto.getMeituan());
        map.put("douyin", dto.getDouyin());
        map.put("xiaohongshu", dto.getXiaohongshu());
        map.put("meituan_tuan", dto.getMeituan_tuan());
        map.put("wechat_friend", dto.getWechat_friend());
        map.put("wechat", dto.getWechat());
        map.put("xiecheng", dto.getXiecheng());
        map.put("dazhong_tuan", dto.getDazhong_tuan());
        map.put("douyin_tuan", dto.getDouyin_tuan());
        map.put("gaode", dto.getGaode());
        return map;
    }
}
