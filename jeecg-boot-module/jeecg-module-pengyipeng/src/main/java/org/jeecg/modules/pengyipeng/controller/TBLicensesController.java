package org.jeecg.modules.pengyipeng.controller;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateTime;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.MinioUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.pengyipeng.entity.TBAgent;
import org.jeecg.modules.pengyipeng.entity.TBLicenses;
import org.jeecg.modules.pengyipeng.service.ITBAgentService;
import org.jeecg.modules.pengyipeng.service.ITBLicensesService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.pengyipeng.utils.ImageCompressUtil;
import org.jeecg.modules.pengyipeng.utils.QRCodeGenerator;
import org.jeecg.modules.pengyipeng.utils.RandomUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * @Description: 证书表
 * @Author: jeecg-boot
 * @Date: 2025-08-13
 * @Version: V1.0
 */
@Tag(name = "证书表")
@RestController
@RequestMapping("/pengyipeng/tBLicenses")
@Slf4j
public class TBLicensesController extends JeecgController<TBLicenses, ITBLicensesService> {
    @Autowired
    private ITBLicensesService tBLicensesService;

    @Autowired
    private ITBAgentService agentService;


    /**
     * 批量生成二维码
     */
    @AutoLog(value = "证书表-批量生成二维码")
    @Operation(summary = "证书表-批量生成二维码")
    @GetMapping(value = "/batchGenerateQrCode")
    public Result<String> batchGenerateQrCode(@RequestParam String ids) {
        if (StringUtils.isEmpty(ids)) {
            return Result.error("请正确输入参数！");
        }
        List<String> idList = Arrays.asList(ids.split(","));
        if (idList.isEmpty()) {
            return Result.error("请选择证书后操作！");
        }
        QueryWrapper<TBLicenses> listQuery = new QueryWrapper<>();
        listQuery.in("id", idList);
        List<TBLicenses> licensesList = tBLicensesService.list(listQuery);
        if (licensesList.size() != idList.size()) {
            return Result.error("数据不齐！部分证书数据再数据库中不存在！");
        }
        List<String> licenseKeyList = licensesList.stream().map(TBLicenses::getLicenseKey).collect(Collectors.toList());
        try {
            tBLicensesService.batchGenerateCustomQRCode(licensesList, licenseKeyList);
            return Result.OK("批量生成成功！");
        } catch (Exception e) {
            log.info("handleGenerateQrCode: 运行出错了！错误信息:{}", e.getMessage());
            return Result.error(e.getMessage());
        }

    }

    /**
     * 批量下载二维码
     */
    @AutoLog(value = "证书表-批量下载二维码")
    @Operation(summary = "证书表-批量下载二维码")
    @GetMapping(value = "/batchDownloadQrCode")
    public Result<String> batchDownloadQrCode(@RequestParam String ids) {
        if (StringUtils.isEmpty(ids)) {
            return Result.error("请正确输入参数！");
        }
        List<String> idList = Arrays.asList(ids.split(","));
        if (idList.isEmpty()) {
            return Result.error("请选择证书后操作！");
        }
        QueryWrapper<TBLicenses> listQuery = new QueryWrapper<>();
        listQuery.in("id", idList);
        List<TBLicenses> licensesList = tBLicensesService.list(listQuery);

        // 精确到十分秒（100 ms）的当前时间字符串
        String tenthSecTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(System.currentTimeMillis() / 100 * 100),
                ZoneId.systemDefault()
        ).format(DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分ss秒"));
        String zipFileName = "二维码_" + RandomUtil.randomAlphanumeric6() + "_" + tenthSecTime + ".zip";
        List<String> urls = licensesList.stream().map(TBLicenses::getQrCodeUrl).filter(Objects::nonNull).toList();
        if (urls.isEmpty()) {
            return Result.error("请确保二维码已经生成！");
        }
        try {
            // 步骤1：压缩图片为ZIP
            File zipFile = ImageCompressUtil.compressImagesToZip(urls, zipFileName);
            // 步骤2：上传ZIP到MinIO，生成下载地址
            String zipFileUrl = MinioUtil.uploadZipToMinIO(zipFile, null, zipFileName);
            return Result.OK(zipFileUrl);
        } catch (Exception e) {
            log.error("handleDownloadQrCode: 报错！错误信息如下:{}", e.getMessage());
            return Result.error("批量下载二维码出错！");
        }
    }

    /**
     * 分页列表查询
     *
     * @param tBLicenses
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "证书表-分页列表查询")
    @Operation(summary = "证书表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<TBLicenses>> queryPageList(TBLicenses tBLicenses,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest req) {
        QueryWrapper<TBLicenses> queryWrapper = QueryGenerator.initQueryWrapper(tBLicenses, req.getParameterMap());
        Page<TBLicenses> page = new Page<TBLicenses>(pageNo, pageSize);
        IPage<TBLicenses> pageList = tBLicensesService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param tBLicenses
     * @return
     */
    @AutoLog(value = "证书表-添加")
    @Operation(summary = "证书表-添加")
    @RequiresPermissions("pengyipeng:t_b_licenses:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody TBLicenses tBLicenses) {
        tBLicensesService.save(tBLicenses);
        return Result.OK("添加成功！");
    }

    /**
     * 批量添加
     *
     * @param licenseKeys
     * @return
     */
    @AutoLog(value = "证书表-批量添加")
    @Operation(summary = "证书表-批量添加")
    @GetMapping(value = "/addBatch")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> addBatch(@RequestParam(name = "licenseKeys", required = true) String licenseKeys) {
        String[] licenseKeysArr = licenseKeys.split("\n");
        if (licenseKeysArr.length == 0) {
            return Result.error("数据添加失败！没有需要证书！");
        } else if (licenseKeysArr.length > 1000) {
            return Result.error("数据添加失败！一次最多添加1000条数据！");
        }
        List<TBLicenses> list = new ArrayList<>();
        for (String s : licenseKeysArr) {
            s = s.trim();
            if (s.length() == 36 && s.contains("-") && tBLicensesService.hasFourHyphens(s)) {
                TBLicenses tbLicenses = new TBLicenses();
                tbLicenses.setLicenseKey(s);
                list.add(tbLicenses);
            } else {
                return Result.error("数据添加失败！存在数据不符合License规范！");
            }
        }
        if (tBLicensesService.saveBatch(list)) {
            return Result.OK("添加成功！");
        } else {
            return Result.error("数据添加失败！违反数据添加规则！");
        }
    }


    /**
     * 分配代理商
     *
     * @param agentId    代理Id
     * @param licenseIds 所选证书的Id
     * @return
     */
    @AutoLog(value = "证书表-分配代理商")
    @Operation(summary = "证书表-分配代理商")
    @GetMapping(value = "/assignAgent")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> addBatch(@RequestParam(name = "agentId", required = true) Integer agentId,
                                   @RequestParam(name = "licenseIds", required = true) String licenseIds) {
        if (agentId == null) {
            return Result.error("未找到该代理商！");
        } else {
            TBAgent tbAgent = agentService.getById(agentId);
            if (tbAgent == null) {
                return Result.error("未找到该代理商！并从指定入口进入！");
            }
        }

        String[] licenseIdsArr = null;
        if (!StringUtils.isEmpty(licenseIds)) {
            licenseIds = licenseIds.trim();
            licenseIds = licenseIds.substring(1, licenseIds.length() - 1);
            licenseIdsArr = licenseIds.split(",");
        }
        if (licenseIdsArr == null || licenseIdsArr.length == 0) {
            return Result.error("请选择证书！并从指定入口进入！");
        }
        List<TBLicenses> list = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        // 将 LocalDateTime 转换为 ZonedDateTime (默认时区)
        Date nowDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Date oneYearDate = Date.from(now.plusYears(1).atZone(ZoneId.systemDefault()).toInstant());
        for (String licenseId : licenseIdsArr) {
            TBLicenses tbLicenses = new TBLicenses();
            tbLicenses.setId(Integer.parseInt(licenseId));
            tbLicenses.setAgentId(agentId);
            tbLicenses.setStatus(2);   // 状态变为已分配
            tbLicenses.setStartDate(nowDate); // 分配开始时间
            tbLicenses.setEndDate(oneYearDate);  // 证书到期时间
            list.add(tbLicenses);
        }
        if (tBLicensesService.saveOrUpdateBatch(list)) {
            // 更新代理商对应的License总数
            TBAgent tbAgent = agentService.getById(agentId);
            tbAgent.setLicenseTotal(Integer.parseInt(tBLicensesService.getByAgentId(agentId).toString()));
            agentService.updateById(tbAgent);
            return Result.ok("代理商分配成功！");
        } else {
            return Result.error("数据添加失败！违反数据添加规则！");

        }
    }


    /**
     * 编辑
     *
     * @param tBLicenses
     * @return
     */
    @AutoLog(value = "证书表-编辑")
    @Operation(summary = "证书表-编辑")
    @RequiresPermissions("pengyipeng:t_b_licenses:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody TBLicenses tBLicenses) {
        tBLicensesService.updateById(tBLicenses);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "证书表-通过id删除")
    @Operation(summary = "证书表-通过id删除")
    @RequiresPermissions("pengyipeng:t_b_licenses:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        tBLicensesService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "证书表-批量删除")
    @Operation(summary = "证书表-批量删除")
    @RequiresPermissions("pengyipeng:t_b_licenses:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.tBLicensesService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
//@AutoLog(value = "证书表-通过id查询")
    @Operation(summary = "证书表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<TBLicenses> queryById(@RequestParam(name = "id", required = true) String id) {
        TBLicenses tBLicenses = tBLicensesService.getById(id);
        if (tBLicenses == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(tBLicenses);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param tBLicenses
     */
    @RequiresPermissions("pengyipeng:t_b_licenses:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBLicenses tBLicenses) {
        return super.exportXls(request, tBLicenses, TBLicenses.class, "证书表");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("pengyipeng:t_b_licenses:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TBLicenses.class);
    }

}
