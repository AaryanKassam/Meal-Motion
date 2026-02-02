# Meal-Motion

```
 __  __            _ __  __       _   _
|  \/  | ___  __ _| |  \/  | ___ | |_(_) ___  _ __
| |\/| |/ _ \/ _` | | |\/| |/ _ \| __| |/ _ \| '_ \
| |  | |  __/ (_| | | |  | | (_) | |_| | (_) | | | |
|_|  |_|\___|\__,_|_|_|  |_|\___/ \__|_|\___/|_| |_|
```

MealMotion is a Java Swing desktop application that provides users with a fully personalized nutrition and workout plan. Designed for anyone looking to reach their health and fitness goals, MealMotion tailors weekly meal recommendations and exercise routines based on the user’s age, weight, height, gender, dietary preferences, body goal, and target weight.

Example Use Case:

- Enter personal details and fitness goals in the wizard.

- Receive a full week of meal and workout recommendations.

- Download your plan as a CSV file for tracking and implementation.

- MealMotion is perfect for students, professionals, and fitness enthusiasts looking for an all-in-one personalized health planner.

## Running the app

### Option A: Run with Gradle (recommended if Gradle is set up)

- **Requirements**: JDK **17+** and Gradle on PATH (this repo currently does **not** include a Gradle wrapper).
- **Run**:

```bash
gradle run
```

If you get Gradle errors, it’s usually one of these:
- **Java missing / wrong version**: run `java -version` and make sure it’s 17+.
- **Gradle missing**: `gradle -v` says “command not found”.

### Making Gradle more reliable (add a Gradle wrapper)

If you have Gradle installed once, you can generate a wrapper so the project no longer depends on a globally-installed Gradle:

```bash
gradle wrapper --gradle-version 8.7
./gradlew run
```

### Option B: Run without Gradle (works even if Gradle is broken)

- **Requirements**: JDK **17+** (needs `javac` and `java`).
- **Run**:

```bash
chmod +x ./run-no-gradle.sh
./run-no-gradle.sh
```

