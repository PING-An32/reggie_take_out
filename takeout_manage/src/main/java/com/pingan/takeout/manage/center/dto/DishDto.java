package com.pingan.takeout.manage.center.dto;

import com.pingan.takeout.manage.center.entity.Dish;
import com.pingan.takeout.manage.center.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
