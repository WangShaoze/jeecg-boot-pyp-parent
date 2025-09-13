package org.jeecg.modules.pengyipeng.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.pengyipeng.dto.XhsAuthInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class XhsAuthService {
    @Value("${app.appKey}")
    private String appKey;

    @Value("${app.appSecret}")
    private String appSecret;


    @Value("${app.nonce}")
    private String nonce;

    @Value("${app.token_url}")
    private String tokenUrl;


    @Autowired
    private RedisUtil redisUtil;


    public String buildSignature(String accessToken) throws Exception {
        long timestamp = getTimestamp();
        Map<String, String> params = Maps.newHashMap();
        params.put("appKey", appKey);
        params.put("nonce", nonce);
        params.put("timeStamp", Long.toString(timestamp));
        if (accessToken != null) {   // 第二次加签，是有小红书平台获取的access_token
            params.put("access_token", accessToken);
        }
        return generateSignature(appSecret, params);
    }

    private static long getTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 构建
     *
     * @param secretKey 密钥
     * @param params    加签参数
     * @return 签名
     */
    public static String generateSignature(String secretKey, Map<String, String> params) {
        // Step 1: Sort parameters by key
        Map<String, String> sortedParams = new TreeMap<>(params);
        // Step 2: Concatenate sorted parameters
        StringBuilder paramsString = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (paramsString.length() > 0) {
                paramsString.append("&");
            }
            paramsString.append(entry.getKey()).append("=").append(entry.getValue());
        }
        // Step 3: Add secret key to the parameter string
        paramsString.append(secretKey);
        // Step 4: Calculate signature using SHA-256
        String signature = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(paramsString.toString().getBytes(StandardCharsets.UTF_8));
            // Convert the byte array to hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            signature = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
        return signature;
    }


    public XhsAuthInfoDTO getAccessToken() throws Exception {
        if (redisUtil.hasKey("xhs_auth_token")) {
            String xhsAuthToken = (String) redisUtil.get("xhs_auth_token");
            if (!StringUtils.isEmpty(xhsAuthToken)) {
                XhsAuthInfoDTO xhsAuthInfoDTO = new XhsAuthInfoDTO();
                xhsAuthInfoDTO.setAppKey(appKey);
                xhsAuthInfoDTO.setNonce(nonce);
                xhsAuthInfoDTO.setTimestamp(Long.toString(getTimestamp()));
                return xhsAuthInfoDTO;
            } else {
                redisUtil.del("xhs_auth_token");
                return getAccessToken();
            }

        }
        // 1. 构造参数
        long timestamp = getTimestamp();

        // 2. 拼接签名字符串
        /*String signStr = "appKey=" + appKey +
                "&appSecret=" + appSecret +
                "&nonce=" + nonce +
                "&timeStamp=" + timestamp;*/

        // 3. 生成 SHA256 签名
        //String signature = DigestUtil.sha256Hex(signStr);
        String signature = buildSignature(null); // 第一次加签，获取signature的时候Access_token是NULL


        // 4. 构造请求体
        Map<String, Object> payload = new HashMap<>();
        payload.put("app_key", appKey);
        payload.put("nonce", nonce);
        payload.put("timestamp", timestamp);
        payload.put("signature", signature);

        // 5. 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 6. 构造请求
        HttpEntity<String> entity = new HttpEntity<>(JSONObject.toJSONString(payload), headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                entity,
                String.class
        );

        // 7. 处理请求
        String body = response.getBody();
        JSONObject json = JSONObject.parseObject(body);
        Long expires_in = json.getLong("expires_in");
        String accessToken = json.getString("access_token");
        redisUtil.set("xhs_auth_token", accessToken, expires_in / 1000);
        XhsAuthInfoDTO xhsAuthInfoDTO = new XhsAuthInfoDTO();
        xhsAuthInfoDTO.setAppKey(appKey);
        xhsAuthInfoDTO.setNonce(nonce);
        xhsAuthInfoDTO.setTimestamp(Long.toString(timestamp));
        xhsAuthInfoDTO.setSignature(buildSignature(accessToken));
        return xhsAuthInfoDTO;
    }
}
