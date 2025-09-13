package org.jeecg.modules.pengyipeng.dto;

/*
 * ClassName: Kop
 * Package: org.jeecg.modules.pengyipeng.dto
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/16 - 16:35
 * @Version: v1.0
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description="APP端-AI流式返回请求实体")
public class GenerationRequestDTO {

    @Schema(description="本条文案的ID, 可以不传，用于有编辑的情况")
    private Integer id;

    @Schema(description = "本次会话的ID, 这个ID使用UUID前端需要自动生成")
    private String sessionId;

    @Schema(description = "提示词（写名词即可中间可以使用逗号隔开）")
    private String prompt;

    @Schema(description = "语气（积极:0保守:1中性:2）")
    private Integer tone;

    @Schema(description = "平台（参考获取平台接口中的 inDictOrder 字段）")
    private Integer platform;

    @Schema(description = "温度参数（0-1之前的小数）")
    private Double temperature;

    @Schema(description = "Top-P参数（0-1之前的小数）")
    private Double topP;

    @Schema(description = "最大令牌数(输出文字数)(建议值300)")
    private Integer maxTokens;

}

