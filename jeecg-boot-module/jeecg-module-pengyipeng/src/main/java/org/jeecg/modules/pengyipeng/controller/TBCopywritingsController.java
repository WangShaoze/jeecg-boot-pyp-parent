package org.jeecg.modules.pengyipeng.controller;

import java.util.Arrays;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.pengyipeng.dto.GenerationRequestDTO;
import org.jeecg.modules.pengyipeng.entity.TBCopywritings;
import org.jeecg.modules.pengyipeng.service.AiService;
import org.jeecg.modules.pengyipeng.service.ITBCopywritingsService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @Description: 文案表
 * @Author: jeecg-boot
 * @Date: 2025-08-16
 * @Version: V1.0
 */
@Tag(name = "文案表")
@RestController
@RequestMapping("/pengyipeng/tBCopywritings")
@Slf4j
public class TBCopywritingsController extends JeecgController<TBCopywritings, ITBCopywritingsService> {

    @Resource
    private ITBCopywritingsService tBCopywritingsService;

    @Resource
    private AiService aiService;


    /**
     * AI生成文案流式返回
     *
     * @param generationRequestDTO
     * @return
     */
    @AutoLog(value = "文案表-AI生成文案流式返回")
    @Operation(summary = "文案表-AI生成文案流式返回")
    @PostMapping(value = "/ai/generate")
    public SseEmitter aiGenerate(@RequestBody GenerationRequestDTO generationRequestDTO) {
        return aiService.aiGeneByPrompt(generationRequestDTO, true);
    }

    /**
     * 分页列表查询
     *
     * @param tBCopywritings
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "文案表-分页列表查询")
    @Operation(summary = "文案表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<TBCopywritings>> queryPageList(TBCopywritings tBCopywritings,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       HttpServletRequest req) {
        QueryWrapper<TBCopywritings> queryWrapper = QueryGenerator.initQueryWrapper(tBCopywritings, req.getParameterMap());
        Page<TBCopywritings> page = new Page<TBCopywritings>(pageNo, pageSize);
        IPage<TBCopywritings> pageList = tBCopywritingsService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param tBCopywritings
     * @return
     */
    @AutoLog(value = "文案表-添加")
    @Operation(summary = "文案表-添加")
    @RequiresPermissions("pengyipeng:t_b_copywritings:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody TBCopywritings tBCopywritings) {

        // 这里是对添加功能的特殊实现
        QueryWrapper<TBCopywritings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("session_id", tBCopywritings.getSessionId());
        TBCopywritings tbCopywritings = tBCopywritingsService.getOne(queryWrapper);
        tBCopywritings.setId(tbCopywritings.getId());
        tBCopywritingsService.saveOrUpdate(tBCopywritings);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param tBCopywritings
     * @return
     */
    @AutoLog(value = "文案表-编辑")
    @Operation(summary = "文案表-编辑")
    @RequiresPermissions("pengyipeng:t_b_copywritings:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody TBCopywritings tBCopywritings) {
        tBCopywritingsService.updateById(tBCopywritings);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "文案表-通过id删除")
    @Operation(summary = "文案表-通过id删除")
    @RequiresPermissions("pengyipeng:t_b_copywritings:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        tBCopywritingsService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "文案表-批量删除")
    @Operation(summary = "文案表-批量删除")
    @RequiresPermissions("pengyipeng:t_b_copywritings:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.tBCopywritingsService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "文案表-通过id查询")
    @Operation(summary = "文案表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<TBCopywritings> queryById(@RequestParam(name = "id", required = true) String id) {
        TBCopywritings tBCopywritings = tBCopywritingsService.getById(id);
        if (tBCopywritings == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(tBCopywritings);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param tBCopywritings
     */
    @RequiresPermissions("pengyipeng:t_b_copywritings:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBCopywritings tBCopywritings) {
        return super.exportXls(request, tBCopywritings, TBCopywritings.class, "文案表");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("pengyipeng:t_b_copywritings:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TBCopywritings.class);
    }

}
