package user11681.soulboundarmory.component.statistics;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.Main;

public class StatisticType {
    protected static final Map<Identifier, StatisticType> STATISTICS = new HashMap<>();

    public static final StatisticType XP = new StatisticType("xp");
    public static final StatisticType LEVEL = new StatisticType("level");
    public static final StatisticType SKILL_POINTS = new StatisticType("skill_points");
    public static final StatisticType ATTRIBUTE_POINTS = new StatisticType("attribute_points");
    public static final StatisticType ENCHANTMENT_POINTS = new StatisticType("enchantment_points");
    public static final StatisticType SPENT_ATTRIBUTE_POINTS = new StatisticType("spent_attribute_points");
    public static final StatisticType SPENT_ENCHANTMENT_POINTS = new StatisticType("spent_enchantment_points");

    public static final StatisticType EFFICIENCY = new StatisticType("efficiency");
    public static final StatisticType REACH = new StatisticType("reach");
    public static final StatisticType MINING_LEVEL = new StatisticType("harvest_level");
    public static final StatisticType ATTACK_SPEED = new StatisticType("attack_speed");
    public static final StatisticType ATTACK_DAMAGE = new StatisticType("attack_damage");
    public static final StatisticType CRITICAL_STRIKE_PROBABILITY = new StatisticType("critical_strike_probability");
    public static final StatisticType KNOCKBACK = new StatisticType("knockback");
    public static final StatisticType ATTACK_RANGE = new StatisticType("attack_range");

    private final Identifier identifier;

    public StatisticType(final String path) {
        this(new Identifier(Main.MOD_ID, path));
    }

    public StatisticType(final Identifier identifier) {
        this.identifier = identifier;

        STATISTICS.put(identifier, this);
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
