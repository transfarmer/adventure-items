package transfarmer.soulweapons.data;

public enum SoulWeaponDatum {
    XP(0),
    LEVEL(1),
    ATTRIBUTE_POINTS(2),
    ENCHANTMENT_POINTS(3),
    SPENT_ATTRIBUTE_POINTS(4),
    SPENT_ENCHANTMENT_POINTS(5),
    SKILLS(6);

    private static final SoulWeaponDatum[] data = {XP, LEVEL, ATTRIBUTE_POINTS, ENCHANTMENT_POINTS, SPENT_ATTRIBUTE_POINTS, SPENT_ENCHANTMENT_POINTS, SKILLS};

    public final int index;

    SoulWeaponDatum(int index) {
        this.index = index;
    }

    public static SoulWeaponDatum getDatum(int index) {
        return data[index];
    }

    public static String getName(int index) {
        return getDatum(index).toString();
    }

    public static SoulWeaponDatum[] getData() {
        return data;
    }
}
