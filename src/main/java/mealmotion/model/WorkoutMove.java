package mealmotion.model;

public final class WorkoutMove {
    private final String name;
    private final WorkoutMoveType type;
    private final Integer sets;
    private final Integer reps;
    private final Integer minutes;
    private final Integer seconds;

    private WorkoutMove(String name, WorkoutMoveType type, Integer sets, Integer reps, Integer minutes, Integer seconds) {
        this.name = name;
        this.type = type;
        this.sets = sets;
        this.reps = reps;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public static WorkoutMove strength(String name, int sets, int reps) {
        return new WorkoutMove(name, WorkoutMoveType.STRENGTH, sets, reps, null, null);
    }

    public static WorkoutMove cardio(String name, int minutes) {
        return new WorkoutMove(name, WorkoutMoveType.CARDIO, null, null, minutes, null);
    }

    public static WorkoutMove coreSeconds(String name, int seconds) {
        return new WorkoutMove(name, WorkoutMoveType.CORE, null, null, null, seconds);
    }

    public static WorkoutMove mobilityMinutes(String name, int minutes) {
        return new WorkoutMove(name, WorkoutMoveType.MOBILITY, null, null, minutes, null);
    }

    public String name() { return name; }
    public WorkoutMoveType type() { return type; }

    public String format() {
        return switch (type) {
            case CARDIO -> name + ": " + minutes + " min";
            case MOBILITY -> name + ": " + minutes + " min";
            case CORE -> name + ": " + seconds + " sec";
            case STRENGTH -> name + ": " + sets + "x" + reps;
        };
    }
}

