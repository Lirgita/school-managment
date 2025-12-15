package org.school;

import java.io.*;
import java.util.*;

public class GradeManager {

    private static Student[] students;
    private static Course[] courses;
    private static int[][] grades; // rows = students, cols = courses
    private static final String FILE_NAME = "advanced-grades.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if(!loadFromFile()) setupSystem(scanner);

        int choice;
        do {
            printMenu();
            choice = safeInt(scanner,"Select option: ");
            switch (choice) {
                case 1:
                    enterGrades(scanner);
                    break;

                case 2:
                    displayGradeTable();
                    break;

                case 3:
                    studentAverage(scanner);
                    break;

                case 4:
                    courseAverage(scanner);
                    break;

                case 5:
                    bestWorstStudent();
                    break;

                case 6:
                    addStudent(scanner);
                    break;

                case 7:
                    addCourse(scanner);
                    break;

                case 8:
                    updateGrade(scanner);
                    break;

                case 9:
                    saveToFile();
                    break;

                case 10:
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid option.");
                    break;
            }

        } while(choice != 10);
    }

    // --------------- SETUP ----------------
    public static void setupSystem(Scanner scanner) {
        int studentCount = safeInt(scanner,"Number of students: ");
        int courseCount = safeInt(scanner,"Number of courses: ");

        students = new Student[studentCount];
        courses = new Course[courseCount];
        grades = new int[studentCount][courseCount];

        scanner.nextLine();
        for(int i=0;i<studentCount;i++) {
            System.out.print("Student name: ");
            String name = scanner.nextLine();
            int age = safeInt(scanner,"Age: ");
            System.out.print("Email: ");
            String email = scanner.nextLine();
            students[i] = new Student(i+1,name,age,email);
        }

        for(int i=0;i<courseCount;i++) {
            System.out.print("Course title: ");
            String title = scanner.nextLine();
            int credits = safeInt(scanner,"Credits: ");
            courses[i] = new Course(i+1,title,credits);
        }
    }

    // --------------- MENU ----------------
    public static void printMenu() {
        System.out.println("\n=== UNIVERSITY GRADE SYSTEM ===");
        System.out.println("1. Enter Grades");
        System.out.println("2. Display Grades");
        System.out.println("3. Student Average");
        System.out.println("4. Course Average");
        System.out.println("5. Best/Worst Student");
        System.out.println("6. Add Student");
        System.out.println("7. Add Course");
        System.out.println("8. Update Grade");
        System.out.println("9. Save Data");
        System.out.println("10. Exit");
    }

    // --------------- ENTER GRADES ----------------
    public static void enterGrades(Scanner scanner) {
        for(int i=0;i<students.length;i++) {
            System.out.println("Grades for " + students[i].getName());
            for(int j=0;j<courses.length;j++) {
                grades[i][j] = safeInt(scanner,"  Grade for " + courses[j].getTitle() + ": ");
            }
        }
    }

    // --------------- DISPLAY TABLE ----------------
    public static void displayGradeTable() {
        System.out.printf("%10s","Student");
        for(Course c: courses) System.out.printf("%10s",c.getTitle());
        System.out.println();
        for(int i=0;i<students.length;i++){
            System.out.printf("%10s",students[i].getName());
            for(int j=0;j<courses.length;j++)
                System.out.printf("%10d",grades[i][j]);
            System.out.println();
        }
    }

    // --------------- STUDENT AVG ----------------
    public static void studentAverage(Scanner scanner) {
        int id = safeInt(scanner,"Student ID: ")-1;
        if(id<0 || id>=students.length){ System.out.println("Invalid student"); return;}
        int sum = 0;
        for(int g: grades[id]) sum+=g;
        System.out.println("Average: "+ (sum/(double)courses.length));
    }

    // --------------- COURSE AVG ----------------
    public static void courseAverage(Scanner scanner) {
        int id = safeInt(scanner,"Course ID: ")-1;
        if(id<0 || id>=courses.length){ System.out.println("Invalid course"); return;}
        int sum = 0;
        for(int i=0;i<students.length;i++) sum+=grades[i][id];
        System.out.println("Average: "+ (sum/(double)students.length));
    }

    // --------------- BEST/WORST ----------------
    public static void bestWorstStudent() {
        double bestAvg=-1, worstAvg=9999;
        Student best=null, worst=null;
        for(int i=0;i<students.length;i++){
            int sum=0;
            for(int g: grades[i]) sum+=g;
            double avg = sum/(double)courses.length;
            if(avg>bestAvg){ bestAvg=avg; best=students[i];}
            if(avg<worstAvg){ worstAvg=avg; worst=students[i];}
        }
        System.out.println("Best: "+best.getName()+" avg="+bestAvg);
        System.out.println("Worst: "+worst.getName()+" avg="+worstAvg);
    }

    // --------------- ADD STUDENT ----------------
    public static void addStudent(Scanner scanner){
        Student[] newArr = Arrays.copyOf(students, students.length+1);
        System.out.print("Name: "); scanner.nextLine();
        String name = scanner.nextLine();
        int age = safeInt(scanner,"Age: ");
        System.out.print("Email: "); String email = scanner.nextLine();
        newArr[newArr.length-1] = new Student(newArr.length,name,age,email);
        students=newArr;

        int[][] newGrades = new int[students.length][courses.length];
        for(int i=0;i<grades.length;i++)
            newGrades[i]=grades[i].clone();
        grades=newGrades;
        System.out.println("Student added!");
    }

    // --------------- ADD COURSE ----------------
    public static void addCourse(Scanner scanner){
        Course[] newArr = Arrays.copyOf(courses,courses.length+1);
        System.out.print("Course title: "); scanner.nextLine();
        String title = scanner.nextLine();
        int credits = safeInt(scanner,"Credits: ");
        newArr[newArr.length-1] = new Course(newArr.length,title,credits);
        courses=newArr;

        int[][] newGrades = new int[students.length][courses.length];
        for(int i=0;i<grades.length;i++)
            for(int j=0;j<grades[i].length;j++)
                newGrades[i][j]=grades[i][j];
        grades=newGrades;
        System.out.println("Course added!");
    }

    // --------------- UPDATE GRADE ----------------
    public static void updateGrade(Scanner scanner){
        int studentId = safeInt(scanner,"Student ID: ")-1;
        int courseId = safeInt(scanner,"Course ID: ")-1;
        if(studentId<0 || studentId>=students.length || courseId<0 || courseId>=courses.length){
            System.out.println("Invalid IDs"); return;
        }
        int grade = safeInt(scanner,"New grade: ");
        grades[studentId][courseId]=grade;
        System.out.println("Grade updated!");
    }

    // --------------- FILE I/O ----------------
    public static void saveToFile(){
        try(FileWriter writer = new FileWriter(FILE_NAME)){
            writer.write(students.length+" "+courses.length+"\n");
            for(Student s: students) writer.write(s.getId()+","+s.getName()+","+s.getAge()+","+s.getEmail()+"\n");
            writer.write("COURSES\n");
            for(Course c: courses) writer.write(c.getId()+","+c.getTitle()+","+c.getCredits()+"\n");
            writer.write("GRADES\n");
            for(int[] row: grades){
                for(int g: row) writer.write(g+" ");
                writer.write("\n");
            }
            System.out.println("Data saved.");
        } catch(IOException e){ System.out.println("Error saving");}
    }

    public static boolean loadFromFile(){
        try{
            File file = new File(FILE_NAME);
            if(!file.exists()) return false;
            Scanner scanner = new Scanner(file);
            int studentCount = scanner.nextInt();
            int courseCount = scanner.nextInt();
            scanner.nextLine();
            students = new Student[studentCount];
            courses = new Course[courseCount];
            grades = new int[studentCount][courseCount];
            for(int i=0;i<studentCount;i++){
                String[] parts = scanner.nextLine().split(",");
                students[i] = new Student(Integer.parseInt(parts[0]),parts[1],Integer.parseInt(parts[2]),parts[3]);
            }
            scanner.nextLine();
            for(int i=0;i<courseCount;i++){
                String[] parts = scanner.nextLine().split(",");
                courses[i] = new Course(Integer.parseInt(parts[0]),parts[1],Integer.parseInt(parts[2]));
            }
            scanner.nextLine();
            for(int i=0;i<studentCount;i++){
                for(int j=0;j<courseCount;j++) grades[i][j]=scanner.nextInt();
            }
            scanner.close();
            System.out.println("Data loaded.");
            return true;
        } catch(Exception e){ return false;}
    }

    // --------------- INPUT VALIDATION ----------------
    public static int safeInt(Scanner scanner, String message){
        while(true){
            try{
                System.out.print(message);
                return scanner.nextInt();
            } catch(InputMismatchException e){
                System.out.println("Invalid number. Try again.");
                scanner.nextLine();
            }
        }
    }
}