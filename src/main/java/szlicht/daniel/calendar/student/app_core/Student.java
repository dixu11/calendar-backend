package szlicht.daniel.calendar.student.app_core;



public class Student {
    private int id;
    private String name;
    private String nick;
    private String email;
    private StudentRang rang;

    public Student(int id, String name, String email, StudentRang rang) {
        this.id = id;
        name = formatStudentName(name);
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

    public static String formatStudentName(String name) {
        String[] split = name.split(" ");
        if (split.length == 2 && split[1].length() > 3) {
            name = split[0] + " " + split[1].substring(0, 3);
        }
        return name;
    }

    public void setRang(StudentRang rang) {
        this.rang = rang;
    }

    public String getNick() {
        return nick;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
