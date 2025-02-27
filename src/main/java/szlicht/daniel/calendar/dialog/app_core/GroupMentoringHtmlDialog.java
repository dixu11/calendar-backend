package szlicht.daniel.calendar.dialog.app_core;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

public class GroupMentoringHtmlDialog extends HtmlDialog {
    public GroupMentoringHtmlDialog(String email) {
        super(email);
    }

    @Override
    String getSubject() {
        return "Otwarte zapisy na grupowy mentoring!";
    }

    @Override
    String getTitle() {
        return getSubject();
    }

    @Override
    String getIntro() {
        return "Razem możemy więcej!";
    }

    @Override
    String getSections() {
        String whyList = asList(
                "Małe grupy (3-5 osób) pozwalają połączyć indywidualne podejście z solo mentoringu oraz korzyści z pracy zespołowej",
                "Moje warsztaty nie są jedynie tym samym programem z inną ilością osób, są specjalnie przygotowane aby wykorzystać do maksimum atuty pracy w zespole",
                "Programiści w IT nigdy nie pracują sami, muszą rozdzielić między siebie zadania, robić sobie code review oraz współpracować z klientem oraz biznesem",
                "Wspólnie jesteście w stanie zrobić projekt portfolio jakiego żadne z was nie mogłoby zrobić indywidualnie",
                "Współpraca nad jednym projektem w grupie oraz jego praktyczne zastosowanie będzie ogromnym atutem w oczach rekrutera",
                "Praca w zespole jest też dodatkową motywacją i okazją do poznania osób z podobnymi zainteresowaniami a nawet dla niektórych okazją do sprawdzenia się w roli lidera lub managera",
                "Zajęcia grupowe są tańsze niż mentoring indywidualny"
        );
        String why = section("Dlaczego warto uczyć się w małych grupach?", whyList);
        String howList = asList(
                "Spotykamy się raz w tygodniu na 1,5h w terminie który pasuje całej grupie",
                params.values().groupPricesOffer(),
                "Komunikujemy się na serwerze Discord"
        );
        String how = section("Jak to wygląda organizacyjnie?", howList);
        return why+how;
    }
}
