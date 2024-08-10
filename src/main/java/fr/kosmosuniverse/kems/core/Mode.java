package fr.kosmosuniverse.kems.core;

/**
 * @author KosmosUniverse
 */
public enum Mode {
    NO_MODE("No Mode"),
    MOST_POINTS_DURATION("Most Points Duration"),
    FIRST_TO_LIMIT("First To Limit"),
    FIRST_TO_RANK("First To Rank");

    private final String displayString;

    Mode(String displayString) {
        this.displayString = displayString;
    }

    public String getdisplayString() {
        return displayString;
    }
}
