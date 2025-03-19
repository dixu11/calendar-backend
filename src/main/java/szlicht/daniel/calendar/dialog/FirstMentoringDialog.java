package szlicht.daniel.calendar.dialog;

import szlicht.daniel.calendar.meeting.Meeting;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

public class FirstMentoringDialog extends Dialog {
    private Meeting meeting;

    public FirstMentoringDialog(String email, Meeting meeting) {
        super(email);
        this.meeting = meeting;
    }

    @Override
    public String getSubject() {
        return "Pierwsze, bezpłatne spotkanie umówione!";
    }

    @Override
    String getTitle() {
        return getSubject();
    }

    @Override
    String getIntro() {
        return "Strasznie się cieszę, że poznam kolejną osobę wkręconą w programowanie!";
    }

    @Override
    String getSections() {
        String list = asList(
                "zainstaluj Skype: " + link("https://www.skype.com/en/get-skype/"),
                "zaproś mnie po tym linku: " + link("https://join.skype.com/invite/fHmXMxdQHyjb"),
                "zagadaj mnie na skype najszybciej jak możesz :)",
                "zainstaluj AnyDesk: " + link("https://anydesk.com/en/downloads/windows"),
                "Zainstaluj najnowszą wersję " + link("Pythona", "https://www.python.org/downloads/") + " lub "
                        + link("Java JDK 21", "https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html"),
                "Zainstaluj najnowszą wersję " + link("Pycharm Community", "https://www.jetbrains.com/pycharm/download/?section=windows") + " lub "
                        + link("IntelliJ Community", "https://www.jetbrains.com/idea/download/?section=windows"),
                "Jeśli chcesz się przygotować możesz porobić trochę zadań z moich arkuszy: " + link("podstawy",
                        "https://docs.google.com/document/d/1_s0fzkBq54Br36P9Y12_o0V-K0C28DpriObbKkftjp0/edit?tab=t.0#heading=h.hbwvvb4llow0") + ", "  + " lub "
                        + link("zaawansowane",
                        "https://docs.google.com/document/d/1Ei2AJU2x2dPBjVUndzDbv0aw4gonT6xtIOns9NqYcwY/edit?fbclid=IwAR336-Z4Qbky5R99H8jAfAtuvJHRoQgeWf20WQU1psNezGRSluaHgSsXYV8&tab=t.0#heading=h.qqt2am6rjsn3") + ".",
                "W razie problemów napisz na: " + params.mail().owner() + " lub zadzwoń na " + params.mail().phone()

        );
        String section1 = section("Przed spotkaniem:", list);
        String section2 = section("Do usłyszenia " + meeting.when() + "!<br>" +
                "Pierwsze spotkanie potrwa dokładnie 55 minut.<br>" +
                "Tuż przed lekcją napisz mi, że jesteś już gotowy/a to zadzwonię od razu :)<br>" +
                "Już nie mogę doczekać się naszego spotkania!");
        return section1 + section2;
    }
}
