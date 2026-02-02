package mealmotion.model;

public final class DayMeals {
    private final Meal breakfast;
    private final Meal lunch;
    private final Meal dinner;
    private final Meal snack;

    public DayMeals(Meal breakfast, Meal lunch, Meal dinner, Meal snack) {
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.snack = snack;
    }

    public Meal breakfast() { return breakfast; }
    public Meal lunch() { return lunch; }
    public Meal dinner() { return dinner; }
    public Meal snack() { return snack; }

    public int totalCalories() {
        return breakfast.calories() + lunch.calories() + dinner.calories() + snack.calories();
    }

    public int totalProteinGrams() {
        return breakfast.proteinGrams() + lunch.proteinGrams() + dinner.proteinGrams() + snack.proteinGrams();
    }
}

