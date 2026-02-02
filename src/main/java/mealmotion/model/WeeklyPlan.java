package mealmotion.model;

import java.util.LinkedHashMap;
import java.util.Map;

public final class WeeklyPlan {
    private final UserProfile profile;
    private final Map<String, DayMeals> mealsByDay;
    private final Map<String, WorkoutSession> workoutsByDay;

    public WeeklyPlan(UserProfile profile) {
        this.profile = profile;
        this.mealsByDay = new LinkedHashMap<>();
        this.workoutsByDay = new LinkedHashMap<>();
    }

    public UserProfile profile() { return profile; }
    public Map<String, DayMeals> mealsByDay() { return mealsByDay; }
    public Map<String, WorkoutSession> workoutsByDay() { return workoutsByDay; }

    public void putMeals(String day, DayMeals meals) { mealsByDay.put(day, meals); }
    public void putWorkout(String day, WorkoutSession workout) { workoutsByDay.put(day, workout); }
}

