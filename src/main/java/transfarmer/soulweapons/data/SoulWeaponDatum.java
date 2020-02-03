package transfarmer.soulweapons.data;

public enum SoulWeaponDatum {
    XP(0),
    LEVEL(1),
    POINTS(2),
    ENCHANTMENT_POINTS(3),
    SKILLS(4);

    public static final SoulWeaponDatum[] data = {XP, LEVEL, POINTS, ENCHANTMENT_POINTS, SKILLS};

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
}
