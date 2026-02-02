package mealmotion.util;

import mealmotion.model.DayMeals;
import mealmotion.model.Meal;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ShoppingListUtil {
    private ShoppingListUtil() {}

    public static Map<String, Integer> buildShoppingList(Map<String, DayMeals> mealsByDay) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (DayMeals dm : mealsByDay.values()) {
            addMeal(counts, dm.breakfast());
            addMeal(counts, dm.lunch());
            addMeal(counts, dm.dinner());
            addMeal(counts, dm.snack());
        }
        return counts;
    }

    private static void addMeal(Map<String, Integer> counts, Meal meal) {
        for (String ing : meal.ingredients()) {
            String key = ing.trim().toLowerCase();
            if (key.isEmpty()) continue;
            counts.put(key, counts.getOrDefault(key, 0) + 1);
        }
    }
}

