package org.jeecg.modules.pengyipeng.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.pengyipeng.entity.TBPackages;
import org.jeecg.modules.pengyipeng.service.ITBPackagesService;

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
 * @Description: 套餐表
 * @Author: jeecg-boot
 * @Date:   2025-08-12
 * @Version: V1.0
 */
@Tag(name="套餐表")
@RestController
@RequestMapping("/pengyipeng/tBPackages")
@Slf4j
public class TBPackagesController extends JeecgController<TBPackages, ITBPackagesService> {
	@Autowired
	private ITBPackagesService tBPackagesService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tBPackages
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "套餐表-分页列表查询")
	@Operation(summary="套餐表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TBPackages>> queryPageList(TBPackages tBPackages,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<TBPackages> queryWrapper = QueryGenerator.initQueryWrapper(tBPackages, req.getParameterMap());
		Page<TBPackages> page = new Page<TBPackages>(pageNo, pageSize);
		IPage<TBPackages> pageList = tBPackagesService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tBPackages
	 * @return
	 */
	@AutoLog(value = "套餐表-添加")
	@Operation(summary="套餐表-添加")
	@RequiresPermissions("pengyipeng:t_b_packages:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TBPackages tBPackages) {
		tBPackagesService.save(tBPackages);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tBPackages
	 * @return
	 */
	@AutoLog(value = "套餐表-编辑")
	@Operation(summary="套餐表-编辑")
	@RequiresPermissions("pengyipeng:t_b_packages:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TBPackages tBPackages) {
		tBPackagesService.updateById(tBPackages);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "套餐表-通过id删除")
	@Operation(summary="套餐表-通过id删除")
	@RequiresPermissions("pengyipeng:t_b_packages:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tBPackagesService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "套餐表-批量删除")
	@Operation(summary="套餐表-批量删除")
	@RequiresPermissions("pengyipeng:t_b_packages:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tBPackagesService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "套餐表-通过id查询")
	@Operation(summary="套餐表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TBPackages> queryById(@RequestParam(name="id",required=true) String id) {
		TBPackages tBPackages = tBPackagesService.getById(id);
		if(tBPackages==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tBPackages);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tBPackages
    */
    @RequiresPermissions("pengyipeng:t_b_packages:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBPackages tBPackages) {
        return super.exportXls(request, tBPackages, TBPackages.class, "套餐表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("pengyipeng:t_b_packages:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TBPackages.class);
    }

}
