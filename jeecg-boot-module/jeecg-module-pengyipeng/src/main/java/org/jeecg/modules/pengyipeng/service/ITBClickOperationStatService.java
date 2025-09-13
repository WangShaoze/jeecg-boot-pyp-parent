package org.jeecg.modules.pengyipeng.service;

import org.jeecg.modules.pengyipeng.entity.TBClickOperationStat;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.pengyipeng.entity.TBMerchants;
import org.jeecg.modules.pengyipeng.entity.TBPlatform;

/**
 * @Description: 碰一碰点击操作统计表
 * @Author: jeecg-boot
 * @Date: 2025-08-30
 * @Version: V1.0
 */
public interface ITBClickOperationStatService extends IService<TBClickOperationStat> {
    boolean saveClickDataToDB(TBMerchants merchant, TBPlatform tbPlatform, Integer clickWifi);

    String getCurrentDate();

    String getYesterdayDate();

    TBClickOperationStat createANewObj(Integer merchantId);

    void getAllClickData(TBClickOperationStat tbClickOperationStatNew);

}
