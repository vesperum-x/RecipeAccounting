package org.example.recipesproject;

import org.example.recipesproject.dto.Cuisine;
import org.example.recipesproject.dto.DishType;
import org.example.recipesproject.dto.Recipe;
import org.example.recipesproject.repository.RecipeRepository;
import org.example.recipesproject.repository.file.RecipeFileRepository;
import org.example.recipesproject.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeFileRepository recipeFileRepository;

    @InjectMocks
    private RecipeService recipeService;

    private Recipe validRecipe;
    private Recipe secondRecipe;

    @BeforeEach
    void setUp() {
        validRecipe = new Recipe(1L, "Борщ", "Смачний суп", 120, 300, 4, "Варити довго", null, DishType.FIRST_COURSE, Cuisine.UKRAINIAN);
        secondRecipe = new Recipe(2L, "Піца", "Італійська страва", 40, 500, 2, "Запікати в печі", null, DishType.SECOND_COURSE, Cuisine.ITALIAN);
    }

    @Test
    void testFindAll() {
        List<Recipe> mockList = Arrays.asList(validRecipe, secondRecipe);
        when(recipeRepository.findAll()).thenReturn(mockList);

        List<Recipe> result = recipeService.findAll();

        assertEquals(2, result.size());
        verify(recipeRepository, times(1)).findAll();
    }

    @ParameterizedTest
    @CsvSource({
            "1, true",
            "99, false"
    })
    void testFindById(Long id, boolean shouldSucceed) {
        if (shouldSucceed) {
            when(recipeRepository.findById(id)).thenReturn(Optional.of(validRecipe));
            Recipe result = recipeService.findById(id);
            assertNotNull(result);
            assertEquals("Борщ", result.getName());
        } else {
            when(recipeRepository.findById(id)).thenReturn(Optional.empty());
            Exception exception = assertThrows(RuntimeException.class, () -> recipeService.findById(id));
            assertEquals("Рецепт не знайдено", exception.getMessage());
        }
    }

    @Test
    void testAddRecipe() {
        when(recipeRepository.save(any(Recipe.class))).thenReturn(validRecipe);

        Recipe result = recipeService.addRecipe(validRecipe);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(recipeRepository, times(1)).save(validRecipe);
    }

    @Test
    void testUpdateRecipe() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(validRecipe));
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(i -> i.getArguments()[0]);

        Recipe updatedInfo = new Recipe(1L, "Зелений Борщ", "Дуже смачний суп", 150, 350, 5, "Варити ще довше", null, DishType.FIRST_COURSE, Cuisine.UKRAINIAN);
        
        Recipe result = recipeService.updateRecipe(1L, updatedInfo);

        assertEquals("Зелений Борщ", result.getName());
        assertEquals(150, result.getCookingTime());
    }

    @Test
    void testDeleteRecipe() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(validRecipe));
        doNothing().when(recipeRepository).deleteById(1L);

        recipeService.deleteRecipe(1L);

        verify(recipeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetFilteredAndSortedRecipes() {
        List<Recipe> mockList = Arrays.asList(validRecipe, secondRecipe);
        when(recipeRepository.findAll()).thenReturn(mockList);

        List<Recipe> resultSearch = recipeService.getFilteredAndSortedRecipes("піца", null, null, null);
        assertEquals(1, resultSearch.size());
        assertEquals("Піца", resultSearch.get(0).getName());

        List<Recipe> resultCuisine = recipeService.getFilteredAndSortedRecipes(null, null, null, "UKRAINIAN");
        assertEquals(1, resultCuisine.size());
        assertEquals("Борщ", resultCuisine.get(0).getName());

        List<Recipe> resultSorted = recipeService.getFilteredAndSortedRecipes(null, "time", null, null);
        assertEquals(2, resultSorted.size());
        assertEquals("Піца", resultSorted.get(0).getName()); // Піца готується швидше (40 хв < 120 хв)
    }

    @Test
    void testExportRecipes() throws Exception {
        when(recipeRepository.findAll()).thenReturn(Arrays.asList(validRecipe));
        doNothing().when(recipeFileRepository).outputList(anyList(), any(File.class));

        File exportedFile = recipeService.exportRecipes();

        assertNotNull(exportedFile);
        assertEquals(RecipeFileRepository.DEFAULT_BIN, exportedFile.getName());
    }

    @Test
    void testImportRecipes() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.bin", "application/octet-stream", "dummy data".getBytes());
        List<Recipe> importedList = Arrays.asList(validRecipe);
        
        when(recipeFileRepository.readList(any(File.class))).thenReturn(importedList);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(validRecipe);

        recipeService.importRecipes(mockFile);

        verify(recipeFileRepository, times(1)).readList(any(File.class));
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }
}