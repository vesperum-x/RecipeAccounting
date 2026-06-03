package org.example.recipesproject.repository.file;
import org.example.recipesproject.dto.Recipe;
import java.io.File;
import java.util.List;

public interface RecipeFileRepository{
    String DEFAULT_BIN = "recipes.bin";
    void outputList(List<Recipe> recipes, File file);
    void outputList(List<Recipe> recipes, String fileName);
    List<Recipe> readList(File file);
    List<Recipe> readList(String fileName);
}