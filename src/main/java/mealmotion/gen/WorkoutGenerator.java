package mealmotion.gen;

import mealmotion.model.*;

import java.util.*;

public final class WorkoutGenerator {
    private WorkoutGenerator() {}

    public static Map<String, WorkoutSession> generateWeeklyWorkouts(UserProfile profile) {
        Objects.requireNonNull(profile, "profile");
        String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};

        Map<String, WorkoutSession> out = new LinkedHashMap<>();
        if (!profile.includeWorkouts()) return out;

        // Decide which days are training days (spread evenly).
        Set<Integer> trainingDayIdx = pickTrainingDays(profile.workoutDaysPerWeek());

        for (int i = 0; i < days.length; i++) {
            if (!trainingDayIdx.contains(i)) {
                out.put(days[i], new WorkoutSession("Rest / Recovery", List.of(
                        WorkoutMove.mobilityMinutes("Easy walk + stretching", 20)
                )));
                continue;
            }

            WorkoutSession session = buildSessionForDay(i, profile);
            out.put(days[i], session);
        }

        return out;
    }

    private static Set<Integer> pickTrainingDays(int daysPerWeek) {
        int d = Math.max(1, Math.min(7, daysPerWeek));
        // Simple even spread patterns
        return switch (d) {
            case 1 -> Set.of(2);
            case 2 -> Set.of(1, 4);
            case 3 -> Set.of(0, 2, 4);
            case 4 -> Set.of(0, 2, 3, 5);
            case 5 -> Set.of(0, 1, 3, 4, 5);
            case 6 -> Set.of(0, 1, 2, 3, 4, 5);
            default -> Set.of(0, 1, 2, 3, 4, 5, 6);
        };
    }

    private static WorkoutSession buildSessionForDay(int dayIndex, UserProfile profile) {
        // Use a realistic split by goal
        BodyGoal goal = profile.bodyGoal();
        int minutes = Math.max(20, Math.min(120, profile.workoutMinutesPerSession()));

        return switch (goal) {
            case BULK -> buildBulkSplit(dayIndex, profile, minutes);
            case LEAN -> buildLeanSplit(dayIndex, profile, minutes);
            case TONE -> buildToneSplit(dayIndex, profile, minutes);
        };
    }

    private static WorkoutSession buildLeanSplit(int dayIndex, UserProfile profile, int minutes) {
        // Alternating: strength / cardio / strength / cardio / full body ...
        int mod = dayIndex % 5;
        if (mod == 1 || mod == 3) {
            return cardioSession("Cardio + Core", profile, minutes);
        }
        if (mod == 4) {
            return fullBodySession("Full Body (Metabolic)", profile, minutes, 3, 12);
        }
        // Strength days: upper/lower
        return (dayIndex % 2 == 0)
                ? upperSession("Upper Body Strength", profile, minutes, 3, 10)
                : lowerSession("Lower Body Strength", profile, minutes, 3, 12);
    }

    private static WorkoutSession buildBulkSplit(int dayIndex, UserProfile profile, int minutes) {
        // Push / Pull / Legs / Upper / Lower rotation
        int mod = dayIndex % 5;
        return switch (mod) {
            case 0 -> pushSession("Push (Chest/Shoulders/Triceps)", profile, minutes, 4, 8);
            case 1 -> pullSession("Pull (Back/Biceps)", profile, minutes, 4, 8);
            case 2 -> legsSession("Legs (Strength)", profile, minutes, 4, 8);
            case 3 -> upperSession("Upper (Hypertrophy)", profile, minutes, 4, 10);
            default -> lowerSession("Lower (Hypertrophy)", profile, minutes, 4, 10);
        };
    }

    private static WorkoutSession buildToneSplit(int dayIndex, UserProfile profile, int minutes) {
        // Strength + conditioning mix
        int mod = dayIndex % 4;
        return switch (mod) {
            case 0 -> fullBodySession("Full Body (Tone)", profile, minutes, 3, 12);
            case 1 -> cardioSession("Cardio + Core", profile, minutes);
            case 2 -> upperSession("Upper (Tone)", profile, minutes, 3, 12);
            default -> lowerSession("Lower (Tone)", profile, minutes, 3, 12);
        };
    }

    private static WorkoutSession cardioSession(String title, UserProfile profile, int minutes) {
        int cardioMin = Math.max(15, Math.min(40, minutes - 10));
        int coreSec = 45;
        List<WorkoutMove> moves = new ArrayList<>();
        moves.add(WorkoutMove.cardio(pickCardio(profile.equipment()), cardioMin));
        moves.add(WorkoutMove.coreSeconds("Plank", coreSec));
        moves.add(WorkoutMove.coreSeconds("Dead bug", 40));
        moves.add(WorkoutMove.mobilityMinutes("Stretching", 8));
        return new WorkoutSession(title, moves);
    }

    private static WorkoutSession fullBodySession(String title, UserProfile profile, int minutes, int sets, int reps) {
        List<WorkoutMove> moves = new ArrayList<>();
        moves.add(WorkoutMove.mobilityMinutes("Warm-up", 5));
        for (String ex : pickFullBody(profile.equipment())) {
            moves.add(WorkoutMove.strength(ex, sets, reps));
            if (moves.size() >= 7) break;
        }
        // finish with a short cardio burst if time allows
        if (minutes >= 45) moves.add(WorkoutMove.cardio("Brisk walk", 10));
        return new WorkoutSession(title, moves);
    }

    private static WorkoutSession upperSession(String title, UserProfile profile, int minutes, int sets, int reps) {
        List<WorkoutMove> moves = new ArrayList<>();
        moves.add(WorkoutMove.mobilityMinutes("Warm-up", 5));
        for (String ex : pickUpper(profile.equipment())) {
            moves.add(WorkoutMove.strength(ex, sets, reps));
            if (moves.size() >= 8) break;
        }
        if (minutes >= 50) moves.add(WorkoutMove.coreSeconds("Side plank", 40));
        return new WorkoutSession(title, moves);
    }

    private static WorkoutSession lowerSession(String title, UserProfile profile, int minutes, int sets, int reps) {
        List<WorkoutMove> moves = new ArrayList<>();
        moves.add(WorkoutMove.mobilityMinutes("Warm-up", 5));
        for (String ex : pickLower(profile.equipment())) {
            moves.add(WorkoutMove.strength(ex, sets, reps));
            if (moves.size() >= 8) break;
        }
        if (minutes >= 45) moves.add(WorkoutMove.coreSeconds("Hollow hold", 30));
        return new WorkoutSession(title, moves);
    }

    private static WorkoutSession pushSession(String title, UserProfile profile, int minutes, int sets, int reps) {
        List<WorkoutMove> moves = new ArrayList<>();
        moves.add(WorkoutMove.mobilityMinutes("Warm-up", 6));
        for (String ex : pickPush(profile.equipment())) {
            moves.add(WorkoutMove.strength(ex, sets, reps));
            if (moves.size() >= 8) break;
        }
        if (minutes >= 60) moves.add(WorkoutMove.cardio("Incline walk", 10));
        return new WorkoutSession(title, moves);
    }

    private static WorkoutSession pullSession(String title, UserProfile profile, int minutes, int sets, int reps) {
        List<WorkoutMove> moves = new ArrayList<>();
        moves.add(WorkoutMove.mobilityMinutes("Warm-up", 6));
        for (String ex : pickPull(profile.equipment())) {
            moves.add(WorkoutMove.strength(ex, sets, reps));
            if (moves.size() >= 8) break;
        }
        if (minutes >= 60) moves.add(WorkoutMove.coreSeconds("Plank", 45));
        return new WorkoutSession(title, moves);
    }

    private static WorkoutSession legsSession(String title, UserProfile profile, int minutes, int sets, int reps) {
        List<WorkoutMove> moves = new ArrayList<>();
        moves.add(WorkoutMove.mobilityMinutes("Warm-up", 6));
        for (String ex : pickLower(profile.equipment())) {
            moves.add(WorkoutMove.strength(ex, sets, reps));
            if (moves.size() >= 8) break;
        }
        if (minutes >= 55) moves.add(WorkoutMove.mobilityMinutes("Stretching", 8));
        return new WorkoutSession(title, moves);
    }

    private static List<String> pickUpper(Equipment eq) {
        return switch (eq) {
            case NONE -> shuffled("Push-ups", "Pike push-ups", "Inverted rows (under table)", "Chair dips", "Superman holds");
            case DUMBBELLS -> shuffled("Dumbbell bench press", "One-arm dumbbell row", "Dumbbell shoulder press", "Dumbbell curls", "Triceps extensions");
            case FULL_GYM -> shuffled("Bench press", "Lat pulldown", "Seated row", "Incline dumbbell press", "Cable fly", "Triceps dips", "Biceps curls");
        };
    }

    private static List<String> pickLower(Equipment eq) {
        return switch (eq) {
            case NONE -> shuffled("Bodyweight squats", "Lunges", "Glute bridge", "Step-ups", "Calf raises");
            case DUMBBELLS -> shuffled("Goblet squat", "Romanian deadlift (DB)", "Walking lunges", "Hip thrust", "Calf raises");
            case FULL_GYM -> shuffled("Back squat", "Deadlift", "Leg press", "Hamstring curl", "Leg extension", "Hip thrust");
        };
    }

    private static List<String> pickFullBody(Equipment eq) {
        return switch (eq) {
            case NONE -> shuffled("Push-ups", "Bodyweight squats", "Lunges", "Burpees", "Mountain climbers", "Glute bridge");
            case DUMBBELLS -> shuffled("Goblet squat", "Dumbbell row", "Dumbbell press", "Romanian deadlift (DB)", "Dumbbell thrusters", "Farmer carry");
            case FULL_GYM -> shuffled("Squat", "Bench press", "Row", "Deadlift", "Overhead press", "Lat pulldown");
        };
    }

    private static List<String> pickPush(Equipment eq) {
        return switch (eq) {
            case NONE -> shuffled("Push-ups", "Pike push-ups", "Chair dips", "Diamond push-ups");
            case DUMBBELLS -> shuffled("Dumbbell bench press", "Dumbbell shoulder press", "Dumbbell fly", "Triceps extensions");
            case FULL_GYM -> shuffled("Bench press", "Incline bench press", "Overhead press", "Cable fly", "Triceps pushdown");
        };
    }

    private static List<String> pickPull(Equipment eq) {
        return switch (eq) {
            case NONE -> shuffled("Inverted rows (under table)", "Towel rows", "Superman holds", "Biceps isometrics");
            case DUMBBELLS -> shuffled("One-arm dumbbell row", "Dumbbell curls", "Rear delt raises", "Hammer curls");
            case FULL_GYM -> shuffled("Lat pulldown", "Seated row", "Barbell row", "Face pulls", "Biceps curls");
        };
    }

    private static String pickCardio(Equipment eq) {
        return switch (eq) {
            case FULL_GYM -> "Treadmill / Bike";
            default -> "Brisk walk / Jog";
        };
    }

    private static List<String> shuffled(String... items) {
        List<String> list = new ArrayList<>(List.of(items));
        Collections.shuffle(list, new Random());
        return list;
    }
}

