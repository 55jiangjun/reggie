package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");

    }
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/page")
    public R<Page> list(Integer page, Integer pageSize,String name){
        Page<Dish> dishPage = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.orderByAsc(Dish::getPrice);
        dishLambdaQueryWrapper.like(name!=null,Dish::getName,name);
        Page<Dish> page1 = dishService.page(dishPage, dishLambdaQueryWrapper);
        BeanUtils.copyProperties(page1,dishDtoPage,"records");
        List<Dish> records = page1.getRecords();
        List<DishDto> collect = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            if(byId!=null){
                String name1 = byId.getName();
                dishDto.setCategoryName(name1);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(collect);
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> modify(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

//    @GetMapping("/list")
//    public R<List<Dish>> list( Long categoryId){
//        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        dishLambdaQueryWrapper.eq(Dish::getCategoryId,categoryId);
//        dishLambdaQueryWrapper.orderByAsc(Dish::getSort);
//        dishLambdaQueryWrapper.eq(Dish::getStatus,1);
//        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
//        
//        return R.success(list);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> list( Long categoryId){
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,categoryId);
        dishLambdaQueryWrapper.orderByAsc(Dish::getSort);
        dishLambdaQueryWrapper.eq(Dish::getStatus,1);
        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
        List<DishDto> dishDtos = list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
            List<DishFlavor> list1 = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(list1);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtos);
    }

    /**
     * 批量删除菜品
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids){
        for (Long id:ids) {
            dishService.removeById(id);
        }
        return R.success("批量删除成功");
    }

    /**
     * 批量禁用用户
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> Status(@PathVariable Integer status,Long[] ids ){
        Dish dish = new Dish();
        dish.setStatus(status);
        for (Long id:ids) {
            LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishLambdaQueryWrapper.eq(Dish::getId,id);
            dishService.update(dish,dishLambdaQueryWrapper);
        }
        return R.success("成功");
    }
}
