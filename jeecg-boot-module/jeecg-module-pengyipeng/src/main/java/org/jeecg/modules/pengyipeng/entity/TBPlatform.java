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
 * @Description: 碰一碰可选平台表
 * @Author: jeecg-boot
 * @Date:   2025-08-20
 * @Version: V1.0
 */
@Data
@TableName("t_b_platform")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="碰一碰可选平台表")
public class TBPlatform implements Serializable {
    private static final long serialVersionUID = 1L;

	/**代理商ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "代理商ID")
    private java.lang.Integer id;
	/**平台名称*/
	@Excel(name = "平台名称", width = 15)
    @Schema(description = "平台名称")
    private java.lang.String labelT;
	/**平台英文代号*/
	@Excel(name = "平台英文代号", width = 15)
    @Schema(description = "平台英文代号")
    private java.lang.String name;
	/**平台描述*/
	@Excel(name = "平台描述", width = 15)
    @Schema(description = "平台描述")
    private java.lang.String descT;
	/**平台图标*/
	@Excel(name = "平台图标", width = 15)
    @Schema(description = "平台图标")
    private java.lang.String icon;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @Schema(description = "状态")
    private java.lang.Integer status;
    /**状态*/
    @Excel(name = "所属功能区", width = 15)
    @Schema(description = "所属功能区")
    private java.lang.Integer functionOrder;
    /**该平台在字典中的编号*/
	@Excel(name = "该平台在字典中的编号", width = 15)
    @Schema(description = "该平台在字典中的编号")
    private java.lang.Integer inDictOrder;
	/**创建人*/
    @Schema(description = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private java.util.Date updateTime;
}
