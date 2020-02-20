package transfarmer.soulboundarmory.statistics;

public class SoulToolAttribute extends SoulAttribute {
    public static final SoulAttribute EFFICIENCY_ATTRIBUTE = new SoulAttribute(0);
    public static final SoulAttribute REACH_DISTANCE = new SoulAttribute(1);
    public static final SoulAttribute HARVEST_LEVEL = new SoulAttribute(2);

    protected SoulToolAttribute(final int index) {
        super(index);
    }
}
