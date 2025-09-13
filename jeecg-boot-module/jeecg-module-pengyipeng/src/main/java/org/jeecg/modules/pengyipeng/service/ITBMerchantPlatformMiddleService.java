package org.jeecg.modules.pengyipeng.service;

import org.jeecg.modules.pengyipeng.entity.TBMerchantPlatformMiddle;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @Description: 商家和平台中间表
 * @Author: jeecg-boot
 * @Date:   2025-08-25
 * @Version: V1.0
 */
public interface ITBMerchantPlatformMiddleService extends IService<TBMerchantPlatformMiddle> {
    String parseShortLink(String shortLinkStr, String platformName);
}
