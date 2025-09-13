package org.jeecg.modules.pengyipeng.service;

import org.jeecg.modules.pengyipeng.entity.TBDeviceGroups;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Description: 设别分组表
 * @Author: jeecg-boot
 * @Date:   2025-08-13
 * @Version: V1.0
 */
public interface ITBDeviceGroupsService extends IService<TBDeviceGroups> {
    /**
     * 从URL获取输入流
     *
     * @param urlStr URL字符串
     * @return 输入流
     */
    InputStream getInputStreamFromUrl(String urlStr) throws IOException;

}
