package szlicht.daniel.calendar.dialog.app_core;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

public class AfterFirstMentoringHtmlDialog extends HtmlDialog {

    private final String formatedPropositions;
    private final String propositionsMailto;

    public AfterFirstMentoringHtmlDialog(String email, String formatedPropositions,String propositionsMailto) {
        super(email);
        this.formatedPropositions = formatedPropositions;
        this.propositionsMailto = propositionsMailto;
    }

    @Override
    String getSubject() {
        return "We did it!";
    }

    @Override
    String getTitle() {
        return getSubject();
    }

    @Override
    String getIntro() {
        return "Pierwsze koty za płoty. Teraz proponuję iść za ciosem i umówić kolejne zajęcia \uD83D\uDE42" +
                " Zachowaj tego maila bo w nim znajduje się numer do przelewów i zasady współpracy.";
    }

    @Override
    String getSections() {
        String rulesList = asList(
                "Na dzień przed lekcją wykonaj przelew na numer konta: " + params.mail().bank() +
                        "W tytule wystarczy Twoje imie i nazwisko." +
                        " Nie musisz wysyłać potwierdzenia. ",
                params.values().pricesOffer(),
                "Mentoring można bezpłatnie odwołać dzień przed. W przeciwnym wypadku potrącam 1/3 ceny lekcji.",
                "Na spotkania można umawiać się przez maile lub ustalić ze mną cykliczny stały termin",
                "Przed spotkaniem zawsze pytam czy jesteś gotowy/a na skype, możesz sam/a napisać tuż przed lekcją to szybciej zaczniemy \uD83D\uDE42"

        );
        String rules = section("Zasady:", rulesList);
        String section = section(tag("center", "Wybierz termin na drugą lekcję (1,5h):"),
                tag("center", "Jeśli interesuje Cię inna długość, kliknij tutaj i wyślij: "
                        + propositionsMailto + "<br>") +
                tag("center", formatedPropositions));
        return rules + section;
    }
}
