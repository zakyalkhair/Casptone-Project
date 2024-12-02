package OOP;

/**
 * The enum OOP.Seed defines:
 * 1. Player: CROSS or NOUGHT.
 * 2. OOP.Cell Content: CROSS, NOUGHT, or NO_SEED.
 * Each OOP.Seed includes a display icon (text).
 */
public enum Seed {
    CROSS("X"),
    NOUGHT("O"),
    NO_SEED(" ");

    // Private variable to store the icon for each OOP.Seed
    private final String icon;

    // Constructor (must be private for enums)
    private Seed(String icon) {
        this.icon = icon;
    }

    // Public getter to retrieve the icon
    public String getIcon() {
        return icon;
    }
}
