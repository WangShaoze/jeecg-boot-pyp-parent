package org.jeecg.modules.pengyipeng.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;
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
 * @Description: 店铺表
 * @Author: jeecg-boot
 * @Date: 2025-08-12
 * @Version: V1.0
 */
@Data
@TableName("t_b_merchants")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "店铺表")
public class TBMerchants implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 店铺ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "店铺ID")
    private Integer id;

    /**
     * 微信静默登录是用的openid
     */
    @Schema(description = "微信静默登录是用的openid")
    private String openid;

    /**
     * 店铺ID
     */
    @Schema(description = "所在系统用户ID")
    private String sysUid;

    /**
     * 店铺名称
     */
    @Excel(name = "店铺名称", width = 15)
    @Schema(description = "店铺名称")
    private String merchantName;
    /**
     * 联系人ID
     */
    @Excel(name = "联系人ID", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Schema(description = "联系人ID")
    private String contactPersonId;
    /**
     * 联系人
     */
    @Excel(name = "联系人", width = 15)
    @Schema(description = "联系人")
    private String contactPerson;
    /**
     * 联系电话
     */
    @Excel(name = "联系电话", width = 15)
    @Schema(description = "联系电话")
    private String contactPhone;
    /**
     * 所属代理商ID
     */
    @Excel(name = "所属代理商ID", width = 15)
    @Schema(description = "所属代理商ID")
    private Integer agentId;
    /**
     * 当前套餐ID
     */
    @Excel(name = "当前套餐ID", width = 15)
    @Schema(description = "当前套餐ID")
    private Integer packageId;
    /**
     * 服务开始日期
     */
    @Excel(name = "服务开始日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "服务开始日期")
    private Date serviceStartDate;
    /**
     * 服务结束日期
     */
    @Excel(name = "服务结束日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "服务结束日期")
    private Date serviceEndDate;
    /**
     * 开通平台
     */
    @Excel(name = "开通平台", width = 15, dicCode = "platforms")
    @Dict(dicCode = "platforms")
    @Schema(description = "开通平台")
    private String enabledPlatforms;
    /**
     * 行业类型
     */
    @Excel(name = "行业类型", width = 15, dicCode = "industry_type")
    @Dict(dicCode = "industry_type")
    @Schema(description = "行业类型")
    private String industryType;
    /**
     * 店铺logoURL
     */
    @Excel(name = "店铺logoURL", width = 15)
    @Schema(description = "店铺logoURL")
    private String logoUrl;
    /**
     * 店铺状态
     */
    @Excel(name = "店铺状态", width = 15, dicCode = "merchant_status")
    @Dict(dicCode = "merchant_status")
    @Schema(description = "店铺状态")
    private String status;
    /**
     * 店铺关键词
     */
    @Excel(name = "店铺关键词", width = 15)
    @Schema(description = "店铺关键词")
    private String keywords;
    /**
     * 店铺简介
     */
    @Excel(name = "店铺简介", width = 15)
    @Schema(description = "店铺简介")
    private String description;
    /**
     * 店铺微信二维码
     */
    @Excel(name = "店铺微信二维码", width = 15)
    @Schema(description = "店铺微信二维码")
    private String wechatQrCode;
    /**
     * 店铺主图
     */
    @Excel(name = "店铺主图", width = 15)
    @Schema(description = "店铺主图")
    private String merchantMainPic;
    /**
     * 地区
     */
    @Excel(name = "地区", width = 15, exportConvert = true, importConvert = true)
    @Schema(description = "地区")
    private String region;

    public String convertisRegion() {
        return SpringContextUtils.getBean(ProvinceCityArea.class).getText(region);
    }

    public void convertsetRegion(String text) {
        this.region = SpringContextUtils.getBean(ProvinceCityArea.class).getCode(text);
    }

    /**
     * 详细地址
     */
    @Excel(name = "详细地址", width = 15)
    @Schema(description = "详细地址")
    private String detailAddress;


    /**
     * 小红书话题
     */
    @Excel(name = "小红书话题", width = 15)
    @Schema(description = "小红书话题")
    private String xhsTopics;

    /**
     * 店铺主页头像
     */
    @Excel(name = "店铺主页头像", width = 15)
    @Schema(description = "店铺主页头像")
    private String mainPic;

    /**
     * wifi用户
     */
    @Excel(name = "wifi用户", width = 15)
    @Schema(description = "wifi用户")
    private String wifiUser;

    /**
     * wifi密码
     */
    @Excel(name = "wifi密码", width = 15)
    @Schema(description = "wifi密码")
    private String wifiPwd;


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

    @TableField(exist = false) //表示该属性不为数据库表字段，但又是必须使用的。
    private String agentName;
}
