package transfarmer.soularsenal.data.tool;

public enum SoulToolAttribute {
    EFFICIENCY_ATTRIBUTE(0, 1),
    HARDNESS(0, 0.2F),
    REACH_DISTANCE(0, 0.1F);

    public final int index;
    private final float[] increase;

    private static final SoulToolAttribute[] ATTRIBUTES = {EFFICIENCY_ATTRIBUTE, HARDNESS, REACH_DISTANCE};

    SoulToolAttribute(final int index, final float ... increase) {
        this.index = index;
        this.increase = increase;
    }

    public static SoulToolAttribute getAttribute(final int index) {
        return ATTRIBUTES[index];
    }

    public float getIncrease(final SoulToolType type) {
        return this.increase[type.index];
    }

    public static String getName(final int index) {
        return getAttribute(index).toString().toLowerCase();
    }
}
