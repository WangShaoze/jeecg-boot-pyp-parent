package org.jeecg.modules.pengyipeng.controller;

/*
 * ClassName: APPUserController
 * Package: org.jeecg.modules.pengyipeng.controller
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/26 - 17:01
 * @Version: v1.0
 */

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
import org.jeecg.modules.pengyipeng.utils.HttpLinkMatcherUtil;
import org.jeecg.modules.pengyipeng.vo.LittleTagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "UserApp端的接口")
@RestController
@RequestMapping("/pengyipeng/user-app/api/")
@Slf4j
public class APPUserController {

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
    private ITBMerchantPlatformMiddleService tBMerchantPlatformMiddleService;

    @Autowired
    private ITBClickOperationStatService tBClickOperationStatService;
    @Autowired
    private ITBMerchantLittleTagService littleTagService;
    @Autowired
    private ITBClassificationMerchantMiddleService classificationMerchantMiddleService;


    /**
     * 生成AI提示词的逻辑
     * 1.基础校验要使用 商家id ===> 传入 [merchantId]
     * 2.用户选择的【店铺分类下的小标签】+【店铺该分类对应的2张图片】  ===> 传入小标签的id列表使用逗号隔开 [littleTagIds]
     * 3.用户选择的【套餐】 ==> 从套餐中获取套餐的【描述信息】和【关键词】并从套餐中随机选择1张【图片】    ===>  传入套餐的id [packageId]
     * 4.发布平台 ===> 传入字数 [usePlatform]
     * 5.小红书平台的文案字数可能较多1000字左右, 这个参数默认200字模拟普通的评论 ===> 传入字数 [aiTokens]
     * 返回值:
     * aiPrompt: string
     * picList: [string,string,string]
     *
     * @param merchantId   商家ID
     * @param tagIdList    小标签的Id列表
     * @param packageId    套餐ID
     * @param postPlatform 发布平台
     * @param aiTokens     AI生成字数
     * @return Map<String, Object>
     */
    @AutoLog(value = "UserApp接口-生成AI提示词")
    @Operation(summary = "UserApp接口-生成AI提示词")
    @GetMapping(value = "generateAiPrompt")
    public Result<Map<String, Object>> generateAiPrompt(
            @RequestParam(name = "merchantId") Integer merchantId,
            @RequestParam(name = "tagIdList") String tagIdList,
            @RequestParam(name = "packageId") Integer packageId,
            @RequestParam(name = "postPlatform") String postPlatform,
            @RequestParam(name = "aiTokens", defaultValue = "200", required = false) Integer aiTokens
    ) {
        if (merchantId == null || StringUtils.isEmpty(tagIdList) || packageId == null) {
            return Result.ok("参数存在问题！");
        }
        try {
            TBMerchants merchant = merchantsService.getById(merchantId);
            if (merchant == null) {
                return Result.error("未找到指定商家!");
            }
            TBPackages packages = packagesService.getById(packageId);
            if (packages == null) {
                return Result.error("未找到指定的套餐！");
            }
            List<String> littleTagIdList = Arrays.asList(tagIdList.split(","));
            if (!littleTagIdList.isEmpty()) {
                QueryWrapper<TBMerchantLittleTag> tagQueryWrapper = new QueryWrapper<>();
                tagQueryWrapper.eq("merchant_id", merchantId);
                tagQueryWrapper.in("id", littleTagIdList);
                List<TBMerchantLittleTag> tagListInDB = littleTagService.getBaseMapper().selectList(tagQueryWrapper);
                if (tagListInDB.size() != littleTagIdList.size()) {
                    return Result.error("部分选择的标签未找到！");
                } else {
                    return Result.ok(merchantsService.getAiPrompt(merchant, packages, aiTokens, postPlatform, tagListInDB));
                }
            } else {
                return Result.error("店铺标签未指定！");
            }
        } catch (Exception e) {
            log.error("generateAiPrompt: 出现错误！错误原因如下:", e);
            return Result.error("生成AI提示词出错！请联系管理员！");
        }

    }


    @AutoLog(value = "UserApp接口-换一批")
    @Operation(summary = "UserApp接口-换一批")
    @GetMapping(value = "changePicList")
    public Result<List<String>> changePicList(
            @RequestParam(name = "merchantId") Integer merchantId,
            @RequestParam(name = "tagIdList") String tagIdList,
            @RequestParam(name = "packageId") Integer packageId
    ) {
        if (merchantId == null || StringUtils.isEmpty(tagIdList) || packageId == null) {
            return Result.ok("参数存在问题！");
        }
        TBMerchants merchant = merchantsService.getById(merchantId);
        if (merchant == null) {
            return Result.error("未找到指定商家!");
        }
        TBPackages packages = packagesService.getById(packageId);
        if (packages == null) {
            return Result.error("未找到指定的套餐！");
        }
        try {
            List<String> littleTagIdList = Arrays.asList(tagIdList.split(","));
            if (!littleTagIdList.isEmpty()) {
                QueryWrapper<TBMerchantLittleTag> tagQueryWrapper = new QueryWrapper<>();
                tagQueryWrapper.eq("merchant_id", merchantId);
                tagQueryWrapper.in("id", littleTagIdList);
                List<TBMerchantLittleTag> tagListInDB = littleTagService.getBaseMapper().selectList(tagQueryWrapper);
                if (tagListInDB.size() != littleTagIdList.size()) {
                    return Result.error("部分选择的标签未找到！");
                } else {
                    return Result.ok(merchantsService.randomSelectedPic(merchant, packages, tagListInDB));
                }
            } else {
                return Result.error("店铺标签未指定！");
            }
        } catch (Exception e) {
            log.error("changePicList:出错！错误原因:{}", e.getMessage());
            return Result.error("出现错误！请联系管理员！");
        }
    }


    @AutoLog(value = "UserApp接口-获取店铺标签列表")
    @Operation(summary = "UserApp接口-获取店铺标签列表")
    @GetMapping(value = "getAllTagList")
    public Result<List<LittleTagVO>> getAllTagList(
            @RequestParam(name = "merchantId") Integer merchantId
    ) {
        if (merchantId == null) {
            return Result.error("请正确传入参数！");
        }
        TBMerchants merchant = merchantsService.getById(merchantId);
        if (merchant == null) {
            return Result.error("该商家不存在！");
        }
        try {
            // 1.获取已经开启的分类
            QueryWrapper<TBClassificationMerchantMiddle> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("merchant_id", merchantId);
            queryWrapper.eq("is_open", "1");
            List<TBClassificationMerchantMiddle> classificationMerchantMiddleList = classificationMerchantMiddleService.getBaseMapper().selectList(queryWrapper);
            List<String> classificationIdList = classificationMerchantMiddleList.stream().map(TBClassificationMerchantMiddle::getId).toList();
            // 2.通过分类和merchantId获取标签列表
            QueryWrapper<TBMerchantLittleTag> littleTagQueryWrapper = new QueryWrapper<>();
            littleTagQueryWrapper.eq("merchant_id", merchantId);
            littleTagQueryWrapper.in("classification_middle_id", classificationIdList);
            littleTagQueryWrapper.orderByDesc("create_time");
            List<TBMerchantLittleTag> merchantLittleTagList = littleTagService.getBaseMapper().selectList(littleTagQueryWrapper);
            return Result.ok(LittleTagVO.create(merchantLittleTagList));
        } catch (Exception e) {
            log.error("getAllTagList:出错！错误原因:{}", e.getMessage());
            return Result.error("出现错误！请联系管理员！");
        }
    }


    /**
     * 统计用户点击数据接口
     */
    @AutoLog(value = "UserApp接口-统计用户点击数据-数据保存接口")
    @Operation(summary = "UserApp接口-统计用户点击数据-数据保存接口")
    @PostMapping(value = "uploadClickData")
    public Result<String> uploadClickData(@RequestParam(name = "merchantId") Integer merchantId,
                                          @RequestParam(name = "clickPlatformId", required = false) Integer clickPlatformId,
                                          @RequestParam(name = "clickWifi", required = false) Integer clickWifi
    ) {
        if (merchantId == null) {
            return Result.error("请正确传入参数！");
        }
        TBMerchants merchant = merchantsService.getById(merchantId);
        if (merchant == null) {
            return Result.error("该商家不存在！");
        }
        boolean isSuccess;
        if (clickPlatformId != null) {
            TBPlatform tbPlatform = platformService.getById(clickPlatformId);
            isSuccess = tBClickOperationStatService.saveClickDataToDB(merchant, tbPlatform, null);
        } else {
            if (clickWifi != null) {
                isSuccess = tBClickOperationStatService.saveClickDataToDB(merchant, null, clickWifi);
            } else {
                isSuccess = tBClickOperationStatService.saveClickDataToDB(merchant, null, null);
            }
        }

        if (isSuccess) {
            return Result.ok("点击数据保存成功！");
        } else {
            log.error("UserApp接口-统计用户点击数据-数据保存接口：uploadClickData 统计数据接口出错了！");
        }
        return Result.ok("点击数据保存失败！");
    }


    /**
     * 统计用户点击数据接口
     */
    @AutoLog(value = "UserApp接口-统计用户点击数据-获取接口")
    @Operation(summary = "UserApp接口-统计用户点击数据-获取接口")
    @GetMapping(value = "getClickData")
    public Result<TBClickOperationStat> getClickData(
            @RequestParam(name = "merchantId") Integer merchantId,
            @RequestParam(name = "isAll") Integer isAll
    ) {
        if (merchantId == null) {
            return Result.error("请正确传入参数 ");
        }
        TBMerchants merchant = merchantsService.getById(merchantId);
        if (merchant == null) {
            return Result.error("该商家不存在！");
        }
        QueryWrapper<TBClickOperationStat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("merchant_id", merchant.getId());
        queryWrapper.eq("stat_date", tBClickOperationStatService.getYesterdayDate());
        try {
            if (isAll != null && isAll == 0) {
                TBClickOperationStat clickOperationStat = tBClickOperationStatService.getBaseMapper().selectOne(queryWrapper);
                if (clickOperationStat == null) {
                    clickOperationStat = tBClickOperationStatService.createANewObj(merchant.getId());
                }
                return Result.ok(clickOperationStat);
            } else if (isAll != null && isAll == 1) {
                TBClickOperationStat tbClickOperationStatNew = new TBClickOperationStat();
                tBClickOperationStatService.getAllClickData(tbClickOperationStatNew);
                return Result.ok(tbClickOperationStatNew);
            } else {
                return Result.error("请正确传入参数 ");
            }
        } catch (Exception e) {
            log.error("UserApp接口-统计用户点击数据-获取接口 uploadClickData ==> {}", e.getMessage());
            return Result.error("数据获取失败！");
        }
    }


    /**
     * 通过店铺的Id查询店铺微信二维码
     */
    @AutoLog(value = "UserApp接口-通过店铺的Id查询店铺微信二维码")
    @Operation(summary = "UserApp接口-查询店铺微信二维码")
    @GetMapping(value = "getMerchantWechatQrCode")
    public Result<String> getMerchantWechatQrCode(@RequestParam(name = "merchantId") Integer merchantId) {
        if (null == merchantId) {
            return Result.error("请正确传入参数 ");
        }
        TBMerchants merchant = merchantsService.getById(merchantId);
        if (null == merchant) {
            return Result.error("找不到商家信息！");
        }
        if (StringUtils.isEmpty(merchant.getWechatQrCode()) || null == HttpLinkMatcherUtil.extractFirstHttpLink(merchant.getWechatQrCode())) {
            return Result.error("该商家还未设置微信二维码！");
        }
        return Result.ok(merchant.getWechatQrCode());

    }

    /**
     * 通过店铺的Id查询套餐ID列表
     */
    @AutoLog(value = "UserApp接口-通过店铺的Id查询套餐ID列表")
    @Operation(summary = "UserApp接口-通过店铺的Id查询套餐ID列表")
    @GetMapping(value = "getPackageIdsByMerchantId")
    public Result<List<Integer>> getPackageIdsByMerchantId(@RequestParam(name = "merchantId") Integer merchantId) {
        if (null == merchantId) {
            return Result.error("请正确传入参数！");
        }
        try {
            QueryWrapper<TBPackages> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("merchant_id", merchantId);
            List<Integer> ids = packagesService.getBaseMapper().selectList(queryWrapper).stream().map(TBPackages::getId).collect(Collectors.toList());
            return Result.ok(ids);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 通过Id查询套餐信息
     */
    @AutoLog(value = "UserApp接口-通过Id查询套餐信息")
    @Operation(summary = "UserApp接口-通过Id查询套餐信息")
    @GetMapping(value = "getPackageInfoIdsById")
    public Result<TBPackages> getPackageInfoIdsById(@RequestParam(name = "packageId") Integer packageId) {
        if (null == packageId) {
            return Result.error("请正确传入参数！");
        }
        try {
            return Result.ok(packagesService.getById(packageId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


    /**
     * 通过店铺的Id查询套餐列表
     */
    @AutoLog(value = "UserApp接口-通过店铺的Id查询套餐列表")
    @Operation(summary = "UserApp接口-通过店铺的Id查询套餐列表")
    @GetMapping(value = "getPackagesByMerchantId")
    public Result<List<TBPackages>> getPackagesByMerchantId(@RequestParam(name = "merchantId") Integer merchantId) {
        if (null == merchantId) {
            return Result.error("请正确传入参数！");
        }
        try {
            QueryWrapper<TBPackages> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("merchant_id", merchantId);
            List<TBPackages> ids = packagesService.getBaseMapper().selectList(queryWrapper);
            return Result.ok(ids);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


    /**
     * 通过套餐ID生成评论提示词
     */
    @AutoLog(value = "UserApp接口-通过套餐ID生成评论提示词")
    @Operation(summary = "UserApp接口-通过套餐ID生成评论提示词")
    @GetMapping(value = "getPromptByPackageId")
    public Result<String> getPromptByPackageId(@RequestParam(name = "packageId") Integer packageId, @RequestParam(name = "platformName", required = true) String platformName) {
        if (null == packageId || StringUtils.isEmpty(platformName)) {
            return Result.error("请正确传入参数！");
        }
        TBPackages packages = packagesService.getById(packageId);
        if (null == packages) {
            return Result.error("该套餐不存在！");
        }
        try {
            return Result.ok(packagesService.generatePrompt(packages, platformName));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


    /**
     * 通过证书查询店铺的Id
     */
    @AutoLog(value = "UserApp接口-通过证书查询店铺的Id")
    @Operation(summary = "UserApp接口-通过证书查询店铺的Id")
    @GetMapping(value = "getMerchantIdByLicenseKey")
    public Result<Integer> getMerchantIdByLicenseKey(@RequestParam(name = "licenseKey") String licenseKey) {

        Integer merchantId = merchantsService.queryMerchantIdByLicenseKey(licenseKey);
        if (null == merchantId) {
            return Result.error("请检查证书密钥是否正确！");
        } else {
            if (merchantId == 0) {
                return Result.error("请检查证书密钥是否正确！");
            } else if (merchantId == -1) {
                return Result.error("商家证书已过期或不可用！请联系管理员或者代理商！");
            }
            return Result.ok(merchantId);
        }
    }


    /**
     * 获取短链信息
     */
    @AutoLog(value = "UserApp接口-获取短链信息")
    @Operation(summary = "UserApp接口-获取短链信息")
    @GetMapping(value = "getShortLinkInfo")
    public Result<TBMerchantPlatformMiddle> getShortLinkInfo(@RequestParam(name = "merchantId") Integer merchantId, @RequestParam(name = "platformName") String platformName) {
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
     * 获取商家的可用平台
     *
     * @param merchantId 用于接受参数
     * @return
     */
    @AutoLog(value = "UserApp接口-获取商家的可用平台")
    @Operation(summary = "UserApp接口-获取商家的可用平台")
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
    @AutoLog(value = "UserApp接口-获取商家的可用平台对应图片")
    @Operation(summary = "UserApp接口-获取商家的可用平台对应图片")
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
    @AutoLog(value = "UserApp接口-AI生成文案流式返回")
    @Operation(summary = "UserApp接口-AI生成文案流式返回")
    @PostMapping(value = "/ai/generate")
    public SseEmitter aiGenerate(@RequestBody GenerationRequestDTO generationRequestDTO) {
        return aiService.aiGeneByPrompt(generationRequestDTO, true);
    }


    /**
     * AI 以流的方式响应 生成的数据
     */
    @Operation(summary = "UserApp接口-AI流式生成(不会自动保存文案)")
    @GetMapping(value = "/ai/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getAiGenerateByStream(@RequestParam String question) {
        return aiService.getAiGenerateStream(question);
    }

    /**
     * AI一次性返回结果，适合简单问题的回答
     */
    @Operation(summary = "UserApp接口-AI普通生成（适合字数较少的生成，不会自动保存文案）")
    @GetMapping(value = "/ai/generate")
    public Result<String> getAiGenerate(@RequestParam String question) {
        String rsl = aiService.getAiGenerate(question);
        return Result.ok(rsl);
    }

    /**
     * AI一次性返回结果，请求超时时间是 300000 ms,也就是 5 minute
     */
    @Operation(summary = "UserApp接口-AI异步生成（非流式，不会自动保存文案）")
    @GetMapping(value = "/ai/asyncGenerate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void getAiGenerate(HttpServletRequest request, HttpServletResponse response, @RequestParam String question) {
        aiService.getAiGenerate(request, response, question);
    }

    /**
     * 获取数据库中碰一碰项目所用到的所有字典
     *
     * @return
     */
    @AutoLog(value = "UserApp接口-获取数据库中碰一碰项目所用到的所有字典")
    @Operation(summary = "UserApp接口-获取数据库中碰一碰项目所用到的所有字典")
    @PostMapping(value = "/get/pypDict")
    public Result<List<SysDict>> getPypDict() {
        return Result.ok(dictCacheService.findPypAllDict());
    }

    /**
     * 通过 id 获取某一个字典的所有-键值对
     *
     * @return
     */
    @AutoLog(value = "UserApp接口-通过id获取某一个字典的所有-键值对")
    @Operation(summary = "UserApp接口-通过id获取某一个字典的所有-键值对")
    @GetMapping(value = "/get/pypKeyCodeById")
    public Result<List<SysDictItem>> getPypKeyCodeById(@RequestParam(name = "id", required = true) String id) {
        return Result.ok(dictCacheService.findKeyCodeById(id));
    }

    /**
     * 通过 dictCode 获取某一个字典的所有-键值对
     *
     * @return
     */
    @AutoLog(value = "UserApp接口-通过 dictCode 获取某一个字典的所有-键值对")
    @Operation(summary = "UserApp接口-通过 dictCode 获取某一个字典的所有-键值对")
    @GetMapping(value = "/get/pypKeyCodeByDictCode")
    public Result<List<SysDictItem>> getKeyCodeByDictCode(@RequestParam(name = "dictCode", required = true) String dictCode) {
        return Result.ok(dictCacheService.findKeyCodeByDictCode(dictCode));
    }

    /**
     * 通过 id 查询店铺信息
     *
     * @param merchantId 查询店铺信息
     * @return
     */
    @AutoLog(value = "UserApp接口-店铺表-通过id查询店铺信息")
    @Operation(summary = "UserApp接口-店铺表-通过id查询店铺信息")
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
     * 获取系统支持的平台
     */
    @AutoLog(value = "UserApp接口-获取系统支持的平台")
    @Operation(summary = "UserApp接口-获取系统支持的平台")
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
     * 获取wifi信息
     *
     * @param merchantId
     * @return
     */
    @AutoLog(value = "UserApp接口-获取wifi信息")
    @Operation(summary = "UserApp接口-获取wifi信息")
    @RequestMapping(value = "/getWifiInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<WifiDTO> getWifiInfo(@RequestParam(name = "merchantId") Integer merchantId) {
        TBMerchants merchants = merchantsService.getById(merchantId);
        if (merchants == null) {
            return Result.error("商家信息不存在! 无法获取wifi信息！");
        } else {
            WifiDTO result = new WifiDTO();
            result.setMerchantId(merchants.getId());
            result.setWifiUser(merchants.getWifiUser());
            result.setWifiPwd(merchants.getWifiPwd());
            return Result.ok(result);
        }
    }

}
