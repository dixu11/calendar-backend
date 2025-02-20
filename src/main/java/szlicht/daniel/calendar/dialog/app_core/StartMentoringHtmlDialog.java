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
        return "Miło, że napisałeś, już nie mogę się doczekać jak zaczniemy razem pracować nad fajnymi projektami \uD83D\uDE00";
    }

    @Override
    String getSections() {
        String howItWorks = section(
                "Jak to działa?",
                """
                        Piszesz właśnie z zaprogramowanym przeze mnie asystentem mailowym który wspiera moich klientów i pozwala umawiać terminy. 
                        Cały jego kod napisałem sam i jest publiczny na github. Odpowiadaj klikając interesującą Cię opcję z listy poniżej. 
                        Wyskoczy szablon maila który możesz od razu wysłać lub dodać coś jeszcze. Po ok minucie powinieneś otrzymać odpowiedź.
                        """
        );
        String whyMeList = asList(
                "Prawdopodobnie nikt w polsce nie ma tak dużego doświadczenia w nauczaniu programowania, od ponad " +
                        "5 lat uczę w licznych szkołach i indywidualnie, przeprowadziłem ponad 6000 godzin zajęć i " +
                        "pomogłem znaleźć pracę ponad setce osób",
                "Stosuję “just in time learning”. Budujemy z kodu fantastyczne projekty a po teorię sięgamy wtedy gdy jest nam do czegoś potrzebna",
                "Stosuję “mastery learning” - nie przechodzimy do trudniejszych zagadnień dopóki uczeń w 100% nie rozumie i nie umie wykorzystać tych podstawowych",
                "Pielęgnuję ciekawość i wewnętrzną motywację - robimy projekty o tematyce która naprawdę interesuje ucznia i pielęgnujemy przyjemność z pisania kodu i satysfakcję z jakości naszych programów i wzrostu umiejętności",
                "Jestem mentorem a nie trenerem, nie uczę jedynie składni języków programowania a kompleksowo pomagam zbudować stabilną karierę opartą o pasję do pisania kodu",
                "Pielęgnuję nastawienie do ciągłego rozwoju, zrozumienie potrzeb klienta i dostarczanie najwyższej jakości oprogramowania co jest natychmiast dostrzegane i doceniane przez rekruterów",
                "Jestem absolutnie szczery co do stanu branży, i brzydzi mnie sprzedawanie pustych obietnic nieświadomym klientom",
                "Szczerze zależy mi na sukcesie każdego z moich uczniów i na budowaniu relacji opartych na zaufaniu"
        );
        String howToStartList = asList(
                "Uczę się absolutnie od zera, szukam kompleksowego wsparcia i pomocy przy wyznaczeniu ścieżki.",
                "Już coś ogarniam i piszę własne projekty. Interesuje mnie któraś z wymienionych technologii: java, python, android, gamedev, kotlin",
                "Interesuje mnie python ale jestem już dość zaawansowany i chcę nauczyć się data sience / machine learningu",
                "Interesują mnie inne, nie wymienione technologie np. C++, C#, React/Angular, devops, bazy danych",
                "Mam bardzo konkretny projekt który chcę wprawić w życie, nie ważne jakich technologii będę musiał nauczyć się po drodze",
                "Mam problem ze znalezieniem pracy a już jestem na nią gotowy, potrzebuję pomocy przy ocenie mojego projektu portfolio, poprawie CV, zaplanowaniu strategii szukania pracy i kolejnych kroków oraz próbna rozmowa rekrutacyjna",
                "Czuję że tracę zapał do programowania, jestem pełen obaw, nie wiem jak zorganizować naukę, boję się czy to ma w ogóle sens, mam wrażenie że bardziej niż techniczne potrzebne mi wsparcie strategiczne i emocjonalne",
                "Mam bardzo ograniczony budżet i szukam najtańszej opcji",
                "Szukam czegoś innego"
        );
        String whyMe = section("Czym wyróżnia się mój mentoring?", whyMeList);
        String howToStart = section("Jak zacząć? Kliknij wybraną opcję i wyślij email!", howToStartList);
        return howItWorks + whyMe + howToStart;
    }

}
