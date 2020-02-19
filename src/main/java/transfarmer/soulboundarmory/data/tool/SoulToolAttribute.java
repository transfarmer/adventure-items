package transfarmer.soulboundarmory.data.tool;

import transfarmer.soulboundarmory.data.IAttribute;
import transfarmer.soulboundarmory.data.IType;

public enum SoulToolAttribute implements IAttribute {
    EFFICIENCY_ATTRIBUTE(0, 0.5F),
    REACH_DISTANCE(1, 0.1F),
    HARVEST_LEVEL(2, 0.2F);

    private final int index;
    private final float[] increase;

    private static final SoulToolAttribute[] ATTRIBUTES = {EFFICIENCY_ATTRIBUTE, REACH_DISTANCE, HARVEST_LEVEL};

    SoulToolAttribute(final int index, final float ... increase) {
        this.index = index;
        this.increase = increase;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public float getIncrease(final IType type) {
        return this.increase[type.getIndex()];
    }

    public static String getName(final int index) {
        return getAttribute(index).toString().toLowerCase();
    }

    public static SoulToolAttribute[] getAttributes() {
        return ATTRIBUTES;
    }

    public static SoulToolAttribute getAttribute(final int index) {
        return ATTRIBUTES[index];
    }

    public static int getAmount() {
        return ATTRIBUTES.length;
    }
}
