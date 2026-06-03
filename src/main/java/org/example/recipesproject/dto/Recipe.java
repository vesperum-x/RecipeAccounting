package org.example.recipesproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe implements Serializable {
    private Long id;
    private String name;
    private String description;
    private int cookingTime;
    private int caloriesAmount;
    private int servingsAmount;
    private String cookingInstruction;
    private String imgReference;
    private DishType dishType;
    private Cuisine cuisineType;
}
