package transfarmer.soulboundarmory.statistics.weapon;

import transfarmer.soulboundarmory.statistics.SoulAttribute;

public class SoulWeaponAttribute extends SoulAttribute {
    public static final SoulAttribute EFFICIENCY_ATTRIBUTE = new SoulWeaponAttribute(0, "efficiencyAttribute", 0.5F, 0.75F, 1);
    public static final SoulAttribute ATTACK_SPEED = new SoulWeaponAttribute(1, "attackSpeed", 0.025F, 0.05F, 0.1F);
    public static final SoulAttribute ATTACK_DAMAGE = new SoulWeaponAttribute(2, "attackDamage", 0.5F, 0.35F, 0.25F);
    public static final SoulAttribute CRITICAL = new SoulWeaponAttribute(3, "critical", 2, 2.5F, 4);
    public static final SoulAttribute KNOCKBACK_ATTRIBUTE = new SoulWeaponAttribute(4, "knockbackAttribute", 1, 0.5F, 0.25F);

    protected static final SoulAttribute[] ATTRIBUTES = {
            EFFICIENCY_ATTRIBUTE,
            ATTACK_SPEED,
            ATTACK_DAMAGE,
            CRITICAL,
            KNOCKBACK_ATTRIBUTE
    };

    protected SoulWeaponAttribute(final int index, final String name, final float... increase) {
        super(index, name, increase);
    }

    public static SoulAttribute get(final int index) {
        return ATTRIBUTES[index];
    }

    public static int getAmount() {
        return ATTRIBUTES.length;
    }
}
