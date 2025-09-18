package org.jeecg.modules.pengyipeng.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.pengyipeng.entity.TBMerchantPlatformMiddle;
import org.jeecg.modules.pengyipeng.mapper.TBMerchantPlatformMiddleMapper;
import org.jeecg.modules.pengyipeng.service.ITBMerchantPlatformMiddleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 商家和平台中间表
 * @Author: jeecg-boot
 * @Date: 2025-08-25
 * @Version: V1.0
 */
@Service
public class TBMerchantPlatformMiddleServiceImpl extends ServiceImpl<TBMerchantPlatformMiddleMapper, TBMerchantPlatformMiddle> implements ITBMerchantPlatformMiddleService {
    @Autowired
    private PlaywrightServiceImpl playwrightService;

    private static Map<String, Object> parseQueryParams(String url) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String baseUrl = url.split("\\?")[0];
        result.put("finalUrl", baseUrl);
        result.put("queryParams", getQueryParams(url));
        return result;
    }

    private static Map<String, String> getQueryParams(String url) throws Exception {
        // 解析 query 参数
        Map<String, String> queryPairs = new LinkedHashMap<>();
        URI uri = new URI(url);
        String query = uri.getRawQuery();
        if (query == null) {
            return queryPairs;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
            String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
            queryPairs.put(key, value);
        }
        return queryPairs;
    }

    private static String getShortLinkUrl(String text) {
        // 正则：匹配 http/https + 域名 + 路径 + 可选参数
        Pattern pattern = Pattern.compile("(https?://[\\w\\.-]+(?:/[\\w\\d\\-._~:/?#\\[\\]@!$&'()*+,;=%]*)?)");
        Matcher matcher = pattern.matcher(text);
        List<String> list = new ArrayList<String>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list.get(list.size() - 1);
    }

    private static String getFirstUrl(String text) {
        // 正则：匹配 http/https + 域名 + 路径 + 可选参数
        Pattern pattern = Pattern.compile("(https?://[\\w\\.-]+(?:/[\\w\\d\\-._~:/?#\\[\\]@!$&'()*+,;=%]*)?)");
        Matcher matcher = pattern.matcher(text);
        List<String> list = new ArrayList<String>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list.get(0);
    }


    @Override
    public String parseShortLink(String shortLinkStr, String platformName) {
        // 点评，抖音，高德，美团是解析和替换，发朋友圈和携程是固定的，加微信和小红书需要对接一下各自的平台
        String parseResult = null;
        if (platformName.equals("xiaohongshu")) {
            //   小红书
            parseResult = "xhsdiscover://post_note";
            return parseResult;
        }
        Map<String, Object> rsl = null;
        if (!StringUtils.isEmpty(shortLinkStr)) {
            String url = getShortLinkUrl(shortLinkStr);
            try {
                String newUrl;
                if (!platformName.equals("dazhong")){
                    newUrl = playwrightService.getFinalUrl(url);
                }else{
                    newUrl = url;
                }
                rsl = parseQueryParams(getShortLinkUrl(newUrl));
                System.out.println(rsl);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        assert rsl != null;
        if (platformName.equals("xiecheng")) {
            parseResult = "ctrip://wireless";
        } else if (platformName.equals("wechat_friend")) {
            parseResult = "weixin://";
        } else if (platformName.equals("dazhong")) {
            String finalUrl = (String) rsl.get("finalUrl");
            parseResult = "dianping://shopinfo?shopuuid=" + finalUrl.substring(finalUrl.lastIndexOf("/")+1);
        } else if (platformName.equals("douyin")) {
            Map<String, String> queryParams = (Map<String, String>) rsl.get("queryParams");
            String poiId = queryParams.get("poi_id");
            parseResult = "snssdk1128://poi/detail/?id=" + poiId + "&track_enter_detail=1&enter_from=link";
        } else if (platformName.equals("gaode")) {
            Map<String, String> queryParams = (Map<String, String>) rsl.get("queryParams");
            List<String> pParams = Arrays.asList(queryParams.get("p").split(","));
            String poiid = pParams.get(0);
            String lat = pParams.get(1);
            String lon = pParams.get(2);
            String poiname = pParams.get(3);
            parseResult = "amapuri://poi/detail?poiname=" + poiname + "&lat=" + lat + "&lon=" + lon + "&poiid=" + poiid;
        } else if (platformName.equals("meituan")) {
            Map<String, String> queryParams = (Map<String, String>) rsl.get("queryParams");
            String shortUrl = queryParams.get("url");
            parseResult = "imeituan://www.meituan.com/food/poi/detail?id=" + shortUrl;
        }
        return parseResult;
    }
}
