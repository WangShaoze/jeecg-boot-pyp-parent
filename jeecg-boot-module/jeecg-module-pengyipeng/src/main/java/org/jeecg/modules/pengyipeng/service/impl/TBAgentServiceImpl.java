package org.jeecg.modules.pengyipeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.pengyipeng.dto.AgentIndexDTO;
import org.jeecg.modules.pengyipeng.dto.TotalIndexDTO;
import org.jeecg.modules.pengyipeng.entity.TBAgent;
import org.jeecg.modules.pengyipeng.entity.TBLicenses;
import org.jeecg.modules.pengyipeng.entity.TBMerchants;
import org.jeecg.modules.pengyipeng.mapper.TBAgentMapper;
import org.jeecg.modules.pengyipeng.service.ITBAgentService;
import org.jeecg.modules.pengyipeng.service.ITBLicensesService;
import org.jeecg.modules.pengyipeng.service.ITBMerchantsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * @Description: 运营商
 * @Author: jeecg-boot
 * @Date: 2025-08-13
 * @Version: V1.0
 */
@Service
public class TBAgentServiceImpl extends ServiceImpl<TBAgentMapper, TBAgent> implements ITBAgentService {

    @Autowired
    private ITBLicensesService licensesService;

    @Autowired
    @Lazy
    private ITBMerchantsService merchantsService;

    @Override
    public TBAgent getAgentBySysUid(String sysUid) {
        QueryWrapper<TBAgent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sys_uid", sysUid);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public AgentIndexDTO queryAgentIndexData(TBAgent agent) {
        return baseMapper.selectAgentIndexData(agent.getId());
    }

    @Override
    public TotalIndexDTO queryTotalIndexData() {
        return baseMapper.selectTotalIndexData();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)   // 如果任何一项数据库操作失败，就回滚所有操作
    public int batchAddLicenseCount(TBAgent tbAgent, List<Integer> licenseIds) {
        licenseIds.forEach(licenseId -> {
            TBLicenses tbLicenses = new TBLicenses();
            tbLicenses.setId(licenseId);
            tbLicenses.setAgentId(tbAgent.getId());
            licensesService.getBaseMapper().updateById(tbLicenses);
        });
        Integer totalCount = Integer.parseInt(licensesService.getByAgentId(tbAgent.getId()).toString());
        tbAgent.setLicenseTotal(totalCount);
        tbAgent.setLicenseLeave(totalCount - licensesService.getAgentLicenseUesed(tbAgent.getId()));
        baseMapper.updateById(tbAgent);
        return licenseIds.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void forbiddenAgent(TBAgent agent, TBAgent platformAgent) {
        agent.setStatus(0);  // 0 表示禁用
        baseMapper.updateById(agent);

        // 需要将未使用的证书回收
        QueryWrapper<TBLicenses> licensesUnUseQueryWrapper = new QueryWrapper<>();
        licensesUnUseQueryWrapper.eq("agent_id", agent.getId());
        licensesUnUseQueryWrapper.eq("status", 2); // 证书状态是 2 表示 已分配 但是还未激活的证书
        List<TBLicenses> tbLicensesUnuseList = licensesService.getBaseMapper().selectList(licensesUnUseQueryWrapper);
        if (!tbLicensesUnuseList.isEmpty()) {
            for (TBLicenses tbLicenses : tbLicensesUnuseList) {
                tbLicenses.setAgentId(null);
                tbLicenses.setStatus(1);   // 状态设置为 可用 表示为空闲可用的证书
            }
            licensesService.updateBatchById(tbLicensesUnuseList);  // 批量更新
        }

        // 需要将已使用的证书的代理设置总代理,对应店铺的代理设置为总代理
        QueryWrapper<TBMerchants> merchantsQueryWrapper = new QueryWrapper<>();
        merchantsQueryWrapper.eq("agent_id", agent.getId());
        merchantsQueryWrapper.in("status", Arrays.asList("ACTIVE", "SERVICE_END"));
        List<TBMerchants> merchantsList = merchantsService.getBaseMapper().selectList(merchantsQueryWrapper);
        if (!merchantsList.isEmpty()) {
            for (TBMerchants tbMerchants : merchantsList) {
                tbMerchants.setAgentId(platformAgent.getId());
            }
            merchantsService.updateBatchById(merchantsList);  // 批量更新
        }

        QueryWrapper<TBLicenses> licensesUsingQueryWrapper = new QueryWrapper<>();
        licensesUsingQueryWrapper.eq("agent_id", agent.getId());
        licensesUsingQueryWrapper.in("status", Arrays.asList(3, 4));   // 证书状态是3,4表示 已过期和使用中
        List<TBLicenses> tbLicensesList = licensesService.getBaseMapper().selectList(licensesUsingQueryWrapper);
        if (!tbLicensesList.isEmpty()) {
            for (TBLicenses tbLicenses : tbLicensesList) {
                tbLicenses.setAgentId(platformAgent.getId());
            }
            licensesService.updateBatchById(tbLicensesList);  // 批量更新
        }
    }

    @Override
    public boolean isSysUser(String sysUid) {
        Integer count = baseMapper.selectUserCount(sysUid);
        return null != count && count == 1;
    }

    @Override
    public void updateLicenseServiceCount() {
        baseMapper.updateLicenseServiceCount();
    }
}
