package org.jeecg.modules.pengyipeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.pengyipeng.entity.TBClickOperationStat;
import org.jeecg.modules.pengyipeng.entity.TBMerchants;
import org.jeecg.modules.pengyipeng.entity.TBPlatform;
import org.jeecg.modules.pengyipeng.mapper.TBClickOperationStatMapper;
import org.jeecg.modules.pengyipeng.service.ITBClickOperationStatService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Description: 碰一碰点击操作统计表
 * @Author: jeecg-boot
 * @Date: 2025-08-30
 * @Version: V1.0
 */
@Service
public class TBClickOperationStatServiceImpl extends ServiceImpl<TBClickOperationStatMapper, TBClickOperationStat> implements ITBClickOperationStatService {

    @Override
    public boolean saveClickDataToDB(TBMerchants merchant, TBPlatform tbPlatform, Integer clickWifi) {
        String formattedDate = getCurrentDate();
        QueryWrapper<TBClickOperationStat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("merchant_id", merchant.getId());
        queryWrapper.eq("stat_date", formattedDate);
        try {
            saveClickPlatform(merchant, queryWrapper, tbPlatform, clickWifi);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveClickPlatform(TBMerchants merchant, QueryWrapper<TBClickOperationStat> queryWrapper, TBPlatform tbPlatform, Integer clickWifi) {
        TBClickOperationStat tbClickOperationStat = baseMapper.selectOne(queryWrapper);
        if (tbClickOperationStat == null) {
            tbClickOperationStat = new TBClickOperationStat();
            tbClickOperationStat.setMerchantId(merchant.getId());
            tbClickOperationStat.setStatDate(new Date());
            if (tbPlatform != null) {
                switch (tbPlatform.getName()) {
                    case "gaode":
                        tbClickOperationStat.setGaodeClickCount(1);
                        tbClickOperationStat.setTotalCheckinCollectCommentCount(1);
                        break;
                    case "wechat_friend":
                        tbClickOperationStat.setMomentsClickCount(1);
                        break;
                    case "douyin":
                        tbClickOperationStat.setDouyinClickCount(1);
                        tbClickOperationStat.setTotalCheckinCollectCommentCount(1);
                        break;
                    case "douyin_tuan":
                        tbClickOperationStat.setDouyinGroupBuyClickCount(1);
                        tbClickOperationStat.setTotalGroupBuyClickCount(1);
                        break;
                    case "meituan":
                        tbClickOperationStat.setMeituanClickCount(1);
                        tbClickOperationStat.setTotalCheckinCollectCommentCount(1);
                        break;
                    case "xiecheng":
                        tbClickOperationStat.setCtripClickCount(1);
                        tbClickOperationStat.setTotalCheckinCollectCommentCount(1);
                        break;
                    case "dazhong_tuan":
                        tbClickOperationStat.setDianpingGroupBuyClickCount(1);
                        tbClickOperationStat.setTotalGroupBuyClickCount(1);
                        break;
                    case "dazhong":
                        tbClickOperationStat.setDianpingClickCount(1);
                        tbClickOperationStat.setTotalCheckinCollectCommentCount(1);
                        break;
                    case "meituan_tuan":
                        tbClickOperationStat.setMeituanGroupBuyClickCount(1);
                        tbClickOperationStat.setTotalGroupBuyClickCount(1);
                        break;
                    case "xiaohongshu":
                        tbClickOperationStat.setXiaohongshuClickCount(1);
                        break;
                    case "wechat":
                        tbClickOperationStat.setAddWechatClickCount(1);
                        break;
                    default:
                        break;
                }
            } else if (null != clickWifi && clickWifi.equals(1)) {
                tbClickOperationStat.setWifiButtonClickCount(1);
            }
            tbClickOperationStat.setTotalEnterCount(1);
            save(tbClickOperationStat);
        } else {
            if (tbPlatform != null) {
                switch (tbPlatform.getName()) {
                    case "gaode":
                        tbClickOperationStat.setGaodeClickCount(tbClickOperationStat.getGaodeClickCount() + 1);
                        tbClickOperationStat.setTotalCheckinCollectCommentCount(tbClickOperationStat.getTotalCheckinCollectCommentCount() + 1);
                        break;
                    case "wechat_friend":
                        tbClickOperationStat.setMomentsClickCount(tbClickOperationStat.getMomentsClickCount() + 1);
                        break;
                    case "douyin":
                        tbClickOperationStat.setDouyinClickCount(tbClickOperationStat.getDouyinClickCount() + 1);
                        tbClickOperationStat.setTotalCheckinCollectCommentCount(tbClickOperationStat.getTotalCheckinCollectCommentCount() + 1);
                        break;
                    case "douyin_tuan":
                        tbClickOperationStat.setDouyinGroupBuyClickCount(tbClickOperationStat.getDouyinGroupBuyClickCount() + 1);
                        tbClickOperationStat.setTotalGroupBuyClickCount(tbClickOperationStat.getTotalGroupBuyClickCount() + 1);
                        break;
                    case "meituan":
                        tbClickOperationStat.setMeituanClickCount(tbClickOperationStat.getMeituanClickCount() + 1);
                        tbClickOperationStat.setTotalCheckinCollectCommentCount(tbClickOperationStat.getTotalCheckinCollectCommentCount() + 1);
                        break;
                    case "xiecheng":
                        tbClickOperationStat.setCtripClickCount(tbClickOperationStat.getCtripClickCount() + 1);
                        tbClickOperationStat.setTotalCheckinCollectCommentCount(tbClickOperationStat.getTotalCheckinCollectCommentCount() + 1);
                        break;
                    case "dazhong_tuan":
                        tbClickOperationStat.setDianpingGroupBuyClickCount(tbClickOperationStat.getDianpingGroupBuyClickCount() + 1);
                        tbClickOperationStat.setTotalGroupBuyClickCount(tbClickOperationStat.getTotalGroupBuyClickCount() + 1);
                        break;
                    case "dazhong":
                        tbClickOperationStat.setDianpingClickCount(tbClickOperationStat.getDianpingClickCount() + 1);
                        tbClickOperationStat.setTotalCheckinCollectCommentCount(tbClickOperationStat.getTotalCheckinCollectCommentCount() + 1);
                        break;
                    case "meituan_tuan":
                        tbClickOperationStat.setMeituanGroupBuyClickCount(tbClickOperationStat.getMeituanGroupBuyClickCount());
                        tbClickOperationStat.setTotalGroupBuyClickCount(tbClickOperationStat.getTotalGroupBuyClickCount() + 1);
                        break;
                    case "xiaohongshu":
                        tbClickOperationStat.setXiaohongshuClickCount(tbClickOperationStat.getTotalCheckinCollectCommentCount() + 1);
                        break;
                    case "wechat":
                        tbClickOperationStat.setAddWechatClickCount(tbClickOperationStat.getAddWechatClickCount() + 1);
                        break;
                    default:
                        break;
                }
            } else if (null != clickWifi && clickWifi.equals(1)) {
                tbClickOperationStat.setWifiButtonClickCount(tbClickOperationStat.getWifiButtonClickCount() + 1);
            }
            tbClickOperationStat.setTotalEnterCount(tbClickOperationStat.getTotalEnterCount() + 1);
            updateById(tbClickOperationStat);
        }
    }

    @Override
    public String getCurrentDate() {
        // 获取当前时间
        Date today = new Date();
        // 定义日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 格式化日期
        return sdf.format(today);
    }

    @Override
    public String getYesterdayDate() {
        // 获取日历实例并设置为当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        // 将日期减1天，即得到昨天
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        // 定义日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // 格式化昨天的日期并返回
        return sdf.format(calendar.getTime());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TBClickOperationStat createANewObj(Integer merchantId) {
        TBClickOperationStat tbClickOperationStat = new TBClickOperationStat();
        tbClickOperationStat.setMerchantId(merchantId);
        // 获取日历实例并设置为当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        // 将日期减1天，即得到昨天
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        tbClickOperationStat.setStatDate(calendar.getTime());
        tbClickOperationStat.setTotalEnterCount(1);
        tbClickOperationStat.setTotalGroupBuyClickCount(0);
        tbClickOperationStat.setTotalCheckinCollectCommentCount(0);
        tbClickOperationStat.setDianpingClickCount(0);
        tbClickOperationStat.setGaodeClickCount(0);
        tbClickOperationStat.setDouyinClickCount(0);
        tbClickOperationStat.setMeituanClickCount(0);
        tbClickOperationStat.setCtripClickCount(0);
        tbClickOperationStat.setXiaohongshuClickCount(0);
        tbClickOperationStat.setMomentsClickCount(0);
        tbClickOperationStat.setAddWechatClickCount(0);
        tbClickOperationStat.setDianpingGroupBuyClickCount(0);
        tbClickOperationStat.setDouyinGroupBuyClickCount(0);
        tbClickOperationStat.setMeituanGroupBuyClickCount(0);
        tbClickOperationStat.setWifiButtonClickCount(0);
        baseMapper.insert(tbClickOperationStat);
        return tbClickOperationStat;
    }

    @Override
    public void getAllClickData(TBClickOperationStat n) {
        List<TBClickOperationStat> clickOperationStatList = baseMapper.selectList(new QueryWrapper<>());
        for (TBClickOperationStat o : clickOperationStatList) {
            if (n.getMerchantId() == null) {
                n.setMerchantId(o.getMerchantId());
            }
            n.setTotalEnterCount(getValueOrDefault(n.getTotalEnterCount()) + o.getTotalEnterCount());
            n.setTotalCheckinCollectCommentCount(getValueOrDefault(n.getTotalCheckinCollectCommentCount()) + o.getTotalCheckinCollectCommentCount());
            n.setTotalGroupBuyClickCount(getValueOrDefault(n.getTotalGroupBuyClickCount()) + o.getTotalGroupBuyClickCount());
            n.setDianpingClickCount(getValueOrDefault(n.getDianpingClickCount()) + o.getDianpingClickCount());
            n.setGaodeClickCount(getValueOrDefault(n.getGaodeClickCount()) + o.getGaodeClickCount());
            n.setDouyinClickCount(getValueOrDefault(n.getDouyinClickCount()) + o.getDouyinClickCount());
            n.setMeituanClickCount(getValueOrDefault(n.getMeituanClickCount()) + o.getMeituanClickCount());
            n.setCtripClickCount(getValueOrDefault(n.getCtripClickCount()) + o.getCtripClickCount());
            n.setXiaohongshuClickCount(getValueOrDefault(n.getXiaohongshuClickCount()) + o.getXiaohongshuClickCount());
            n.setMomentsClickCount(getValueOrDefault(n.getMomentsClickCount()) + o.getMomentsClickCount());
            n.setAddWechatClickCount(getValueOrDefault(n.getAddWechatClickCount()) + o.getAddWechatClickCount());
            n.setDianpingGroupBuyClickCount(getValueOrDefault(n.getDianpingGroupBuyClickCount()) + o.getDianpingGroupBuyClickCount());
            n.setDouyinGroupBuyClickCount(getValueOrDefault(n.getDouyinGroupBuyClickCount()) + o.getDouyinGroupBuyClickCount());
            n.setMeituanGroupBuyClickCount(getValueOrDefault(n.getMeituanGroupBuyClickCount()) + o.getMeituanGroupBuyClickCount());
            n.setWifiButtonClickCount(getValueOrDefault(n.getWifiButtonClickCount()) + o.getWifiButtonClickCount());
        }
    }

    private Integer getValueOrDefault(Integer value) {
        return value != null ? value : 0;
    }
}
