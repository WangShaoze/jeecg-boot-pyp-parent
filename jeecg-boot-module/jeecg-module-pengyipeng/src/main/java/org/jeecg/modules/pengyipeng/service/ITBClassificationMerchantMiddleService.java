package org.jeecg.modules.pengyipeng.service;

import org.jeecg.modules.pengyipeng.dto.ClassificationDTO;
import org.jeecg.modules.pengyipeng.entity.TBClassificationMerchantMiddle;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 标签分类中间表
 * @Author: jeecg-boot
 * @Date:   2025-09-18
 * @Version: V1.0
 */
public interface ITBClassificationMerchantMiddleService extends IService<TBClassificationMerchantMiddle> {
    List<ClassificationDTO> getClassificationIdAndName(List<String> classificationIdList);

}
