package org.jeecg.modules.pengyipeng.service.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.pengyipeng.dto.MerchantPackageDTO;
import org.jeecg.modules.pengyipeng.entity.TBMerchants;
import org.jeecg.modules.pengyipeng.entity.TBPackages;
import org.jeecg.modules.pengyipeng.entity.TBPlatform;
import org.jeecg.modules.pengyipeng.mapper.TBPackagesMapper;
import org.jeecg.modules.pengyipeng.service.ITBMerchantsService;
import org.jeecg.modules.pengyipeng.service.ITBPackagesService;
import org.jeecg.modules.pengyipeng.service.ITBPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 套餐表
 * @Author: jeecg-boot
 * @Date: 2025-08-12
 * @Version: V1.0
 */
@Service
public class TBPackagesServiceImpl extends ServiceImpl<TBPackagesMapper, TBPackages> implements ITBPackagesService {
    @Autowired
    private ITBPlatformService platformService;

    @Autowired
    private ITBMerchantsService merchantsService;


    @Override
    public void packageInfoSetting(MerchantPackageDTO packageDTO, TBPackages tbPackages, TBMerchants merchants) {
        tbPackages.setMerchantId(packageDTO.getMerchantId());
        tbPackages.setMerchantName(merchants.getMerchantName());
        tbPackages.setPackageName(packageDTO.getPackageName());
        tbPackages.setPackageDetails(packageDTO.getPackageDetails());
        tbPackages.setTags(JSON.toJSONString(packageDTO.getTags()));
        tbPackages.setPlatforms(String.join(",", packageDTO.getPlatformList()));
        tbPackages.setPackagePicList(JSON.toJSONString(packageDTO.getPackagePicList()));
    }

    @Override
    public void setDtoProperties(TBPackages tbPackages, MerchantPackageDTO merchantPackageDTO) {
        merchantPackageDTO.setId(tbPackages.getId());
        merchantPackageDTO.setMerchantId(tbPackages.getMerchantId());
        merchantPackageDTO.setPackageName(tbPackages.getPackageName());
        merchantPackageDTO.setPackageDetails(tbPackages.getPackageDetails());
        merchantPackageDTO.setTags(JSON.parseArray(tbPackages.getTags(), String.class));

        List<String> platforms = null;
        if (tbPackages.getPlatforms() == null || tbPackages.getPlatforms().isEmpty()) {
            platforms = new ArrayList<>();
        } else {
            platforms = Arrays.asList(tbPackages.getPlatforms().split(","));
        }
        merchantPackageDTO.setPlatformList(platforms);
        merchantPackageDTO.setPackagePicList(JSON.parseArray(tbPackages.getPackagePicList(), String.class));
    }


    @Override
    public String generatePrompt(TBPackages packages, String platformName) {
        String prompt = "我需要一篇platform的评论大概120字左右，\n" +
                "店铺关键信息: \n" +
                "   merchantInfo \n" +
                "套餐关键信息：\n" +
                "   packageInfo\n" +
                "其他要求: 不能使用markdown语法，直接给出评论文案，态度积极。";
        TBPlatform tbPlatform = platformService.getEnablePlatformByName(platformName);
        if (tbPlatform != null && !StringUtils.isEmpty(tbPlatform.getLabelT())) {
            prompt = prompt.replace("platform", tbPlatform.getLabelT());
        } else {
            prompt = prompt.replace("platform的", "");
        }
        TBMerchants merchant = merchantsService.getById(packages.getMerchantId());
        if (merchant == null) {
            prompt = prompt.replace("店铺关键信息:", "").replace("merchantInfo", "");
        } else {
            StringBuilder s = new StringBuilder();
            if (!StringUtils.isEmpty(merchant.getMerchantName())) {
                s.append("店铺名称:  ").append(merchant.getMerchantName()).append("\n");
            }
            if (!StringUtils.isEmpty(merchant.getKeywords())) {
                s.append("店铺关键字:  ").append(merchant.getKeywords()).append("\n");
            }
            if (!StringUtils.isEmpty(merchant.getDescription())) {
                s.append("店铺描述:  ").append(merchant.getDescription()).append("\n");
            }
            prompt = prompt.replace("merchantInfo", s.toString());

            StringBuilder s1 = new StringBuilder();
            if (!StringUtils.isEmpty(packages.getPackageName())) {
                s1.append("套餐名称:  ").append(packages.getPackageName()).append("\n");
            }
            if (!StringUtils.isEmpty(packages.getTags())) {
                s1.append("标签:  ").append(packages.getTags()).append("\n");
            }
            if (!StringUtils.isEmpty(packages.getPackageDetails())) {
                s1.append("套餐详情:  ").append(packages.getPackageDetails()).append("\n");
            }
            prompt = prompt.replace("packageInfo", s1.toString());
        }
        return prompt;
    }

}
