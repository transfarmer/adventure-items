package transfarmer.soulweapons.data;

public enum SoulWeaponAttribute {
    ATTACK_SPEED(0),
    ATTACK_DAMAGE(1),
    CRITICAL(2),
    KNOCKBACK_ATTRIBUTE(3),
    EFFICIENCY(4);

    private static final SoulWeaponAttribute[] attributes = {ATTACK_SPEED, ATTACK_DAMAGE, CRITICAL, KNOCKBACK_ATTRIBUTE, EFFICIENCY};

    public final int index;

    SoulWeaponAttribute(final int index) {
        this.index = index;
    }

    public static SoulWeaponAttribute getAttribute(int index) {
        return attributes[index];
    }

    public static String getName(int index) {
        return getAttribute(index).toString();
    }
}
