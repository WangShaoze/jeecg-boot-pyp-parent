package org.jeecg.modules.pengyipeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.pengyipeng.dto.PypUsedPlatformDTO;
import org.jeecg.modules.pengyipeng.entity.TBMerchants;
import org.jeecg.modules.pengyipeng.entity.TBPlatform;
import org.jeecg.modules.pengyipeng.mapper.TBPlatformMapper;
import org.jeecg.modules.pengyipeng.service.ITBMerchantsService;
import org.jeecg.modules.pengyipeng.service.ITBPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Description: 碰一碰可选平台表
 * @Author: jeecg-boot
 * @Date: 2025-08-20
 * @Version: V1.0
 */
@Service
public class TBPlatformServiceImpl extends ServiceImpl<TBPlatformMapper, TBPlatform> implements ITBPlatformService {

    @Autowired
    private ITBMerchantsService merchantsService;

    @Override
    public PypUsedPlatformDTO setPypUsedPlatformDtoInfo(TBPlatform tbPlatform) {
        PypUsedPlatformDTO pypUsedPlatformDTO = new PypUsedPlatformDTO();
        pypUsedPlatformDTO.setId(tbPlatform.getId());
        pypUsedPlatformDTO.setLabel(tbPlatform.getLabelT());
        pypUsedPlatformDTO.setName(tbPlatform.getName());
        pypUsedPlatformDTO.setDesc(tbPlatform.getDescT());
        pypUsedPlatformDTO.setIcon(tbPlatform.getIcon());
        pypUsedPlatformDTO.setStatus(tbPlatform.getStatus());
        pypUsedPlatformDTO.setInDictOrder(tbPlatform.getInDictOrder());
        return pypUsedPlatformDTO;
    }

    @Override
    public Map<String, Integer> platformStatusTrans(TBMerchants merchant) {
        List<TBPlatform> platformList = getEnablePlatformList();
        Map<String, Integer> map = new HashMap<>();
        if (!StringUtils.isEmpty(merchant.getEnabledPlatforms())) {
            String[] arr = merchant.getEnabledPlatforms().split(",");
            if (arr.length != 0) {
                List<Integer> list = Arrays.stream(arr).map(s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (Exception e) {
                        return null;
                    }
                }).filter(Objects::nonNull).toList();
                for (TBPlatform tbPlatform : platformList) {
                    if (list.contains(tbPlatform.getInDictOrder())) {
                        tbPlatform.setStatus(1);
                    } else {
                        tbPlatform.setStatus(0);
                    }
                    map.put(tbPlatform.getName(), tbPlatform.getStatus());
                }
            } else {
                getNonePlatform(platformList, map);
            }
        } else {
            getNonePlatform(platformList, map);
        }
        return map;
    }

    @Override
    public Map<String, String> platformPngTrans(TBMerchants merchant) {
        List<TBPlatform> platformList = getEnablePlatformList();
        Map<String, String> map = new HashMap<>();
        if (!StringUtils.isEmpty(merchant.getEnabledPlatforms())) {
            String[] arr = merchant.getEnabledPlatforms().split(",");
            if (arr.length != 0) {
                List<Integer> list = Arrays.stream(arr).map(s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (Exception e) {
                        return null;
                    }
                }).filter(Objects::nonNull).toList();
                for (TBPlatform tbPlatform : platformList) {
                    if (list.contains(tbPlatform.getInDictOrder())) {
                        map.put(tbPlatform.getName(), tbPlatform.getIcon());
                    }
                }
            }
        }
        return map;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setEnablePlatformTrans(TBMerchants merchant, Map<String, Integer> enablePlatform) {
        List<TBPlatform> platformList = getEnablePlatformList();
        StringBuilder enablePlatformStr = new StringBuilder();
        for (TBPlatform tbPlatform : platformList) {
            if (enablePlatform.containsKey(tbPlatform.getName()) && enablePlatform.get(tbPlatform.getName()) == 1) {
                enablePlatformStr.append(tbPlatform.getInDictOrder()).append(",");
            }
        }
        merchant.setEnabledPlatforms(enablePlatformStr.toString());
        merchantsService.getBaseMapper().updateById(merchant);

    }

    @Override
    public List<TBPlatform> getEnablePlatformList() {
        return getBaseMapper().selectList(new QueryWrapper<>());
    }

    @Override
    public TBPlatform getEnablePlatformByName(String platformName) {
        for (TBPlatform platform : getEnablePlatformList()) {
            if (platform.getName().equals(platformName)) {
                return platform;
            }
        }
        return null;
    }

    private static void getNonePlatform(List<TBPlatform> platformList, Map<String, Integer> map) {
        for (TBPlatform tbPlatform : platformList) {
            tbPlatform.setStatus(0);
            map.put(tbPlatform.getName(), tbPlatform.getStatus());
        }
    }
}
