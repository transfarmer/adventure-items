package transfarmer.soulboundarmory.statistics;

import net.minecraft.util.Identifier;
import transfarmer.soulboundarmory.Main;

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

    protected static final Map<Identifier, StatisticType> STATISTICS = new HashMap<>();

    private final Identifier identifier;

    public StatisticType(final String path) {
        this(new Identifier(Main.MOD_ID, path));
    }

    public StatisticType(final Identifier identifier) {
        STATISTICS.put(identifier, this);

        this.identifier = identifier;
    }

    public static StatisticType valueOf(final String path) {
        for (final Identifier identifier : STATISTICS.keySet()) {
            if (identifier.getNamespace().equals(Main.MOD_ID) && identifier.getPath().equals(path)) {
                return valueOf(identifier);
            }
        }

        return null;
    }

    public static StatisticType valueOf(final Identifier identifier) {
        STATISTICS.get(identifier);

        return null;
    }

    @Override
    public String toString() {
        return this.identifier.toString();
    }
}
