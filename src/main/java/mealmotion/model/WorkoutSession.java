package mealmotion.model;

import java.util.List;

public final class WorkoutSession {
    private final String title;
    private final List<WorkoutMove> moves;

    public WorkoutSession(String title, List<WorkoutMove> moves) {
        this.title = title;
        this.moves = List.copyOf(moves);
    }

    public String title() { return title; }
    public List<WorkoutMove> moves() { return moves; }

    public String formatForCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append(": ");
        for (int i = 0; i < moves.size(); i++) {
            if (i > 0) sb.append(" | ");
            sb.append(moves.get(i).format());
        }
        return sb.toString();
    }
}

