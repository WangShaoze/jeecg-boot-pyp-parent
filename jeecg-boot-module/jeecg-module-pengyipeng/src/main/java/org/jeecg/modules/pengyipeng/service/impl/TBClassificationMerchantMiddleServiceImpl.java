package org.jeecg.modules.pengyipeng.service.impl;

import org.jeecg.modules.pengyipeng.dto.ClassificationDTO;
import org.jeecg.modules.pengyipeng.entity.TBClassificationMerchantMiddle;
import org.jeecg.modules.pengyipeng.mapper.TBClassificationMerchantMiddleMapper;
import org.jeecg.modules.pengyipeng.service.ITBClassificationMerchantMiddleService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 标签分类中间表
 * @Author: jeecg-boot
 * @Date:   2025-09-18
 * @Version: V1.0
 */
@Service
public class TBClassificationMerchantMiddleServiceImpl extends ServiceImpl<TBClassificationMerchantMiddleMapper, TBClassificationMerchantMiddle> implements ITBClassificationMerchantMiddleService {

    @Override
    public List<ClassificationDTO> getClassificationIdAndName(List<String> classificationIdList) {
        return baseMapper.getClassificationDto(classificationIdList);
    }
}
