package org.jeecg.modules.pengyipeng.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.pengyipeng.dto.ClassificationDTO;
import org.jeecg.modules.pengyipeng.entity.TBClassificationMerchantMiddle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 标签分类中间表
 * @Author: jeecg-boot
 * @Date:   2025-09-18
 * @Version: V1.0
 */
public interface TBClassificationMerchantMiddleMapper extends BaseMapper<TBClassificationMerchantMiddle> {
    @Select("<script>" +
            "select m.id, m.pic_list, co.classification_chinese_name as classification_name " +
            "from t_b_classification_merchant_middle m " +
            "left join t_b_classification_option co on m.classification_option_id = co.id " +
            "where m.id in " +
            "<foreach collection='classificationIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<ClassificationDTO> getClassificationDto(@Param("classificationIds") List<String> classificationIds);

}
