package transfarmer.soularsenal.data.tool;

public enum SoulToolDatum {
    XP(0),
    LEVEL(1),
    ATTRIBUTE_POINTS(2),
    SPENT_ATTRIBUTE_POINTS(3),
    ENCHANTMENT_POINTS(4),
    SPENT_ENCHANTMENT_POINTS(5),
    SKILLS(6);

    public final int index;

    private static final SoulToolDatum[] DATA = {XP, LEVEL, ATTRIBUTE_POINTS, SPENT_ATTRIBUTE_POINTS,
            ENCHANTMENT_POINTS, SPENT_ENCHANTMENT_POINTS, SKILLS};

    SoulToolDatum(final int index) {
        this.index = index;
    }

    public static SoulToolDatum getDatum(final int index) {
        return DATA[index];
    }

    public static String getName(final int index) {
        return getDatum(index).toString().toLowerCase();
    }
}
