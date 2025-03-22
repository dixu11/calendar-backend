package szlicht.daniel.calendar.dialog;

import java.util.Arrays;

enum ScenarioType {
    MENTORING("mentoring"),
    SOLO_MENTORING_OFFER("indywidualne lekcje"),
    PROPOSITIONS("terminy"),
    ARRANGE("spotkanie"),
    GROUP_MENTORING_OFFER("grupowe lekcje"),
    OTHER("??");

    private String keyword;

    ScenarioType(String keyword) {
        this.keyword = keyword;
    }

    String getKeyword() {
      /* return switch (this) {
           case SOLO_MENTORING_OFFER -> params.keywords().soloMentoring();
           case GROUP_MENTORING_OFFER -> params.keywords().groupMentoring();
           case PROPOSITIONS -> params.keywords().propositions();
           case ARRANGE -> params.keywords().arrange();
           case MENTORING -> params.keywords().offer();
           case OTHER -> "Mam inną sytuację, proszę o indywidualną odpowiedź";
        };*/
        return keyword;
    }

    static ScenarioType getByKeyword(String content) {
        return Arrays.stream(ScenarioType.values())
                .filter(dialogType -> content.toLowerCase().contains(dialogType.getKeyword()))
                .findFirst()
                .orElse(OTHER);
    }
}
