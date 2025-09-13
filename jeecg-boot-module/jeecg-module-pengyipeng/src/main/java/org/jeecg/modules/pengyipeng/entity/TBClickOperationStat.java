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
 * @Description: 碰一碰点击操作统计表
 * @Author: jeecg-boot
 * @Date: 2025-08-30
 * @Version: V1.0
 */
@Data
@TableName("t_b_click_operation_stat")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "碰一碰点击操作统计表")
public class TBClickOperationStat implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private java.lang.Integer id;

    /**
     * 统计日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd ")
    @Excel(name = "统计日期", width = 15)
    @Schema(description = "统计日期")
    private java.util.Date statDate;

    /**
     * 所属店家ID
     */
    @Excel(name = "所属店家ID", width = 15)
    @Schema(description = "所属店家ID")
    private java.lang.Integer merchantId;

    /**
     * 总碰一下次数（只要进入网站就统计）
     */
    @Excel(name = "总碰一下次数", width = 15)
    @Schema(description = "总碰一下次数，只要进入网站就统计")
    private Integer totalEnterCount;

    /**
     * 点评收藏打卡总点击数（统计打卡收藏点评的所有数据）
     */
    @Excel(name = "点评收藏打卡总点击数", width = 15)
    @Schema(description = "点评收藏打卡总点击数，统计打卡收藏点评的所有数据")
    private Integer totalCheckinCollectCommentCount;

    /**
     * 大众点评点击数
     */
    @Excel(name = "大众点评点击数", width = 15)
    @Schema(description = "大众点评点击数")
    private Integer dianpingClickCount;

    /**
     * 高德点击数
     */
    @Excel(name = "高德点击数", width = 15)
    @Schema(description = "高德点击数")
    private Integer gaodeClickCount;

    /**
     * 抖音点击数
     */
    @Excel(name = "抖音点击数", width = 15)
    @Schema(description = "抖音点击数")
    private Integer douyinClickCount;


    /**
     * 美团点击数
     */
    @Excel(name = "美团点击数", width = 15)
    @Schema(description = "美团点击数")
    private Integer meituanClickCount;
    /**
     * 携程点击数
     */
    @Excel(name = "携程点击数", width = 15)
    @Schema(description = "携程点击数")
    private Integer ctripClickCount;
    /**
     * 小红书点击数
     */
    @Excel(name = "小红书点击数", width = 15)
    @Schema(description = "小红书点击数")
    private Integer xiaohongshuClickCount;
    /**
     * 朋友圈点击数
     */
    @Excel(name = "朋友圈点击数", width = 15)
    @Schema(description = "朋友圈点击数")
    private Integer momentsClickCount;
    /**
     * 加微信点击数
     */
    @Excel(name = "加微信点击数", width = 15)
    @Schema(description = "加微信点击数")
    private Integer addWechatClickCount;
    /**
     * 团购按钮总点击数（只要点击含有团购的按钮就统计）
     */
    @Excel(name = "团购按钮总点击数", width = 15)
    @Schema(description = "团购按钮总点击数，只要点击含有团购的按钮就统计")
    private Integer totalGroupBuyClickCount;
    /**
     * 大众点评团购点击数
     */
    @Excel(name = "大众点评团购点击数", width = 15)
    @Schema(description = "大众点评团购点击数")
    private Integer dianpingGroupBuyClickCount;
    /**
     * 抖音团购点击数
     */
    @Excel(name = "抖音团购点击数", width = 15)
    @Schema(description = "抖音团购点击数")
    private Integer douyinGroupBuyClickCount;
    /**
     * 美团团购点击数
     */
    @Excel(name = "美团团购点击数", width = 15)
    @Schema(description = "美团团购点击数")
    private Integer meituanGroupBuyClickCount;
    /**
     * WIFI按钮点击数
     */
    @Excel(name = "WIFI按钮点击数", width = 15)
    @Schema(description = "WIFI按钮点击数")
    private Integer wifiButtonClickCount;


    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private java.lang.String createBy;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private java.lang.String updateBy;

    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private java.util.Date updateTime;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private java.util.Date createTime;
}
