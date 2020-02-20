package transfarmer.soulboundarmory.statistics;

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

    protected SoulAttribute(final int index) {
        super(index);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof SoulAttribute && ((SoulAttribute) obj).index == this.index;
    }

    public static String getName(final int index) {
        return get(index).toString().toLowerCase();
    }

    public static SoulAttribute[] get() {
        return ATTRIBUTES;
    }

    public static SoulAttribute get(final int index) {
        return ATTRIBUTES[index];
    }

    public static int getAmount() {
        return ATTRIBUTES.length;
    }
}
