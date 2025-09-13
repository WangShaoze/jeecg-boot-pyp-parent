package org.jeecg.modules.pengyipeng.service;

import org.jeecg.modules.pengyipeng.entity.TBAgent;
import org.jeecg.modules.pengyipeng.entity.TBLicenses;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: 证书表
 * @Author: jeecg-boot
 * @Date: 2025-08-13
 * @Version: V1.0
 */
public interface ITBLicensesService extends IService<TBLicenses> {
    void assignLicense(Integer agentId, Integer merchantId, LocalDateTime now, LocalDateTime twoYearsLater);

    Integer getAgentLicenseUesed(Integer agentId);

    String getKeyByMerchantId(Integer merchantId);

    TBLicenses getByMerchantId(Integer merchantId);

    Long getByAgentId(Integer merchantId);

    boolean hasFourHyphens(String str);

    TBAgent getAgentByLicenseKey(String licenseKey);

    void batchGenerateCustomQRCode(List<TBLicenses> licensesList, List<String> licenseKeyList) throws Exception;
}
