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
 * @Description: 商家和平台中间表
 * @Author: jeecg-boot
 * @Date: 2025-08-25
 * @Version: V1.0
 */
@Data
@TableName("t_b_merchant_platform_middle")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "商家和平台中间表")
public class TBMerchantPlatformMiddle implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "ID")
    private java.lang.Integer id;
    /**
     * 商家ID
     */
    @Excel(name = "商家ID", width = 15)
    @Schema(description = "商家ID")
    private java.lang.Integer merchantId;
    /**
     * 平台ID
     */
    @Excel(name = "平台ID", width = 15)
    @Schema(description = "平台ID")
    private java.lang.Integer platformId;
    /**
     * 短链
     */
    @Excel(name = "短链", width = 15)
    @Schema(description = "短链")
    private java.lang.String shortLink;
    /**
     * 解析结果
     */
    @Excel(name = "解析结果", width = 15)
    @Schema(description = "解析结果")
    private java.lang.String parseInfo;
}
