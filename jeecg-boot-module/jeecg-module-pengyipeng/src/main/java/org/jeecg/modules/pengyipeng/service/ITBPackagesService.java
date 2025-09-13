package org.jeecg.modules.pengyipeng.service;

import org.jeecg.modules.pengyipeng.dto.MerchantPackageDTO;
import org.jeecg.modules.pengyipeng.entity.TBMerchants;
import org.jeecg.modules.pengyipeng.entity.TBPackages;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 套餐表
 * @Author: jeecg-boot
 * @Date:   2025-08-12
 * @Version: V1.0
 */
public interface ITBPackagesService extends IService<TBPackages> {
    void packageInfoSetting(MerchantPackageDTO packageDTO, TBPackages tbPackages, TBMerchants merchants);

    void setDtoProperties(TBPackages tbPackages, MerchantPackageDTO merchantPackageDTO);

    String generatePrompt(TBPackages packages, String platformName);
}
