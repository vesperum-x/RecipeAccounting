package org.example.recipesproject.repository;

import org.example.recipesproject.dto.Recipe;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RecipeRepository {
    private List<Recipe> recipes = new ArrayList<>();

    public List<Recipe> findAll() {
        return new ArrayList<>(recipes);
    }

    public void setRecipes(List<Recipe> newRecipes) {
        if (newRecipes != null) {
            this.recipes = new ArrayList<>(newRecipes);
        } else {
            this.recipes = new ArrayList<>();
        }
    }

    public Optional<Recipe> findById(Long id) {
        return recipes.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    public Recipe save(Recipe recipe) {
        if (recipe.getId() == null) {
            long maxId = 0;
            for (Recipe r : recipes) {
                if (r.getId() != null && r.getId() > maxId) {
                    maxId = r.getId();
                }
            }
            recipe.setId(maxId + 1);
            recipes.add(recipe);
        } else {
            int index = -1;
            for (int i = 0; i < recipes.size(); i++) {
                if (recipes.get(i).getId().equals(recipe.getId())) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                recipes.set(index, recipe);
            } else {
                recipes.add(recipe);
            }
        }
        return recipe;
    }

    public void deleteById(Long id) {
        recipes.removeIf(r -> r.getId().equals(id));
    }

    public void deleteAll() {
        recipes.clear();
    }
}
