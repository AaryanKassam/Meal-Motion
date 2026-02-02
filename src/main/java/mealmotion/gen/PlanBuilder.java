package mealmotion.gen;

import mealmotion.model.*;

import java.util.Map;

public final class PlanBuilder {
    private PlanBuilder() {}

    public static WeeklyPlan build(UserProfile profile) {
        WeeklyPlan plan = new WeeklyPlan(profile);
        Map<String, DayMeals> meals = MealGenerator.generateWeeklyMeals(profile);
        Map<String, WorkoutSession> workouts = WorkoutGenerator.generateWeeklyWorkouts(profile);

        for (Map.Entry<String, DayMeals> e : meals.entrySet()) {
            plan.putMeals(e.getKey(), e.getValue());
        }
        for (Map.Entry<String, WorkoutSession> e : workouts.entrySet()) {
            plan.putWorkout(e.getKey(), e.getValue());
        }
        return plan;
    }
}

