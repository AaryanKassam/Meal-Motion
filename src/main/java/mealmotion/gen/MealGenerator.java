package mealmotion.gen;

import mealmotion.model.*;

import java.util.*;

public final class MealGenerator {
    private MealGenerator() {}

    private static final List<Meal> CATALOG = List.of(
            // Breakfast
            meal("Oatmeal + Berries", 340, 12, true, true, true, true, "oats", "berries", "cinnamon", "chia"),
            meal("Greek Yogurt Parfait", 360, 24, true, true, false, true, "greek yogurt", "honey", "berries", "granola"),
            meal("Avocado Toast", 380, 12, true, true, true, false, "bread", "avocado", "tomato", "olive oil"),
            meal("Eggs + Spinach", 330, 22, true, true, false, true, "eggs", "spinach", "olive oil"),
            meal("Vegan Protein Smoothie", 420, 28, true, true, true, true, "banana", "spinach", "pea protein", "almond milk", "peanut butter"),

            // Lunch
            meal("Chicken Salad Bowl", 520, 42, true, false, false, true, "chicken", "lettuce", "tomato", "cucumber", "olive oil"),
            meal("Halal Chicken Wrap", 560, 38, true, false, false, false, "halal chicken", "tortilla", "lettuce", "yogurt sauce"),
            meal("Quinoa Chickpea Bowl", 540, 22, true, true, true, true, "quinoa", "chickpeas", "spinach", "lemon"),
            meal("Tuna + Rice Bowl", 590, 40, true, false, false, true, "tuna", "rice", "cucumber", "soy sauce"),
            meal("Veggie Hummus Wrap", 480, 18, true, true, true, false, "tortilla", "hummus", "carrot", "cucumber"),

            // Dinner
            meal("Salmon + Veggies", 620, 42, true, false, false, true, "salmon", "broccoli", "lemon", "olive oil"),
            meal("Beef Stir Fry", 680, 45, true, false, false, true, "beef", "pepper", "onion", "rice", "soy sauce"),
            meal("Tofu Stir Fry", 560, 28, true, true, true, true, "tofu", "broccoli", "rice", "soy sauce"),
            meal("Vegan Lentil Curry", 610, 26, true, true, true, true, "lentils", "tomato", "coconut milk", "spices", "rice"),
            meal("Gluten-Free Turkey Chili", 630, 44, true, false, false, true, "turkey", "beans", "tomato", "spices"),

            // Snacks
            meal("Apple + Peanut Butter", 240, 7, true, true, true, true, "apple", "peanut butter"),
            meal("Carrots + Hummus", 200, 7, true, true, true, true, "carrot", "hummus"),
            meal("Trail Mix", 260, 8, true, true, true, true, "nuts", "raisins"),
            meal("Protein Bar", 220, 20, true, true, false, false, "protein bar"),
            meal("Cottage Cheese + Fruit", 240, 18, true, true, false, true, "cottage cheese", "berries")
    );

    private static Meal meal(String name, int calories, int protein, boolean halal, boolean vegetarian, boolean vegan, boolean glutenFree, String... ingredients) {
        List<String> ing = new ArrayList<>();
        for (String s : ingredients) ing.add(s.toLowerCase());
        return new Meal(name, calories, protein, halal, vegetarian, vegan, glutenFree, ing);
    }

    public static Map<String, DayMeals> generateWeeklyMeals(UserProfile profile) {
        Objects.requireNonNull(profile, "profile");
        String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        Random r = new Random();

        int target = profile.targetCalories();
        // Simple day split
        int breakfastTarget = (int) Math.round(target * 0.25);
        int lunchTarget = (int) Math.round(target * 0.30);
        int dinnerTarget = (int) Math.round(target * 0.30);
        int snackTarget = target - breakfastTarget - lunchTarget - dinnerTarget;

        Map<String, DayMeals> plan = new LinkedHashMap<>();
        Deque<String> recent = new ArrayDeque<>(); // avoid repeats over last N selections

        for (String day : days) {
            Meal breakfast = selectMeal(breakfastTarget, profile, r, recent, Set.of("Breakfast"));
            Meal lunch = selectMeal(lunchTarget, profile, r, recent, Set.of("Lunch"));
            Meal dinner = selectMeal(dinnerTarget, profile, r, recent, Set.of("Dinner"));
            Meal snack = selectMeal(snackTarget, profile, r, recent, Set.of("Snack"));

            plan.put(day, new DayMeals(breakfast, lunch, dinner, snack));
        }

        return plan;
    }

    private static Meal selectMeal(int targetCalories, UserProfile profile, Random r, Deque<String> recent, Set<String> slotTags) {
        List<Meal> candidates = new ArrayList<>();
        for (Meal m : CATALOG) {
            if (!m.matchesDiet(profile.dietPreference())) continue;
            if (blockedByPreferences(m, profile)) continue;
            candidates.add(m);
        }
        if (candidates.isEmpty()) {
            // Fallback: ignore diet if we filtered too hard
            candidates.addAll(CATALOG);
        }

        // Score and pick among top 5 for variety.
        candidates.sort(Comparator.comparingDouble(m -> score(m, targetCalories, profile, recent)));
        int pickPool = Math.min(5, candidates.size());
        Meal chosen = candidates.get(r.nextInt(pickPool));

        // Track recently used meals (name-based)
        recent.addLast(chosen.name());
        while (recent.size() > 8) recent.removeFirst();

        return chosen;
    }

    private static boolean blockedByPreferences(Meal meal, UserProfile profile) {
        String name = meal.name().toLowerCase();
        for (String bad : profile.dislikedFoods()) {
            if (bad.isEmpty()) continue;
            if (name.contains(bad)) return true;
            for (String ing : meal.ingredients()) if (ing.contains(bad)) return true;
        }
        for (String allergy : profile.allergies()) {
            if (allergy.isEmpty()) continue;
            if (name.contains(allergy)) return true;
            for (String ing : meal.ingredients()) if (ing.contains(allergy)) return true;
        }
        return false;
    }

    private static double score(Meal meal, int targetCalories, UserProfile profile, Deque<String> recent) {
        double caloriePenalty = Math.abs(meal.calories() - targetCalories);
        // nudge protein up a bit, especially for bulk/lean
        double proteinBonus = meal.proteinGrams() * (profile.bodyGoal() == BodyGoal.BULK ? 2.0 : 1.2);

        double repeatPenalty = recent.contains(meal.name()) ? 250.0 : 0.0;

        return caloriePenalty - proteinBonus + repeatPenalty;
    }
}

