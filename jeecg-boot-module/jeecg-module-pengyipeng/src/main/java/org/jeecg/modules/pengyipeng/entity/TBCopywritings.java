package org.jeecg.modules.pengyipeng.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
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
 * @Description: 文案表
 * @Author: jeecg-boot
 * @Date:   2025-08-16
 * @Version: V1.0
 */
@Data
@TableName("t_b_copywritings")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="文案表")
public class TBCopywritings implements Serializable {
    private static final long serialVersionUID = 1L;

	/**文案ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "文案ID")
    private Integer id;

    /**文案ID*/
    @Excel(name = "会话ID", width = 15)
    @Schema(description = "会话ID")
    private String sessionId;

    /**提示词*/
    @Excel(name = "提示词", width = 15)
    @Schema(description = "提示词")
    private String prompt;
	/**文案内容*/
	@Excel(name = "文案内容", width = 15)
    @Schema(description = "文案内容")
    private String content;
	/**AI服务提供商*/
	@Excel(name = "AI服务提供商", width = 15)
    @Schema(description = "AI服务提供商")
    private String provider;
	/**语气*/
	@Excel(name = "语气", width = 15, dicCode = "tone_status")
	@Dict(dicCode = "tone_status")
    @Schema(description = "语气")
    private Integer tone;
	/**平台*/
	@Excel(name = "平台", width = 15, dicCode = "platforms")
	@Dict(dicCode = "platforms")
    @Schema(description = "平台")
    private Integer platform;
	/**温度参数*/
	@Excel(name = "温度参数", width = 15)
    @Schema(description = "温度参数")
    private Double temperature;
	/**Top-P参数*/
	@Excel(name = "Top-P参数", width = 15)
    @Schema(description = "Top-P参数")
    private Double topP;
	/**最大令牌数*/
	@Excel(name = "最大令牌数", width = 15)
    @Schema(description = "最大令牌数")
    private Integer maxTokens;
	/**生成是否成功*/
	@Excel(name = "生成是否成功", width = 15)
    @Schema(description = "生成是否成功")
    private Integer success;
	/**错误信息*/
	@Excel(name = "错误信息", width = 15)
    @Schema(description = "错误信息")
    private String errorMessage;
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
