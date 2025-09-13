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
 * @Description: APP相关信息
 * @Author: jeecg-boot
 * @Date:   2025-09-09
 * @Version: V1.0
 */
@Data
@TableName("t_b_app_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="APP相关信息")
public class TBAppInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "ID")
    private java.lang.Integer id;
	/**标识*/
	@Excel(name = "标识", width = 15)
    @Schema(description = "标识")
    private java.lang.String flagKey;
	/**用户协议*/
	@Excel(name = "用户协议", width = 15)
    @Schema(description = "用户协议")
    private java.lang.String appUserAgreement;
	/**隐私政策*/
	@Excel(name = "隐私政策", width = 15)
    @Schema(description = "隐私政策")
    private java.lang.String appUserPrivacyPolicy;
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
