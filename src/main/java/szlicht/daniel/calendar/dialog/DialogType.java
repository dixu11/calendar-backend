package szlicht.daniel.calendar.dialog;

import java.util.Arrays;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

enum DialogType {
    SOLO_MENTORING_OFFER,PROPOSITIONS, ARRANGE, OFFER,GROUP_MENTORING_OFFER, OTHER;

    String getKeyword() {
       return switch (this) {
           case SOLO_MENTORING_OFFER -> params.keywords().soloMentoring();
           case GROUP_MENTORING_OFFER -> params.keywords().groupMentoring();
           case PROPOSITIONS -> params.keywords().propositions();
           case ARRANGE -> params.keywords().arrange();
           case OFFER -> params.keywords().offer();
           case OTHER -> "Mam inną sytuację, proszę o indywidualną odpowiedź";
        };
    }

    static DialogType getByKeyword(String content) {
        return Arrays.stream(DialogType.values())
                .filter(dialogType -> content.toLowerCase().contains(dialogType.getKeyword()))
                .findFirst()
                .orElse(OTHER);
    }
}
