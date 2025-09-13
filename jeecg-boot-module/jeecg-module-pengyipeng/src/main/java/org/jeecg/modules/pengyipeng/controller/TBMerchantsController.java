package org.jeecg.modules.pengyipeng.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.pengyipeng.dto.MerchantServiceInfoDTO;
import org.jeecg.modules.pengyipeng.entity.TBAgent;
import org.jeecg.modules.pengyipeng.entity.TBLicenses;
import org.jeecg.modules.pengyipeng.entity.TBMerchants;
import org.jeecg.modules.pengyipeng.service.ITBAgentService;
import org.jeecg.modules.pengyipeng.service.ITBLicensesService;
import org.jeecg.modules.pengyipeng.service.ITBMerchantsService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * @Description: 店铺表
 * @Author: jeecg-boot
 * @Date: 2025-08-12
 * @Version: V1.0
 */
@Tag(name = "店铺表")
@RestController
@RequestMapping("/pengyipeng/tBMerchants")
@Slf4j
public class TBMerchantsController extends JeecgController<TBMerchants, ITBMerchantsService> {
    @Autowired
    private ITBMerchantsService tBMerchantsService;

    @Autowired
    private ITBLicensesService itbLicensesService;

    @Autowired
    private ITBAgentService itbAgentService;


    /**
     * 冻结
     */
    @Operation(summary = "店铺表-冻结商户")
    @GetMapping(value = "/freezeMerchant")
    public Result<String> freezeMerchant(@RequestParam(name = "merchantId", required = true) Integer merchantId) {
        if (merchantId == null) {
            return Result.error("请传入商户ID");
        }
        // 找到代理商Id
        TBMerchants tbMerchants = tBMerchantsService.getById(merchantId);
        if (tbMerchants == null) {
            return Result.error("商户不存在！");
        }
        if (null != tbMerchants.getAgentId()) {
            TBAgent tbAgent = itbAgentService.getById(tbMerchants.getAgentId());
            if (null == tbAgent) {
                return Result.error("该商户不存在代理商！不可冻结！");
            }
            try {
                tBMerchantsService.freezeMerchant(tbMerchants, tbAgent);
                return Result.OK("冻结成功！");
            } catch (Exception e) {
                return Result.error(e.getMessage());
            }
        } else {
            return Result.error("该商户不存在代理商！不可冻结！");
        }

    }


    /**
     * 续费 每一次续费证书延期2年
     */
    @Operation(summary = "店铺表-为商户续费")
    @GetMapping(value = "/renew")
    public Result<String> renew(@RequestParam(name = "merchantId", required = true) Integer merchantId) {
        if (merchantId == null) {
            return Result.error("请传入商户ID");
        }
        TBMerchants tbMerchants = tBMerchantsService.getById(merchantId);
        if (tbMerchants == null) {
            return Result.error("商户不存在！");
        }
        // 找到代理商Id
        if (null != tbMerchants.getAgentId()) {
            TBAgent tbAgent = itbAgentService.getById(tbMerchants.getAgentId());
            if (null == tbAgent) {
                return Result.error("该商户不存在代理商！不可续费！");
            }
            try {
                // 修改商户的状态  已过期 3 ---》 使用中 4
                // 修改证书的服务结束时间
                // 需要 更新代理商信息 ===> 到期商家数量 && 即将到期商家数量
                tBMerchantsService.renewForMerchant(tbMerchants, tbAgent);
                return Result.OK("续费成功！");
            } catch (Exception e) {
                return Result.error(e.getMessage());
            }
        } else {
            return Result.error("该商户不存在代理商！不可续费！");
        }


    }

    /**
     * 给指定的店家分配 License
     * 如果代理商没有分配，就报 "请先选择代理商！"
     */
    @Operation(summary = "店铺表-给指定的店家分配License")
    @GetMapping(value = "/assignLicense")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> assignLicense(@RequestParam(name = "merchantId", required = true) Integer merchantId) {
        // 找到代理商Id
        TBMerchants tbMerchants = tBMerchantsService.getById(merchantId);
        if (tbMerchants == null) {
            return Result.error("店铺不存在！");
        }
        if (null != tbMerchants.getAgentId()) {
            // 判断这个商户的证书是否已经存在
            TBLicenses tbLicensesJudge = itbLicensesService.getByMerchantId(merchantId);
            if (tbLicensesJudge != null) {
                return Result.error("该店铺已经分配了证书！");
            }

            // 获取时间
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime twoYearsLater = now.plusYears(2);

            itbLicensesService.assignLicense(tbMerchants.getAgentId(), merchantId, now, twoYearsLater);


            Integer licenseUsedCount = itbLicensesService.getAgentLicenseUesed(tbMerchants.getAgentId());
            //  更新代理商的 ( 已使用License数量 ==》 license_used) 和 (商户剩余开通数量)
            TBAgent agent = itbAgentService.getById(tbMerchants.getAgentId());
            agent.setLicenseUsed(licenseUsedCount);
            agent.setLicenseLeave(agent.getLicenseTotal() - licenseUsedCount);
            itbAgentService.updateById(agent);

            // 证书分配以后需要更新商家的服务时间
            tbMerchants.setServiceStartDate(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));
            tbMerchants.setServiceEndDate(Date.from(twoYearsLater.atZone(ZoneId.systemDefault()).toInstant()));
            tbMerchants.setStatus("ACTIVE");
            tBMerchantsService.updateById(tbMerchants);
            return Result.OK("证书分配成功！");
        } else {
            return Result.error("证书分配失败！请先选择代理商！");
        }

    }


    /**
     * 查看 LicenseKey
     */
    @Operation(summary = "店铺表-查看LicenseKey")
    @GetMapping(value = "/showLicenseKey")
    public Result<String> showLicense(@RequestParam(name = "merchantId", required = true) Integer merchantId) {
        String license = itbLicensesService.getKeyByMerchantId(merchantId);
        if (!StringUtils.isEmpty(license)) {
            return Result.ok(license);
        }
        return Result.error("未找到该商家的证书！");
    }

    /**
     * 查看 License
     */
    @Operation(summary = "店铺表-查看License")
    @GetMapping(value = "/showLicense")
    public Result<TBLicenses> showLicenseStatus(@RequestParam(name = "merchantId", required = true) Integer merchantId) {

        TBLicenses tbLicenses = itbLicensesService.getByMerchantId(merchantId);
        if (null != tbLicenses) {
            return Result.ok(tbLicenses);
        }
        return Result.error("未找到该商家的证书信息！");
    }


    /**
     * 给商家分配代理商
     */
    @Operation(summary = "店铺表-给指定的店家指定代理商")
    @GetMapping(value = "/assignAgent")
    public Result<String> assignAgent(@RequestParam(name = "sysUid", required = true) String sysUid,
                                      @RequestParam(name = "merchantId", required = true) Integer merchantId) {

        TBAgent tbAgent = itbAgentService.getAgentBySysUid(sysUid);
        if (tbAgent == null) {
            return Result.error("代理商不存在！");
        }
        try {
            TBMerchants tbMerchants = new TBMerchants();
            tbMerchants.setId(merchantId);
            tbMerchants.setAgentId(tbAgent.getId());
            tBMerchantsService.updateById(tbMerchants);
            return Result.OK("代理商指定成功！");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Result.error("数据库异常！");
        }
    }

    /**
     * 设置平台总代理
     * 是一个特殊的代理，如果其他代理被禁用的话，其名下的所有商户和证书将被转移到平台总代理下
     */
    @AutoLog(value = "店铺表-绑定系统用户")
    @Operation(summary = "店铺表-绑定系统用户")
    @PostMapping(value = "/bindSysUserForMerchant")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> bindSysUserForMerchant(@RequestParam(name = "merchantId", required = true) Integer merchantId,
                                                 @RequestParam(name = "sysUid", required = true) String sysUid) {
        if (merchantId == null) {
            return Result.error("请输入代理商ID!");
        }
        if (StringUtils.isEmpty(sysUid)) {
            return Result.error("请选择系统用户！");
        }
        if (!itbAgentService.isSysUser(sysUid)) {
            return Result.error("该用户不是系统用户！");
        }
        TBMerchants merchant = tBMerchantsService.getById(merchantId);
        if (merchant == null) {
            return Result.error("未找到该商户!");
        } else {
            if (!StringUtils.isEmpty(merchant.getSysUid())) {
                return Result.error("该用户已经是系统用户了！");
            }
            merchant.setSysUid(sysUid);
            tBMerchantsService.updateById(merchant);
            return Result.ok("指定成功！");
        }
    }


    /**
     * 分页列表查询
     */
    @AutoLog(value = "店铺表-分页商家的服务信息查询")
    @Operation(summary = "店铺表-分页商家的服务信息查询")
    @GetMapping(value = "getMerchantServiceInfo/list")
    public Result<IPage<MerchantServiceInfoDTO>> queryPageList(
            @RequestParam(name = "agentSysUid") String agentSysUid,
            @RequestParam(name = "requestEntrance") String requestEntrance,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "column") String column,
            @RequestParam(name = "order", defaultValue = "10") String order) {

        int licenseStatus = switch (requestEntrance) {
            case "EXPIRED" -> 3;
            case "UPCOMING_EXPIRED" -> 5;
            default -> 4;
        };
        Page<MerchantServiceInfoDTO> page = new Page<>(pageNo, pageSize);
        IPage<MerchantServiceInfoDTO> pageList = tBMerchantsService.getMerchantServiceInfo(
                page, agentSysUid, licenseStatus,
                column,
                order);
        return Result.OK(pageList);
    }


    /**
     * 分页列表查询
     *
     * @param tBMerchants
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "店铺表-分页列表查询")
    @Operation(summary = "店铺表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<TBMerchants>> queryPageList(TBMerchants tBMerchants,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        QueryWrapper<TBMerchants> queryWrapper = QueryGenerator.initQueryWrapper(tBMerchants, req.getParameterMap());
        if (tBMerchants.getAgentName() != null && StringUtils.isNotEmpty(tBMerchants.getAgentName().replace("*", ""))) {
            queryWrapper.inSql("agent_id", "select id from t_b_agent ag where ag.`name` like '%" + tBMerchants.getAgentName().replace("*", "") + "%'");
        }
        Page<TBMerchants> page = new Page<TBMerchants>(pageNo, pageSize);
        IPage<TBMerchants> pageList = tBMerchantsService.page(page, queryWrapper);
        return Result.OK(pageList);
    }


    /**
     * 添加
     *
     * @param tBMerchants
     * @return
     */
    @AutoLog(value = "店铺表-添加")
    @Operation(summary = "店铺表-添加")
    @RequiresPermissions("pengyipeng:t_b_merchants:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody TBMerchants tBMerchants) {
        tBMerchantsService.save(tBMerchants);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param tBMerchants
     * @return
     */
    @AutoLog(value = "店铺表-编辑")
    @Operation(summary = "店铺表-编辑")
    @RequiresPermissions("pengyipeng:t_b_merchants:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody TBMerchants tBMerchants) {
        tBMerchantsService.updateById(tBMerchants);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "店铺表-通过id删除")
    @Operation(summary = "店铺表-通过id删除")
    @RequiresPermissions("pengyipeng:t_b_merchants:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        tBMerchantsService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "店铺表-批量删除")
    @Operation(summary = "店铺表-批量删除")
    @RequiresPermissions("pengyipeng:t_b_merchants:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.tBMerchantsService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "店铺表-通过id查询")
    @Operation(summary = "店铺表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<TBMerchants> queryById(@RequestParam(name = "id", required = true) String id) {
        TBMerchants tBMerchants = tBMerchantsService.getById(id);
        if (tBMerchants == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(tBMerchants);
    }


    /**
     * 导出excel
     *
     * @param request
     * @param tBMerchants
     */
    @RequiresPermissions("pengyipeng:t_b_merchants:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBMerchants tBMerchants) {
        return super.exportXls(request, tBMerchants, TBMerchants.class, "店铺表");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("pengyipeng:t_b_merchants:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TBMerchants.class);
    }

}
