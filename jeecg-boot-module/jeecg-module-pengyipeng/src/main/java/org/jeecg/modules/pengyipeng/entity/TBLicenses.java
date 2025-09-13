package org.jeecg.modules.pengyipeng.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 证书表
 * @Author: jeecg-boot
 * @Date: 2025-08-13
 * @Version: V1.0
 */
@Data
@TableName("t_b_licenses")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "证书表")
public class TBLicenses implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * License ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "License ID")
    private Integer id;
    /**
     * License密钥
     */
    @Excel(name = "License密钥", width = 15)
    @Schema(description = "License密钥")
    private String licenseKey;
    /**
     * 所属代理商ID
     */
    @Excel(name = "所属代理商ID", width = 15)
    @Schema(description = "所属代理商ID")
    private Integer agentId;
    /**
     * 分配的店家ID
     */
    @Excel(name = "分配的店家ID", width = 15)
    @Schema(description = "分配的店家ID")
    private Integer merchantId;
    /**
     * 状态
     */
    @Excel(name = "状态", width = 15, dicCode = "license_status")
    @Dict(dicCode = "license_status")
    @Schema(description = "状态")
    private Integer status;
    /**
     * 生效日期
     */
    @Excel(name = "生效日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "生效日期")
    private Date startDate;
    /**
     * 过期日期
     */
    @Excel(name = "过期日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "过期日期")
    private Date endDate;
    /**
     * 二维码
     */
    @Excel(name = "二维码", width = 15)
    @Schema(description = "二维码")
    private String qrCodeUrl;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private Date createTime;
    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private Date updateTime;
}
