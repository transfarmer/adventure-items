package transfarmer.soulweapons;

public enum SoulWeaponAttribute {
    LEVEL(0),
    POINTS(1),
    SPECIAL(2),
    MAX_SPECIAL(3),
    ATTACK_DAMAGE(4),
    ATTACK_SPEED(5),
    CRITICAL(6),
    KNOCKBACK(7),
    EFFICIENCY(8);

    public final int index;

    SoulWeaponAttribute(final int index) {
        this.index = index;
    }
}
