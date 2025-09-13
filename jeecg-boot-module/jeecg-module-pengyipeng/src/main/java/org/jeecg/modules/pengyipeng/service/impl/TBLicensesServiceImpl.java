package org.jeecg.modules.pengyipeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.MinioUtil;
import org.jeecg.modules.pengyipeng.entity.TBAgent;
import org.jeecg.modules.pengyipeng.entity.TBLicenses;
import org.jeecg.modules.pengyipeng.mapper.TBLicensesMapper;
import org.jeecg.modules.pengyipeng.service.ITBAgentService;
import org.jeecg.modules.pengyipeng.service.ITBLicensesService;
import org.jeecg.modules.pengyipeng.utils.QRCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 证书表
 * @Author: jeecg-boot
 * @Date: 2025-08-13
 * @Version: V1.0
 */
@Service
@Slf4j
public class TBLicensesServiceImpl extends ServiceImpl<TBLicensesMapper, TBLicenses> implements ITBLicensesService {
    @Lazy
    @Autowired
    private ITBAgentService agentService;

    @Value("${user-app.url}")
    private String userAppUrl;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignLicense(Integer agentId, Integer merchantId, LocalDateTime now, LocalDateTime twoYearsLater) {
        // 判断这个商户的证书是否已经存在
        /*TBLicenses tbLicensesJudge = this.getByMerchantId(merchantId);
        if (null != tbLicensesJudge) {
            throw new RuntimeException("该商户的证书已经存在！");
        }*/
        QueryWrapper<TBLicenses> queryWrapper = new QueryWrapper<TBLicenses>();
        queryWrapper.eq("agent_id", agentId);
        List<TBLicenses> tbLicensesList = this.getBaseMapper().selectList(queryWrapper);
        for (TBLicenses tbLicenses : tbLicensesList) {
            if (null != tbLicenses && null == tbLicenses.getMerchantId()) {
                tbLicenses.setMerchantId(merchantId);
                //  license_status ==》 使用中 ===> 4
                tbLicenses.setStatus(4);
                // 证书时间
                tbLicenses.setStartDate(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));
                tbLicenses.setEndDate(Date.from(twoYearsLater.atZone(ZoneId.systemDefault()).toInstant()));

                this.updateById(tbLicenses);
                return;
            }
        }
    }


    @Override
    public Integer getAgentLicenseUesed(Integer agentId) {
        //  更新代理商的的 已使用License数量 ==》 license_used
        QueryWrapper<TBLicenses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("agent_id", agentId);
        queryWrapper.ne("merchant_id", "").isNotNull("merchant_id");
        return Integer.parseInt(getBaseMapper().selectCount(queryWrapper).toString());
    }

    @Override
    public String getKeyByMerchantId(Integer merchantId) {

        TBLicenses tbLicenses = getByMerchantId(merchantId);
        if (null != tbLicenses) {
            return tbLicenses.getLicenseKey();
        } else {
            return null;
        }
    }

    @Override
    public TBLicenses getByMerchantId(Integer merchantId) {
        QueryWrapper<TBLicenses> queryWrapper = new QueryWrapper<TBLicenses>();
        queryWrapper.eq("merchant_id", merchantId);
        return getBaseMapper().selectOne(queryWrapper);
    }

    @Override
    public Long getByAgentId(Integer agentId) {
        QueryWrapper<TBLicenses> queryWrapper = new QueryWrapper<TBLicenses>();
        queryWrapper.eq("agent_id", agentId);
        return getBaseMapper().selectCount(queryWrapper);
    }

    @Override
    public boolean hasFourHyphens(String str) {
        // 空字符串直接返回false
        if (str == null) {
            return false;
        }

        int count = 0;
        // 遍历字符串中的每个字符
        for (int i = 0; i < str.length(); i++) {
            // 遇到"-"就计数加1
            if (str.charAt(i) == '-') {
                count++;
                // 提前退出优化，如果计数超过4就没必要继续了
                if (count > 4) {
                    return false;
                }
            }
        }
        // 判断是否正好有4个"-"
        return count == 4;
    }

    @Override
    public TBAgent getAgentByLicenseKey(String licenseKey) {
        QueryWrapper<TBLicenses> licensesQueryWrapper = new QueryWrapper<>();
        licensesQueryWrapper.eq("license_key", licenseKey);
        TBLicenses licenses = getBaseMapper().selectOne(licensesQueryWrapper);
        if (licenses == null) {
            log.info("getAgentByLicenseKey:{}", "证书查询异常");
            throw new RuntimeException("证书查询异常");
        }
        TBAgent agent = agentService.getById(licenses.getAgentId());
        if (agent == null) {
            log.info("getAgentByLicenseKey:{}", "代理商查询异常");
            throw new RuntimeException("代理商查询异常");
        }
        return agent;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchGenerateCustomQRCode(List<TBLicenses> licensesList, List<String> licenseKeyList) throws Exception {

        // 创建缓存
        Map<String, TBLicenses> keyTBLicensesMap = new HashMap<>();
        licensesList.forEach(licenses -> keyTBLicensesMap.put(licenses.getLicenseKey(), licenses));


        for (String licenseKey : licenseKeyList) {
            TBLicenses tbLicenses = keyTBLicensesMap.get(licenseKey);

            // 创建二维码配置
            QRCodeGenerator.QRCodeConfig config = new QRCodeGenerator.QRCodeConfig();
            // 自定义样式 - 示例配置
            config.setForegroundColor(new Color(0x2C3E50)); // 深蓝色前景
            config.setBackgroundColor(new Color(0xECF0F1)); // 浅灰色背景
            config.setMargin(2); // 边距
            config.setErrorCorrectionLevel(ErrorCorrectionLevel.H); // 高纠错级别（适合添加Logo）
            config.setRoundedCorners(true); // 圆角效果
            // 生成自定义二维码
            // https://pyp.ylkj668.com/h5/#/?licenseKey=7c92160b-ae4f-4455-9331-91671b64b4ec
            if (userAppUrl != null && !userAppUrl.isEmpty() && userAppUrl.endsWith("/")) {
                userAppUrl = userAppUrl.substring(0, userAppUrl.length() - 1);
            }
            byte[] qrCodeImage = QRCodeGenerator.generateCustomQRCode(userAppUrl + "/?licenseKey=" + licenseKey, 300, 300, config);
            // 编码文件名，防止中文乱码
            String encodedFilename = URLEncoder.encode(licenseKey.replace("-", ""), StandardCharsets.UTF_8) + ".png";
            String pngUrl = MinioUtil.upload(qrCodeImage, null, encodedFilename, "image/png");
            tbLicenses.setQrCodeUrl(pngUrl);
        }
        // 批量更新
        updateBatchById(keyTBLicensesMap.values());
    }
}
