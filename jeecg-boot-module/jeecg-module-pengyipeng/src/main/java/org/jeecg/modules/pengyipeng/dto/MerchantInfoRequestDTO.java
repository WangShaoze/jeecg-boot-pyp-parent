package org.jeecg.modules.pengyipeng.dto;

/*
 * ClassName: MerchantInfoRequestDTO
 * Package: org.jeecg.modules.pengyipeng.dto
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/20 - 13:31
 * @Version: v1.0
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "APP端-店铺基本信息请求DTO")
public class MerchantInfoRequestDTO {
    /**
     * 商家的ID
     */

    @Schema(description = "商家的ID")
    private Integer merchantId;
    /**
     * 商家的名称
     */
    @Schema(description = "商家的名称")
    private String merchantName;

    @Schema(description = "店铺主图")
    private String merchantMainPic;

    /**
     * 商家的关建字
     */
    @Schema(description = "商家的关建字")
    private List<String> merchantKeywordList;
}

