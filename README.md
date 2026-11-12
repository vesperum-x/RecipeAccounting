# Recipe Accounting

A web application for managing a collection of cooking recipes, built with Spring Boot and Thymeleaf.
University project, 2nd year (spring 2026). The interface is in Ukrainian.

## Features

- Create, view, edit and delete recipes
- Upload an image for each recipe
- Search recipes by name
- Filter by dish type (first course, second course, salad, dessert) and cuisine (Ukrainian, Italian, French, Asian)
- Sort by name, cooking time or calories
- Export all recipes to a binary file and import them back

Each recipe stores its name, description, cooking time, calories, number of servings, cooking instructions, image, dish type and cuisine.

## Tech stack

- Java 25
- Spring Boot 4 (Spring Web MVC)
- Thymeleaf, HTML, CSS
- Lombok
- Maven
- JUnit 5

## Architecture

The code follows a layered structure:

```
src/main/java/org/example/recipesproject/
├── controller/   RecipeController: HTTP endpoints, renders Thymeleaf pages
├── service/      RecipeService: business logic, search, filters, sorting, image upload
├── repository/   RecipeRepository: in-memory storage
│   └── file/     RecipeFileRepository: export and import via Java serialization
└── dto/          Recipe model and the DishType, Cuisine enums
```

Recipes are kept in memory while the app runs. To save them between runs, use export and import.

## Running locally

Requirements: JDK 25 and Maven.

```bash
mvn spring-boot:run
```

Then open http://localhost:8080/recipes

## Tests

```bash
mvn test
```

`RecipeServiceTest` covers finding, adding, updating and deleting recipes.
