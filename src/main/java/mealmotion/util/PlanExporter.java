package mealmotion.util;

import mealmotion.model.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public final class PlanExporter {
    private PlanExporter() {}

    public static void exportWithChooser(Component parent, WeeklyPlan plan) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save MealMotion Plan CSV");
        chooser.setSelectedFile(new File("MealMotionPlan.csv"));
        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File csvFile = chooser.getSelectedFile();
        if (!csvFile.getName().toLowerCase().endsWith(".csv")) {
            csvFile = new File(csvFile.getParentFile(), csvFile.getName() + ".csv");
        }

        try {
            writePlanCsv(csvFile, plan);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent, "Could not write CSV:\n" + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Offer shopping list export
        File listFileSaved = null;
        int choice = JOptionPane.showConfirmDialog(parent, "CSV saved.\nAlso save a shopping list?", "Shopping List", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            JFileChooser chooser2 = new JFileChooser();
            chooser2.setDialogTitle("Save Shopping List");
            chooser2.setSelectedFile(new File("MealMotionShoppingList.txt"));
            int res2 = chooser2.showSaveDialog(parent);
            if (res2 == JFileChooser.APPROVE_OPTION) {
                File listFile = chooser2.getSelectedFile();
                try {
                    writeShoppingList(listFile, plan);
                    listFileSaved = listFile;
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(parent, "Could not write shopping list:\n" + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        openAllIfSupported(parent, csvFile, listFileSaved);
    }

    public static void writePlanCsv(File file, WeeklyPlan plan) throws IOException {
        try (PrintWriter pw = new PrintWriter(file)) {
            UserProfile p = plan.profile();
            pw.println("Day,Breakfast,BreakfastCalories,BreakfastProtein,Lunch,LunchCalories,LunchProtein,Dinner,DinnerCalories,DinnerProtein,Snack,SnackCalories,SnackProtein,DailyMealCalories,DailyMealProtein,TargetCalories,TargetProtein,Workout");

            for (Map.Entry<String, DayMeals> e : plan.mealsByDay().entrySet()) {
                String day = e.getKey();
                DayMeals dm = e.getValue();
                WorkoutSession ws = plan.workoutsByDay().get(day);

                pw.println(String.join(",",
                        CsvUtil.escape(day),
                        CsvUtil.escape(dm.breakfast().name()), String.valueOf(dm.breakfast().calories()), String.valueOf(dm.breakfast().proteinGrams()),
                        CsvUtil.escape(dm.lunch().name()), String.valueOf(dm.lunch().calories()), String.valueOf(dm.lunch().proteinGrams()),
                        CsvUtil.escape(dm.dinner().name()), String.valueOf(dm.dinner().calories()), String.valueOf(dm.dinner().proteinGrams()),
                        CsvUtil.escape(dm.snack().name()), String.valueOf(dm.snack().calories()), String.valueOf(dm.snack().proteinGrams()),
                        String.valueOf(dm.totalCalories()),
                        String.valueOf(dm.totalProteinGrams()),
                        String.valueOf(p.targetCalories()),
                        String.valueOf(p.proteinTargetGrams()),
                        CsvUtil.escape(ws == null ? "" : ws.formatForCsv())
                ));
            }
        }
    }

    public static void writeShoppingList(File file, WeeklyPlan plan) throws IOException {
        Map<String, Integer> list = ShoppingListUtil.buildShoppingList(plan.mealsByDay());
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println("MealMotion Shopping List (ingredient -> times used)");
            pw.println();
            for (Map.Entry<String, Integer> e : list.entrySet()) {
                pw.println("- " + e.getKey() + " x" + e.getValue());
            }
        }
    }

    private static void openAllIfSupported(Component parent, File csvFile, File maybeListFile) {
        if (csvFile == null) return;
        try {
            if (!Desktop.isDesktopSupported()) {
                showSavedPaths(parent, csvFile, maybeListFile);
                return;
            }
            Desktop d = Desktop.getDesktop();
            if (!d.isSupported(Desktop.Action.OPEN)) {
                showSavedPaths(parent, csvFile, maybeListFile);
                return;
            }

            d.open(csvFile);
            if (maybeListFile != null) d.open(maybeListFile);
        } catch (Exception ex) {
            showSavedPaths(parent, csvFile, maybeListFile);
        }
    }

    private static void showSavedPaths(Component parent, File csvFile, File maybeListFile) {
        StringBuilder sb = new StringBuilder();
        sb.append("Saved files:\n");
        sb.append("- ").append(csvFile.getAbsolutePath()).append("\n");
        if (maybeListFile != null) sb.append("- ").append(maybeListFile.getAbsolutePath()).append("\n");
        JOptionPane.showMessageDialog(parent, sb.toString(), "Saved", JOptionPane.INFORMATION_MESSAGE);
    }
}

