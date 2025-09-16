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
 * @Description: 标签表
 * @Author: jeecg-boot
 * @Date:   2025-09-16
 * @Version: V1.0
 */
@Data
@TableName("t_b_tag")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="标签表")
public class TBTag implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private java.lang.String id;
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
	/**所属部门*/
    @Schema(description = "所属部门")
    private java.lang.String sysOrgCode;
	/**是否套餐关键词*/
	@Excel(name = "是否套餐关键词", width = 15, dicCode = "pyp_true_false")
	@Dict(dicCode = "pyp_true_false")
    @Schema(description = "是否套餐关键词")
    private java.lang.String isPackageKeyword;
	/**是否是大分类*/
	@Excel(name = "是否是大分类", width = 15, dicCode = "pyp_true_false")
	@Dict(dicCode = "pyp_true_false")
    @Schema(description = "是否是大分类")
    private java.lang.String isBigClassification;
	/**店家ID*/
	@Excel(name = "店家ID", width = 15)
    @Schema(description = "店家ID")
    private java.lang.Integer merchantId;
	/**套餐ID*/
	@Excel(name = "套餐ID", width = 15)
    @Schema(description = "套餐ID")
    private java.lang.String packageId;
	/**关键词（标签）*/
	@Excel(name = "关键词（标签）", width = 15)
    @Schema(description = "关键词（标签）")
    private java.lang.String keyword;
	/**图片列表*/
	@Excel(name = "图片列表", width = 15)
    @Schema(description = "图片列表")
    private java.lang.String picList;
	/**所属大标签ID*/
	@Excel(name = "所属大标签ID", width = 15)
    @Schema(description = "所属大标签ID")
    private java.lang.String parentId;
}
