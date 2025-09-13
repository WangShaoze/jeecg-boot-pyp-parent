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
 * @Description: 设别分组表
 * @Author: jeecg-boot
 * @Date:   2025-08-13
 * @Version: V1.0
 */
@Data
@TableName("t_b_device_groups")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="设别分组表")
public class TBDeviceGroups implements Serializable {
    private static final long serialVersionUID = 1L;

	/**分组ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "分组ID")
    private Integer id;
	/**所属店家ID*/
	@Excel(name = "所属店家ID", width = 15)
    @Schema(description = "所属店家ID")
    private Integer merchantId;
	/**所属店家*/
	@Excel(name = "所属店家", width = 15)
    @Schema(description = "所属店家")
    private String merchantName;
    /**店家证书*/
	@Excel(name = "店家证书", width = 15)
    @Schema(description = "店家证书")
    private String merchantLicense;
	/**分组名称*/
	@Excel(name = "分组名称", width = 15)
    @Schema(description = "分组名称")
    private String groupName;
	/**分组描述*/
	@Excel(name = "分组描述", width = 15)
    @Schema(description = "分组描述")
    private String description;
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
