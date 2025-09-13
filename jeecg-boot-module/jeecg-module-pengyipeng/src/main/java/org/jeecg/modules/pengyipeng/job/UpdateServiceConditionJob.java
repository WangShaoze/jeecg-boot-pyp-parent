package org.jeecg.modules.pengyipeng.job;

/*
 * ClassName: UpdateServiceConditionJob
 * Package: org.jeecg.modules.pengyipeng.job
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/27 - 16:11
 * @Version: v1.0
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.pengyipeng.entity.TBLicenses;
import org.jeecg.modules.pengyipeng.entity.TBMerchants;
import org.jeecg.modules.pengyipeng.service.ITBAgentService;
import org.jeecg.modules.pengyipeng.service.ITBLicensesService;
import org.jeecg.modules.pengyipeng.service.ITBMerchantsService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class UpdateServiceConditionJob implements Job {

    @Autowired
    private ITBLicensesService licensesService;

    @Autowired
    private ITBMerchantsService merchantsService;

    @Autowired
    private ITBAgentService agentService;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 查询已激活的证书
        QueryWrapper<TBLicenses> licensesQueryWrapper = new QueryWrapper<>();
        licensesQueryWrapper.isNotNull("agent_id");
        licensesQueryWrapper.isNotNull("merchant_id");
        List<TBLicenses> licensesList = licensesService.getBaseMapper().selectList(licensesQueryWrapper);

        // 证书和商家的状态变更
        try {
            statusChange(licensesList);
        } catch (Exception e) {
            log.error("定时任务[org.jeecg.modules.pengyipeng.job.UpdateServiceConditionJob] -- [1]:" + e.getMessage());
        }

        // 查询过期的数量和即将过期的数量
        QueryWrapper<TBLicenses> licensesQueryWrapperCheck = new QueryWrapper<>();
        /*licensesQueryWrapperCheck.eq("status", 5).or().eq("status", 4);
        licensesQueryWrapperCheck.isNotNull("agent_id");
        licensesQueryWrapperCheck.isNotNull("merchant_id");*/
        licensesQueryWrapperCheck
                .and(wrapper -> wrapper.eq("status", 5).or().eq("status", 4))
                .isNotNull("merchant_id")
                .isNotNull("agent_id");
        Long checkCount = licensesService.getBaseMapper().selectCount(licensesQueryWrapperCheck);
        if (checkCount != null && checkCount > 0L) {
            // 代理商更新 【到期商家数量】【即将到期商家数量(提前5天)】
            try {
                agentService.updateLicenseServiceCount();
            } catch (Exception e) {
                log.error("定时任务[org.jeecg.modules.pengyipeng.job.UpdateServiceConditionJob] -- [2]:" + e.getMessage());
            }
        }

        log.info("定时任务[org.jeecg.modules.pengyipeng.job.UpdateServiceConditionJob] -- [3] 运行正常！");
    }

    @Transactional(rollbackFor = Exception.class)
    public void statusChange(List<TBLicenses> licensesList) {
        // 选择 （证书服务结束时间 - 当前时间） <= 5天  && （证书服务结束时间 - 当前时间） > 0天  即将过期
        // 选择  （证书服务结束时间 - 当前时间） <= 0天   过期
        Date currentDate = new Date();
        licensesList.forEach(o -> {
            Date serviceEndData = o.getEndDate();
            // 计算剩余天数
            long diffMillis = serviceEndData.getTime() - currentDate.getTime();
            long diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis);

            if (diffDays <= 5 && diffDays > 0) {
                // 证书状态 变为 即将过期=5
                // 商家状态不变
                o.setStatus(5);
                licensesService.updateById(o);
            } else if (diffDays <= 0) {
                // 证书状态变为 已过期 = 3
                o.setStatus(3);
                licensesService.updateById(o);
                // 商家状态 变为 服务到期 = SERVICE_END
                TBMerchants merchant = merchantsService.getById(o.getMerchantId());
                merchant.setStatus("SERVICE_END");
                merchantsService.updateById(merchant);
            }
        });
    }
}
