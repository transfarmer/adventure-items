package transfarmer.soulboundarmory.data.weapon;

import transfarmer.soulboundarmory.data.IDatum;

public enum SoulWeaponDatum implements IDatum {
    XP(0),
    LEVEL(1),
    ATTRIBUTE_POINTS(2),
    ENCHANTMENT_POINTS(3),
    SPENT_ATTRIBUTE_POINTS(4),
    SPENT_ENCHANTMENT_POINTS(5),
    SKILLS(6);

    private static final SoulWeaponDatum[] DATA = {XP, LEVEL, ATTRIBUTE_POINTS, ENCHANTMENT_POINTS, SPENT_ATTRIBUTE_POINTS, SPENT_ENCHANTMENT_POINTS, SKILLS};

    private final int index;

    SoulWeaponDatum(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    public static int getAmount() {
        return DATA.length;
    }

    public static SoulWeaponDatum getDatum(int index) {
        return DATA[index];
    }

    public static String getName(int index) {
        return getDatum(index).toString();
    }

    public static SoulWeaponDatum[] getData() {
        return DATA;
    }
}
