package org.jeecg.modules.pengyipeng.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.pengyipeng.dto.MerchantInfoResponseDTO;
import org.jeecg.modules.pengyipeng.dto.MerchantLoginDto;
import org.jeecg.modules.pengyipeng.dto.MerchantServiceInfoDTO;
import org.jeecg.modules.pengyipeng.entity.TBAgent;
import org.jeecg.modules.pengyipeng.entity.TBMerchants;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.pengyipeng.mapper.TBMerchantsMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 店铺表
 * @Author: jeecg-boot
 * @Date: 2025-08-12
 * @Version: V1.0
 */
public interface ITBMerchantsService extends IService<TBMerchants> {
    Integer queryMerchantIdByLicenseKey(String licenseKey);

    void setInfoForResp(TBMerchants tBMerchants, MerchantInfoResponseDTO responseDTO);

    void renewForMerchant(TBMerchants tbMerchants, TBAgent tbAgent);

    void freezeMerchant(TBMerchants tbMerchants, TBAgent tbAgent);

    IPage<MerchantServiceInfoDTO> getMerchantServiceInfo(Page<MerchantServiceInfoDTO> page, String agentSysUid, Integer licenseStatus,
                                                         String column,
                                                         String order);


    TBMerchants getByLicenseKey(String merchantLicense);

    String getMobilePhoneByWechat(String code);

    MerchantLoginDto registerSysUser(String phone);
    String getOpenId(String code);

    TBMerchants saveMerchant(String openId, MerchantLoginDto loginDto);


}
