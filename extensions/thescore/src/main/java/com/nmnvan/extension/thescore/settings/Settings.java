package com.nmnvan.extension.thescore.settings;

import com.nmnvan.extension.thescore.patches.BottomNavPatch;
import com.nmnvan.extension.thescore.patches.GameScreenPatch;
import com.nmnvan.extension.thescore.patches.HeaderTabsPatch;
import com.nmnvan.extension.thescore.patches.LeaguesScreenPatch;
import com.nmnvan.extension.thescore.patches.ScorecastPatch;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Settings {

    private Settings() {
    }

    /**
     * A titled group of settings on the settings screen.
     */
    public static final class Section {
        public final String title;
        public final String note;
        public final List<BooleanSetting> settings;

        Section(String title, String note, BooleanSetting... settings) {
            this.title = title;
            this.note = note;
            this.settings = Collections.unmodifiableList(Arrays.asList(settings));
        }
    }

    private static BooleanSetting.Availability bottomNav() {
        return BottomNavPatch::isPatchIncluded;
    }

    private static BooleanSetting.Availability headerTabs() {
        return HeaderTabsPatch::isPatchIncluded;
    }

    private static BooleanSetting.Availability scorecast() {
        return ScorecastPatch::isPatchIncluded;
    }

    // region Bottom navigation.

    public static final BooleanSetting HIDE_NAV_NEWS = new BooleanSetting("hide_nav_news", false,
            "Hide News", null, bottomNav());
    public static final BooleanSetting HIDE_NAV_SCORES = new BooleanSetting("hide_nav_scores", false,
            "Hide Scores", null, bottomNav());
    public static final BooleanSetting HIDE_NAV_FAVORITES = new BooleanSetting("hide_nav_favorites", false,
            "Hide Favorites", null, bottomNav());
    public static final BooleanSetting HIDE_NAV_BET = new BooleanSetting("hide_nav_bet", false,
            "Hide Bet", "Also hides Videos, which uses the same slot when Bet is off.", bottomNav());

    // endregion

    // region Game screen.

    public static final BooleanSetting KEEP_CLASSIC_GAME_SCREEN = new BooleanSetting(
            "keep_classic_game_screen", true,
            "Keep classic game screen",
            "theScore can switch game screens to a redesigned layout remotely. Keeping the classic layout "
                    + "makes sure the game tab and Scorecast settings below keep working.",
            GameScreenPatch::isPatchIncluded);

    // endregion

    // region Game screen tabs.

    public static final BooleanSetting HIDE_GAME_TAB_FEED = new BooleanSetting("hide_game_tab_feed", false,
            "Hide Feed", "Also the News tab on golf tournaments.", headerTabs());
    public static final BooleanSetting HIDE_GAME_TAB_CHAT = new BooleanSetting("hide_game_tab_chat", false,
            "Hide Chat", null, headerTabs());
    public static final BooleanSetting HIDE_GAME_TAB_SCORECAST = new BooleanSetting("hide_game_tab_scorecast", false,
            "Hide Scorecast", "Also the Matchup tab before and after games. The next tab opens instead.", headerTabs());
    public static final BooleanSetting HIDE_GAME_TAB_LINEUPS = new BooleanSetting("hide_game_tab_lineups", false,
            "Hide Lineups", "Soccer.", headerTabs());
    public static final BooleanSetting HIDE_GAME_TAB_STATS = new BooleanSetting("hide_game_tab_stats", false,
            "Hide Stats", null, headerTabs());
    public static final BooleanSetting HIDE_GAME_TAB_BETTING = new BooleanSetting("hide_game_tab_betting", false,
            "Hide Betting", "Also Betting Info.", headerTabs());
    public static final BooleanSetting HIDE_GAME_TAB_PLAYS = new BooleanSetting("hide_game_tab_plays", false,
            "Hide Plays", "Also Drives, Pitch-By-Pitch and MbM.", headerTabs());
    public static final BooleanSetting HIDE_GAME_TAB_FIGHTS = new BooleanSetting("hide_game_tab_fights", false,
            "Hide Fights", "MMA.", headerTabs());
    public static final BooleanSetting HIDE_GAME_TAB_RESULTS = new BooleanSetting("hide_game_tab_results", false,
            "Hide Results", "Auto racing.", headerTabs());
    public static final BooleanSetting HIDE_GAME_TAB_QUALIFYING = new BooleanSetting("hide_game_tab_qualifying", false,
            "Hide Qualifying", "Auto racing.", headerTabs());
    public static final BooleanSetting HIDE_GAME_TAB_LEADERBOARD = new BooleanSetting("hide_game_tab_leaderboard", false,
            "Hide Field / Leaders / Matches", "Golf.", headerTabs());
    public static final BooleanSetting HIDE_GAME_TAB_COURSE = new BooleanSetting("hide_game_tab_course", false,
            "Hide Course", "Golf.", headerTabs());
    public static final BooleanSetting HIDE_GAME_TAB_INFO = new BooleanSetting("hide_game_tab_info", false,
            "Hide Info", "Golf.", headerTabs());

    // endregion

    // region League screen tabs.

    public static final BooleanSetting HIDE_LEAGUE_TAB_SCORES = new BooleanSetting("hide_league_tab_scores", false,
            "Hide Scores", null, headerTabs());
    public static final BooleanSetting HIDE_LEAGUE_TAB_NEWS = new BooleanSetting("hide_league_tab_news", false,
            "Hide News", null, headerTabs());
    public static final BooleanSetting HIDE_LEAGUE_TAB_CHAT = new BooleanSetting("hide_league_tab_chat", false,
            "Hide Chat", null, headerTabs());
    public static final BooleanSetting HIDE_LEAGUE_TAB_STANDINGS = new BooleanSetting("hide_league_tab_standings", false,
            "Hide Standings", null, headerTabs());
    public static final BooleanSetting HIDE_LEAGUE_TAB_LEADERS = new BooleanSetting("hide_league_tab_leaders", false,
            "Hide Leaders", null, headerTabs());
    public static final BooleanSetting HIDE_LEAGUE_TAB_BRACKET = new BooleanSetting("hide_league_tab_bracket", false,
            "Hide Bracket", null, headerTabs());
    public static final BooleanSetting HIDE_LEAGUE_TAB_OTHER = new BooleanSetting("hide_league_tab_other", false,
            "Hide other tabs", "Any other tabs theScore adds to a league, such as Teams or Players.", headerTabs());

    // endregion

    // region Leagues tab.

    public static final BooleanSetting HIDE_LEAGUE_CHIPS = new BooleanSetting("hide_league_chips", false,
            "Hide league chips",
            "Hides the row of recently viewed leagues (NCAA Football, MLB, NFL…) at the top of the Leagues tab.",
            LeaguesScreenPatch::isPatchIncluded);
    public static final BooleanSetting HIDE_SPOTLIGHT = new BooleanSetting("hide_spotlight", false,
            "Hide Spotlight", "Hides the Spotlight section on the Leagues tab.",
            LeaguesScreenPatch::isPatchIncluded);

    // endregion

    // region Scorecast blocks. The setting key ends with the app's block name.

    private static BooleanSetting block(String blockName, String title, String summary) {
        return new BooleanSetting("hide_scorecast_" + blockName.toLowerCase(), false, title, summary, scorecast());
    }

    public static final BooleanSetting HIDE_SCORECAST_GAME_NEWS = block("GAME_NEWS", "Hide Game News", null);
    public static final BooleanSetting HIDE_SCORECAST_ODDS = block("LIVE_ODDS", "Hide Odds", "Including Go to SBET and See All Odds.");
    public static final BooleanSetting HIDE_SCORECAST_BETTING_POLL = block("BETTING_POLL", "Hide betting poll", null);
    public static final BooleanSetting HIDE_SCORECAST_BET_HISTORY = block("BET_HISTORY", "Hide your bets", "Bets you placed on this game.");
    public static final BooleanSetting HIDE_SCORECAST_PROP_ANALYSIS = block("PROP_ANALYSIS", "Hide prop analysis", null);
    public static final BooleanSetting HIDE_SCORECAST_LIVE_STREAM_CTA = block("LIVE_STREAM_CTA", "Hide watch live prompt", null);
    public static final BooleanSetting HIDE_SCORECAST_CLIPS = block("CLIPS", "Hide clips", null);
    public static final BooleanSetting HIDE_SCORECAST_PREVIEW_RECAP = block("MATCHUP_PREVIEW_RECAP", "Hide preview / recap", null);
    public static final BooleanSetting HIDE_SCORECAST_LAST_PLAY = block("LAST_PLAY", "Hide last play", null);
    public static final BooleanSetting HIDE_SCORECAST_SCORING_SUMMARY = block("SCORING_SUMMARY", "Hide scoring summary", null);
    public static final BooleanSetting HIDE_SCORECAST_TIMELINE = block("TIMELINE", "Hide timeline", null);
    public static final BooleanSetting HIDE_SCORECAST_BOXSCORE = block("BOXSCORE", "Hide box score", null);
    public static final BooleanSetting HIDE_SCORECAST_LINE_SCORE = block("LINE_SCORE", "Hide line score", null);
    public static final BooleanSetting HIDE_SCORECAST_TEAM_STATS = block("TEAM_STATS", "Hide team stats", null);
    public static final BooleanSetting HIDE_SCORECAST_TEAM_RECORDS = block("TEAM_RECORDS", "Hide team records", null);
    public static final BooleanSetting HIDE_SCORECAST_TEAM_FORM = block("TEAM_FORM", "Hide team form", null);
    public static final BooleanSetting HIDE_SCORECAST_PLAYER_COMPARISON = block("PLAYER_COMPARISON", "Hide player comparison", null);
    public static final BooleanSetting HIDE_SCORECAST_PLAYER_PROFILES = block("PLAYER_PROFILES", "Hide player profiles", null);
    public static final BooleanSetting HIDE_SCORECAST_STARTING_LINEUP = block("STARTING_LINEUP", "Hide starting lineups", null);
    public static final BooleanSetting HIDE_SCORECAST_PLAYER_INJURIES = block("PLAYER_INJURIES", "Hide injuries", null);
    public static final BooleanSetting HIDE_SCORECAST_PAST_MEETINGS = block("PAST_MEETINGS", "Hide past meetings",
            "Also head to head and last 5 / 10 meetings.");
    public static final BooleanSetting HIDE_SCORECAST_GAME_DETAILS = block("GAME_DETAILS", "Hide game details",
            "Also match and tournament details.");
    public static final BooleanSetting HIDE_SCORECAST_THREE_STARS = block("THREE_STARS", "Hide three stars", "Hockey.");
    public static final BooleanSetting HIDE_SCORECAST_SHOTS_ON_GOAL = block("SHOTS_ON_GOAL", "Hide shots on goal", "Hockey.");
    public static final BooleanSetting HIDE_SCORECAST_PENALTIES = block("MATCHUP_PENALTIES", "Hide penalties",
            "Also penalty shootouts.");
    public static final BooleanSetting HIDE_SCORECAST_LIVE_TABLE = block("LIVE_TABLE_PREVIEW", "Hide live table", "Soccer.");
    public static final BooleanSetting HIDE_SCORECAST_MATCH_SUMMARY = block("MATCH_SUMMARY", "Hide match summary", null);

    // endregion

    /**
     * All settings by section, in the order shown on the settings screen.
     */
    public static final List<Section> SECTIONS = Collections.unmodifiableList(Arrays.asList(
            new Section("Bottom navigation",
                    "Leagues is always shown. If the tab the app would open on is hidden, it opens on Leagues.",
                    HIDE_NAV_NEWS, HIDE_NAV_SCORES, HIDE_NAV_FAVORITES, HIDE_NAV_BET),
            new Section("Game screen", null,
                    KEEP_CLASSIC_GAME_SCREEN),
            new Section("Game screen tabs",
                    "If every tab of a game would be hidden, all tabs are shown.",
                    HIDE_GAME_TAB_FEED, HIDE_GAME_TAB_CHAT, HIDE_GAME_TAB_SCORECAST, HIDE_GAME_TAB_LINEUPS,
                    HIDE_GAME_TAB_STATS, HIDE_GAME_TAB_BETTING, HIDE_GAME_TAB_PLAYS, HIDE_GAME_TAB_FIGHTS,
                    HIDE_GAME_TAB_RESULTS, HIDE_GAME_TAB_QUALIFYING, HIDE_GAME_TAB_LEADERBOARD,
                    HIDE_GAME_TAB_COURSE, HIDE_GAME_TAB_INFO),
            new Section("League screen tabs",
                    "If every tab of a league would be hidden, all tabs are shown.",
                    HIDE_LEAGUE_TAB_SCORES, HIDE_LEAGUE_TAB_NEWS, HIDE_LEAGUE_TAB_CHAT, HIDE_LEAGUE_TAB_STANDINGS,
                    HIDE_LEAGUE_TAB_LEADERS, HIDE_LEAGUE_TAB_BRACKET, HIDE_LEAGUE_TAB_OTHER),
            new Section("Leagues tab", null,
                    HIDE_LEAGUE_CHIPS, HIDE_SPOTLIGHT),
            new Section("Scorecast", "Blocks on a game's Scorecast tab. Not every block appears for every sport.",
                    HIDE_SCORECAST_GAME_NEWS, HIDE_SCORECAST_ODDS, HIDE_SCORECAST_BETTING_POLL,
                    HIDE_SCORECAST_BET_HISTORY, HIDE_SCORECAST_PROP_ANALYSIS, HIDE_SCORECAST_LIVE_STREAM_CTA,
                    HIDE_SCORECAST_CLIPS, HIDE_SCORECAST_PREVIEW_RECAP, HIDE_SCORECAST_LAST_PLAY,
                    HIDE_SCORECAST_SCORING_SUMMARY, HIDE_SCORECAST_TIMELINE, HIDE_SCORECAST_BOXSCORE,
                    HIDE_SCORECAST_LINE_SCORE, HIDE_SCORECAST_TEAM_STATS, HIDE_SCORECAST_TEAM_RECORDS,
                    HIDE_SCORECAST_TEAM_FORM, HIDE_SCORECAST_PLAYER_COMPARISON, HIDE_SCORECAST_PLAYER_PROFILES,
                    HIDE_SCORECAST_STARTING_LINEUP, HIDE_SCORECAST_PLAYER_INJURIES, HIDE_SCORECAST_PAST_MEETINGS,
                    HIDE_SCORECAST_GAME_DETAILS, HIDE_SCORECAST_THREE_STARS, HIDE_SCORECAST_SHOTS_ON_GOAL,
                    HIDE_SCORECAST_PENALTIES, HIDE_SCORECAST_LIVE_TABLE, HIDE_SCORECAST_MATCH_SUMMARY)
    ));
}
