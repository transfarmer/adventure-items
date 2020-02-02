package transfarmer.soulweapons.weapon;

public enum SoulWeaponDatum {
    XP(0),
    LEVEL(1),
    POINTS(2),
    SKILL(3);

    public final int index;

    SoulWeaponDatum(int index) {
        this.index = index;
    }

    public static SoulWeaponDatum getDatum(int index) {
        switch (index) {
            case 0:
                return XP;
            case 1:
                return LEVEL;
            case 2:
                return POINTS;
            case 3:
                return SKILL;
            default:
                return null;
        }
    }

    public static String getName(int index) {
        return getDatum(index).toString();
    }
}
