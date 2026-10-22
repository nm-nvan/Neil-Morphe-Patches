package com.nmnvan.extension.thescore.settings;

import com.nmnvan.extension.thescore.patches.LeaguesScreenPatch;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Settings {

    private Settings() {
    }

    // Leagues screen.
    public static final BooleanSetting HIDE_LEAGUE_CHIPS = new BooleanSetting(
            "hide_league_chips",
            false,
            "Hide league chips",
            "Hides the row of recently viewed leagues (NCAA Football, MLB, NFL…) at the top of the Leagues tab.",
            LeaguesScreenPatch::isPatchIncluded
    );

    /**
     * All settings, in the order shown on the settings screen.
     */
    public static final List<BooleanSetting> ALL = Collections.unmodifiableList(Arrays.asList(
            HIDE_LEAGUE_CHIPS
    ));
}
