package org.jeecg.modules.pengyipeng.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/*
 * ClassName: PypUsedPlatformDTO
 * Package: org.jeecg.modules.pengyipeng.dto
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/20 - 18:01
 * @Version: v1.0
 */
@Data
@Schema(description="APP端-功能（可选平台）实体")
public class PypUsedPlatformDTO {
    /**平台ID*/
    @Schema(description = "平台ID")
    private java.lang.Integer id;

    /**平台名称*/
    @Schema(description = "平台名称")
    private java.lang.String label;
    /**平台英文代号*/
    @Schema(description = "平台英文代号")
    private java.lang.String name;
    /**平台描述*/
    @Schema(description = "平台描述")
    private java.lang.String desc;
    /**平台图标*/
    @Schema(description = "平台图标")
    private java.lang.String icon;
    /**状态*/
    @Schema(description = "状态")
    private java.lang.Integer status;
    /**该平台在字典中的编号*/
    @Schema(description = "该平台在字典中的编号:(用于文案生成时，平台的传入参数)")
    private java.lang.Integer inDictOrder;

}
