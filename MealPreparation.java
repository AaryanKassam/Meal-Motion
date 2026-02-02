import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;

// ================= Main =================
public class MealPreparation {

    public static void main(String[] args) {
        MealMotionSplashScreen splash = new MealMotionSplashScreen();
        splash.waitForSplashToFinish();

        SwingUtilities.invokeLater(() -> new WizardApp().show());
    }

    /**
     * Loads a logo from `resources/logo.png` (relative to the working directory),
     * falling back to classpath `/resources/logo.png` if packaged that way.
     */
    static ImageIcon loadLogoIcon(int maxWidthPx) {
        // 1) Easy dev workflow: drop a file into ./resources/logo.png
        File disk = new File("resources/logo.png");
        if (disk.exists() && disk.isFile()) {
            return new ImageIcon(disk.getPath());
        }

        // 2) Packaged workflow: include it on the classpath at /resources/logo.png
        try {
            java.net.URL url = MealPreparation.class.getResource("/resources/logo.png");
            if (url != null) return new ImageIcon(url);
        } catch (Exception ignored) { }

        return null;
    }

    static ImageIcon scaleIcon(ImageIcon icon, int maxWidthPx) {
        if (icon == null) return null;
        int w0 = icon.getIconWidth();
        int h0 = icon.getIconHeight();
        if (w0 <= 0 || h0 <= 0) return icon;
        int w = Math.min(maxWidthPx, w0);
        int h = (int) ((double) h0 / (double) w0 * (double) w);
        Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}

/* -------------------- Splash Screen -------------------- */
class MealMotionSplashScreen {
    private final CountDownLatch latch = new CountDownLatch(1);

    public MealMotionSplashScreen() {
        SwingUtilities.invokeLater(this::createAndShow);
    }

    private void createAndShow() {
        JWindow window = new JWindow();
        JPanel content = new JPanel(null);
        content.setBackground(new Color(250, 250, 250));

        try {
            ImageIcon icon = MealPreparation.scaleIcon(MealPreparation.loadLogoIcon(240), 240);
            if (icon == null) throw new RuntimeException("Logo not found");

            JLabel logo = new JLabel(icon);
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            logo.setBounds((400 - w) / 2, 30, w, h);
            logo.setHorizontalAlignment(SwingConstants.CENTER);
            content.add(logo);
        } catch (Exception ex) {
            JLabel title = new JLabel("MealMotion", SwingConstants.CENTER);
            title.setFont(new Font("SansSerif", Font.BOLD, 36));
            title.setBounds(0, 30, 400, 60);
            content.add(title);
        }

        JLabel loadingLabel = new JLabel("Preparing MealMotion...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        loadingLabel.setBounds(0, 240, 400, 20);
        content.add(loadingLabel);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setBounds(50, 270, 300, 20);
        bar.setStringPainted(false);
        bar.setForeground(new Color(60, 179, 113));
        content.add(bar);

        window.getContentPane().add(content);
        window.setSize(400, 320);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        javax.swing.Timer t = new javax.swing.Timer(25, null);
        t.addActionListener(e -> {
            int v = bar.getValue();
            if (v < 100) bar.setValue(v + 1);
            else {
                ((javax.swing.Timer)e.getSource()).stop();
                window.dispose();
                latch.countDown();
            }
        });
        t.start();
    }

    public void waitForSplashToFinish() {
        try { latch.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}

/* -------------------- Wizard App -------------------- */
class WizardApp {
    private JFrame frame;
    private JPanel cards;
    private CardLayout cardLayout;

    // Page fields
    private JTextField nameField;
    private JSpinner ageSpinner, heightSpinner, weightSpinner, targetWeightSpinner;
    private JComboBox<String> genderBox, bodyTypeBox, dietBox;
    private JCheckBox wantWorkoutBox;

    public void show() {
        frame = new JFrame("MealMotion — Personalized Plan Wizard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        try {
            ImageIcon icon = MealPreparation.loadLogoIcon(256);
            if (icon != null) frame.setIconImage(icon.getImage());
        } catch (Exception ignored) { }

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        cards.add(createPage1(), "page1");
        cards.add(createPage2(), "page2");
        cards.add(createPage3(), "page3");
        cards.add(createPage4(), "page4");
        cards.add(createPage5(), "page5");
        cards.add(createPage6(), "page6");

        frame.add(cards);
        frame.setVisible(true);
    }

    // ---------- Page 1 ----------
    private JPanel createPage1() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(12,12,12,12);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx=0; gc.gridy=0; panel.add(new JLabel("Full Name:"), gc);
        nameField = new JTextField(20);
        gc.gridx=1; gc.gridy=0; panel.add(nameField, gc);

        gc.gridx=0; gc.gridy=1; panel.add(new JLabel("Age:"), gc);
        ageSpinner = new JSpinner(new SpinnerNumberModel(20, 13, 120, 1));
        gc.gridx=1; gc.gridy=1; panel.add(ageSpinner, gc);

        JPanel btns = new JPanel();
        JButton next = new JButton("Next →");
        next.addActionListener(e -> cardLayout.show(cards, "page2"));
        btns.add(next);
        gc.gridx=0; gc.gridy=2; gc.gridwidth=2;
        panel.add(btns, gc);

        return panel;
    }

    // ---------- Page 2 ----------
    private JPanel createPage2() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(12,12,12,12);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx=0; gc.gridy=0; panel.add(new JLabel("Height (cm):"), gc);
        heightSpinner = new JSpinner(new SpinnerNumberModel(170, 120, 230, 1));
        gc.gridx=1; gc.gridy=0; panel.add(heightSpinner, gc);

        gc.gridx=0; gc.gridy=1; panel.add(new JLabel("Weight (kg):"), gc);
        weightSpinner = new JSpinner(new SpinnerNumberModel(70, 30, 250, 1));
        gc.gridx=1; gc.gridy=1; panel.add(weightSpinner, gc);

        JPanel btns = new JPanel();
        JButton back = new JButton("← Back");
        back.addActionListener(e -> cardLayout.show(cards, "page1"));
        JButton next = new JButton("Next →");
        next.addActionListener(e -> cardLayout.show(cards, "page3"));
        btns.add(back); btns.add(next);

        gc.gridx=0; gc.gridy=2; gc.gridwidth=2; panel.add(btns, gc);

        return panel;
    }

    // ---------- Page 3 ----------
    private JPanel createPage3() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(12,12,12,12); gc.anchor = GridBagConstraints.WEST;

        gc.gridx=0; gc.gridy=0; panel.add(new JLabel("Target Weight (kg):"), gc);
        targetWeightSpinner = new JSpinner(new SpinnerNumberModel(70,30,300,1));
        gc.gridx=1; gc.gridy=0; panel.add(targetWeightSpinner, gc);

        gc.gridx=0; gc.gridy=1; panel.add(new JLabel("Gender:"), gc);
        genderBox = new JComboBox<>(new String[]{"Male","Female","Other"});
        gc.gridx=1; gc.gridy=1; panel.add(genderBox, gc);

        JPanel btns = new JPanel();
        JButton back = new JButton("← Back");
        back.addActionListener(e -> cardLayout.show(cards, "page2"));
        JButton next = new JButton("Next →");
        next.addActionListener(e -> cardLayout.show(cards, "page4"));
        btns.add(back); btns.add(next);

        gc.gridx=0; gc.gridy=2; gc.gridwidth=2; panel.add(btns, gc);

        return panel;
    }

    // ---------- Page 4 ----------
    private JPanel createPage4() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets=new Insets(12,12,12,12); gc.anchor=GridBagConstraints.WEST;

        gc.gridx=0; gc.gridy=0; panel.add(new JLabel("Body Goal:"), gc);
        bodyTypeBox = new JComboBox<>(new String[]{"Lean","Bulk","Tone"});
        gc.gridx=1; gc.gridy=0; panel.add(bodyTypeBox, gc);

        gc.gridx=0; gc.gridy=1; panel.add(new JLabel("Diet Preference:"), gc);
        dietBox = new JComboBox<>(new String[]{"None","Halal","Vegetarian","Vegan","Gluten-Free"});
        gc.gridx=1; gc.gridy=1; panel.add(dietBox, gc);

        JPanel btns = new JPanel();
        JButton back = new JButton("← Back");
        back.addActionListener(e -> cardLayout.show(cards, "page3"));
        JButton next = new JButton("Next →");
        next.addActionListener(e -> cardLayout.show(cards, "page5"));
        btns.add(back); btns.add(next);

        gc.gridx=0; gc.gridy=2; gc.gridwidth=2; panel.add(btns, gc);
        return panel;
    }

    // ---------- Page 5 ----------
    private JPanel createPage5() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets=new Insets(12,12,12,12); gc.anchor=GridBagConstraints.WEST;

        gc.gridx=0; gc.gridy=0; panel.add(new JLabel("Include workout plan?"), gc);
        wantWorkoutBox = new JCheckBox("Yes, include workout plan");
        gc.gridx=1; gc.gridy=0; panel.add(wantWorkoutBox, gc);

        JPanel btns = new JPanel();
        JButton back = new JButton("← Back");
        back.addActionListener(e -> cardLayout.show(cards,"page4"));
        JButton next = new JButton("Next →");
        next.addActionListener(e -> cardLayout.show(cards,"page6"));
        btns.add(back); btns.add(next);

        gc.gridx=0; gc.gridy=1; gc.gridwidth=2; panel.add(btns, gc);
        return panel;
    }

    // ---------- Page 6 (THANK YOU + CSV download) ----------
    private JPanel createPage6() {
        JPanel panel = new JPanel(new BorderLayout(8,8));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("THANK YOU!", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        panel.add(label, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);

        JButton download = new JButton("CLICK HERE to download CSV");
        JButton exit = new JButton("Exit Program");

        btnPanel.add(download);
        btnPanel.add(exit);
        panel.add(btnPanel, BorderLayout.CENTER);

        download.addActionListener(e -> {
            Person p = new Person(
                    nameField.getText(),
                    (Integer) ageSpinner.getValue(),
                    (Integer) heightSpinner.getValue(),
                    (Integer) weightSpinner.getValue(),
                    (Integer) targetWeightSpinner.getValue(),
                    (String) genderBox.getSelectedItem()
            );

            Map<String,List<String>> meals = MealGeneratorFull.generateWeeklyMeals(p, (String) dietBox.getSelectedItem());
            Map<String,List<String>> workouts = wantWorkoutBox.isSelected()
                    ? WorkoutGeneratorFull.generateWeeklyWorkouts(p, (String) bodyTypeBox.getSelectedItem())
                    : new LinkedHashMap<>();

            String fileName = "MealMotionPlan.csv";
            CSVWriterFull.writePlanCSV(fileName, p, meals, workouts);

            try { Desktop.getDesktop().open(new File(fileName)); }
            catch(IOException ex){ JOptionPane.showMessageDialog(panel, "Cannot open CSV file."); }
        });

        exit.addActionListener(e -> System.exit(0));

        return panel;
    }

    /* -------------------- Inner Classes for Person, Meals, Workouts -------------------- */
    static class Person {
        String name; int age; int height; int weight; int targetWeight; String gender; int caloriesNeeded;
        public Person(String name, int age, int height, int weight, int targetWeight, String gender) {
            this.name=name; this.age=age; this.height=height; this.weight=weight; this.targetWeight=targetWeight; this.gender=gender;
            this.caloriesNeeded = (int)((10*weight)+(6.25*height)-(5*age)+(gender.equalsIgnoreCase("Male")?5:-161)*1.375);
        }
    }

    static class MealGeneratorFull {
        static final String[][] meals = {
                {"Oatmeal + Fruit","300","None"}, {"Scrambled Eggs","250","None"}, {"Smoothie","350","None"},
                {"Vegan Pancakes","320","Vegan"}, {"Avocado Toast","280","Vegetarian"},
                {"Grilled Chicken Salad","500","None"}, {"Beef Stir Fry","550","None"}, {"Veggie Wrap","450","Vegetarian"},
                {"Quinoa Bowl","500","Vegan"}, {"Halal Chicken Wrap","520","Halal"},
                {"Salmon + Veggies","600","None"}, {"Vegetarian Lasagna","550","Vegetarian"}, {"Vegan Pasta","520","Vegan"},
                {"Gluten-Free Stir Fry","480","Gluten-Free"}, {"Tofu Stir Fry","500","Vegan"},
                {"Apple","100","None"}, {"Trail Mix","150","None"}, {"Carrots + Hummus","120","Vegan"},
                {"Banana","100","None"}, {"Granola Bar","150","Vegetarian"}
        };

        public static Map<String,List<String>> generateWeeklyMeals(Person p, String diet) {
            Map<String,List<String>> plan = new LinkedHashMap<>();
            String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
            Random r = new Random();

            for(String day : days) {
                List<String> daily = new ArrayList<>();
                int cal = p.caloriesNeeded;
                daily.add("Breakfast: "+selectMeal(cal/4,diet,r));
                daily.add("Lunch: "+selectMeal(cal/3,diet,r));
                daily.add("Dinner: "+selectMeal(cal/3,diet,r));
                daily.add("Snack: "+selectMeal(cal/6,diet,r));
                daily.add("Calories: "+cal);
                plan.put(day,daily);
            }
            return plan;
        }

        private static String selectMeal(int cal, String diet, Random r) {
            List<String> options = new ArrayList<>();
            for(String[] m : meals) if(diet.equals("None")||m[2].equals("None")||m[2].equalsIgnoreCase(diet)) options.add(m[0]);
            return options.get(r.nextInt(options.size()));
        }
    }

    static class WorkoutGeneratorFull {
        static final Map<String,String[]> baseWorkouts = new HashMap<>();
static {
    // Lean: focus on fat burn + overall strength
    baseWorkouts.put("Lean", new String[]{
        "Treadmill Running", "Stationary Bike", "Rowing Machine", "Jump Rope", 
        "Push-ups", "Pull-ups", "Plank", "Burpees", "Kettlebell Swings", "Dumbbell Shoulder Press",
        "Lunges", "Jump Squats"
    });

    // Bulk: focus on muscle building
    baseWorkouts.put("Bulk", new String[]{
        "Squats", "Bench Press", "Deadlifts", "Bicep Curls", "Shoulder Press",
        "Leg Press", "Pull-ups", "Lat Pulldown", "Incline Dumbbell Press", "Barbell Rows",
        "Tricep Dips", "Cable Fly"
    });

    // Tone: focus on muscle definition + endurance
    baseWorkouts.put("Tone", new String[]{
        "Lunges", "Pull-ups", "Bicep Curls", "Plank", "Dumbbell Lateral Raise",
        "Medicine Ball Slams", "Kettlebell Deadlift", "Push-ups", "Jump Rope", "Leg Raises",
        "Seated Row", "Hip Thrusts"
    });
}
        public static Map<String,List<String>> generateWeeklyWorkouts(Person p, String bodyGoal) {
            Map<String,List<String>> plan = new LinkedHashMap<>();
            String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
            for(String day:days) {
                List<String> daily = new ArrayList<>();
                for(String ex : baseWorkouts.getOrDefault(bodyGoal,new String[]{"Rest"})) {
                    int sets=3; int reps=10;
                    if(bodyGoal.equals("Lean")){reps+=2;if(ex.equals("Cardio")) reps=30;}
                    else if(bodyGoal.equals("Bulk")){sets+=1;reps-=2;if(ex.equals("Cardio")) reps=20;}
                    else if(bodyGoal.equals("Tone")){sets=3;reps=12;if(ex.equals("Cardio")) reps=25;}
                    if(p.targetWeight>p.weight) reps+=2; else if(p.targetWeight<p.weight) reps-=1;
                    if(ex.equals("Cardio")) daily.add(ex+": "+reps+" min");
                    else if(ex.equals("Plank")) daily.add(ex+": "+reps+" sec");
                    else daily.add(ex+": "+sets+"x"+reps);
                }
                plan.put(day,daily);
            }
            return plan;
        }
    }

    static class CSVWriterFull {
        public static void writePlanCSV(String fileName, Person p, Map<String,List<String>> meals, Map<String,List<String>> workouts) {
            try(PrintWriter pw = new PrintWriter(new File(fileName))) {
                pw.println("Day,Breakfast,Lunch,Dinner,Snack,Calories,Workout");
                for(String day:meals.keySet()) {
                    List<String> m = meals.get(day);
                    List<String> w = workouts.getOrDefault(day,new ArrayList<>());
                    pw.printf("%s,%s,%s,%s,%s,%s,%s\n",
                            day, m.get(0).split(": ")[1], m.get(1).split(": ")[1],
                            m.get(2).split(": ")[1], m.get(3).split(": ")[1], m.get(4).split(": ")[1],
                            String.join(" | ", w));
                }
            } catch(Exception e){e.printStackTrace();}
        }
    }

}