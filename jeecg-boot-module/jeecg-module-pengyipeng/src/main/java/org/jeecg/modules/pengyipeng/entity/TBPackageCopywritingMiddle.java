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
 * @Description: 套餐和文案的中间表
 * @Author: jeecg-boot
 * @Date:   2025-08-28
 * @Version: V1.0
 */
@Data
@TableName("t_b_package_copywriting_middle")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="套餐和文案的中间表")
public class TBPackageCopywritingMiddle implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "ID")
    private java.lang.Integer id;
	/**文案ID*/
	@Excel(name = "文案ID", width = 15)
    @Schema(description = "文案ID")
    private java.lang.Integer copywritingId;
	/**套餐ID*/
	@Excel(name = "套餐ID", width = 15)
    @Schema(description = "套餐ID")
    private java.lang.Integer packageId;
}
