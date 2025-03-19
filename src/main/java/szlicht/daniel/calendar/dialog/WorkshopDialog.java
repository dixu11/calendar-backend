package szlicht.daniel.calendar.dialog;

import szlicht.daniel.calendar.common.java.LocalDateUtils;
import szlicht.daniel.calendar.workshop.Workshop;

import java.util.List;

import static szlicht.daniel.calendar.common.mail.MailUtils.mailto;
import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

public class WorkshopDialog extends Dialog {

    private List<Workshop> workshops;

    public WorkshopDialog(String email, List<Workshop> workshops) {
        super(email);
        this.workshops = workshops;
    }

    @Override
    public String getSubject() {
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

        String openWorkshopsList = "";
        for (Workshop workshop : workshops) {
            openWorkshopsList += toMailtoLi(workshop);
        }
        String openWorkshops = section("Otwarte grupy: (kliknij żeby się dopisać)",openWorkshopsList);
        return why+how+openWorkshops;
    }

    private String toMailtoLi(Workshop workshop) {
        String label = tag("li", workshop.getTitle() + " start: " + LocalDateUtils.simpleDate(workshop.getStart()));
        return mailto(params.keywords().workshopMentoringApply() + workshop.getId(),
                "Chcę dołączyć do nowej grupy na warsztaty z programowania",label , getEmail());
    }
}
