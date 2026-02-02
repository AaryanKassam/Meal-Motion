package mealmotion.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UserProfile {
    private final String name;
    private final int age;
    private final int heightCm;
    private final int weightKg;
    private final int targetWeightKg;

    private final Gender gender;
    private final ActivityLevel activityLevel;
    private final BodyGoal bodyGoal;
    private final DietPreference dietPreference;

    private final boolean includeWorkouts;
    private final Equipment equipment;
    private final int workoutDaysPerWeek;
    private final int workoutMinutesPerSession;

    private final List<String> allergies;
    private final List<String> dislikedFoods;

    public UserProfile(
            String name,
            int age,
            int heightCm,
            int weightKg,
            int targetWeightKg,
            Gender gender,
            ActivityLevel activityLevel,
            BodyGoal bodyGoal,
            DietPreference dietPreference,
            boolean includeWorkouts,
            Equipment equipment,
            int workoutDaysPerWeek,
            int workoutMinutesPerSession,
            List<String> allergies,
            List<String> dislikedFoods
    ) {
        this.name = name;
        this.age = age;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.targetWeightKg = targetWeightKg;
        this.gender = gender;
        this.activityLevel = activityLevel;
        this.bodyGoal = bodyGoal;
        this.dietPreference = dietPreference;
        this.includeWorkouts = includeWorkouts;
        this.equipment = equipment;
        this.workoutDaysPerWeek = workoutDaysPerWeek;
        this.workoutMinutesPerSession = workoutMinutesPerSession;
        this.allergies = normalizeList(allergies);
        this.dislikedFoods = normalizeList(dislikedFoods);
    }

    private static List<String> normalizeList(List<String> items) {
        if (items == null || items.isEmpty()) return Collections.emptyList();
        List<String> out = new ArrayList<>();
        for (String s : items) {
            if (s == null) continue;
            String t = s.trim();
            if (!t.isEmpty()) out.add(t.toLowerCase());
        }
        return Collections.unmodifiableList(out);
    }

    public String name() { return name; }
    public int age() { return age; }
    public int heightCm() { return heightCm; }
    public int weightKg() { return weightKg; }
    public int targetWeightKg() { return targetWeightKg; }
    public Gender gender() { return gender; }
    public ActivityLevel activityLevel() { return activityLevel; }
    public BodyGoal bodyGoal() { return bodyGoal; }
    public DietPreference dietPreference() { return dietPreference; }
    public boolean includeWorkouts() { return includeWorkouts; }
    public Equipment equipment() { return equipment; }
    public int workoutDaysPerWeek() { return workoutDaysPerWeek; }
    public int workoutMinutesPerSession() { return workoutMinutesPerSession; }
    public List<String> allergies() { return allergies; }
    public List<String> dislikedFoods() { return dislikedFoods; }

    /** Mifflin-St Jeor BMR in kcal/day. */
    public int bmrCalories() {
        double bmr = (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age) + gender.mifflinStJeorConstant();
        return (int) Math.round(bmr);
    }

    /** Total daily energy expenditure (TDEE) in kcal/day based on activity level. */
    public int tdeeCalories() {
        return (int) Math.round(bmrCalories() * activityLevel.factor());
    }

    /** Goal-adjusted daily calories in kcal/day (simple, safe offset). */
    public int targetCalories() {
        int tdee = tdeeCalories();
        int delta = targetWeightKg - weightKg;
        int adjustment;
        if (delta >= 5) adjustment = 300;          // gentle surplus
        else if (delta <= -5) adjustment = -400;   // gentle deficit
        else if (delta > 0) adjustment = 150;
        else if (delta < 0) adjustment = -200;
        else adjustment = 0;
        return Math.max(1200, tdee + adjustment);
    }

    /** Simple protein target in grams/day. */
    public int proteinTargetGrams() {
        double gramsPerKg;
        switch (bodyGoal) {
            case BULK -> gramsPerKg = 1.8;
            case LEAN -> gramsPerKg = 1.7;
            default -> gramsPerKg = 1.6;
        }
        return (int) Math.round(weightKg * gramsPerKg);
    }
}

