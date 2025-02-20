package szlicht.daniel.calendar.dialog.app_core;

public class StartMentoringHtmlDialog extends HtmlDialog {


    @Override
    String getSubject() {
        return "Startujemy mentoring z programowania!";
    }

    @Override
    String getTitle() {
        return getSubject();
    }

    @Override
    String getIntro() {
        return "Dziękujemy za kontakt. Poniżej znajdziesz ważne informacje.";
    }

    @Override
    String getSections() {
        String pointsList = asList(
                "Pierwszy punkt ważnej informacji",
                "Drugi punkt z kolejną istotną kwestią",
                "Trzeci punkt, który warto zapamiętać"
        );
        String importantPoints = section("Najważniejsze punkty:", pointsList);
        String additionalInfo = section("Dodatkowe informacje", "<p>Jeśli masz jakiekolwiek pytania, śmiało do nas pisz. Jesteśmy tu, aby pomóc!</p>");
        return importantPoints + additionalInfo;
    }

}
