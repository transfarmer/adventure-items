package transfarmer.soulweapons.data;

public enum SoulWeaponAttribute {
    ATTACK_SPEED(0, 0.2F),
    ATTACK_DAMAGE(1, 1),
    CRITICAL(2, 3),
    KNOCKBACK_ATTRIBUTE(3, 1),
    EFFICIENCY(4, 0.5F);

    private static final SoulWeaponAttribute[] attributes = {ATTACK_SPEED, ATTACK_DAMAGE, CRITICAL, KNOCKBACK_ATTRIBUTE, EFFICIENCY};

    public final int index;
    public final float increase;

    SoulWeaponAttribute(final int index, final float increase) {
        this.index = index;
        this.increase = increase;
    }

    public static SoulWeaponAttribute getAttribute(int index) {
        return attributes[index];
    }

    public static String getName(int index) {
        return getAttribute(index).toString();
    }

    public static SoulWeaponAttribute[] getAttributes() {
        return attributes;
    }
}
