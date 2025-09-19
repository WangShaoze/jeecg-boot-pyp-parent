package org.jeecg.modules.pengyipeng.dto;

/*
 * ClassName: ClassificationDTO
 * Package: org.jeecg.modules.pengyipeng.dto
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/9/19 - 10:48
 * @Version: v1.0
 */

import lombok.Data;

@Data
public class ClassificationDTO {
    private String id;   // 中间表的ID
    private String classificationName;  // 该分类的名称
    private String picList; // 该分类对应的图片
}
