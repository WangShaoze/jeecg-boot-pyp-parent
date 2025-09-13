package org.jeecg.modules.pengyipeng.dto;

/*
 * ClassName: ds
 * Package: org.jeecg.modules.pengyipeng.dto
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/24 - 11:34
 * @Version: v1.0
 */

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AgentIndexDTO {
    private Integer agentId;
    private Integer licenseTotal;
    private Integer licenseLeave;
    private Integer licenseUsed;
    private Integer licenseExpired;
    private Integer licenseUpcomingExpired;
}
