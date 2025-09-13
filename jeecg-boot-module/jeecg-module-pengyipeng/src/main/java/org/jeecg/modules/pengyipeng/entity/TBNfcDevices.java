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
 * @Description: 设备表
 * @Author: jeecg-boot
 * @Date: 2025-08-13
 * @Version: V1.0
 */
@Data
@TableName("t_b_nfc_devices")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "设备表")
public class TBNfcDevices implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "设备ID")
    private Integer id;
    /**
     * 设备类型
     */
    @Excel(name = "设备类型", width = 15, dicCode = "device_type")
    @Dict(dicCode = "device_type")
    @Schema(description = "设备类型")
    private Integer type;
    /**
     * NFC标识
     */
    @Excel(name = "NFC标识", width = 15)
    @Schema(description = "NFC标识")
    private String nfcUid;
    /**
     * 二维码标识
     */
    @Excel(name = "二维码标识", width = 15)
    @Schema(description = "二维码标识")
    private String qrCodeUid;
    /**
     * 所属店家ID
     */
    @Excel(name = "所属店家ID", width = 15)
    @Schema(description = "所属店家ID")
    private Integer merchantId;
    /**
     * 所属店家
     */
    @Excel(name = "所属店家", width = 15)
    @Schema(description = "所属店家")
    private String merchantName;
    /**
     * 证书店家
     */
    @Excel(name = "证书店家", width = 15)
    @Schema(description = "证书店家")
    private String merchantLicense;
    /**
     * 分组ID
     */
    @Excel(name = "分组ID", width = 15)
    @Schema(description = "分组ID")
    private Integer groupId;
    /**
     * 所属分组
     */
    @Excel(name = "所属分组", width = 15)
    @Schema(description = "所属分组")
    private String groupName;
    /**
     * 设备名称
     */
    @Excel(name = "设备名称", width = 15)
    @Schema(description = "设备名称")
    private String name;
    /**
     * 状态
     */
    @Excel(name = "状态", width = 15, dicCode = "device_status")
    @Dict(dicCode = "device_status")
    @Schema(description = "状态")
    private Integer status;
    /**
     * 最后活跃时间
     */
    @Excel(name = "最后活跃时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后活跃时间")
    private Date lastActiveTime;
    /**
     * 绑定码
     */
    @Excel(name = "绑定码", width = 15)
    @Schema(description = "绑定码")
    private String bindingCode;
    /**
     * 绑定码过期时间
     */
    @Excel(name = "绑定码过期时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "绑定码过期时间")
    private Date bindingExpireTime;
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
