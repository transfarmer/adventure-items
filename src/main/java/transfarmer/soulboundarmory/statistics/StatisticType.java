package transfarmer.soulboundarmory.statistics;

import java.util.HashMap;
import java.util.Map;

public class StatisticType {
    public static final StatisticType XP = new StatisticType("xp");
    public static final StatisticType LEVEL = new StatisticType("level");
    public static final StatisticType SKILL_POINTS = new StatisticType("skillPoints");
    public static final StatisticType ATTRIBUTE_POINTS = new StatisticType("attributePoints");
    public static final StatisticType ENCHANTMENT_POINTS = new StatisticType("enchantmentPoints");
    public static final StatisticType SPENT_ATTRIBUTE_POINTS = new StatisticType("spentAttributePoints");
    public static final StatisticType SPENT_ENCHANTMENT_POINTS = new StatisticType("spentEnchantmentPoints");

    public static final StatisticType EFFICIENCY = new StatisticType("efficiency");
    public static final StatisticType REACH = new StatisticType("reach");
    public static final StatisticType HARVEST_LEVEL = new StatisticType("harvestLevel");
    public static final StatisticType ATTACK_SPEED = new StatisticType("attackSpeed");
    public static final StatisticType ATTACK_DAMAGE = new StatisticType("attackDamage");
    public static final StatisticType CRITICAL_STRIKE_PROBABILITY = new StatisticType("criticalStrikeProbability");
    public static final StatisticType KNOCKBACK = new StatisticType("knockback");
    public static final StatisticType ATTACK_RANGE = new StatisticType("attackRange");

    protected static final Map<String, StatisticType> STATISTICS = new HashMap<>();

    private final String name;

    public StatisticType(final String name) {
        STATISTICS.put(name, this);

        this.name = name;
    }

    public static StatisticType get(final String string) {
        for (final StatisticType type : STATISTICS.values()) {
            if (type.toString().equals(string)) {
                return type;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public StatisticType valueOf(final String string) {
        return STATISTICS.get(string);
    }
}
