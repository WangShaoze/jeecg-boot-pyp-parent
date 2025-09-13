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
@Schema(description="APP端口店铺信息查询")
public class MerchantInfoResponseDTO {
    @Schema(description = "店铺ID")
    private Integer id;
    /**店铺名称*/
    @Schema(description = "店铺名称")
    private String merchantName;
    /**开通平台*/
    @Schema(description = "开通平台")
    private List<Integer> enabledPlatforms;
    /**店铺关键词*/
    @Schema(description = "店铺关键词")
    private List<String> keywords;
    /**店铺微信二维码*/
    @Schema(description = "店铺微信二维码")
    private String wechatQrCode;
    /**店铺主图*/
    @Schema(description = "店铺主图")
    private String merchantMainPic;
    /**小红书话题*/
    @Schema(description = "小红书话题")
    private String xhsTopics;

    /**店铺主页头像*/
    @Schema(description = "店铺主页头像")
    private String mainPic;

    /**wifi用户*/
    @Schema(description = "wifi用户")
    private String wifiUser;

    /**wifi密码*/
    @Schema(description = "wifi密码")
    private String wifiPwd;
}

