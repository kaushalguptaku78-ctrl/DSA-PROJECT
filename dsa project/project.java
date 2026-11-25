import java.util.*;
import java.io.*;

public class AttendanceTracker {

    static final int MAX_STUDENTS = 100;
    static final int MAX_DAYS = 100;

    static String[] names = new String[MAX_STUDENTS];
    static String[] rolls = new String[MAX_STUDENTS];

    // 2D Array: rows = students, columns = days
    // -1 = Not Marked, 1 = Present, 0 = Absent
    static int[][] attendance = new int[MAX_STUDENTS][MAX_DAYS];

    // HashMap: Roll Number -> Row Index
    static HashMap<String, Integer> rollMap = new HashMap<>();

    static int studentCount = 0;
    static int totalDays = 0;

    static Scanner sc = new Scanner(System.in);

    static final String FILE_NAME = "attendance.txt";

    // Initialize attendance with -1
    static {
        for (int i = 0; i < MAX_STUDENTS; i++) {
            Arrays.fill(attendance[i], -1);
        }
    }

    // Generate roll number automatically
    static String generateRoll() {
        return "R" + (101 + studentCount);
    }

    // Add student
    static void addStudent() {
        if (studentCount >= MAX_STUDENTS) {
            System.out.println("Student limit reached.");
            return;
        }

        System.out.print("Enter student name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        String roll = generateRoll();

        names[studentCount] = name;
        rolls[studentCount] = roll;

        rollMap.put(roll, studentCount);

        System.out.println("Student added. Roll: " + roll + " | " + name);

        studentCount++;
        saveData();
    }

    // Mark attendance for a student
    static void markAttendance() {
        if (studentCount == 0) {
            System.out.println("No students registered.");
            return;
        }

        System.out.print("Enter roll number: ");
        String roll = sc.nextLine().trim().toUpperCase();

        if (!rollMap.containsKey(roll)) {
            System.out.println("Roll number " + roll + " not found.");
            return;
        }

        System.out.print("Enter day number: ");
        int day;

        try {
            day = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid day number.");
            return;
        }

        if (day < 1 || day > MAX_DAYS) {
            System.out.println("Day must be between 1 and " + MAX_DAYS + ".");
            return;
        }

        System.out.print("Enter attendance (P/A): ");
        String status = sc.nextLine().trim().toUpperCase();

        if (!status.equals("P") && !status.equals("A")) {
            System.out.println("Invalid input. Enter P or A.");
            return;
        }

        int row = rollMap.get(roll);
        int col = day - 1;

        attendance[row][col] = status.equals("P") ? 1 : 0;

        if (day > totalDays) {
            totalDays = day;
        }

        System.out.println("Attendance marked for Day " + day + ".");
        saveData();
    }

    // Calculate attendance percentage
    static double calculatePercentage(int row) {
        int present = 0;
        int marked = 0;

        for (int j = 0; j < totalDays; j++) {
            if (attendance[row][j] != -1) {
                marked++;

                if (attendance[row][j] == 1) {
                    present++;
                }
            }
        }

        if (marked == 0) {
            return 0.0;
        }

        return (present * 100.0) / marked;
    }

    // View percentage of a particular student
    static void viewPercentage() {
        System.out.print("Enter roll number: ");
        String roll = sc.nextLine().trim().toUpperCase();

        if (!rollMap.containsKey(roll)) {
            System.out.println("Roll number " + roll + " not found.");
            return;
        }

        int row = rollMap.get(roll);

        int present = 0;
        int marked = 0;

        for (int j = 0; j < totalDays; j++) {
            if (attendance[row][j] != -1) {
                marked++;

                if (attendance[row][j] == 1) {
                    present++;
                }
            }
        }

        double percentage = calculatePercentage(row);

        System.out.printf(
            "%s | %s | Present: %d/%d | %.1f%%\n",
            rolls[row], names[row], present, marked, percentage
        );
    }

    // View attendance of all students
    static void viewAllAttendance() {
        if (studentCount == 0) {
            System.out.println("No students registered.");
            return;
        }

        System.out.println("\n--- Attendance Report ---");

        for (int i = 0; i < studentCount; i++) {
            int present = 0;
            int marked = 0;

            for (int j = 0; j < totalDays; j++) {
                if (attendance[i][j] != -1) {
                    marked++;

                    if (attendance[i][j] == 1) {
                        present++;
                    }
                }
            }

            double percentage = calculatePercentage(i);

            System.out.printf(
                "%s | %s | Present: %d/%d | %.1f%%\n",
                rolls[i], names[i], present, marked, percentage
            );
        }
    }

    // Sort students by attendance percentage
    static void sortByPercentage() {
        if (studentCount == 0) {
            System.out.println("No students registered.");
            return;
        }

        // Create temporary array of student row indices
        Integer[] indices = new Integer[studentCount];

        for (int i = 0; i < studentCount; i++) {
            indices[i] = i;
        }

        // Sorting in descending order of percentage
        Arrays.sort(indices, (a, b) ->
            Double.compare(
                calculatePercentage(b),
                calculatePercentage(a)
            )
        );

        System.out.println("\n--- Sorted by Attendance Percentage ---");

        for (int index : indices) {
            int present = 0;
            int marked = 0;

            for (int j = 0; j < totalDays; j++) {
                if (attendance[index][j] != -1) {
                    marked++;

                    if (attendance[index][j] == 1) {
                        present++;
                    }
                }
            }

            System.out.printf(
                "%s | %s | Present: %d/%d | %.1f%%\n",
                rolls[index],
                names[index],
                present,
                marked,
                calculatePercentage(index)
            );
        }
    }

    // Save data into attendance.txt
    static void saveData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {

            pw.println(studentCount);
            pw.println(totalDays);

            for (int i = 0; i < studentCount; i++) {
                pw.print(rolls[i] + "|" + names[i] + "|");

                for (int j = 0; j < MAX_DAYS; j++) {
                    pw.print(attendance[i][j]);

                    if (j < MAX_DAYS - 1) {
                        pw.print(",");
                    }
                }

                pw.println();
            }

        } catch (IOException e) {
            System.out.println("Error saving data.");
        }
    }

    // Load data from attendance.txt
    static void loadData() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line = br.readLine();

            if (line == null) {
                return;
            }

            studentCount = Integer.parseInt(line);
            totalDays = Integer.parseInt(br.readLine());

            for (int i = 0; i < studentCount; i++) {
                line = br.readLine();

                if (line == null) {
                    break;
                }

                String[] parts = line.split("\\|", 3);

                rolls[i] = parts[0];
                names[i] = parts[1];

                rollMap.put(rolls[i], i);

                String[] values = parts[2].split(",");

                for (int j = 0; j < values.length && j < MAX_DAYS; j++) {
                    attendance[i][j] = Integer.parseInt(values[j]);
                }
            }

            System.out.println("Attendance data loaded successfully.");

        } catch (Exception e) {
            System.out.println("Error loading data.");
        }
    }

    // Main method
    public static void main(String[] args) {

        loadData();

        while (true) {

            System.out.println("\n===== ATTENDANCE TRACKER =====");
            System.out.println("1. Add Student");
            System.out.println("2. Mark Attendance");
            System.out.println("3. View All Attendance");
            System.out.println("4. View Percentage");
            System.out.println("5. Sort by Percentage");
            System.out.println("6. Exit");

            System.out.print("Enter menu option: ");

            int choice;

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid menu option.");
                continue;
            }

            switch (choice) {
                case 1:
                    addStudent();
                    break;

                case 2:
                    markAttendance();
                    break;

                case 3:
                    viewAllAttendance();
                    break;

                case 4:
                    viewPercentage();
                    break;

                case 5:
                    sortByPercentage();
                    break;

                case 6:
                    saveData();
                    System.out.println("Data saved. Exiting program...");
                    return;

                default:
                    System.out.println("Invalid menu option.");
            }
        }
    }
}
