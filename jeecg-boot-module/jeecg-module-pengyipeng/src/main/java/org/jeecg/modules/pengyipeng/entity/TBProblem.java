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
 * @Description: 问题反馈表
 * @Author: jeecg-boot
 * @Date:   2025-09-07
 * @Version: V1.0
 */
@Data
@TableName("t_b_problem")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="问题反馈表")
public class TBProblem implements Serializable {
    private static final long serialVersionUID = 1L;

    /**问题ID*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "问题ID")
    private java.lang.String id;
    /**问题标题*/
    @Excel(name = "问题标题", width = 15)
    @Schema(description = "问题标题")
    private java.lang.String problemTitle;
    /**问题类型*/
    @Excel(name = "问题类型", width = 15, dicCode = "pyp_problem_type")
    @Dict(dicCode = "pyp_problem_type")
    @Schema(description = "问题类型")
    private java.lang.String problemType;
    /**问题状态*/
    @Excel(name = "问题状态", width = 15, dicCode = "pyp_problem_deal_status")
    @Dict(dicCode = "pyp_problem_deal_status")
    @Schema(description = "问题状态")
    private java.lang.String problemStatus;
    /**优先级*/
    @Excel(name = "优先级", width = 15, dicCode = "pyp_problem_priority")
    @Dict(dicCode = "pyp_problem_priority")
    @Schema(description = "优先级")
    private java.lang.String priority;
    /**问题详细描述*/
    @Excel(name = "问题详细描述", width = 15)
    @Schema(description = "问题详细描述")
    private java.lang.String problemDesc;
    /**问题图片URL列表*/
    @Excel(name = "问题图片URL列表", width = 15)
    @Schema(description = "问题图片URL列表")
    private java.lang.String problemPicList;
    /**关闭原因*/
    @Excel(name = "关闭原因", width = 15)
    @Schema(description = "关闭原因")
    private java.lang.String closeReason;
    /**问题关闭时间*/
    @Excel(name = "问题关闭时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "问题关闭时间")
    private java.util.Date closeTime;
    /**逻辑删除*/
    @Excel(name = "逻辑删除", width = 15)
    @Schema(description = "逻辑删除")
    private java.lang.Integer isDeleted;
    /**创建人*/
    @Schema(description = "创建人")
    private java.lang.String createBy;
    /**创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间 ")
    private java.util.Date createTime;
    /**更新人*/
    @Schema(description = "更新人")
    private java.lang.String updateBy;
    /**更新时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private java.util.Date updateTime;
}
