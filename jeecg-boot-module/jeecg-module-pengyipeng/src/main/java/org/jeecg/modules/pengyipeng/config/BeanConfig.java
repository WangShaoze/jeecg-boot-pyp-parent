package org.jeecg.modules.pengyipeng.config;

/*
 * ClassName: BeanConfig
 * Package: org.jeecg.modules.pengyipeng.config
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/9/8 - 14:14
 * @Version: v1.0
 */

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaRedisBetterConfigImpl;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.common.redis.WxRedisOps;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@Configuration
public class BeanConfig {
    @Value("${wechat.appId}")
    private String appId;
    @Value("${wechat.appSecret}")
    private String appSecret;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 创建WxMaService Bean
     * @return WxMaService实例
     */
    @Bean(name = "wxMaService")
    public WxMaService wxMaService() {
        WxMaService wxMaService = new WxMaServiceImpl();
        WxRedisOps wxRedisOps = new RedisTemplateWxRedisOps(stringRedisTemplate);
        WxMaDefaultConfigImpl wxMaConfig = new WxMaRedisBetterConfigImpl(wxRedisOps, "consumer");
        wxMaConfig.setAppid(appId);
        wxMaConfig.setSecret(appSecret);
        wxMaService.setWxMaConfig(wxMaConfig);
        return wxMaService;
    }
}
