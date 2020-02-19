package transfarmer.soulboundarmory.data.weapon;

import transfarmer.soulboundarmory.data.IAttribute;
import transfarmer.soulboundarmory.data.IType;

public enum SoulWeaponAttribute implements IAttribute {
    ATTACK_SPEED(0, 0.025F, 0.05F, 0.1F),
    ATTACK_DAMAGE(1, 0.5F, 0.35F, 0.25F),
    CRITICAL(2, 2, 2.5F, 4),
    KNOCKBACK_ATTRIBUTE(3, 1, 0.5F, 0.25F),
    EFFICIENCY(4, 0.5F, 0.75F, 1);

    private static final SoulWeaponAttribute[] ATTRIBUTES = {ATTACK_SPEED, ATTACK_DAMAGE, CRITICAL, KNOCKBACK_ATTRIBUTE, EFFICIENCY};

    public final int index;
    private final float[] increases;

    SoulWeaponAttribute(final int index, final float ... increases) {
        this.index = index;
        this.increases = increases;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public float getIncrease(final IType type) {
        return this.increases[type.getIndex()];
    }

    public static SoulWeaponAttribute[] getAttributes() {
        return ATTRIBUTES;
    }

    public static SoulWeaponAttribute getAttribute(final int index) {
        return ATTRIBUTES[index];
    }

    public static String getName(final int index) {
        return getAttribute(index).toString();
    }
}
