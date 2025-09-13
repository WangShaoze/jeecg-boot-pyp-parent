package org.jeecg.modules.pengyipeng.service.impl;

import org.jeecg.modules.pengyipeng.entity.TBDeviceGroups;
import org.jeecg.modules.pengyipeng.mapper.TBDeviceGroupsMapper;
import org.jeecg.modules.pengyipeng.service.ITBDeviceGroupsService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

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
@Service
public class TBDeviceGroupsServiceImpl extends ServiceImpl<TBDeviceGroupsMapper, TBDeviceGroups> implements ITBDeviceGroupsService {

    /**
     * 从URL获取输入流
     *
     * @param urlStr URL字符串
     * @return 输入流
     */
    @Override
    public InputStream getInputStreamFromUrl(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 设置请求方法和超时时间
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000); // 5秒连接超时
        connection.setReadTimeout(5000);    // 5秒读取超时
        // 检查响应码
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return connection.getInputStream();
        }
        return null;
    }
}
