package org.example.recipesproject.service;

import lombok.RequiredArgsConstructor;
import org.example.recipesproject.dto.Cuisine;
import org.example.recipesproject.dto.DishType;
import org.example.recipesproject.dto.Recipe;
import org.example.recipesproject.repository.RecipeRepository;
import org.example.recipesproject.repository.file.RecipeFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final RecipeFileRepository recipeFileRepository;
    
    private final String UPLOAD_DIR = "uploads/";

    public Recipe findById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Рецепт не знайдено"));
    }

    public Recipe addRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    public Recipe updateRecipe(Long id, Recipe updatedRecipe) {
        Recipe existing = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Рецепт не знайдено"));

        existing.setName(updatedRecipe.getName());
        existing.setDescription(updatedRecipe.getDescription());
        existing.setCookingTime(Math.max(0, updatedRecipe.getCookingTime()));
        existing.setCaloriesAmount(Math.max(0, updatedRecipe.getCaloriesAmount()));
        existing.setServingsAmount(Math.max(0, updatedRecipe.getServingsAmount()));
        existing.setCookingInstruction(updatedRecipe.getCookingInstruction());
        existing.setImgReference(updatedRecipe.getImgReference());
        existing.setDishType(updatedRecipe.getDishType());
        existing.setCuisineType(updatedRecipe.getCuisineType());

        return recipeRepository.save(existing);
    }

    public void deleteRecipe(Long id) {
        Recipe entity = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Рецепт не знайдено"));

        
        recipeRepository.deleteById(id);
    }


    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    public List<Recipe> getFilteredAndSortedRecipes(String searchStr, String sort, String dishType, String cuisineType) {
        Stream<Recipe> stream = recipeRepository.findAll().stream();

        stream = searchByName(stream, searchStr);
        stream = filterByDishType(stream, dishType);
        stream = filterByCuisine(stream, cuisineType);
        stream = sortByParameter(stream, sort);

        return stream.toList();
    }

    private Stream<Recipe> searchByName(Stream<Recipe> stream, String searchStr) {
        if (searchStr == null || searchStr.trim().isEmpty()) return stream;
        String lowerSearch = searchStr.trim().toLowerCase();
        return stream.filter(r -> r.getName() != null && r.getName().toLowerCase().contains(lowerSearch));
    }

    private Stream<Recipe> filterByDishType(Stream<Recipe> stream, String dishType) {
        if (dishType == null || dishType.trim().isEmpty()) return stream;
        return stream.filter(r -> r.getDishType() != null && dishType.equals(r.getDishType().name()));
    }

    private Stream<Recipe> filterByCuisine(Stream<Recipe> stream, String cuisineType) {
        if (cuisineType == null || cuisineType.trim().isEmpty()) return stream;
        return stream.filter(r -> r.getCuisineType() != null && cuisineType.equals(r.getCuisineType().name()));
    }

    private Stream<Recipe> sortByParameter(Stream<Recipe> stream, String sort) {
        if ("name".equals(sort)) return stream.sorted(java.util.Comparator.comparing(Recipe::getName));
        if ("time".equals(sort)) return stream.sorted(java.util.Comparator.comparingInt(Recipe::getCookingTime));
        if ("calories".equals(sort)) return stream.sorted(java.util.Comparator.comparingInt(Recipe::getCaloriesAmount));
        return stream;
    }

    public void saveRecipe(Recipe recipe, MultipartFile imageFile) throws IOException {
        String oldImgRef = null;
        if (recipe.getId() != null) {
            oldImgRef = recipeRepository.findById(recipe.getId())
                    .map(Recipe::getImgReference)
                    .orElse(null);
        }

        String imgRef = recipe.getImgReference();
        if (imageFile != null && !imageFile.isEmpty()) {
            imgRef = saveImage(imageFile);
            recipe.setImgReference(imgRef);

        } else {
            recipe.setImgReference(oldImgRef != null ? oldImgRef : imgRef);
        }

        if (recipe.getId() != null) {
            updateRecipe(recipe.getId(), recipe);
        } else {
            addRecipe(recipe);
        }
    }

    public File exportRecipes() throws Exception {
        List<Recipe> recipes = findAll();
        File file = new File(RecipeFileRepository.DEFAULT_BIN);
        recipeFileRepository.outputList(recipes, file);
        return file;
    }

    public void importRecipes(MultipartFile multipartFile) throws Exception {
        File tempFile = File.createTempFile("uploaded_", ".bin");
        multipartFile.transferTo(tempFile);
        List<Recipe> recipes = recipeFileRepository.readList(tempFile);
        if (recipes != null) {
            recipeRepository.setRecipes(recipes);
        }
        tempFile.delete();
    }

    private String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        Path filePath = Paths.get(UPLOAD_DIR, uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + uniqueFilename;
    }
}
