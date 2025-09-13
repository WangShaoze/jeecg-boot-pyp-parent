package org.jeecg.modules.pengyipeng.controller;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.modules.pengyipeng.entity.TBProblem;
import org.jeecg.modules.pengyipeng.service.ITBProblemService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * @Description: 问题反馈表
 * @Author: jeecg-boot
 * @Date: 2025-09-07
 * @Version: V1.0
 */
@Tag(name = "问题反馈表")
@RestController
@RequestMapping("/pengyipeng/tBProblem")
@Slf4j
public class TBProblemController extends JeecgController<TBProblem, ITBProblemService> {
    @Autowired
    private ITBProblemService tBProblemService;


    /**
     * 关闭问题
     */
    @AutoLog(value = "问题反馈表-关闭问题")
    @Operation(summary = "问题反馈表-关闭问题")
    @GetMapping(value = "/closeProblem")
    public Result<String> closeProblem(@RequestParam(name = "problemId") String problemId,
                                       @RequestParam(name = "closeReason", required = false) String closeReason) {
        if (StringUtils.isEmpty(problemId)) {
            return Result.error("请输入正确的参数！");
        }
        TBProblem problem = tBProblemService.getById(problemId);
        if (problem == null) {
            return Result.error("未找到该问题！");
        }
        if (problem.getIsDeleted().equals(1)) {
            return Result.error("该问题已经关闭！请勿重复操作！");
        }
        problem.setProblemStatus("2");
        problem.setIsDeleted(1);
        problem.setCloseReason(closeReason);
        problem.setCloseTime(new Date());
        boolean isSuccess = tBProblemService.updateById(problem);
        if (!isSuccess) {
            return Result.error("关闭失败！请重试！");
        }
        return Result.ok("问题关闭成功！");
    }


    /**
     * 分页列表查询
     *
     * @param tBProblem
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "问题反馈表-分页列表查询")
    @Operation(summary = "问题反馈表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<TBProblem>> queryPageList(TBProblem tBProblem,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("problemType", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("problemStatus", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("priority", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<TBProblem> queryWrapper = QueryGenerator.initQueryWrapper(tBProblem, req.getParameterMap(), customeRuleMap);
        Page<TBProblem> page = new Page<TBProblem>(pageNo, pageSize);
        IPage<TBProblem> pageList = tBProblemService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param tBProblem
     * @return
     */
    @AutoLog(value = "问题反馈表-添加")
    @Operation(summary = "问题反馈表-添加")
    @RequiresPermissions("pengyipeng:t_b_problem:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody TBProblem tBProblem) {
        if (tBProblem.getIsDeleted() == null) {
            tBProblem.setIsDeleted(0);
        }
        if (StringUtils.isEmpty(tBProblem.getProblemType())) {
            tBProblem.setProblemType("4");
        }
        if (StringUtils.isEmpty(tBProblem.getProblemStatus())) {
            tBProblem.setProblemStatus("0");
        }
        if (StringUtils.isEmpty(tBProblem.getPriority())) {
            tBProblem.setPriority("4");
        }
        tBProblemService.save(tBProblem);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param tBProblem
     * @return
     */
    @AutoLog(value = "问题反馈表-编辑")
    @Operation(summary = "问题反馈表-编辑")
    @RequiresPermissions("pengyipeng:t_b_problem:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody TBProblem tBProblem) {
        tBProblemService.updateById(tBProblem);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "问题反馈表-通过id删除")
    @Operation(summary = "问题反馈表-通过id删除")
    @RequiresPermissions("pengyipeng:t_b_problem:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        tBProblemService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "问题反馈表-批量删除")
    @Operation(summary = "问题反馈表-批量删除")
    @RequiresPermissions("pengyipeng:t_b_problem:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.tBProblemService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "问题反馈表-通过id查询")
    @Operation(summary = "问题反馈表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<TBProblem> queryById(@RequestParam(name = "id", required = true) String id) {
        TBProblem tBProblem = tBProblemService.getById(id);
        if (tBProblem == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(tBProblem);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param tBProblem
     */
    @RequiresPermissions("pengyipeng:t_b_problem:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBProblem tBProblem) {
        return super.exportXls(request, tBProblem, TBProblem.class, "问题反馈表");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("pengyipeng:t_b_problem:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TBProblem.class);
    }
}
