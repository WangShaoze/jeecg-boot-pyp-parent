package org.jeecg.modules.pengyipeng.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.pengyipeng.dto.AgentIndexDTO;
import org.jeecg.modules.pengyipeng.dto.TotalIndexDTO;
import org.jeecg.modules.pengyipeng.entity.TBAgent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 运营商
 * @Author: jeecg-boot
 * @Date: 2025-08-13
 * @Version: V1.0
 */
public interface TBAgentMapper extends BaseMapper<TBAgent> {

    @Select("SELECT\n" +
            "  ag.id AS \"agentId\",\n" +
            "  COALESCE(ag.license_total, 0) AS \"licenseTotal\",\n" +
            "  COALESCE(ag.license_leave, 0) AS \"licenseLeave\",\n" +
            "  COALESCE(ag.license_used, 0) AS \"licenseUsed\",\n" +
            "  COALESCE(ag.license_expired, 0) AS \"licenseExpired\",\n" +
            "  COALESCE(ag.license_upcoming_expired, 0) AS \"licenseUpcomingExpired\"\n" +
            "FROM t_b_agent ag \n" +
            "WHERE ag.id = #{agentId};")
    AgentIndexDTO selectAgentIndexData(@Param("agentId") Integer agentId);


    @Select("SELECT (select count(*) from t_b_merchants) as merchantNumber, (select count(*) from t_b_agent) as agentNumber;")
    TotalIndexDTO selectTotalIndexData();


    @Select("select count(*) from sys_user where id = #{userId}")
    Integer selectUserCount(@Param("userId") String userId);


    @Update("UPDATE t_b_agent a\n" +
            "         JOIN (\n" +
            "         SELECT\n" +
            "         agent_id,\n" +
            "         SUM(CASE WHEN STATUS = 5 THEN 1 ELSE 0 END) AS upcoming_cnt,\n" +
            "         SUM(CASE WHEN STATUS = 3 THEN 1 ELSE 0 END) AS expired_cnt \n" +
            "         FROM\n" +
            "         t_b_licenses \n" +
            "         WHERE\n" +
            "         agent_id IS NOT NULL \n" +
            "         AND merchant_id IS NOT NULL \n" +
            "         GROUP BY\n" +
            "         agent_id) t ON a.id = t.agent_id \n" +
            "         SET a.license_upcoming_expired = t.upcoming_cnt,\n" +
            "         a.license_expired = t.expired_cnt;")
    void updateLicenseServiceCount();
}
