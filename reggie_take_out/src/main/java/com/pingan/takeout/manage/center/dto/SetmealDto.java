package com.pingan.takeout.manage.center.dto;

import com.pingan.takeout.manage.center.entity.Setmeal;
import com.pingan.takeout.manage.center.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
