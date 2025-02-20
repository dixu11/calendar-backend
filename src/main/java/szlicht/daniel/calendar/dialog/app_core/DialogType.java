package szlicht.daniel.calendar.dialog.app_core;

import java.util.Arrays;
import java.util.Optional;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

enum DialogType {
PROPOSITIONS, ARRANGE, OFFER, OTHER;

    String getKeyword() {
       return switch (this) {
           case PROPOSITIONS -> params.keywords().propositions();
           case ARRANGE -> params.keywords().arrange();
           case OFFER -> params.keywords().offer();
           case OTHER -> "NO KEYWORD FOR OTHER DIALOG TYPE";
        };
    }

    static DialogType getByKeyword(String content) {
        return Arrays.stream(DialogType.values())
                .filter(dialogType -> content.toLowerCase().contains(dialogType.getKeyword()))
                .findFirst()
                .orElse(OTHER);
    }
}
