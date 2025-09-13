package org.jeecg.modules.pengyipeng.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.pengyipeng.dto.AgentIndexDTO;
import org.jeecg.modules.pengyipeng.dto.TotalIndexDTO;
import org.jeecg.modules.pengyipeng.entity.TBAgent;
import org.jeecg.modules.pengyipeng.service.ITBAgentService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

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
 * @Description: 运营商
 * @Author: jeecg-boot
 * @Date: 2025-08-13
 * @Version: V1.0
 */
@Tag(name = "运营商")
@RestController
@RequestMapping("/pengyipeng/tBAgent")
@Slf4j
public class TBAgentController extends JeecgController<TBAgent, ITBAgentService> {
    @Autowired
    private ITBAgentService tBAgentService;


    /**
     * @param agentSysUid 代理商在系统中的用户ID
     */
    @AutoLog(value = "运营商-获取首页展示的数据")
    @Operation(summary = "运营商-获取首页展示的数据")
    @PostMapping(value = "/getAgentIndexData")
    public Result<AgentIndexDTO> getAgentIndexData(
            @RequestParam(name = "agentSysUid", required = true)
            String agentSysUid) {
        try {
            if (StringUtils.isEmpty(agentSysUid)) {
                return Result.ok("找不到该代理商！");
            } else {
                TBAgent agent = tBAgentService.getAgentBySysUid(agentSysUid);
                if (agent == null) {
                    return Result.ok("找不到该代理商！");
                } else {
                    return Result.ok(tBAgentService.queryAgentIndexData(agent));
                }
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取总后台的 代理数量 、商户数量
     */
    @AutoLog(value = "运营商-获取首页展示的数据")
    @Operation(summary = "运营商-获取首页展示的数据")
    @PostMapping(value = "/getTotalIndexData")
    public Result<TotalIndexDTO> getTotalIndexData() {
        try {
            return Result.ok(tBAgentService.queryTotalIndexData());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


    /**
     * 设置平台总代理
     * 是一个特殊的代理，如果其他代理被禁用的话，其名下的所有商户和证书将被转移到平台总代理下
     */
    @AutoLog(value = "运营商-绑定系统用户")
    @Operation(summary = "运营商-绑定系统用户")
    @PostMapping(value = "/setSysUserForAgent")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> setSysUserForAgent(@RequestParam(name = "agentId", required = true) Integer agentId,
                                             @RequestParam(name = "sysUid", required = true) String sysUid) {
        if (agentId == null) {
            return Result.error("请输入代理商ID!");
        }
        if (StringUtils.isEmpty(sysUid)) {
            return Result.error("请选择系统用户！");
        }

        if (!tBAgentService.isSysUser(sysUid)) {
            return Result.error("该用户不是系统用户！");
        }
        TBAgent agent = tBAgentService.getById(agentId);
        if (agent == null) {
            return Result.error("未找到该代理商!");
        } else {
            if (!StringUtils.isEmpty(agent.getSysUid())) {
                return Result.error("该用户已经是系统用户了！");
            }
            agent.setSysUid(sysUid);
            tBAgentService.updateById(agent);
            return Result.ok("指定成功！");
        }
    }


    /**
     * 设置平台总代理
     * 是一个特殊的代理，如果其他代理被禁用的话，其名下的所有商户和证书将被转移到平台总代理下
     */
    @AutoLog(value = "运营商-设置平台总代理")
    @Operation(summary = "运营商-设置平台总代理")
    @PostMapping(value = "/setPlatformAgent")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> setPlatformAgent(@RequestParam(name = "agentId", required = true) Integer agentId) {
        if (agentId == null) {
            return Result.ok("传入参数有误！");
        }
        // 查询中代理的数量
        QueryWrapper<TBAgent> platformAgentWrapper = new QueryWrapper<>();
        platformAgentWrapper.eq("status", 1);  // 可用
        platformAgentWrapper.eq("level", 4);   // 平台总代理
        long platformAgentCount = tBAgentService.count(platformAgentWrapper);
        if (platformAgentCount > 0) {
            log.error("setPlatformAgent: 方法中存在异常操作，只能设置1个总代理");
            return Result.error("只能设置1个总代理！");
        }

        QueryWrapper<TBAgent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", agentId);
        TBAgent agent = tBAgentService.getById(queryWrapper);
        if (agent == null) {
            return Result.ok("未找到该代理商！");
        } else {
            agent.setLevel(4);  //  4 在数据字典中表示 平台总代理
            if (tBAgentService.updateById(agent)) {
                return Result.ok("已将" + agent.getName() + "设置为平台总代理！");
            } else {
                return Result.error("出现错误！设置失败！请重试！");
            }
        }
    }


    /**
     * 冻结代理商
     */
    @AutoLog(value = "运营商-冻结代理商")
    @Operation(summary = "运营商-冻结代理商")
    @PostMapping(value = "/frozenAgent")
    public Result<String> frozenAgent(@RequestParam(name = "agentId", required = true) Integer agentId) {
        if (agentId == null) {
            return Result.ok("传入参数有误！");
        }
        // 查询中代理的数量
        QueryWrapper<TBAgent> platformAgentWrapper = new QueryWrapper<>();
        platformAgentWrapper.eq("status", 1);  // 可用
        platformAgentWrapper.eq("level", 4);   // 平台总代理
        List<TBAgent> list = tBAgentService.getBaseMapper().selectList(platformAgentWrapper);
        if (list.size() != 1) {
            log.error("frozenAgent: {}", "存在多个个总代理！或者不存在总代理！请检查！");
            return Result.error("存在多个个总代理！或者不存在总代理！请检查！");
        }
        QueryWrapper<TBAgent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", agentId);
        TBAgent agent = tBAgentService.getById(queryWrapper);
        if (agent == null) {
            return Result.ok("未找到该代理！请从前端页面操作！");
        } else {
            try {
                tBAgentService.forbiddenAgent(agent, list.get(0));
                return Result.OK("冻结成功！");
            } catch (Exception e) {
                log.error("frozenAgent: {}", e.getMessage());
                return Result.error("冻结失败！", e.getMessage());
            }
        }
    }


    /**
     * batchAddLicenseCount
     * 批量给 代理商 增加 License 数量
     */
    @AutoLog(value = "运营商-批量增加License数量")
    @Operation(summary = "运营商-批量增加License数量")
    @PostMapping(value = "/batchAddLicenseCount")
    public Result<String> batchAddLicenseCount(@RequestParam(name = "agentId", required = true) Integer agentId,
                                               @RequestParam(name = "licenseIds", required = true) List<Integer> licenseIds) {
        if (agentId == null || licenseIds == null || licenseIds.isEmpty()) {
            return Result.error("参数输入有误！");
        } else {
            TBAgent tbAgent = tBAgentService.getById(agentId);
            if (null == tbAgent) {
                return Result.error("未找到指定的代理商！");
            } else {
                int successNum = tBAgentService.batchAddLicenseCount(tbAgent, licenseIds);
                return Result.OK("代理商" + tbAgent.getName() + "成功增加" + successNum + "个License！");
            }
        }
    }


    /**
     * 分页列表查询
     *
     * @param tBAgent
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "运营商-分页列表查询")
    @Operation(summary = "运营商-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<TBAgent>> queryPageList(TBAgent tBAgent,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                HttpServletRequest req) {
        QueryWrapper<TBAgent> queryWrapper = QueryGenerator.initQueryWrapper(tBAgent, req.getParameterMap());
        Page<TBAgent> page = new Page<TBAgent>(pageNo, pageSize);
        IPage<TBAgent> pageList = tBAgentService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param tBAgent
     * @return
     */
    @AutoLog(value = "运营商-添加")
    @Operation(summary = "运营商-添加")
    @RequiresPermissions("pengyipeng:t_b_agent:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody TBAgent tBAgent) {
        tBAgentService.save(tBAgent);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param tBAgent
     * @return
     */
    @AutoLog(value = "运营商-编辑")
    @Operation(summary = "运营商-编辑")
    @RequiresPermissions("pengyipeng:t_b_agent:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody TBAgent tBAgent) {
        tBAgentService.updateById(tBAgent);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "运营商-通过id删除")
    @Operation(summary = "运营商-通过id删除")
    @RequiresPermissions("pengyipeng:t_b_agent:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        tBAgentService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "运营商-批量删除")
    @Operation(summary = "运营商-批量删除")
    @RequiresPermissions("pengyipeng:t_b_agent:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.tBAgentService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "运营商-通过id查询")
    @Operation(summary = "运营商-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<TBAgent> queryById(@RequestParam(name = "id", required = true) String id) {
        TBAgent tBAgent = tBAgentService.getById(id);
        if (tBAgent == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(tBAgent);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param tBAgent
     */
    @RequiresPermissions("pengyipeng:t_b_agent:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBAgent tBAgent) {
        return super.exportXls(request, tBAgent, TBAgent.class, "运营商");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("pengyipeng:t_b_agent:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TBAgent.class);
    }

}
