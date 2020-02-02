package transfarmer.soulweapons.weapon;

public enum SoulWeaponAttribute {
    ATTACK_SPEED(0),
    ATTACK_DAMAGE(1),
    CRITICAL(2),
    KNOCKBACK(3),
    EFFICIENCY(4);

    public final int index;

    SoulWeaponAttribute(final int index) {
        this.index = index;
    }

    public static SoulWeaponAttribute getAttribute(int index) {
        switch (index) {
            case 0:
                return ATTACK_SPEED;
            case 1:
                return ATTACK_DAMAGE;
            case 2:
                return CRITICAL;
            case 3:
                return KNOCKBACK;
            case 4:
                return EFFICIENCY;
            default:
                return null;
        }
    }

    public static String getName(int index) {
        return getAttribute(index).toString();
    }
}
