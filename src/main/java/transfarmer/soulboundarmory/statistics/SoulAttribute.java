package transfarmer.soulboundarmory.statistics;

import transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType;

public class SoulAttribute extends Statistic {
    public static final SoulAttribute EFFICIENCY_ATTRIBUTE = new SoulAttribute(0);

    public static final SoulAttribute REACH_DISTANCE = new SoulAttribute(1);
    public static final SoulAttribute HARVEST_LEVEL = new SoulAttribute(2);

    public static final SoulAttribute ATTACK_SPEED = new SoulAttribute(1);
    public static final SoulAttribute ATTACK_DAMAGE = new SoulAttribute(2);
    public static final SoulAttribute CRITICAL = new SoulAttribute(3);
    public static final SoulAttribute KNOCKBACK_ATTRIBUTE = new SoulAttribute(4);

    protected static final SoulAttribute[] ATTRIBUTES = {
            EFFICIENCY_ATTRIBUTE,
            REACH_DISTANCE,
            HARVEST_LEVEL,
            ATTACK_SPEED,
            ATTACK_DAMAGE,
            CRITICAL,
            KNOCKBACK_ATTRIBUTE
    };

    protected final float[] increase;

    protected SoulAttribute(final int index, final float ... increase) {
        super(index);
        this.increase = increase;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof SoulAttribute && ((SoulAttribute) obj).index == this.index;
    }

    public static SoulAttribute get(final IType type, final int index) {
        return type instanceof SoulWeaponType
                ? SoulWeaponAttribute.get(index)
                : type instanceof SoulToolType
                ? SoulToolAttribute.get(index)
                : null;
    }

    public static int getAmount() {
        return ATTRIBUTES.length;
    }

    public float getIncrease(final IType type) {
        return this.increase[type.getIndex()];
    }
}
