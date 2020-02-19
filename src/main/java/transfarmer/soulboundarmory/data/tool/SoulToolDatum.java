package transfarmer.soulboundarmory.data.tool;

import transfarmer.soulboundarmory.data.IDatum;

public enum SoulToolDatum implements IDatum {
    XP(0),
    LEVEL(1),
    ATTRIBUTE_POINTS(2),
    SPENT_ATTRIBUTE_POINTS(3),
    ENCHANTMENT_POINTS(4),
    SPENT_ENCHANTMENT_POINTS(5),
    SKILLS(6);

    private final int index;

    private static final SoulToolDatum[] DATA = {XP, LEVEL, ATTRIBUTE_POINTS, SPENT_ATTRIBUTE_POINTS, ENCHANTMENT_POINTS, SPENT_ENCHANTMENT_POINTS, SKILLS};

    SoulToolDatum(final int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    public static SoulToolDatum[] getData() {
        return DATA;
    }

    public static SoulToolDatum getDatum(final int index) {
        return DATA[index];
    }

    public static String getName(final int index) {
        return getDatum(index).toString().toLowerCase();
    }
}
