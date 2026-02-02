package mealmotion.model;

import java.util.Collections;
import java.util.List;

public final class Meal {
    private final String name;
    private final int calories;
    private final int proteinGrams;
    private final boolean halal;
    private final boolean vegetarian;
    private final boolean vegan;
    private final boolean glutenFree;
    private final List<String> ingredients; // lowercased tokens for shopping list + filtering

    public Meal(
            String name,
            int calories,
            int proteinGrams,
            boolean halal,
            boolean vegetarian,
            boolean vegan,
            boolean glutenFree,
            List<String> ingredients
    ) {
        this.name = name;
        this.calories = calories;
        this.proteinGrams = proteinGrams;
        this.halal = halal;
        this.vegetarian = vegetarian;
        this.vegan = vegan;
        this.glutenFree = glutenFree;
        this.ingredients = ingredients == null ? Collections.emptyList() : List.copyOf(ingredients);
    }

    public String name() { return name; }
    public int calories() { return calories; }
    public int proteinGrams() { return proteinGrams; }
    public List<String> ingredients() { return ingredients; }

    public boolean matchesDiet(DietPreference pref) {
        if (pref == null || pref == DietPreference.NONE) return true;
        return switch (pref) {
            case HALAL -> halal;
            case VEGETARIAN -> vegetarian;
            case VEGAN -> vegan;
            case GLUTEN_FREE -> glutenFree;
            case NONE -> true;
        };
    }
}

