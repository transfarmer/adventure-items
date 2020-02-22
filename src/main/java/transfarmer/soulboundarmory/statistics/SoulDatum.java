package transfarmer.soulboundarmory.statistics;

public class SoulDatum extends Statistic {
    public static final SoulDatum XP = new SoulDatum(0, "xp");
    public static final SoulDatum LEVEL = new SoulDatum(1, "level");
    public static final SoulDatum SKILLS = new SoulDatum(2, "skills");
    public static final SoulDatum ATTRIBUTE_POINTS = new SoulDatum(3, "attributePoints");
    public static final SoulDatum SPENT_ATTRIBUTE_POINTS = new SoulDatum(4, "spentAttributePoints");
    public static final SoulDatum ENCHANTMENT_POINTS = new SoulDatum(5, "enchantmentPoints");
    public static final SoulDatum SPENT_ENCHANTMENT_POINTS = new SoulDatum(6, "spentEnchantmentPoints");

    protected static final SoulDatum[] DATA = {
            XP,
            LEVEL,
            SKILLS,
            ATTRIBUTE_POINTS,
            SPENT_ATTRIBUTE_POINTS,
            ENCHANTMENT_POINTS,
            SPENT_ENCHANTMENT_POINTS,
    };

    protected SoulDatum(final int index, final String name) {
        super(index, name);
    }

    public static SoulDatum[] get() {
        return DATA;
    }

    public static SoulDatum get(final int index) {
        return DATA[index];
    }

    public static int getAmount() {
        return DATA.length;
    }
}
