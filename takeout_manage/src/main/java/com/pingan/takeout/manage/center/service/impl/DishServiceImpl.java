package com.pingan.takeout.manage.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.takeout.manage.center.common.NotEnoughStockException;
import com.pingan.takeout.manage.center.dto.DishDto;
import com.pingan.takeout.manage.center.entity.Dish;
import com.pingan.takeout.manage.center.entity.DishFlavor;
import com.pingan.takeout.manage.center.entity.ShoppingCart;
import com.pingan.takeout.manage.center.mapper.DishMapper;
import com.pingan.takeout.manage.center.service.DishFlavorService;
import com.pingan.takeout.manage.center.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishService dishService;
    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);
        //保存菜品口味数据到菜品口味表dish_flavor
        Long dishId = dishDto.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);//实际上是根据dish，但dishDto是dish的子类

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(queryWrapper);
        //delete from dish_flavor where dish_id = ???

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        //跟新增时一样，需要从dishDto中拿到dishId赋给flavors
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateStock(List<ShoppingCart> shoppingCarts) {
        Map<Long,Integer> dishAmount = new HashMap<>();//扩展功能

        for(ShoppingCart shoppingCart : shoppingCarts) {
            dishAmount.put(shoppingCart.getDishId(),shoppingCart.getNumber());//扩展功能
        }

        //扩展功能：菜品超售检测
        //查询购物车涉及到的dish的余量，通过map的key来查询
        LambdaQueryWrapper<Dish> wrapperDish = new LambdaQueryWrapper<>();
        wrapperDish.in(Dish::getId,
                dishAmount.keySet().stream().collect(Collectors.toList())//map的keyset包含了所有菜品id
        ).last("FOR UPDATE");//添加行锁
        List<Dish> dishList =  dishService.list(wrapperDish);
        //对dish中的remainingAmount做 修改 并 保存
        List<Dish> dishes = dishList.stream().map((item)->{
            Integer remains = item.getRemainingAmount();
            Integer sub = dishAmount.get(item.getId());
            if(remains-sub<0){//下单不成功，点单数大于库存数
                throw new NotEnoughStockException("库存不足，下单失败");
            }else{
                item.setRemainingAmount(remains-sub);
            }
            return item;
        }).collect(Collectors.toList());
        dishService.updateBatchById(dishes);
    }
}
