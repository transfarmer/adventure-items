package transfarmer.soularsenal.data.weapon;

public enum SoulWeaponAttribute {
    ATTACK_SPEED(0, 0.025F, 0.05F, 0.1F),
    ATTACK_DAMAGE(1, 0.5F, 0.35F, 0.25F),
    CRITICAL(2, 2, 2.5F, 4),
    KNOCKBACK_ATTRIBUTE(3, 1, 0.5F, 0.25F),
    EFFICIENCY(4, 0.5F, 0.75F, 1);

    private static final SoulWeaponAttribute[] attributes = {ATTACK_SPEED, ATTACK_DAMAGE, CRITICAL, KNOCKBACK_ATTRIBUTE, EFFICIENCY};

    public final int index;
    private final float greatswordIncrease;
    private final float swordIncrease;
    private final float daggerIncrease;

    SoulWeaponAttribute(final int index, final float greatswordIncrease, final float swordIncrease, final float daggerIncrease) {
        this.index = index;
        this.greatswordIncrease = greatswordIncrease;
        this.swordIncrease = swordIncrease;
        this.daggerIncrease = daggerIncrease;
    }

    public float getIncrease(final SoulWeaponType type) {
        switch (type) {
            case GREATSWORD:
                return this.greatswordIncrease;
            case SWORD:
                return this.swordIncrease;
            case DAGGER:
                return this.daggerIncrease;
        }

        return 0;
    }

    public static SoulWeaponAttribute[] getAttributes() {
        return attributes;
    }

    public static SoulWeaponAttribute getAttribute(final int index) {
        return attributes[index];
    }

    public static String getName(final int index) {
        return getAttribute(index).toString();
    }
}
