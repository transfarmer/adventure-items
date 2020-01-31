package transfarmer.soulweapons;

public enum SoulWeaponAttribute {
    XP(0, "xp"),
    LEVEL(1, "level"),
    POINTS(2, "points"),
    SPECIAL(3, "special"),
    ATTACK_SPEED(4, "attackSpeed"),
    ATTACK_DAMAGE(5, "attackDamage"),
    CRITICAL(6, "critical"),
    KNOCKBACK(7, "knockback"),
    EFFICIENCY(8, "efficiency");

    public final int index;
    public final String name;

    SoulWeaponAttribute(final int index, final String name) {
        this.index = index;
        this.name = name;
    }

    public static SoulWeaponAttribute getAttribute(int index) {
        switch (index) {
            case 0:
                return XP;
            case 1:
                return LEVEL;
            case 2:
                return POINTS;
            case 3:
                return SPECIAL;
            case 4:
                return ATTACK_SPEED;
            case 5:
                return ATTACK_DAMAGE;
            case 6:
                return CRITICAL;
            case 7:
                return KNOCKBACK;
            case 8:
                return EFFICIENCY;
            default:
                return null;
        }
    }

    public static String getName(int index) {
        switch (index) {
            case 0:
                return "xp";
            case 1:
                return "level";
            case 2:
                return "points";
            case 3:
                return "special";
            case 4:
                return "attackDamage";
            case 5:
                return "attackSpeed";
            case 6:
                return "critical";
            case 7:
                return "knockback";
            case 8:
                return "efficiency";
            default:
                return null;
        }
    }
}
