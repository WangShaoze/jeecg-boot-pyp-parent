package org.jeecg.modules.pengyipeng.service;

import org.jeecg.modules.pengyipeng.dto.AgentIndexDTO;
import org.jeecg.modules.pengyipeng.dto.TotalIndexDTO;
import org.jeecg.modules.pengyipeng.entity.TBAgent;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 运营商
 * @Author: jeecg-boot
 * @Date:   2025-08-13
 * @Version: V1.0
 */
public interface ITBAgentService extends IService<TBAgent> {

    TBAgent getAgentBySysUid(String sysUid);
    AgentIndexDTO queryAgentIndexData(TBAgent agent);
    TotalIndexDTO queryTotalIndexData();

    int batchAddLicenseCount(TBAgent tbAgent, List<Integer> licenseIds);

    void forbiddenAgent(TBAgent agent, TBAgent platformAgent);

    boolean isSysUser(String sysUid);

    void updateLicenseServiceCount();

}
