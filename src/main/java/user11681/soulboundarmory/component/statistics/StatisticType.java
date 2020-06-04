package user11681.soulboundarmory.component.statistics;

import net.minecraft.util.Identifier;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.registry.Registries;
import user11681.usersmanual.registry.AbstractRegistryEntry;

public class StatisticType extends AbstractRegistryEntry {
    public static final StatisticType EXPERIENCE = Registries.STATISTIC.register(new StatisticType(new Identifier(Main.MOD_ID, "xp")));
    public static final StatisticType LEVEL = Registries.STATISTIC.register(new StatisticType(new Identifier(Main.MOD_ID, "level")));
    public static final StatisticType SKILL_POINTS = Registries.STATISTIC.register(new StatisticType(new Identifier(Main.MOD_ID, "skill_points")));
    public static final StatisticType ATTRIBUTE_POINTS = Registries.STATISTIC.register(new StatisticType(new Identifier(Main.MOD_ID, "attribute_points")));
    public static final StatisticType ENCHANTMENT_POINTS = Registries.STATISTIC.register(new StatisticType(new Identifier(Main.MOD_ID, "enchantment_points")));
    public static final StatisticType SPENT_ATTRIBUTE_POINTS = Registries.STATISTIC.register(new StatisticType(new Identifier(Main.MOD_ID, "spent_attribute_points")));
    public static final StatisticType SPENT_ENCHANTMENT_POINTS = Registries.STATISTIC.register(new StatisticType(new Identifier(Main.MOD_ID, "spent_enchantment_points")));
    public static final StatisticType EFFICIENCY = Registries.STATISTIC.register(new StatisticType(new Identifier(Main.MOD_ID, "efficiency")));
    public static final StatisticType REACH = Registries.STATISTIC.register(new StatisticType(new Identifier(Main.MOD_ID, "reach")));
    public static final StatisticType MINING_LEVEL = Registries.STATISTIC.register(new StatisticType(new Identifier(Main.MOD_ID, "harvest_level")));
    public static final StatisticType ATTACK_SPEED = Registries.STATISTIC.register(new StatisticType(new Identifier(Main.MOD_ID, "attack_speed")));
    public static final StatisticType ATTACK_DAMAGE = Registries.STATISTIC.register(new StatisticType(new Identifier(Main.MOD_ID, "attack_damage")));
    public static final StatisticType CRITICAL_STRIKE_PROBABILITY = Registries.STATISTIC.register(new StatisticType(new Identifier(Main.MOD_ID, "critical_strike_probability")));
    public static final StatisticType KNOCKBACK = Registries.STATISTIC.register(new StatisticType(new Identifier(Main.MOD_ID, "knockback")));
    public static final StatisticType ATTACK_RANGE = Registries.STATISTIC.register(new StatisticType(new Identifier(Main.MOD_ID, "attack_range")));

    protected StatisticType(final Identifier identifier) {
        super(identifier);
    }
}
