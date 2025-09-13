package org.jeecg.modules.pengyipeng.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 套餐表
 * @Author: jeecg-boot
 * @Date:   2025-08-12
 * @Version: V1.0
 */
@Data
@TableName("t_b_packages")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="套餐表")
public class TBPackages implements Serializable {
    private static final long serialVersionUID = 1L;

	/**套餐ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "套餐ID")
    private Integer id;
	/**商家ID*/
	@Excel(name = "商家ID", width = 15)
    @Schema(description = "商家ID")
    private Integer merchantId;
	/**商家名称*/
	@Excel(name = "商家名称", width = 15)
    @Schema(description = "商家名称")
    private String merchantName;
	/**套餐名称*/
	@Excel(name = "套餐名称", width = 15)
    @Schema(description = "套餐名称")
    private String packageName;
    /**套餐名称*/
    @Excel(name = "套餐的图片", width = 15)
    @Schema(description = "套餐的图片")
    private String packagePicList;
	/**套餐详情*/
	@Excel(name = "套餐详情", width = 15)
    @Schema(description = "套餐详情")
    private String packageDetails;
	/**标签*/
	@Excel(name = "标签", width = 15, dictTable = "t_b_tags", dicText = "tag_name", dicCode = "tag_name")
	@Dict(dictTable = "t_b_tags", dicText = "tag_name", dicCode = "tag_name")
    @Schema(description = "标签")
    private String tags;
	/**套餐价格*/
	@Excel(name = "套餐价格", width = 15)
    @Schema(description = "套餐价格")
    private BigDecimal price;
	/**有效期*/
	@Excel(name = "有效期", width = 15)
    @Schema(description = "有效期")
    private Integer durationDays;
	/**支持平台*/
	@Excel(name = "支持平台", width = 15, dicCode = "platforms")
	@Dict(dicCode = "platforms")
    @Schema(description = "支持平台")
    private String platforms;
	/**状态*/
    @Excel(name = "状态", width = 15,replace = {"是_Y","否_N"} )
    @Schema(description = "状态")
    private String status;
    /**创建人*/
    @Schema(description = "创建人")
    private String createBy;
    /**创建日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private Date createTime;
    /**更新人*/
    @Schema(description = "更新人")
    private String updateBy;
    /**更新日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private Date updateTime;
}
