package szlicht.daniel.calendar.student.app_core;



public class Student {
    private int id;
    private String name;
    private String email;
    private StudentRang rang;

    public Student(String name, String email, StudentRang rang) {
        this.name = name;
        this.email = email;
        this.rang = rang;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public StudentRang getRank() {
        return rang;
    }
}
