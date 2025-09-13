package org.jeecg.modules.pengyipeng.service;

import org.jeecg.modules.pengyipeng.dto.PypUsedPlatformDTO;
import org.jeecg.modules.pengyipeng.entity.TBMerchants;
import org.jeecg.modules.pengyipeng.entity.TBPlatform;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: 碰一碰可选平台表
 * @Author: jeecg-boot
 * @Date:   2025-08-20
 * @Version: V1.0
 */
public interface ITBPlatformService extends IService<TBPlatform> {
    PypUsedPlatformDTO setPypUsedPlatformDtoInfo(TBPlatform tbPlatform);

    Map<String, Integer> platformStatusTrans(TBMerchants merchant);
    Map<String, String> platformPngTrans(TBMerchants merchant);

    void setEnablePlatformTrans(TBMerchants merchant, Map<String, Integer> enablePlatform);
    List<TBPlatform> getEnablePlatformList();
    TBPlatform getEnablePlatformByName(String platformName);

}
