package transfarmer.soulboundarmory.statistics.tool;

import transfarmer.soulboundarmory.statistics.SoulAttribute;

public class SoulToolAttribute extends SoulAttribute {
    public static final SoulAttribute EFFICIENCY_ATTRIBUTE = new SoulToolAttribute(0, 0.5F);
    public static final SoulAttribute REACH_DISTANCE = new SoulToolAttribute(1, 0.1F);
    public static final SoulAttribute HARVEST_LEVEL = new SoulToolAttribute(2, 0.2F);

    protected static final SoulAttribute[] ATTRIBUTES = {
            EFFICIENCY_ATTRIBUTE,
            REACH_DISTANCE,
            HARVEST_LEVEL,
    };

    protected SoulToolAttribute(final int index, final float... increase) {
        super(index, increase);
    }

    public static String getName(final int index) {
        return get(index).toString().toLowerCase();
    }

    public static SoulAttribute get(final int index) {
        return ATTRIBUTES[index];
    }

    public static int getAmount() {
        return ATTRIBUTES.length;
    }
}
