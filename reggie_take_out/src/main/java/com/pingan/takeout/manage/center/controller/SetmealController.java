package com.pingan.takeout.manage.center.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pingan.takeout.manage.center.common.R;
import com.pingan.takeout.manage.center.dto.SetmealDto;
import com.pingan.takeout.manage.center.entity.Category;
import com.pingan.takeout.manage.center.entity.Setmeal;
import com.pingan.takeout.manage.center.service.CategoryService;
import com.pingan.takeout.manage.center.service.SetmealDishService;
import com.pingan.takeout.manage.center.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
@Api(tags="套餐相关接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @CacheEvict(value="setmealCache",allEntries = true)
    @ApiOperation(value="新增套餐接口")
    public R<String> save(@RequestBody SetmealDto setmealDto){//json格式数据要加requestbody
        log.info("套餐信息:{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value="套餐分页查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value="页码",required = true),
            @ApiImplicitParam(name="pageSize",value="每页记录数",required = true),
            @ApiImplicitParam(name="name",value="套餐名称",required = false),
    })
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();


        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(name != null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            //分类对象
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }
    @DeleteMapping
    @CacheEvict(value="setmealCache",allEntries = true)
    @ApiOperation(value = "套餐删除接口")
    public R<String> delete(@RequestParam List<Long> ids){//参数可能有多个
        log.info("ids:{}",ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value="setmealCache",key="#setmeal.categoryId + '_' + #setmeal.status")
    @ApiOperation(value="套餐条件查询接口")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
    @PostMapping("/status/0")
    public R<String> closeStatus(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId,ids);

        List<Setmeal> setmeals = setmealService.list(queryWrapper);

        for(Setmeal setmeal : setmeals){
            setmeal.setStatus(0);
            setmealService.updateById(setmeal);
        }
        return R.success("修改成功");
    }
    @PostMapping("/status/1")
    public R<String> openStatus(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId,ids);

        List<Setmeal> setmeals = setmealService.list(queryWrapper);

        for(Setmeal setmeal : setmeals){
            setmeal.setStatus(1);
            setmealService.updateById(setmeal);
        }
        return R.success("修改成功");
    }
}
