package org.example.recipesproject.repository.file;

import org.example.recipesproject.dto.Recipe;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.List;

@Repository
public class RecipeRepositoryBinaryImpl implements RecipeFileRepository {

    @Override
    public void outputList(List<Recipe> recipes, File file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(recipes);
        } catch (IOException e) {
            System.err.println("Помилка запису у бінарний файл: " + e.getMessage());
        }
    }

    @Override
    public void outputList(List<Recipe> recipes, String fileName) {
        outputList(recipes, new File(fileName));
    }

    @Override
    public List<Recipe> readList(File file) {
        List<Recipe> recipes = null;
        if (!file.exists() || file.length() == 0) {
            return new java.util.ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            recipes = (List<Recipe>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Помилка читання з бінарного файлу: " + e.getMessage());
        }
        return recipes;
    }

    @Override
    public List<Recipe> readList(String fileName) {
        return readList(new File(fileName));
    }
}
