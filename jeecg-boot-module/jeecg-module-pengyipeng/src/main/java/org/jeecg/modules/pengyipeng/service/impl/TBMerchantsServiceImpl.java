package org.jeecg.modules.pengyipeng.service.impl;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.net.url.UrlBuilder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xkcoding.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.pengyipeng.dto.ClassificationDTO;
import org.jeecg.modules.pengyipeng.dto.MerchantInfoResponseDTO;
import org.jeecg.modules.pengyipeng.dto.MerchantLoginDto;
import org.jeecg.modules.pengyipeng.dto.MerchantServiceInfoDTO;
import org.jeecg.modules.pengyipeng.entity.*;
import org.jeecg.modules.pengyipeng.mapper.TBMerchantsMapper;
import org.jeecg.modules.pengyipeng.service.*;
//import org.jeecg.modules.pengyipeng.service.ITBTagService;
import org.jeecg.modules.pengyipeng.utils.RandomUtil;
import org.jeecg.modules.pengyipeng.utils.UUIDGenerator;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import cn.binarywang.wx.miniapp.api.WxMaService;


import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 店铺表
 * @Author: jeecg-boot
 * @Date: 2025-08-12
 * @Version: V1.0
 */
@Slf4j
@Service
public class TBMerchantsServiceImpl extends ServiceImpl<TBMerchantsMapper, TBMerchants> implements ITBMerchantsService {
    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ITBLicensesService licensesService;

    @Autowired
    @Lazy
    private ITBAgentService agentService;

    @Autowired
    private WxMaService wxMaService;
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private BaseCommonService baseCommonService;

    @Autowired
    private ITBClassificationMerchantMiddleService classificationMerchantMiddleService;


    public IPage<MerchantServiceInfoDTO> getMerchantServiceInfo(Page<MerchantServiceInfoDTO> page,
                                                                String agentSysUid,
                                                                Integer licenseStatus,
                                                                String column,
                                                                String order) {
        return baseMapper.getMerchantServiceInfo(page, agentSysUid, licenseStatus, column, order);
    }

    @Override
    public TBMerchants getByLicenseKey(String merchantLicense) {
        return baseMapper.selectByLicenseKey(merchantLicense);
    }


    @Override
    public Integer queryMerchantIdByLicenseKey(String licenseKey) {
        QueryWrapper<TBLicenses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("license_key", licenseKey);
        TBLicenses tbLicenses = licensesService.getBaseMapper().selectOne(queryWrapper);
        if (tbLicenses == null || tbLicenses.getMerchantId() == null) {
            return 0;
        }
        if (tbLicenses.getStatus() == 1) {
            log.info("商家:{} 证书：{} 状态：{}, 状态寓意: 商家的证书不可用。", tbLicenses.getMerchantId(), tbLicenses.getLicenseKey(), tbLicenses.getStatus());
            return -1;
        }
        if (tbLicenses.getStatus() == 3) {
            log.info("商家:{} 证书：{} 状态：{}, 状态寓意: 商家的证书过期。", tbLicenses.getMerchantId(), tbLicenses.getLicenseKey(), tbLicenses.getStatus());
            return -1;
        }
        return tbLicenses.getMerchantId();
    }

    @Override
    public void setInfoForResp(TBMerchants tBMerchants, MerchantInfoResponseDTO responseDTO) {
        responseDTO.setId(tBMerchants.getId());
        responseDTO.setMerchantName(tBMerchants.getMerchantName());

        if (StringUtils.isEmpty(tBMerchants.getEnabledPlatforms())) {
            tBMerchants.setEnabledPlatforms("3,4,8");
        }
        responseDTO.setEnabledPlatforms(Arrays.stream(tBMerchants.getEnabledPlatforms().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
        responseDTO.setKeywords(JSON.parseArray(tBMerchants.getKeywords(), String.class));
        responseDTO.setWechatQrCode(tBMerchants.getWechatQrCode());
        responseDTO.setMerchantMainPic(tBMerchants.getMerchantMainPic());
        responseDTO.setXhsTopics(tBMerchants.getXhsTopics());
        responseDTO.setMainPic(tBMerchants.getMainPic());
        responseDTO.setWifiUser(tBMerchants.getWifiUser());
        responseDTO.setWifiPwd(tBMerchants.getWifiPwd());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void renewForMerchant(TBMerchants tbMerchants, TBAgent tbAgent) {

        // 修改证书的状态 （已过期 3 ---》 使用中 4） 和 服务结束时间
        TBLicenses tbLicenses = licensesService.getByMerchantId(tbMerchants.getId());
        tbLicenses.setStatus(4);

        Date endDate = tbLicenses.getEndDate();
        Instant endInstant = endDate.toInstant();
        // 增加2年
        Instant twoYearsLaterInstant = endInstant.plus(2, ChronoUnit.YEARS);
        // 转回Date类型（如果需要）
        Date twoYearsLater = Date.from(twoYearsLaterInstant);

        tbLicenses.setEndDate(twoYearsLater);
        licensesService.updateById(tbLicenses);

        // 修改商户的状态 （服务到期 = SERVICE_END） ==> （已激活 = ACTIVE）
        tbMerchants.setStatus("ACTIVE");
        // 更新服务结束时间
        tbLicenses.setEndDate(twoYearsLater);
        updateById(tbMerchants);

        // 需要 更新代理商信息 ===> 到期商家数量 && 即将到期商家数量
        //   这里可以选择不做操作，使用定时任务去更新
        /*tbAgent.setLicenseExpired(null);
        tbAgent.setLicenseUpcomingExpired(null);
        agentService.updateById(tbAgent);*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void freezeMerchant(TBMerchants tbMerchants, TBAgent tbAgent) {
        // 服务取消
        tbMerchants.setStatus("CANCEL_SERVICE");
        updateById(tbMerchants);

        // 证书表中找到该证书并设置证书状态、License => (merchantID ==> NULL && agentID ==> NULL)
        TBLicenses tbLicenses = licensesService.getByMerchantId(tbMerchants.getId());
        tbLicenses.setAgentId(null);
        tbLicenses.setMerchantId(null);
        tbLicenses.setStatus(1);  // 证书状态变为可用
        tbLicenses.setStartDate(null);
        tbLicenses.setEndDate(null);
        licensesService.updateById(tbLicenses);


        // 更新代理商的证书管理证书状态
        Integer totalCount = Integer.parseInt(licensesService.getByAgentId(Integer.parseInt(tbAgent.getId().toString())).toString());
        Integer usedCount = licensesService.getAgentLicenseUesed(tbAgent.getId());
        tbAgent.setLicenseTotal(totalCount);
        tbAgent.setLicenseUsed(usedCount);
        tbAgent.setLicenseLeave(totalCount - usedCount);
        agentService.updateById(tbAgent);
    }


    /**
     * 获取微信用户手机号
     *
     * @param code code值
     */
    @Override
    public String getMobilePhoneByWechat(String code) {
        try {
            String accessToken = wxMaService.getAccessToken(true);
            String url = UrlBuilder
                    .of("https://api.weixin.qq.com/wxa/business/getuserphonenumber")
                    .addQuery("access_token", accessToken)
                    .build();
            Map<String, Object> paramBody = new HashMap<>(2);
            paramBody.put("code", code);
            String result = HttpUtil.post(url, JSON.toJSONString(paramBody));
            JSONObject jsonObject = JSON.parseObject(result);
            if ("40029".equals(jsonObject.getString("errcode"))) {
                throw new RuntimeException("code无效");
            }
            if ("-1".equals(jsonObject.getString("errcode"))) {
                throw new RuntimeException("微信系统繁忙");
            }
            JSONObject phoneInfo = jsonObject.getJSONObject("phone_info");
            return phoneInfo.getString("phoneNumber");
        } catch (WxErrorException e) {
            e.printStackTrace();
            wxMaService.getWxMaConfig().expireAccessToken();
            throw new RuntimeException("获取手机号失败");
        }
    }

    /**
     * 获取openid
     *
     * @param code code值
     */
    @Override
    public String getOpenId(String code) {
        try {
            WxMaJscode2SessionResult session = wxMaService.jsCode2SessionInfo(code);
            return session.getOpenid();
        } catch (WxErrorException e) {
            log.error("获取OpenID失败", e);
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TBMerchants saveMerchant(String openId, MerchantLoginDto loginDto) {
        TBMerchants tbMerchants = new TBMerchants();
        tbMerchants.setOpenid(openId);
        tbMerchants.setSysUid(loginDto.getId());
        tbMerchants.setMerchantName(loginDto.getUsername());
        tbMerchants.setStatus("UNACTIVE"); // 未激活
        save(tbMerchants);
        return tbMerchants;

    }

    @Override
    public String getXAccessToken(String sysUid) {
        SysUser sysUser = sysUserService.getById(sysUid);
        System.out.println("username:" + sysUser.getUsername());
        System.out.println("password:" + sysUser.getPassword());
        //1.生成token
        String token = JwtUtil.sign(sysUser.getUsername(), sysUser.getPassword());
        System.out.println("token:" + token);
        // 设置token缓存有效时间
        redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);  // 写入之前，应该先删除redis中的token
        redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
        redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);

        // step.6  记录用户登录日志
        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(sysUser, loginUser);
        baseCommonService.addLog("用户名: " + sysUser.getUsername() + ",登录成功！", CommonConstant.LOG_TYPE_1, null, loginUser);
        return token;
    }

    @Override
    public Map<String, Object> getAiPrompt(TBMerchants merchant, TBPackages packages, Integer aiTokens, String usePlatform, List<TBMerchantLittleTag> littleTagList) {
        String prompt = "店铺信息:\n" +
                "    店铺名称: #{merchantName}\n" +
                "    店铺标签:（重点参照）\n" +
                "#{classifications}" +
                "\n" +
                "    用户选择套餐信息:\n" +
                "        套餐名称: #{packageName}\n" +
                "        套餐详情: #{packageDetail} （此项若无，不用参照）\n" +
                "        套餐标签: #{packageTagList} （此项若无，不用参照）\n" +
                "        套餐价格: #{packagePrice}  （此项若无，不用参照）\n" +
                "\n" +
                "根据上面的店铺信息及套餐信息生成一个#{wenAnOrPinLun},要求如下:\n" +
                "    1.字数#{aiTokens}左右\n" +
                "    2.发布平台:#{usePlatform}\n" +
                "    3.不使用MarkDown格式\n" +
                "    4.态度积极\n" +
                "    5.遇到与店铺不匹配的词语可以忽略";
        prompt = prompt.replace("#{merchantName}", merchant.getMerchantName())
                .replace("#{packageName}", packages.getPackageName())
                .replace("#{packageDetail}", packages.getPackageDetails());
        if (packages.getPrice() == null) {
            prompt = prompt.replace("#{packagePrice}", "空");
        } else {
            prompt = prompt.replace("#{packagePrice}", packages.getPrice().toString());
        }

        if (packages.getTags() == null) {
            prompt = prompt.replace("#{packageTagList}", "空");
        } else {
            List<String> packageTagsList = RandomUtil.processJsonList(packages.getTags());   // 随机挑选2个标签
            prompt = prompt.replace("#{packageTagList}", JSON.toJSONString(packageTagsList));
        }

        String wenAnOrPinLun;
        if (StringUtils.isEmpty(usePlatform)) {
            usePlatform = "微信朋友圈  不需要表情包";
            wenAnOrPinLun = "文案";
        } else if (usePlatform.equals("小红书")) {
            wenAnOrPinLun = "文案";
            aiTokens = 900;
        } else {
            wenAnOrPinLun = "评论";
        }
        prompt = prompt.replace("#{usePlatform}", usePlatform).replace("#{wenAnOrPinLun}", wenAnOrPinLun).replace("#{aiTokens}", String.valueOf(aiTokens));

        //店铺分类标签及图片推送
        List<String> picList = new ArrayList<>();
        Map<String, List<String>> classificationIdTagMap = new HashMap<>(); //{ 中间表ID : List<标签名>}
        littleTagList.forEach(littleTag -> {
            List<String> tagList;
            if (!classificationIdTagMap.containsKey(littleTag.getClassificationMiddleId())) {
                tagList = new ArrayList<>();
                tagList.add(littleTag.getTagName());
                classificationIdTagMap.put(littleTag.getClassificationMiddleId(), tagList);
            } else {
                tagList = classificationIdTagMap.get(littleTag.getClassificationMiddleId());
                tagList.add(littleTag.getTagName());
                classificationIdTagMap.put(littleTag.getClassificationMiddleId(), tagList);
            }
        });

        Map<String, ClassificationDTO> classificationIdClassificationNameMap = new HashMap<>();
        List<ClassificationDTO> classificationIdList = classificationMerchantMiddleService.getClassificationIdAndName(new ArrayList<>(classificationIdTagMap.keySet()));
        classificationIdList.forEach(classificationDTO -> {
            classificationIdClassificationNameMap.put(classificationDTO.getId(), classificationDTO);
        });


        StringBuilder classifications = new StringBuilder();
        for (String id : classificationIdTagMap.keySet()) {
            ClassificationDTO classification = classificationIdClassificationNameMap.get(id);
            classifications.append("\t\t").append(classification.getClassificationName()).append(": ").append(JSON.toJSONString(classificationIdTagMap.get(id))).append("\n");
            if (!StringUtils.isEmpty(classification.getPicList())) {
                picList.addAll(Arrays.asList(classification.getPicList().split(",")));
            }
        }
        prompt = prompt.replace("#{classifications}", classifications.toString());
        List<String> newPicList = new ArrayList<>(Arrays.asList(picList.toArray(new String[0])));
        if (!StringUtils.isEmpty(packages.getPackagePicList())) {
            // 将JSON字符串转换为List<String>
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<String> list = objectMapper.readValue(packages.getPackagePicList(), new TypeReference<List<String>>() {
                });
                newPicList.add(RandomUtil.randomSelectOneElement(list));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return Map.of(
                "aiPrompt", prompt,
                "picList", newPicList
        );
    }

    @Override
    public List<String> randomSelectedPic(TBMerchants merchant, TBPackages packages, List<TBMerchantLittleTag> littleTagList) {
        // { 中间表ID : List<标签名>}
        Map<String, List<String>> classificationIdTagMap = new HashMap<>();
        littleTagList.forEach(littleTag -> {
            List<String> tagList;
            if (!classificationIdTagMap.containsKey(littleTag.getClassificationMiddleId())) {
                tagList = new ArrayList<>();
                tagList.add(littleTag.getTagName());
                classificationIdTagMap.put(littleTag.getClassificationMiddleId(), tagList);
            } else {
                tagList = classificationIdTagMap.get(littleTag.getClassificationMiddleId());
                tagList.add(littleTag.getTagName());
                classificationIdTagMap.put(littleTag.getClassificationMiddleId(), tagList);
            }
        });

        Map<String, ClassificationDTO> classificationIdClassificationNameMap = new HashMap<>();
        List<ClassificationDTO> classificationIdList = classificationMerchantMiddleService.getClassificationIdAndName(new ArrayList<>(classificationIdTagMap.keySet()));
        classificationIdList.forEach(classificationDTO -> {
            classificationIdClassificationNameMap.put(classificationDTO.getId(), classificationDTO);
        });

        List<String> newPicList = new ArrayList<>();
        List<String> picList = new ArrayList<>();
        for (String id : classificationIdTagMap.keySet()) {
            ClassificationDTO classification = classificationIdClassificationNameMap.get(id);
            if (!StringUtils.isEmpty(classification.getPicList())) {
                picList.addAll(Arrays.asList(classification.getPicList().split(",")));
            }
        }

        newPicList.addAll(RandomUtil.randomSelectTwoElement(picList));
        if (!StringUtils.isEmpty(packages.getPackagePicList())) {
            // 将JSON字符串转换为List<String>
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<String> list = objectMapper.readValue(packages.getPackagePicList(), new TypeReference<List<String>>() {
                });
                newPicList.add(RandomUtil.randomSelectOneElement(list));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return newPicList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MerchantLoginDto registerSysUser(String phone) {
        //LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //log.info("当前登录用户信息:\n{}", JSON.toJSONString(sysUser));
        MerchantLoginDto loginDto = new MerchantLoginDto();
        String sixRandomNumber = RandomUtil.randomNumeric6();
        loginDto.setId(UUIDGenerator.generate32BitUUID());
        loginDto.setUsername(phone);
        loginDto.setRealname("店家" + sixRandomNumber);
        loginDto.setWorkNo(sixRandomNumber + RandomUtil.randomNumeric6() + RandomUtil.randomNumeric6());
        loginDto.setSelectedroles("1953634960707457025");  // 店家角色
        loginDto.setUserIdentity(1);
        loginDto.setEmail("dianjia" + sixRandomNumber + CommonConstant.EMAIL_DEFAULT_SUFFIX);
        loginDto.setPhone(phone);
        loginDto.setActivitiSync(1);
        String salt = oConvertUtils.randomGen(8);
        loginDto.setSalt(salt);
        String passwordEncode = PasswordUtil.encrypt(loginDto.getUsername(), CommonConstant.DEFAULT_PASSWORD, salt);
        loginDto.setPassword(passwordEncode);
        loginDto.setWorkNo(RandomUtil.randomAlphanumeric6() + RandomUtil.randomAlphanumeric6());
        loginDto.setDepartIds("");
        loginDto.setStatus(CommonConstant.USER_UNFREEZE);
        loginDto.setDelFlag(CommonConstant.DEL_FLAG_0);
        loginDto.setActivitiSync(CommonConstant.ACT_SYNC_1);
        loginDto.setUserIdentity(CommonConstant.USER_IDENTITY_1);

        try {
            SysUser user = new SysUser();
            BeanUtils.copyProperties(loginDto, user);
            user.setCreateTime(new Date());
            user.setCreateBy("admin");
            sysUserService.addUserWithRole(user, loginDto.getSelectedroles());  // 店家角色
            return loginDto;
        } catch (Exception e) {
            log.error("registerSysUser:{}", e.getMessage());
            throw new RuntimeException("手机号登录时，注册系统用户出错！请联系管理员！");
        }

//        int successNum = baseMapper.saveSysUser(loginDto.getId(), loginDto.getUsername(),
//                loginDto.getRealname(), loginDto.getWorkNo(),
//                loginDto.getUserIdentity(), loginDto.getEmail(),
//                loginDto.getPhone(), loginDto.getActivitiSync(),
//                loginDto.getPassword(), loginDto.getSalt(), loginDto.getDepartIds());
//        if (successNum == 1) {
//            // 用户角色插入
//            int successNum1 = baseMapper.insertSysUserRole(UUIDGenerator.generate32BitUUID(), loginDto.getId(), loginDto.getSelectedroles());
//            if (successNum1 != 1) {
//                throw new RuntimeException("注册失败");
//            }
//            return loginDto;
//        } else {
//            throw new RuntimeException("注册失败");
//        }
    }

}
