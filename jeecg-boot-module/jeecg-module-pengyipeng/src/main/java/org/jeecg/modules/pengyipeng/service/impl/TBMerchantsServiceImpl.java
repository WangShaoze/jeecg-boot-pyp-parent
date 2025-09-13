package org.jeecg.modules.pengyipeng.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.DesensitizedUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.xml.bind.v2.TODO;
import com.xkcoding.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.pengyipeng.dto.MerchantInfoResponseDTO;
import org.jeecg.modules.pengyipeng.dto.MerchantLoginDto;
import org.jeecg.modules.pengyipeng.dto.MerchantServiceInfoDTO;
import org.jeecg.modules.pengyipeng.entity.TBAgent;
import org.jeecg.modules.pengyipeng.entity.TBLicenses;
import org.jeecg.modules.pengyipeng.entity.TBMerchants;
import org.jeecg.modules.pengyipeng.mapper.TBMerchantsMapper;
import org.jeecg.modules.pengyipeng.service.ITBAgentService;
import org.jeecg.modules.pengyipeng.service.ITBLicensesService;
import org.jeecg.modules.pengyipeng.service.ITBMerchantsService;
import org.jeecg.modules.pengyipeng.utils.RandomUtil;
import org.jeecg.modules.pengyipeng.utils.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import cn.binarywang.wx.miniapp.api.WxMaService;


import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 店铺表
 * @Author: jeecg-boot
 * @Date: 2025-08-12
 * @Version: V1.0
 */
@Slf4j
@Service
public class TBMerchantsServiceImpl extends ServiceImpl<TBMerchantsMapper, TBMerchants> implements ITBMerchantsService {

    @Autowired
    private ITBLicensesService licensesService;

    @Autowired
    @Lazy
    private ITBAgentService agentService;

    @Autowired
    private WxMaService wxMaService;


    public IPage<MerchantServiceInfoDTO> getMerchantServiceInfo(Page<MerchantServiceInfoDTO> page,
                                                                String agentSysUid,
                                                                Integer licenseStatus,
                                                                String column,
                                                                String order) {
        return baseMapper.getMerchantServiceInfo(page, agentSysUid, licenseStatus, column, order);
    }

    @Override
    public TBMerchants getByLicenseKey(String merchantLicense) {
        return baseMapper.selectByLicenseKey(merchantLicense);
    }


    @Override
    public Integer queryMerchantIdByLicenseKey(String licenseKey) {
        QueryWrapper<TBLicenses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("license_key", licenseKey);
        TBLicenses tbLicenses = licensesService.getBaseMapper().selectOne(queryWrapper);
        if (tbLicenses == null || tbLicenses.getMerchantId() == null) {
            return 0;
        }
        if (tbLicenses.getStatus() == 1) {
            log.info("商家:{} 证书：{} 状态：{}, 状态寓意: 商家的证书不可用。", tbLicenses.getMerchantId(), tbLicenses.getLicenseKey(), tbLicenses.getStatus());
            return -1;
        }
        if (tbLicenses.getStatus() == 3) {
            log.info("商家:{} 证书：{} 状态：{}, 状态寓意: 商家的证书过期。", tbLicenses.getMerchantId(), tbLicenses.getLicenseKey(), tbLicenses.getStatus());
            return -1;
        }
        return tbLicenses.getMerchantId();
    }

    @Override
    public void setInfoForResp(TBMerchants tBMerchants, MerchantInfoResponseDTO responseDTO) {
        responseDTO.setId(tBMerchants.getId());
        responseDTO.setMerchantName(tBMerchants.getMerchantName());

        if (StringUtils.isEmpty(tBMerchants.getEnabledPlatforms())) {
            tBMerchants.setEnabledPlatforms("3,4,8");
        }
        responseDTO.setEnabledPlatforms(Arrays.stream(tBMerchants.getEnabledPlatforms().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
        responseDTO.setKeywords(JSON.parseArray(tBMerchants.getKeywords(), String.class));
        responseDTO.setWechatQrCode(tBMerchants.getWechatQrCode());
        responseDTO.setMerchantMainPic(tBMerchants.getMerchantMainPic());
        responseDTO.setXhsTopics(tBMerchants.getXhsTopics());
        responseDTO.setMainPic(tBMerchants.getMainPic());
        responseDTO.setWifiUser(tBMerchants.getWifiUser());
        responseDTO.setWifiPwd(tBMerchants.getWifiPwd());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void renewForMerchant(TBMerchants tbMerchants, TBAgent tbAgent) {

        // 修改证书的状态 （已过期 3 ---》 使用中 4） 和 服务结束时间
        TBLicenses tbLicenses = licensesService.getByMerchantId(tbMerchants.getId());
        tbLicenses.setStatus(4);

        Date endDate = tbLicenses.getEndDate();
        Instant endInstant = endDate.toInstant();
        // 增加2年
        Instant twoYearsLaterInstant = endInstant.plus(2, ChronoUnit.YEARS);
        // 转回Date类型（如果需要）
        Date twoYearsLater = Date.from(twoYearsLaterInstant);

        tbLicenses.setEndDate(twoYearsLater);
        licensesService.updateById(tbLicenses);

        // 修改商户的状态 （服务到期 = SERVICE_END） ==> （已激活 = ACTIVE）
        tbMerchants.setStatus("ACTIVE");
        // 更新服务结束时间
        tbLicenses.setEndDate(twoYearsLater);
        updateById(tbMerchants);

        // 需要 更新代理商信息 ===> 到期商家数量 && 即将到期商家数量
        // TODO  这里可以选择不做操作，使用定时任务去更新
        /*tbAgent.setLicenseExpired(null);
        tbAgent.setLicenseUpcomingExpired(null);
        agentService.updateById(tbAgent);*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void freezeMerchant(TBMerchants tbMerchants, TBAgent tbAgent) {
        // 服务取消
        tbMerchants.setStatus("CANCEL_SERVICE");
        updateById(tbMerchants);

        // 证书表中找到该证书并设置证书状态、License => (merchantID ==> NULL && agentID ==> NULL)
        TBLicenses tbLicenses = licensesService.getByMerchantId(tbMerchants.getId());
        tbLicenses.setAgentId(null);
        tbLicenses.setMerchantId(null);
        tbLicenses.setStatus(1);  // 证书状态变为可用
        tbLicenses.setStartDate(null);
        tbLicenses.setEndDate(null);
        licensesService.updateById(tbLicenses);


        // 更新代理商的证书管理证书状态
        Integer totalCount = Integer.parseInt(licensesService.getByAgentId(Integer.parseInt(tbAgent.getId().toString())).toString());
        Integer usedCount = licensesService.getAgentLicenseUesed(tbAgent.getId());
        tbAgent.setLicenseTotal(totalCount);
        tbAgent.setLicenseUsed(usedCount);
        tbAgent.setLicenseLeave(totalCount - usedCount);
        agentService.updateById(tbAgent);
    }


    /**
     * 获取微信用户手机号
     *
     * @param code code值
     */
    @Override
    public String getMobilePhoneByWechat(String code) {
        try {
            String accessToken = wxMaService.getAccessToken(true);
            String url = UrlBuilder
                    .of("https://api.weixin.qq.com/wxa/business/getuserphonenumber")
                    .addQuery("access_token", accessToken)
                    .build();
            Map<String, Object> paramBody = new HashMap<>(2);
            paramBody.put("code", code);
            String result = HttpUtil.post(url, JSON.toJSONString(paramBody));
            JSONObject jsonObject = JSON.parseObject(result);
            if ("40029".equals(jsonObject.getString("errcode"))) {
                throw new RuntimeException("code无效");
            }
            if ("-1".equals(jsonObject.getString("errcode"))) {
                throw new RuntimeException("微信系统繁忙");
            }
            JSONObject phoneInfo = jsonObject.getJSONObject("phone_info");
            return phoneInfo.getString("phoneNumber");
        } catch (WxErrorException e) {
            e.printStackTrace();
            wxMaService.getWxMaConfig().expireAccessToken();
            throw new RuntimeException("获取手机号失败");
        }
    }

    /**
     * 获取openid
     *
     * @param code code值
     */
    @Override
    public String getOpenId(String code) {
        try {
            WxMaJscode2SessionResult session = wxMaService.jsCode2SessionInfo(code);
            return session.getOpenid();
        } catch (WxErrorException e) {
            log.error("获取OpenID失败", e);
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TBMerchants saveMerchant(String openId, MerchantLoginDto loginDto) {
        TBMerchants tbMerchants = new TBMerchants();
        tbMerchants.setOpenid(openId);
        tbMerchants.setSysUid(loginDto.getId());
        tbMerchants.setMerchantName(loginDto.getUsername());
        tbMerchants.setStatus("UNACTIVE"); // 未激活
        save(tbMerchants);
        return tbMerchants;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MerchantLoginDto registerSysUser(String phone) {
        MerchantLoginDto loginDto = new MerchantLoginDto();
        String sixRandomNumber = RandomUtil.randomNumeric6();
        loginDto.setId(UUIDGenerator.generate32BitUUID());
        loginDto.setUsername("dianjia" + sixRandomNumber);
        loginDto.setRealname("店家" + sixRandomNumber);
        loginDto.setWorkNo(sixRandomNumber + RandomUtil.randomNumeric6() + RandomUtil.randomNumeric6());
        loginDto.setSelectedroles("1953634960707457025");  // 店家角色
        loginDto.setUserIdentity(1);
        loginDto.setEmail("dianjia" + sixRandomNumber + "@qq.com");
        loginDto.setPhone(phone);
        loginDto.setActivitiSync(1);
        loginDto.setSalt(RandomUtil.randomAlphanumeric6());
        loginDto.setPassword(PasswordUtil.encrypt(loginDto.getUsername(), "123456", loginDto.getSalt()));
        loginDto.setDepartIds("");
        int successNum = baseMapper.saveSysUser(loginDto.getId(), loginDto.getUsername(),
                loginDto.getRealname(), loginDto.getWorkNo(),
                loginDto.getUserIdentity(), loginDto.getEmail(),
                loginDto.getPhone(), loginDto.getActivitiSync(),
                loginDto.getPassword(), loginDto.getSalt(), loginDto.getDepartIds());
        if (successNum == 1) {
            // 用户角色插入
            int successNum1 = baseMapper.insertSysUserRole(UUIDGenerator.generate32BitUUID(), loginDto.getId(), loginDto.getSelectedroles());
            if (successNum1 != 1) {
                throw new RuntimeException("注册失败");
            }
            return loginDto;
        } else {
            throw new RuntimeException("注册失败");
        }
    }

}
