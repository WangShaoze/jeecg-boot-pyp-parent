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
 * @Description: 运营商
 * @Author: jeecg-boot
 * @Date: 2025-08-13
 * @Version: V1.0
 */
@Data
@TableName("t_b_agent")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "运营商")
public class TBAgent implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 代理商ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "代理商ID")
    private Integer id;
    /**
     * 代理商ID
     */
    @Schema(description = "代理商在系统中的ID")
    private String sysUid;

    /**
     * 代理商名称
     */
    @Excel(name = "代理商名称", width = 15)
    @Schema(description = "代理商名称")
    private String name;

    /**
     * 代理商所在省市区
     */
    @Excel(name = "代理商所在省市区", width = 15)
    @Schema(description = "代理商所在省市区")
    private String region;

    /**
     * 代理商详细地址
     */
    @Excel(name = "代理商详细地址", width = 15)
    @Schema(description = "代理商详细地址")
    private String detailAddress;

    /**
     * 代理商级别
     */
    @Excel(name = "代理商级别", width = 15)
    @Dict(dicCode = "pyp_agent_level")
    @Schema(description = "代理商级别")
    private Integer level;


    /**
     * 代理商首次开通License的数量
     */
    @Excel(name = "代理商首次开通License的数量", width = 15)
    @Dict(dicCode = "pyp_agent_first_license_count")
    @Schema(description = "代理商首次开通License的数量")
    private Integer firstLicenseCount;


    /**
     * 联系人姓名
     */
    @Excel(name = "联系人姓名", width = 15)
    @Schema(description = "联系人姓名")
    private String contactName;
    /**
     * 联系人电话
     */
    @Excel(name = "联系人电话", width = 15)
    @Schema(description = "联系人电话")
    private String contactPhone;
    /**
     * 邮箱
     */
    @Excel(name = "邮箱", width = 15)
    @Schema(description = "邮箱")
    private String email;
    /**
     * Licence数量
     */
    @Excel(name = "Licence数量", width = 15)
    @Schema(description = "Licence数量")
    private Integer licenseTotal;
    /**
     * 已使用License数量
     */
    @Excel(name = "已使用License数量", width = 15)
    @Schema(description = "已使用License数量")
    private Integer licenseUsed;


    /**
     * 商户剩余开通数量
     */
    @Excel(name = "商户剩余开通数量", width = 15)
    @Schema(description = "商户剩余开通数量")
    private Integer licenseLeave;

    /**
     * 到期商家数量
     */
    @Excel(name = "到期商家数量", width = 15)
    @Schema(description = "到期商家数量")
    private Integer licenseExpired;

    /**
     * 即将到期商家数量
     */
    @Excel(name = "即将到期商家数量", width = 15)
    @Schema(description = "即将到期商家数量")
    private Integer licenseUpcomingExpired;

    /**
     * 代理商二维码
     */
    @Excel(name = "代理商二维码", width = 15)
    @Schema(description = "代理商二维码")
    private String agentQrCode;

    /**
     * 联系平台
     */
    @Excel(name = "联系平台", width = 15)
    @Schema(description = "联系平台")
    private String agentContactPlatform;

    /**
     * 状态
     */
    @Excel(name = "状态", width = 15, dicCode = "agent_status")
    @Dict(dicCode = "agent_status")
    @Schema(description = "状态")
    private Integer status;
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
