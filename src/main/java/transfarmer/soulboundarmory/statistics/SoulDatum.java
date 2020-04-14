package transfarmer.soulboundarmory.statistics;

import transfarmer.soulboundarmory.util.ListUtils;

import java.util.List;

public class SoulDatum extends Statistic {
    public static final SoulDatum DATA = new SoulDatum();

    protected int index;
    public SoulDatum xp;
    public SoulDatum level;
    public SoulDatum skills;
    public SoulDatum attributePoints;
    public SoulDatum spentAttributePoints;
    public SoulDatum enchantmentPoints;
    public SoulDatum spentEnchantmentPoints;

    protected List<SoulDatum> data;

    protected SoulDatum(final int index, final String name) {
        super(index, name);
    }

    protected SoulDatum() {
        this.xp = new SoulDatum(this.index++, "xp");
        this.level = new SoulDatum(this.index++, "level");
        this.skills = new SoulDatum(this.index++, "skills");
        this.attributePoints = new SoulDatum(this.index++, "attributePoints");
        this.spentAttributePoints = new SoulDatum(this.index++, "spentAttributePoints");
        this.enchantmentPoints = new SoulDatum(this.index++, "enchantmentPoints");
        this.spentEnchantmentPoints = new SoulDatum(this.index++, "spentEnchantmentPoints");

        this.data = ListUtils.arrayList(
                this.xp,
                this.level,
                this.skills,
                this.attributePoints,
                this.spentAttributePoints,
                this.enchantmentPoints,
                this.spentEnchantmentPoints
        );
    }

    public SoulDatum get(final int index) {
        return this.data.get(index);
    }

    public int getAmount() {
        return this.data.size();
    }

    public static class SoulWeaponDatum extends SoulDatum {
        public static final SoulDatum WEAPON_DATA = new SoulWeaponDatum();
    }

    public static class SoulToolDatum extends SoulDatum {
        public static final SoulDatum TOOL_DATA = new SoulToolDatum();
    }
}
