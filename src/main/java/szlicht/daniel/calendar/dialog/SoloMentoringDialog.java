package szlicht.daniel.calendar.dialog;

public class SoloMentoringDialog extends Dialog {

    private String formatedPropositions;

    public SoloMentoringDialog(String email, String formatedPropositions) {
        super(email);
        this.formatedPropositions = formatedPropositions;
    }

    @Override
    public String getSubject() {
        return "Indywidualne lekcje programowania";
    }

    @Override
    String getTitle() {
        return getSubject();
    }

    @Override
    String getIntro() {
        return "Gratuluję! Kwalifikujesz się na indywidualny mentoring z programowania. " +
                "Ale zanim umówisz się na pierwszą bezpłatną godzinę, opowiem Ci o moich lekcjach.";
    }

    @Override
    String getSections() {
        String informationList = asList(
                "Stawka godzinowa to 200zł/h. Jeśli to za dużo, sprawdź tańsze opcje",
                "Przy zakupie pakietu 10h jedna godzina bezpłatnie",
                "Najczęściej umawiamy się na 1,5h regularnie co tydzień ale to zależy wyłącznie od " +
                        "Ciebie (nawet 1h raz na 2 tygodnie daje duże efekty)",
                "Pozostałe informacje, takie jak instalacja środowiska, " +
                        "czy zasady w przypadku nieobecności otrzymasz w kolejnym mailu"
        );
        String informationSection = section("Informacje podstawowe:", informationList);
        String firstMeetingList = asList(
                "Jest całkowicie bezpłatne, i możesz zrezygnować bez żadnych kosztów",
                "Wspólnie ustalimy indywidualny plan Twojej nauki i zidentyfikujemy największe przeszkody",
                "Chętnie Cię poznam i odpowiem na wszystkie Twoje pytania",
                "Po rozmowie przejdziemy do programowania, pokażę Ci mój styl nauczania",
                "Na kilku zadaniach zorientuję się jaki jest Twój poziom i powiem Ci jakie ćwiczenia najbardziej przyspieszą Twój rozwój",
                "Spotkania 1h trwają dokładnie 55 minut ponieważ nie planuję po nich przerw"
        );
        String firstMeetingSection = section("Pierwsze spotkanie:", firstMeetingList);
        String propositionsSection = section(tag("center","Teraz już tylko wybierz termin i możemy zaczynać!"),
                tag("center", formatedPropositions));
        return informationSection + firstMeetingSection + propositionsSection;
    }
}
