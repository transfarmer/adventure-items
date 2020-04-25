package transfarmer.soulboundarmory.statistics.base.enumeration;

import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;
import transfarmer.soulboundarmory.util.CollectionUtil;
import transfarmer.soulboundarmory.util.StringUtil;

public enum StatisticType implements IStatistic {
    XP,
    LEVEL,
    SKILL_POINTS,
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

    EFFICIENCY_ENCHANTMENT,
    FORTUNE,
    SILK_TOUCH;

    static {
        CollectionUtil.addAll(STATISTICS,
                XP,
                LEVEL,
                SKILL_POINTS,
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

                EFFICIENCY_ENCHANTMENT,
                FORTUNE,
                SILK_TOUCH
        );
    }

    @Override
    public String toString() {
        return StringUtil.macroCaseToCamelCase(this.name());
    }
}
