package org.example.recipesproject.dto;

import lombok.Getter;

@Getter
public enum DishType {
    FIRST_COURSE("Перші страви"),
    SECOND_COURSE("Другі страви"),
    SALAD("Салати"),
    DESSERT("Десерти");

    private final String displayName;

    DishType(String displayName) {
        this.displayName = displayName;
    }

}
