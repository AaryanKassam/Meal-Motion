package mealmotion.ui;

import mealmotion.gen.PlanBuilder;
import mealmotion.model.*;
import mealmotion.util.LogoLoader;
import mealmotion.util.PlanExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public final class WizardFrame {
    private final JFrame frame;
    private final JPanel cards;
    private final CardLayout cardLayout;
    private final JLabel stepLabel;
    private static final int LABEL_COL_WIDTH = 200;

    private int stepIdx = 0;
    private final String[] stepKeys = {"p1","p2","p3","p4","p5","review","export"};

    // Fields
    private JTextField nameField;
    private JSpinner ageSpinner, heightSpinner, weightSpinner, targetWeightSpinner;
    private JComboBox<Gender> genderBox;
    private JComboBox<ActivityLevel> activityBox;
    private JComboBox<BodyGoal> bodyGoalBox;
    private JComboBox<DietPreference> dietBox;

    private JTextField allergiesField;
    private JTextField dislikesField;

    private JCheckBox includeWorkoutBox;
    private JComboBox<Equipment> equipmentBox;
    private JSpinner workoutDaysSpinner;
    private JSpinner workoutMinutesSpinner;

    // Review
    private JTextArea reviewArea;
    private WeeklyPlan generatedPlan;

    public WizardFrame() {
        frame = new JFrame("MealMotion");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setMinimumSize(new Dimension(820, 560));
        frame.setSize(900, 620);
        Image iconImg = LogoLoader.loadWindowIconImage();
        if (iconImg != null) frame.setIconImage(iconImg);

        stepLabel = new JLabel();
        stepLabel.setBorder(new EmptyBorder(10, 12, 6, 12));
        stepLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        cards.add(createPage1(), "p1");
        cards.add(createPage2(), "p2");
        cards.add(createPage3(), "p3");
        cards.add(createPage4(), "p4");
        cards.add(createPage5(), "p5");
        cards.add(createReviewPage(), "review");
        cards.add(createExportPage(), "export");

        JPanel root = new JPanel(new BorderLayout());
        root.add(stepLabel, BorderLayout.NORTH);
        root.add(cards, BorderLayout.CENTER);
        frame.setContentPane(root);

        updateStepLabel();
        cardLayout.show(cards, "p1");

        frame.setLocationRelativeTo(null);
    }

    public void show() {
        frame.setVisible(true);
    }

    // ---------- Pages ----------

    private JPanel createPage1() {
        JPanel form = formPanel();
        nameField = new JTextField(22);
        ageSpinner = new JSpinner(new SpinnerNumberModel(20, 13, 120, 1));
        addRow(form, 0, "Full Name:", nameField);
        addRow(form, 1, "Age:", ageSpinner);

        return pageWithNav("Step 1: Basics", form,
                null,
                () -> goNextValidated(this::validatePage1));
    }

    private JPanel createPage2() {
        JPanel form = formPanel();
        heightSpinner = new JSpinner(new SpinnerNumberModel(170, 120, 230, 1));
        weightSpinner = new JSpinner(new SpinnerNumberModel(70, 30, 250, 1));
        targetWeightSpinner = new JSpinner(new SpinnerNumberModel(70, 30, 300, 1));

        addRow(form, 0, "Height (cm):", heightSpinner);
        addRow(form, 1, "Weight (kg):", weightSpinner);
        addRow(form, 2, "Target Weight (kg):", targetWeightSpinner);

        return pageWithNav("Step 2: Body Stats", form,
                this::goBack,
                () -> goNextValidated(this::validatePage2));
    }

    private JPanel createPage3() {
        JPanel form = formPanel();
        genderBox = new JComboBox<>(Gender.values());
        activityBox = new JComboBox<>(ActivityLevel.values());
        bodyGoalBox = new JComboBox<>(BodyGoal.values());

        addRow(form, 0, "Gender:", genderBox);
        addRow(form, 1, "Activity Level:", activityBox);
        addRow(form, 2, "Body Goal:", bodyGoalBox);

        JLabel hint = new JLabel("Calories are based on Mifflin-St Jeor BMR × activity level, then adjusted slightly for your target weight.");
        hint.setForeground(new Color(85, 85, 85));
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        gc.insets = new Insets(10, 4, 0, 4);
        form.add(hint, gc);

        return pageWithNav("Step 3: Goal & Activity", form,
                this::goBack,
                () -> goNextValidated(this::validatePage3));
    }

    private JPanel createPage4() {
        JPanel form = formPanel();
        dietBox = new JComboBox<>(DietPreference.values());
        allergiesField = new JTextField(22);
        dislikesField = new JTextField(22);

        addRow(form, 0, "Diet Preference:", dietBox);
        addRow(form, 1, "Allergies (comma-separated):", allergiesField);
        addRow(form, 2, "Disliked foods (comma-separated):", dislikesField);

        return pageWithNav("Step 4: Food Preferences", form,
                this::goBack,
                () -> goNextValidated(this::validatePage4));
    }

    private JPanel createPage5() {
        JPanel form = formPanel();
        includeWorkoutBox = new JCheckBox("Yes, include workout plan");
        includeWorkoutBox.setSelected(true);
        equipmentBox = new JComboBox<>(Equipment.values());
        workoutDaysSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 7, 1));
        workoutMinutesSpinner = new JSpinner(new SpinnerNumberModel(45, 20, 120, 5));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(6, 4, 6, 4);
        form.add(includeWorkoutBox, gc);

        addRow(form, 1, "Equipment:", equipmentBox);
        addRow(form, 2, "Days / week:", workoutDaysSpinner);
        addRow(form, 3, "Minutes / session:", workoutMinutesSpinner);

        includeWorkoutBox.addActionListener(e -> setWorkoutControlsEnabled(includeWorkoutBox.isSelected()));
        setWorkoutControlsEnabled(includeWorkoutBox.isSelected());

        return pageWithNav("Step 5: Workout Preferences", form,
                this::goBack,
                () -> {
                    if (!validatePage5()) return;
                    updateReview();
                    goNext(); // to review
                });
    }

    private JPanel createReviewPage() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        reviewArea = new JTextArea();
        reviewArea.setEditable(false);
        reviewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reviewArea.setLineWrap(true);
        reviewArea.setWrapStyleWord(true);

        panel.add(new JLabel("Review Summary (confirm before generating)"), BorderLayout.NORTH);
        panel.add(new JScrollPane(reviewArea), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = new JButton("← Back");
        JButton generate = new JButton("Generate Plan →");
        back.addActionListener(e -> goBack());
        generate.addActionListener(e -> {
            UserProfile profile = buildProfileFromInputs();
            generatedPlan = PlanBuilder.build(profile);
            goNext(); // to export page
        });
        btns.add(back);
        btns.add(generate);
        panel.add(btns, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createExportPage() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel label = new JLabel("Plan ready!", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 22));
        panel.add(label, BorderLayout.NORTH);

        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setOpaque(false);
        info.setFont(new Font("SansSerif", Font.PLAIN, 13));
        info.setText("Click below to save your CSV. You can also save a shopping list after the CSV is saved.");
        panel.add(info, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        JButton save = new JButton("Save CSV (+ Shopping List)");
        JButton back = new JButton("← Back");
        JButton exit = new JButton("Exit");

        save.addActionListener(e -> {
            if (generatedPlan == null) {
                JOptionPane.showMessageDialog(panel, "No plan generated yet. Go back and generate first.");
                return;
            }
            PlanExporter.exportWithChooser(frame, generatedPlan);
        });
        back.addActionListener(e -> goBack());
        exit.addActionListener(e -> System.exit(0));

        btns.add(save);
        btns.add(back);
        btns.add(exit);
        panel.add(btns, BorderLayout.SOUTH);

        return panel;
    }

    // ---------- Navigation + Validation ----------

    private void goNext() {
        stepIdx = Math.min(stepIdx + 1, stepKeys.length - 1);
        updateStepLabel();
        cardLayout.show(cards, stepKeys[stepIdx]);
    }

    private void goBack() {
        stepIdx = Math.max(stepIdx - 1, 0);
        updateStepLabel();
        cardLayout.show(cards, stepKeys[stepIdx]);
    }

    private void goNextValidated(Validator validator) {
        if (!validator.validate()) return;
        goNext();
    }

    private boolean validatePage1() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter your full name.");
            return false;
        }
        return true;
    }

    private boolean validatePage2() {
        int w = (Integer) weightSpinner.getValue();
        int tw = (Integer) targetWeightSpinner.getValue();
        if (w < 30 || w > 250) {
            JOptionPane.showMessageDialog(frame, "Weight looks out of range.");
            return false;
        }
        if (tw < 30 || tw > 300) {
            JOptionPane.showMessageDialog(frame, "Target weight looks out of range.");
            return false;
        }
        if (Math.abs(tw - w) > 120) {
            JOptionPane.showMessageDialog(frame, "Target weight is very far from current weight.\nPlease double-check.");
            return false;
        }
        return true;
    }

    private boolean validatePage3() {
        if (genderBox.getSelectedItem() == null || activityBox.getSelectedItem() == null || bodyGoalBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(frame, "Please select gender, activity level, and body goal.");
            return false;
        }
        return true;
    }

    private boolean validatePage4() {
        // Free-form fields; nothing to hard-fail here.
        return true;
    }

    private boolean validatePage5() {
        if (!includeWorkoutBox.isSelected()) return true;
        int days = (Integer) workoutDaysSpinner.getValue();
        int mins = (Integer) workoutMinutesSpinner.getValue();
        if (days < 1 || days > 7) {
            JOptionPane.showMessageDialog(frame, "Workout days/week must be 1 to 7.");
            return false;
        }
        if (mins < 20 || mins > 120) {
            JOptionPane.showMessageDialog(frame, "Workout minutes/session must be 20 to 120.");
            return false;
        }
        return true;
    }

    private void setWorkoutControlsEnabled(boolean enabled) {
        equipmentBox.setEnabled(enabled);
        workoutDaysSpinner.setEnabled(enabled);
        workoutMinutesSpinner.setEnabled(enabled);
    }

    private void updateReview() {
        UserProfile profile = buildProfileFromInputs();
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(profile.name()).append("\n");
        sb.append("Age: ").append(profile.age()).append("\n");
        sb.append("Height: ").append(profile.heightCm()).append(" cm\n");
        sb.append("Weight: ").append(profile.weightKg()).append(" kg\n");
        sb.append("Target Weight: ").append(profile.targetWeightKg()).append(" kg\n");
        sb.append("Gender: ").append(profile.gender()).append("\n");
        sb.append("Activity: ").append(profile.activityLevel()).append("\n");
        sb.append("Body Goal: ").append(profile.bodyGoal()).append("\n");
        sb.append("Diet: ").append(profile.dietPreference()).append("\n");
        sb.append("\n");
        sb.append("BMR (kcal/day): ").append(profile.bmrCalories()).append("\n");
        sb.append("TDEE (kcal/day): ").append(profile.tdeeCalories()).append("\n");
        sb.append("Target Calories (kcal/day): ").append(profile.targetCalories()).append("\n");
        sb.append("Protein Target (g/day): ").append(profile.proteinTargetGrams()).append("\n");
        sb.append("\n");
        sb.append("Allergies: ").append(String.join(", ", profile.allergies())).append("\n");
        sb.append("Dislikes: ").append(String.join(", ", profile.dislikedFoods())).append("\n");
        sb.append("\n");
        if (profile.includeWorkouts()) {
            sb.append("Workout plan: Yes\n");
            sb.append("Equipment: ").append(profile.equipment()).append("\n");
            sb.append("Days/week: ").append(profile.workoutDaysPerWeek()).append("\n");
            sb.append("Minutes/session: ").append(profile.workoutMinutesPerSession()).append("\n");
        } else {
            sb.append("Workout plan: No\n");
        }
        reviewArea.setText(sb.toString());
        reviewArea.setCaretPosition(0);
    }

    private UserProfile buildProfileFromInputs() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        int age = (Integer) ageSpinner.getValue();
        int height = (Integer) heightSpinner.getValue();
        int weight = (Integer) weightSpinner.getValue();
        int targetWeight = (Integer) targetWeightSpinner.getValue();

        Gender gender = (Gender) genderBox.getSelectedItem();
        ActivityLevel activity = (ActivityLevel) activityBox.getSelectedItem();
        BodyGoal goal = (BodyGoal) bodyGoalBox.getSelectedItem();
        DietPreference diet = (DietPreference) dietBox.getSelectedItem();

        boolean includeWorkouts = includeWorkoutBox.isSelected();
        Equipment equipment = includeWorkouts ? (Equipment) equipmentBox.getSelectedItem() : Equipment.NONE;
        int workoutDays = includeWorkouts ? (Integer) workoutDaysSpinner.getValue() : 0;
        int workoutMins = includeWorkouts ? (Integer) workoutMinutesSpinner.getValue() : 0;

        List<String> allergies = splitCsvLike(allergiesField.getText());
        List<String> dislikes = splitCsvLike(dislikesField.getText());

        return new UserProfile(
                name,
                age,
                height,
                weight,
                targetWeight,
                gender == null ? Gender.OTHER : gender,
                activity == null ? ActivityLevel.LIGHT : activity,
                goal == null ? BodyGoal.TONE : goal,
                diet == null ? DietPreference.NONE : diet,
                includeWorkouts,
                equipment == null ? Equipment.NONE : equipment,
                workoutDays,
                workoutMins,
                allergies,
                dislikes
        );
    }

    private List<String> splitCsvLike(String s) {
        if (s == null) return List.of();
        String t = s.trim();
        if (t.isEmpty()) return List.of();
        return Arrays.stream(t.split(","))
                .map(String::trim)
                .filter(x -> !x.isEmpty())
                .toList();
    }

    private void updateStepLabel() {
        int step = stepIdx + 1;
        int total = stepKeys.length;
        String title = switch (stepKeys[stepIdx]) {
            case "p1" -> "Basics";
            case "p2" -> "Body Stats";
            case "p3" -> "Goal & Activity";
            case "p4" -> "Food Preferences";
            case "p5" -> "Workout Preferences";
            case "review" -> "Review Summary";
            case "export" -> "Export";
            default -> "";
        };
        stepLabel.setText("Step " + step + " of " + total + " — " + title);
    }

    // ---------- UI helpers ----------

    private JPanel pageWithNav(String title, JPanel content, Runnable backAction, Runnable nextAction) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(t, BorderLayout.NORTH);

        panel.add(content, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (backAction != null) {
            JButton back = new JButton("← Back");
            back.addActionListener(e -> backAction.run());
            btns.add(back);
        }
        JButton next = new JButton("Next →");
        next.addActionListener(e -> nextAction.run());
        btns.add(next);

        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel formPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(8, 6, 8, 6));
        return panel;
    }

    private void addRow(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0;
        lc.gridy = row;
        lc.anchor = GridBagConstraints.EAST;
        lc.insets = new Insets(6, 4, 6, 10);
        JLabel l = new JLabel(label);
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        Dimension d = l.getPreferredSize();
        l.setPreferredSize(new Dimension(Math.max(LABEL_COL_WIDTH, d.width), d.height));
        panel.add(l, lc);

        GridBagConstraints fc = new GridBagConstraints();
        fc.gridx = 1;
        fc.gridy = row;
        fc.weightx = 1.0;
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.insets = new Insets(6, 4, 6, 4);
        panel.add(field, fc);
    }

    @FunctionalInterface
    private interface Validator {
        boolean validate();
    }
}

