package org.jeecg.modules.pengyipeng.controller;

/*
 * ClassName: AppController
 * Package: org.jeecg.modules.pengyipeng.controller
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/20 - 11:33
 * @Version: v1.0
 */

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.pengyipeng.dto.*;
import org.jeecg.modules.pengyipeng.entity.*;
import org.jeecg.modules.pengyipeng.service.*;
import org.jeecg.modules.pengyipeng.service.impl.XhsAuthService;
import org.jeecg.modules.pengyipeng.utils.PhoneNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "App端的接口")
@RestController
@RequestMapping("/pengyipeng/api")
@Slf4j
public class AppController {

    @Autowired
    private ITBMerchantsService merchantsService;

    @Autowired
    private ITBPackagesService packagesService;

    @Autowired
    private ITBPlatformService platformService;

    @Autowired
    private AiService aiService;

    @Autowired
    private DictCacheService dictCacheService;

    @Autowired
    private ITBSourceService sourceService;

    @Autowired
    private ITBMerchantPlatformMiddleService tBMerchantPlatformMiddleService;

    @Autowired
    private ITBLicensesService licensesService;

    @Autowired
    private ITBAgentService agentService;

    @Autowired
    private XhsAuthService xhsAuthService;
    @Autowired
    private ITBAppInfoService appInfoService;

    /**
     * 获取商家端入口主图
     */
    @AutoLog(value = "App接口-小程序-获取商家端入口主图")
    @Operation(summary = "App接口-小程序-获取商家端入口主图")
    @GetMapping(value = "getMerchantEntranceMainPng")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> getMerchantEntranceMainPng() {
        QueryWrapper<TBSource> sourceQueryWrapper = new QueryWrapper<>();
        sourceQueryWrapper.eq("name", "app-main-png").eq("id", 497635330);
        TBSource source = sourceService.getBaseMapper().selectOne(sourceQueryWrapper);
        if (source == null) {
            return Result.error("找不到对应的资源！");
        }
        if (StringUtils.isEmpty(source.getUrl())) {
            return Result.error("请在后端维护好该资源以后，重试！");
        }
        return Result.ok(source.getUrl());
    }


    /**
     * 获取 用户协议和隐私政策
     */
    @AutoLog(value = "App接口-小程序-用户协议和隐私政策")
    @Operation(summary = "App接口-小程序-用户协议和隐私政策")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.TEXT_MARKDOWN_VALUE, value = "/getAppInfo")
    public Result<TBAppInfo> getAppInfo(@RequestParam(name = "flagKey", required = false) String flagKey) {
        Integer defaultAppInfoId = -1518268415;
        if (StringUtils.isEmpty(flagKey)) {
            TBAppInfo appInfo = appInfoService.getById(defaultAppInfoId);
            if (appInfo == null) {
                return Result.error("用户协议和隐私政策查询失败！请联系管理员！");
            } else {
                return Result.ok(appInfo);
            }
        } else {
            QueryWrapper<TBAppInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("flag_key", flagKey);
            TBAppInfo appInfo = appInfoService.getBaseMapper().selectOne(queryWrapper);
            if (appInfo == null) {
                return Result.error("用户协议和隐私政策查询失败！请联系管理员！");
            } else {
                return Result.ok(appInfo);
            }
        }
    }


    /**
     * /pengyipeng/api/merchantLogin
     *
     * @param licenseKey 证书密钥
     * @param code       微信用户手机号码编码
     */
    @AutoLog(value = "App接口-小程序登录（非静默）")
    @Operation(summary = "App接口-小程序登录（非静默）")
    @PostMapping(value = "merchantLogin")
    @Transactional(rollbackFor = Exception.class)
    public Result<TBMerchants> merchantLogin(@RequestParam(name = "licenseKey") String licenseKey, @RequestParam(name = "code") String code) {
        if (!StringUtils.isEmpty(code)) {
            try {
                TBMerchants merchant;
                // 获取openid
                String openId = merchantsService.getOpenId(code);
                QueryWrapper<TBMerchants> m = new QueryWrapper<>();
                m.eq("openid", openId);
                merchant = merchantsService.getBaseMapper().selectOne(m);
                if (null != merchant) {
                    return Result.ok(merchant);
                }
                // 获取手机号码
                String phone = merchantsService.getMobilePhoneByWechat(code);

                if (!StringUtils.isEmpty(phone) && PhoneNumberValidator.isValidPhoneNumber(phone)) {
                    // 注册系统用户信息
                    MerchantLoginDto loginDto = merchantsService.registerSysUser(phone);

                    merchant = merchantsService.saveMerchant(openId, loginDto);
                    // 分配License并绑定代理商
                    // 1. 查询代理商通过证书密钥,并更新商家的代理商
                    TBAgent agent = licensesService.getAgentByLicenseKey(licenseKey);
                    merchant.setAgentId(agent.getId());
                    merchantsService.updateById(merchant);
                    // 2. 分配证书并激活
                    // 获取时间
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime twoYearsLater = now.plusYears(2);
                    licensesService.assignLicense(merchant.getAgentId(), merchant.getId(), now, twoYearsLater);
                    Integer licenseUsedCount = licensesService.getAgentLicenseUesed(merchant.getAgentId());
                    //  更新代理商的 ( 已使用License数量 ==》 license_used) 和 (商户剩余开通数量)
                    agent.setLicenseLeave(agent.getLicenseTotal() - licenseUsedCount);
                    agent.setLicenseUsed(licenseUsedCount);
                    agentService.updateById(agent);
                    // 证书分配以后需要更新商家的服务时间
                    merchant.setServiceStartDate(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));
                    merchant.setServiceEndDate(Date.from(twoYearsLater.atZone(ZoneId.systemDefault()).toInstant()));
                    merchant.setEnabledPlatforms("3,");   // 设置可用平台
                    merchant.setStatus("ACTIVE");
                    merchantsService.updateById(merchant);
                } else {
                    return Result.error("用户code获取到的手机号无效！");
                }
                return Result.ok(merchant);
            } catch (Exception e) {
                return Result.error("手机号获取失败！" + e.getMessage());
            }
        } else {
            return Result.error("请输入微信用户手机号凭据！");
        }
    }

    /**
     * 保存openId
     */
    @AutoLog(value = "App接口-小程序登录（静默）-保存openid")
    @Operation(summary = "App接口-小程序登录（静默）-保存openid")
    @PostMapping(value = "saveOpenid")
    public Result<TBMerchants> saveOpenid(@RequestParam(name = "code") String code, @RequestParam(name = "merchantId") Integer merchantId) {
        if (StringUtils.isEmpty(code) || merchantId == null) {
            return Result.error("请传入正确的参数！");
        }
        String openid = merchantsService.getOpenId(code);
        if (openid == null) {
            log.info("saveOpenid:  code:" + code + " -> null");
            return Result.error("code:" + code + " -> null");
        }
        TBMerchants merchant = merchantsService.getById(merchantId);
        if (merchant == null) {
            log.info("saveOpenid   merchant:" + merchant + " -> null");
            return Result.error("merchant:" + merchant + " -> null");
        }
        merchant.setOpenid(openid);
        if (merchantsService.updateById(merchant)) {
            return Result.ok(merchant);
        } else {
            return Result.error("数据库更新异常！请重试！");
        }


    }

    /**
     * /pengyipeng/api/merchantSilentLogin
     *
     * @param code 微信用户手机号码编码
     */
    @AutoLog(value = "App接口-小程序登录（静默）")
    @Operation(summary = "App接口-小程序登录（静默）")
    @PostMapping(value = "merchantSilentLogin")
    @Transactional(rollbackFor = Exception.class)
    public Result<TBMerchants> merchantSilentLogin(@RequestParam(name = "code") String code) {
        if (StringUtils.isEmpty(code)) {
            return Result.error("请输入微信用户手机号凭据！");
        }
        // 获取openid
        String openId = merchantsService.getOpenId(code);
        QueryWrapper<TBMerchants> merchantsQueryWrapper = new QueryWrapper<>();
        merchantsQueryWrapper.eq("openid", openId);
        try {
            TBMerchants merchant = merchantsService.getBaseMapper().selectOne(merchantsQueryWrapper);
            if (merchant == null) {
                log.info("merchantSilentLogin: merchant -> {}", merchant);
                return Result.error("登录失败！请联系管理员！");
            }
            TBLicenses licenses = licensesService.getByMerchantId(merchant.getId());
            if (licenses == null) {
                log.info("merchantSilentLogin: licenses -> {}", licenses);
                return Result.error("登录失败！请联系管理员！");
            }
            if (licenses.getStatus() == 1) {
                log.info("merchantSilentLogin: licenses.getStatus() -> 1");
                return Result.error("登录失败！证书不可用！");
            }
            if (licenses.getStatus() == 3) {
                log.info("merchantSilentLogin: licenses.getStatus() -> 3");
                return Result.error("登录失败！证书不已过期！");
            }
            return Result.ok(merchant);
        } catch (Exception e) {
            log.info("merchantSilentLogin:{}", e.getMessage());
            return Result.error("登录失败！请联系管理员！");
        }
    }


    /**
     * 前端获取操作指导图片
     */
    @AutoLog(value = "App接口-前端获取操作指导图片")
    @Operation(summary = "App接口-前端获取操作指导图片")
    @PostMapping(value = "getOperationGuidancePng")
    public Result<Map<String, String>> getOperationGuidancePng() {
        try {
            QueryWrapper<TBSource> queryWrapper = new QueryWrapper<>();
            queryWrapper.likeRight("description", "前端操作引导图-");
            List<TBSource> sources = sourceService.list(queryWrapper);
            Map<String, String> map = new HashMap<>();
            for (TBSource source : sources) {
                map.put(source.getName().replace(".png", ""), source.getUrl());
            }
            return Result.ok(map);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 设置商家的可用平台
     *
     * @param merchantId   商家ID
     * @param platformName platform (英文)
     * @param status       平台的状态
     * @return
     */
    @AutoLog(value = "App接口-设置商家的可用平台")
    @Operation(summary = "App接口-设置商家的可用平台")
    @PostMapping(value = "setEnablePlatform")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> setEnablePlatform(@RequestParam("merchantId") Integer merchantId,
                                            @RequestParam("platformName") String platformName,
                                            @RequestParam("status") Integer status) {
        if (merchantId == null || StringUtils.isEmpty(platformName) || status == null) {
            return Result.error("请正确传入参数！");
        }

        List<String> platformNames = platformService.getEnablePlatformList().stream().map(TBPlatform::getName).collect(Collectors.toList());
        if (!platformNames.contains(platformName)) {
            return Result.error("平台不可用！");
        }
        TBMerchants merchant = merchantsService.getById(merchantId);
        if (merchant == null) {
            return Result.error("未找到该商家！");
        }
        try {
            List<String> merchantEnablePlatformListOld = Arrays.asList(merchant.getEnabledPlatforms().split(","));
            List<String> merchantEnablePlatformList = new ArrayList<>(merchantEnablePlatformListOld);
            String getPlatformInDictOrder = platformService.getEnablePlatformByName(platformName).getInDictOrder().toString();
            if (merchantEnablePlatformList.contains(getPlatformInDictOrder)) {
                if (status == 0) {
                    merchantEnablePlatformList.remove(getPlatformInDictOrder);
                }
            } else {
                if (status == 1) {
                    merchantEnablePlatformList.add(getPlatformInDictOrder);
                }
            }
            merchant.setEnabledPlatforms(String.join(",", merchantEnablePlatformList));
            merchantsService.updateById(merchant);
            return Result.ok("设置成功！");
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }


    /**
     * 保存短链
     */
    @AutoLog(value = "App接口-保存短链")
    @Operation(summary = "App接口-保存短链")
    @PostMapping(value = "saveShortLink")
    public Result<String> saveShortLink(@RequestParam(name = "merchantId") Integer merchantId,
                                        @RequestParam(name = "platformName") String platformName,
                                        @RequestParam(name = "shortLink") String shortLink,
                                        @RequestParam(name = "parseResult") String parseResult) {
        if (merchantId == null || StringUtils.isEmpty(platformName) || StringUtils.isEmpty(shortLink)) {
            return Result.error("请正确传入参数！");
        } else {
            TBMerchants merchant = merchantsService.getById(merchantId);
            if (merchant == null) {
                return Result.error("未找到该商家ID！");
            }
            Map<String, TBPlatform> platformNameMap = new HashMap<>();
            List<TBPlatform> platforms = platformService.getBaseMapper().selectList(new QueryWrapper<>());
            platforms.forEach(i -> platformNameMap.put(i.getName(), i));
            if (!platformNameMap.containsKey(platformName)) {
                return Result.error("未找到对应的平台");
            }
            TBPlatform platform = platformNameMap.get(platformName);
            QueryWrapper<TBMerchantPlatformMiddle> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("merchant_id", merchantId);
            queryWrapper.eq("platform_id", platform.getId());
            TBMerchantPlatformMiddle middle = tBMerchantPlatformMiddleService.getBaseMapper().selectOne(queryWrapper);
            if (middle == null) {
                middle = new TBMerchantPlatformMiddle();
                middle.setMerchantId(merchantId);
                middle.setPlatformId(platform.getId());
                middle.setShortLink(shortLink);
                middle.setParseInfo(parseResult);
                log.info("短链信息:{}", JSON.toJSONString(middle));
                log.info("短链保存成功！save");
                tBMerchantPlatformMiddleService.save(middle);
                return Result.ok("短链保存成功！");
            } else {
                middle.setShortLink(shortLink);
                middle.setParseInfo(parseResult);
                log.info("短链信息:{}", JSON.toJSONString(middle));
                log.info("短链保存成功！saveOrUpdate");
                tBMerchantPlatformMiddleService.saveOrUpdate(middle);
                return Result.ok("短链保存成功！");
            }
        }
    }

    /**
     * 获取短链信息
     */
    @AutoLog(value = "App接口-获取短链信息")
    @Operation(summary = "App接口-获取短链信息")
    @GetMapping(value = "getShortLinkInfo")
    public Result<TBMerchantPlatformMiddle> getShortLinkInfo(@RequestParam(name = "merchantId") Integer merchantId,
                                                             @RequestParam(name = "platformName") String platformName) {
        TBMerchants merchant = merchantsService.getById(merchantId);
        if (merchant == null) {
            return Result.error("未找到该商家ID！");
        }
        Map<String, TBPlatform> platformNameMap = new HashMap<>();
        List<TBPlatform> platforms = platformService.getBaseMapper().selectList(new QueryWrapper<>());
        platforms.forEach(i -> platformNameMap.put(i.getName(), i));
        if (!platformNameMap.containsKey(platformName)) {
            return Result.error("未找到对应的平台");
        }
        TBPlatform platform = platformNameMap.get(platformName);
        QueryWrapper<TBMerchantPlatformMiddle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("merchant_id", merchantId);
        queryWrapper.eq("platform_id", platform.getId());
        return Result.ok(tBMerchantPlatformMiddleService.getBaseMapper().selectOne(queryWrapper));
    }

    /**
     * 解析短链
     */
    @AutoLog(value = "App接口-解析短链")
    @Operation(summary = "App接口-解析短链")
    @GetMapping(value = "parseShortLink")
    public Result<String> parseShortLink(@RequestParam(name = "shortLinkStr", required = false) String shortLinkStr, @RequestParam(name = "platformName") String platformName) {
        String parseResult = tBMerchantPlatformMiddleService.parseShortLink(shortLinkStr, platformName);
        if (StringUtils.isEmpty(parseResult)) {
            return Result.error("短链解析失败！");
        }
        return Result.ok(parseResult);
    }


    /**
     * 获取小红书分享平台的访问信息
     * appKey + nonce + timestamp + signature(已经是二次签名了)
     */
    @AutoLog(value = "App接口-获取小红书分享平台的访问信息")
    @Operation(summary = "App接口-获取小红书分享平台的访问信息")
    @GetMapping(value = "getAccessXhsPlatformInfo")
    public Result<XhsAuthInfoDTO> getAccessXhsPlatformInfo() {
        try {
            XhsAuthInfoDTO xhsAuthInfoDTO = xhsAuthService.getAccessToken();
            return Result.ok(xhsAuthInfoDTO);
        } catch (Exception e) {
            return Result.error("获取签名信息失败！错误原因:" + e.getMessage());
        }

    }


    /**
     * 获取商家的可用平台
     *
     * @param merchantId 用于接受参数
     * @return
     */
    @AutoLog(value = "App接口-获取商家的可用平台")
    @Operation(summary = "App接口-获取商家的可用平台")
    @GetMapping(value = "getEnablePlatform")
    public Result<Map<String, Integer>> getEnablePlatform(@RequestParam(name = "merchantId", required = true) Integer merchantId) {
        if (merchantId == null) {
            return Result.error("请正确传入参数！");
        }
        TBMerchants merchant = merchantsService.getById(merchantId);
        if (merchant == null) {
            return Result.error("未找到该商家！");
        }
        return Result.ok(platformService.platformStatusTrans(merchant));
    }

    /**
     * 获取商家的可用平台对应图片
     *
     * @param merchantId 用于接受参数
     * @return
     */
    @AutoLog(value = "App接口-获取商家的可用平台对应图片")
    @Operation(summary = "App接口-获取商家的可用平台对应图片")
    @GetMapping(value = "getEnablePlatformPng")
    public Result<Map<String, String>> getEnablePlatformPng(@RequestParam(name = "merchantId", required = true) Integer merchantId) {
        if (merchantId == null) {
            return Result.error("请正确传入参数！");
        }
        TBMerchants merchant = merchantsService.getById(merchantId);
        if (merchant == null) {
            return Result.error("未找到该商家！");
        }
        return Result.ok(platformService.platformPngTrans(merchant));
    }


    /**
     * AI生成文案流式返回
     *
     * @param generationRequestDTO 用于接受参数
     * @return
     */
    @AutoLog(value = "App接口-AI生成文案流式返回")
    @Operation(summary = "App接口-AI生成文案流式返回")
    @PostMapping(value = "/ai/generate22")
    public SseEmitter aiGenerate(@RequestBody GenerationRequestDTO generationRequestDTO) {
        return aiService.aiGeneByPrompt(generationRequestDTO, true);
    }


    /**
     * AI 以流的方式响应 生成的数据
     */
    @Operation(summary = "App接口-AI流式生成(不会自动保存文案)")
    @GetMapping(value = "ai/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getAiGenerateByStream(@RequestParam String question) {
        return aiService.getAiGenerateStream(question);
    }

    /**
     * AI一次性返回结果，适合简单问题的回答
     */
    @Operation(summary = "App接口-AI普通生成（适合字数较少的生成，不会自动保存文案）")
    @GetMapping(value = "ai/generate")
    public Result<String> getAiGenerate(@RequestParam String question) {
        String rsl = aiService.getAiGenerate(question);
        return Result.ok(rsl);
    }

    /**
     * AI一次性返回结果，请求超时时间是 300000 ms,也就是 5 minute
     */
    @Operation(summary = "App接口-AI异步生成（非流式，不会自动保存文案）")
    @GetMapping(value = "ai/asyncGenerate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void getAiGenerate(HttpServletRequest request, HttpServletResponse response, @RequestParam String question) {
        aiService.getAiGenerate(request, response, question);
    }


    /**
     * 获取数据库中碰一碰项目所用到的所有字典
     *
     * @return
     */
    @AutoLog(value = "App接口-获取数据库中碰一碰项目所用到的所有字典")
    @Operation(summary = "App接口-获取数据库中碰一碰项目所用到的所有字典")
    @PostMapping(value = "/get/pypDict")
    public Result<List<SysDict>> getPypDict() {
        return Result.ok(dictCacheService.findPypAllDict());
    }

    /**
     * 通过 id 获取某一个字典的所有-键值对
     *
     * @return
     */
    @AutoLog(value = "App接口-通过id获取某一个字典的所有-键值对")
    @Operation(summary = "App接口-通过id获取某一个字典的所有-键值对")
    @GetMapping(value = "/get/pypKeyCodeById")
    public Result<List<SysDictItem>> getPypKeyCodeById(@RequestParam(name = "id", required = true) String id) {
        return Result.ok(dictCacheService.findKeyCodeById(id));
    }

    /**
     * 通过 dictCode 获取某一个字典的所有-键值对
     *
     * @return
     */
    @AutoLog(value = "App接口-通过 dictCode 获取某一个字典的所有-键值对")
    @Operation(summary = "App接口-通过 dictCode 获取某一个字典的所有-键值对")
    @GetMapping(value = "/get/pypKeyCodeByDictCode")
    public Result<List<SysDictItem>> getKeyCodeByDictCode(@RequestParam(name = "dictCode", required = true) String dictCode) {
        return Result.ok(dictCacheService.findKeyCodeByDictCode(dictCode));
    }

    /**
     * 套餐信息保存或者更新
     */
    @AutoLog(value = "App接口-获取套餐信息")
    @Operation(summary = "App接口-获取套餐信息")
    @RequestMapping(value = "/getMerchantPackageInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<List<MerchantPackageDTO>> getMerchantPackageInfo(@RequestParam(name = "merchantId", required = true) String merchantId) {
        QueryWrapper<TBPackages> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("merchant_id", merchantId);
        List<MerchantPackageDTO> dtos = new ArrayList<>();
        for (TBPackages tbPackages : packagesService.getBaseMapper().selectList(queryWrapper)) {
            MerchantPackageDTO merchantPackageDTO = new MerchantPackageDTO();
            //BeanUtils.copyProperties(tbPackages, merchantPackageDTO);
            packagesService.setDtoProperties(tbPackages, merchantPackageDTO);
            dtos.add(merchantPackageDTO);
        }
        return Result.ok(dtos);
    }

    /**
     * 套餐信息保存或者更新
     */
    @AutoLog(value = "App接口-套餐信息保存或者更新")
    @Operation(summary = "App接口-套餐信息保存或者更新")
    @RequestMapping(value = "/editMerchantPackageInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<String> editMerchantPackageInfo(@RequestBody MerchantPackageDTO packageDTO) {
        if (packageDTO.getMerchantId() == null) {
            return Result.error("请确保传入商家ID信息!");
        }
        TBMerchants merchants = merchantsService.getById(packageDTO.getMerchantId());
        if (merchants == null) {
            return Result.error("商家信息不存在! 请确保传入商家ID信息正确！");
        }

        if (packageDTO.getId() == null) {
            // 新增套餐
            TBPackages tbPackages = new TBPackages();
            packagesService.packageInfoSetting(packageDTO, tbPackages, merchants);
            packagesService.save(tbPackages);
        } else {
            TBPackages tbPackages = packagesService.getById(packageDTO.getId());
            if (tbPackages == null) {
                // 新增套餐
                tbPackages = new TBPackages();
                packagesService.packageInfoSetting(packageDTO, tbPackages, merchants);
                packagesService.save(tbPackages);
            } else {
                // 编辑套餐
                packagesService.packageInfoSetting(packageDTO, tbPackages, merchants);
                packagesService.updateById(tbPackages);
            }
        }
        if (merchantsService.updateById(merchants)) {
            return Result.ok("店铺基础信息编辑成功！");
        } else {
            return Result.error("数据库异常！请联系管理员！");
        }
    }

    /**
     * 通过 id 查询店铺信息
     *
     * @param merchantId 查询店铺信息
     * @return
     */
    @AutoLog(value = "App接口-店铺表-通过id查询店铺信息")
    @Operation(summary = "App接口-店铺表-通过id查询店铺信息")
    @GetMapping(value = "/merchantQueryById")
    public Result<MerchantInfoResponseDTO> queryById(@RequestParam(name = "merchantId", required = true) String merchantId) {
        if (StringUtils.isEmpty(merchantId)) {
            return Result.error("店铺ID输入有误！");
        }
        TBMerchants tBMerchants = merchantsService.getById(merchantId);
        if (tBMerchants == null) {
            return Result.error("未找到对应的店铺数据数据");
        }
        MerchantInfoResponseDTO responseDTO = new MerchantInfoResponseDTO();
        // 需要把数据规范化后在返回给前端
        merchantsService.setInfoForResp(tBMerchants, responseDTO);
        return Result.OK(responseDTO);
    }


    /**
     * 店铺信息编辑
     *
     * @param merchantInfoRequestDTO 店铺信息
     * @return
     */
    @AutoLog(value = "App接口-店铺信息编辑")
    @Operation(summary = "App接口-店铺信息编辑")
    @RequestMapping(value = "/editMerchantInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<String> editMerchantInfo(@RequestBody MerchantInfoRequestDTO merchantInfoRequestDTO) {
        Integer merchantId = merchantInfoRequestDTO.getMerchantId();
        if (merchantId == null) {
            return Result.error("请正确传入请求参数!");
        }
        TBMerchants merchants = merchantsService.getById(merchantId);
        if (merchants == null) {
            return Result.error("商家信息不存在");
        }
        merchants.setId(merchantId);
        merchants.setMerchantMainPic(merchantInfoRequestDTO.getMerchantMainPic());
        merchants.setMerchantName(merchantInfoRequestDTO.getMerchantName());
        merchants.setKeywords(JSON.toJSONString(merchantInfoRequestDTO.getMerchantKeywordList()));
        if (merchantsService.updateById(merchants)) {
            return Result.ok("店铺基础信息编辑成功！");
        } else {
            return Result.error("数据库异常！请联系管理员！");
        }
    }


    /**
     * 获取系统支持的平台
     */
    @AutoLog(value = "App接口-获取系统支持的平台")
    @Operation(summary = "App接口-获取系统支持的平台")
    @RequestMapping(value = "/getPypUsedPlatform", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<PypUsedPlatformResponseDTO> getPypUsedPlatform() {
        List<TBPlatform> platformList = platformService.getBaseMapper().selectList(new QueryWrapper<TBPlatform>());
        PypUsedPlatformResponseDTO responseDTO = new PypUsedPlatformResponseDTO();
        for (TBPlatform tbPlatform : platformList) {
            if (tbPlatform.getFunctionOrder() == 1) {
                PypUsedPlatformDTO pypUsedPlatformDTO = platformService.setPypUsedPlatformDtoInfo(tbPlatform);
                responseDTO.getList1().add(pypUsedPlatformDTO);
            } else if (tbPlatform.getFunctionOrder() == 2) {
                PypUsedPlatformDTO pypUsedPlatformDTO = platformService.setPypUsedPlatformDtoInfo(tbPlatform);
                responseDTO.getList2().add(pypUsedPlatformDTO);
            } else if (tbPlatform.getFunctionOrder() == 3) {
                PypUsedPlatformDTO pypUsedPlatformDTO = platformService.setPypUsedPlatformDtoInfo(tbPlatform);
                responseDTO.getList3().add(pypUsedPlatformDTO);
            }
        }
        return Result.ok(responseDTO);
    }


    /**
     * 保存小红书话题
     *
     * @param merchantId 店家ID
     * @param xhsTopics  店家小红书话题内容
     * @return
     */
    @AutoLog(value = "App接口-保存小红书话题")
    @Operation(summary = "App接口-保存小红书话题")
    @RequestMapping(value = "/saveXhsTopics", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<String> saveXhsTopics(@RequestParam(name = "merchantId", required = true) String merchantId,
                                        @RequestParam(name = "xhsTopics", required = true) String xhsTopics) {
        Integer newMerchantId = null;
        if (StringUtils.isEmpty(merchantId) || StringUtils.isEmpty(xhsTopics)) {
            return Result.error("请正确传入请求参数!");
        } else {
            newMerchantId = Integer.parseInt(merchantId);
        }
        TBMerchants merchants = merchantsService.getById(newMerchantId);
        if (merchants == null) {
            return Result.error("商家信息不存在");
        }
        merchants.setId(newMerchantId);
        merchants.setXhsTopics(xhsTopics);
        if (merchantsService.updateById(merchants)) {
            return Result.ok("小红书话题保存成功！");
        } else {
            return Result.error("数据库异常！请联系管理员！");
        }
    }


    /**
     * 上传店家微信二维码，保存前，前端的图片上传组件应该先去访问后台提供的图片保存接口，并从返回值中获取到 图片地址
     * 请求 URL: http://ip:8080/jeecg-boot/sys/common/upload
     * 请求方法: POST
     *
     * @param merchantId           店家ID
     * @param merchantWechatPicUrl 店家微信二维码地址
     * @return
     */
    @AutoLog(value = "App接口-上传店家微信二维码")
    @Operation(summary = "App接口-上传店家微信二维码")
    @RequestMapping(value = "/uploadWechatPic", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<String> uploadWechatPic(@RequestParam(name = "merchantId", required = true) Integer merchantId, @RequestParam(name = "merchantWechatPicUrl", required = true) String merchantWechatPicUrl) {
        if (merchantId == null || StringUtils.isEmpty(merchantWechatPicUrl)) {
            return Result.error("请正确传入请求参数!");
        }
        TBMerchants merchants = merchantsService.getById(merchantId);
        if (merchants == null) {
            return Result.error("商家信息不存在");
        }
        merchants.setId(merchantId);
        merchants.setWechatQrCode(merchantWechatPicUrl);
        if (merchantsService.updateById(merchants)) {
            return Result.ok("商家微信二维码保存成功！");
        } else {
            return Result.error("数据库异常！请联系管理员！");
        }
    }


    /**
     * 保存wifi信息
     *
     * @param wifiDto WIFI信息请求
     * @return
     */
    @AutoLog(value = "App接口-保存wifi信息")
    @Operation(summary = "App接口-保存wifi信息")
    @RequestMapping(value = "/saveWifiInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<String> saveWifiInfo(@RequestBody WifiDTO wifiDto) {
        if (wifiDto == null || wifiDto.getMerchantId() == null) {
            return Result.error("请正确传入请求参数!");
        }
        TBMerchants merchants = merchantsService.getById(wifiDto.getMerchantId());
        if (merchants == null) {
            return Result.error("商家信息不存在");
        }
        merchants.setId(wifiDto.getMerchantId());
        merchants.setWifiUser(wifiDto.getWifiUser());
        merchants.setWifiPwd(wifiDto.getWifiPwd());
        if (merchantsService.updateById(merchants)) {
            return Result.ok("wifi信息保存成功！");
        } else {
            return Result.error("数据库异常！请联系管理员！");
        }
    }


    /**
     * 获取wifi信息
     *
     * @param wifiDto
     * @return
     */
    @AutoLog(value = "App接口-获取wifi信息")
    @Operation(summary = "App接口-获取wifi信息")
    @RequestMapping(value = "/getWifiInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<WifiDTO> getWifiInfo(@RequestBody WifiDTO wifiDto) {
        if (wifiDto == null || wifiDto.getMerchantId() == null) {
            return Result.error("请正确传入请求参数!");
        }
        TBMerchants merchants = merchantsService.getById(wifiDto.getMerchantId());
        if (merchants == null) {
            return Result.error("商家信息不存在! 无法获取wifi信息！");
        } else {
            WifiDTO result = new WifiDTO();
            result.setMerchantId(merchants.getId());
            result.setWifiUser(merchants.getWifiUser());
            return Result.ok(result);
        }
    }

}
