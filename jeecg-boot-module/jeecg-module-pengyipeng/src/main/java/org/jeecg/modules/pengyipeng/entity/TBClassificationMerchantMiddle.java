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
 * @Description: 标签分类中间表
 * @Author: jeecg-boot
 * @Date:   2025-09-18
 * @Version: V1.0
 */
@Data
@TableName("t_b_classification_merchant_middle")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="标签分类中间表")
public class TBClassificationMerchantMiddle implements Serializable {
    private static final long serialVersionUID = 1L;

	/**中间表ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "中间表ID")
    private java.lang.String id;
	/**商家表ID*/
	@Excel(name = "商家表ID", width = 15)
    @Schema(description = "商家表ID")
    private java.lang.Integer merchantId;
	/**可选分类表ID*/
	@Excel(name = "可选分类表ID", width = 15)
    @Schema(description = "可选分类表ID")
    private java.lang.String classificationOptionId;
	/**是否开启*/
	@Excel(name = "是否开启", width = 15)
    @Schema(description = "是否开启")
    private java.lang.String isOpen;
	/**图片列表*/
	@Excel(name = "图片列表", width = 15)
    @Schema(description = "图片列表")
    private java.lang.String picList;
}
