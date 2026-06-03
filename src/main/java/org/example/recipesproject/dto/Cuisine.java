package org.example.recipesproject.dto;

import lombok.Getter;

@Getter
public enum Cuisine {
    UKRAINIAN("Українська"),
    ITALIAN("Італійська"),
    FRENCH("Французька"),
    ASIAN("Азіатська");

    private final String displayName;

    Cuisine(String displayName) {
        this.displayName = displayName;
    }

}
