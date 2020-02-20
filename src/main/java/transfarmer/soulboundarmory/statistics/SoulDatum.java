package transfarmer.soulboundarmory.statistics;

public class SoulDatum extends Statistic {
    public static final SoulDatum XP = new SoulDatum(0);
    public static final SoulDatum LEVEL = new SoulDatum(1);
    public static final SoulDatum SKILLS = new SoulDatum(2);
    public static final SoulDatum ATTRIBUTE_POINTS = new SoulDatum(3);
    public static final SoulDatum SPENT_ATTRIBUTE_POINTS = new SoulDatum(4);
    public static final SoulDatum ENCHANTMENT_POINTS = new SoulDatum(5);
    public static final SoulDatum SPENT_ENCHANTMENT_POINTS = new SoulDatum(6);

    protected static final SoulDatum[] DATA = {
            XP,
            LEVEL,
            SKILLS,
            ATTRIBUTE_POINTS,
            SPENT_ATTRIBUTE_POINTS,
            ENCHANTMENT_POINTS,
            SPENT_ENCHANTMENT_POINTS
    };

    protected SoulDatum(final int index) {
        super(index);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof SoulDatum && ((SoulDatum) obj).index == this.index;
    }

    public static String getName(final int index) {
        return getDatum(index).toString().toLowerCase();
    }

    public static SoulDatum[] getData() {
        return DATA;
    }

    public static SoulDatum getDatum(final int index) {
        return DATA[index];
    }

    public static int getAmount() {
        return DATA.length;
    }
}
