package transfarmer.soulweapons;

public enum SoulWeaponAttribute {
    LEVEL(0),
    POINTS(1),
    SPECIAL(2),
    ATTACK_DAMAGE(3),
    ATTACK_SPEED(4),
    CRITICAL(5),
    KNOCKBACK(6),
    EFFICIENCY(7);

    public final int index;

    SoulWeaponAttribute(final int index) {
        this.index = index;
    }
}
