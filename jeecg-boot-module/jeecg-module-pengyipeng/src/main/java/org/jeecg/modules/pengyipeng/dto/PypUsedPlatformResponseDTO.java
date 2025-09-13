package org.jeecg.modules.pengyipeng.dto;

/*
 * ClassName: PypUsedPlatformResponseDTO
 * Package: org.jeecg.modules.pengyipeng.dto
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/20 - 17:59
 * @Version: v1.0
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "APP端-可选平台信息查询")
public class PypUsedPlatformResponseDTO {
    @Schema(description = "功能区1")
    private List<PypUsedPlatformDTO> list1 = new ArrayList<>();
    @Schema(description = "功能区2")
    private List<PypUsedPlatformDTO> list2 = new ArrayList<>();
    @Schema(description = "功能区3")
    private List<PypUsedPlatformDTO> list3 = new ArrayList<>();
}
