package org.example.recipesproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.recipesproject.dto.Cuisine;
import org.example.recipesproject.dto.DishType;
import org.example.recipesproject.dto.Recipe;

import org.example.recipesproject.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public String findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dishType,
            @RequestParam(required = false) String cuisine,
            Model model
    ) {
        model.addAttribute("recipes", recipeService.getFilteredAndSortedRecipes(search, sort, dishType, cuisine));
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("currentDishType", dishType);
        model.addAttribute("currentCuisine", cuisine);
        model.addAttribute("allDishTypes", DishType.values());
        model.addAttribute("allCuisines", Cuisine.values());
        return "recipes-list";
    }

    @GetMapping("/{id}")
    public String findById(
            @PathVariable Long id,
            Model model
    ) {
        Recipe recipe = recipeService.findById(id);
        model.addAttribute("recipe", recipe);
        return "recipe-details";
    }

    @GetMapping("/add")
    public String addRecipe(Model model) {
        model.addAttribute("recipeForm", new Recipe());
        model.addAttribute("allDishTypes", DishType.values());
        model.addAttribute("allCuisines", Cuisine.values());
        return "recipe-form";
    }

    @GetMapping("/edit/{id}")
    public String editRecipe(@PathVariable Long id, Model model) {
        model.addAttribute("recipeForm", recipeService.findById(id));
        model.addAttribute("allDishTypes", DishType.values());
        model.addAttribute("allCuisines", Cuisine.values());
        return "recipe-form";
    }

    @PostMapping("/save")
    public String saveRecipe(
            @ModelAttribute("recipeForm") Recipe recipe,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        try {
            recipeService.saveRecipe(recipe, imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Помилка завантаження зображення: невірний формат або файл пошкоджено.");
        }
        return "redirect:/recipes";
    }

    @PostMapping("/delete/{id}")
    public String deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return "redirect:/recipes";
    }

    @GetMapping("/export")
    public ResponseEntity<org.springframework.core.io.Resource> exportRecipes() {
        try {
            File file = recipeService.exportRecipes();
            org.springframework.core.io.Resource resource = new org.springframework.core.io.FileSystemResource(file);
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/import")
    public String importRecipes(@RequestParam("file") MultipartFile multipartFile, RedirectAttributes redirectAttributes) {
        try {
            recipeService.importRecipes(multipartFile);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Помилка імпорту: файл пошкоджений або має невірний формат.");
        }
        return "redirect:/recipes";
    }
}