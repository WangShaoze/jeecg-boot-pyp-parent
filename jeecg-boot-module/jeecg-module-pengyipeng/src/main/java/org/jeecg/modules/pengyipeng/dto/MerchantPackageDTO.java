package org.jeecg.modules.pengyipeng.dto;

/*
 * ClassName: MerchantPackageDTO
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
@Schema(description = "APP端-套餐信息查询/返回-DTO")
public class MerchantPackageDTO {

    /**
     * 套餐的ID ( 用于编辑，创建的时候可以不用传)
     */
    @Schema(description = "套餐的ID ( 用于编辑，创建的时候可以不用传)")
    private Integer id;


    /**
     * 所属店家ID ( 如果和商家信息一起创建不用传 )
     */
    @Schema(description = "所属店家ID ( 如果和商家信息一起创建不用传 )")
    private Integer merchantId;

    /**
     * 套餐名称
     */
    @Schema(description = "套餐名称")
    private String packageName;

    /**
     * 套餐详情
     */
    @Schema(description = "套餐详情")
    private String packageDetails;

    /**
     * 套餐标签
     */
    @Schema(description = "套餐标签")
    private List<String> tags;

    /**
     * 套餐支持的平台
     */
    @Schema(description = "套餐支持的平台")
    private List<String> platformList;

    /**
     * 套餐中的图片url列表
     */
    @Schema(description = "套餐中的图片url列表")
    private List<String> packagePicList;

}
