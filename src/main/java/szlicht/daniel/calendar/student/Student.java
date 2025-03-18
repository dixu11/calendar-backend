package szlicht.daniel.calendar.student;


public class Student {
    private int id;
    private String name;
    private String nick;
    private String email;
    private StudentRang rang;
    private String story;

    public Student(int id, String name, String email, StudentRang rang,String story) {
        this.id = id;
        name = formatStudentName(name);
        this.name = name;
        this.email = email;
        this.rang = rang;
        this.story = story;
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
        name = name.replaceAll("[^\\p{L}\\s]", "");
        name = name.trim();
        String[] split = name.split("\\s+");
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

    public String getStory() {
        return story;
    }

    public boolean hasOnlyNameFilled() {
        return !name.contains(" ");
    }
}
