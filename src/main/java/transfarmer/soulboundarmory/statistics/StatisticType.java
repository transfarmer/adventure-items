package transfarmer.soulboundarmory.statistics;

import transfarmer.soulboundarmory.util.StringUtil;

public enum StatisticType implements IStatistic {
    XP,
    LEVEL,
    SKILLS,
    ATTRIBUTE_POINTS,
    ENCHANTMENT_POINTS,
    SPENT_ATTRIBUTE_POINTS,
    SPENT_ENCHANTMENT_POINTS,

    EFFICIENCY_ATTRIBUTE,
    REACH_DISTANCE,
    HARVEST_LEVEL,
    ATTACK_SPEED,
    ATTACK_DAMAGE,
    CRITICAL,
    KNOCKBACK_ATTRIBUTE,

    SHARPNESS,
    SWEEPING_EDGE,
    LOOTING,
    FIRE_ASPECT,
    KNOCKBACK_ENCHANTMENT,
    SMITE,
    BANE_OF_ARTHROPODS,

    EFFICIENCY_ENCHANTMENT,
    FORTUNE,
    SILK_TOUCH;

    private static final IStatistic[] STATISTIC_TYPES = {
            XP,
            LEVEL,
            SKILLS,
            ATTRIBUTE_POINTS,
            ENCHANTMENT_POINTS,
            SPENT_ATTRIBUTE_POINTS,
            SPENT_ENCHANTMENT_POINTS,

            EFFICIENCY_ATTRIBUTE,
            REACH_DISTANCE,
            HARVEST_LEVEL,
            ATTACK_SPEED,
            ATTACK_DAMAGE,
            CRITICAL,
            KNOCKBACK_ATTRIBUTE,

            SHARPNESS,
            SWEEPING_EDGE,
            LOOTING,
            FIRE_ASPECT,
            KNOCKBACK_ENCHANTMENT,
            SMITE,
            BANE_OF_ARTHROPODS,

            EFFICIENCY_ENCHANTMENT,
            FORTUNE,
            SILK_TOUCH
    };

    public static IStatistic get(final String name) {
        for (final IStatistic type : STATISTIC_TYPES) {
            if (type.name().equals(name)) {
                return type;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return StringUtil.macroCaseToCamelCase(this.name());
    }
}
