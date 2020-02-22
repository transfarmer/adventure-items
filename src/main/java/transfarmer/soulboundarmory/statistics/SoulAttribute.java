package transfarmer.soulboundarmory.statistics;

import transfarmer.soulboundarmory.statistics.tool.SoulToolAttribute;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponAttribute;
import transfarmer.soulboundarmory.statistics.weapon.SoulWeaponType;

public class SoulAttribute extends Statistic {
    public static final SoulAttribute EFFICIENCY_ATTRIBUTE = new SoulAttribute(0, "efficiencyAttribute");

    public static final SoulAttribute REACH_DISTANCE = new SoulAttribute(1, "reachDistance");
    public static final SoulAttribute HARVEST_LEVEL = new SoulAttribute(2, "harvestLevel");

    public static final SoulAttribute ATTACK_SPEED = new SoulAttribute(1, "attackSpeed");
    public static final SoulAttribute ATTACK_DAMAGE = new SoulAttribute(2, "attackDamage");
    public static final SoulAttribute CRITICAL = new SoulAttribute(3, "critical");
    public static final SoulAttribute KNOCKBACK_ATTRIBUTE = new SoulAttribute(4, "knockbackAttribute");

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

    protected SoulAttribute(final int index, final String name, final float ... increase) {
        super(index, name);
        this.increase = increase;
    }

    public static SoulAttribute get(final SoulType type, final int index) {
        return type instanceof SoulWeaponType
                ? SoulWeaponAttribute.get(index)
                : type instanceof SoulToolType
                ? SoulToolAttribute.get(index)
                : null;
    }

    public static int getAmount() {
        return ATTRIBUTES.length;
    }

    public float getIncrease(final SoulType type) {
        return this.increase[type.getIndex()];
    }
}
