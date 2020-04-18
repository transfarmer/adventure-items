package transfarmer.soulboundarmory.statistics;

import transfarmer.soulboundarmory.util.StringUtil;

public enum Skill implements ISkill {
    LEAPING,

    SUMMON_LIGHTNING,

    THROWING,
    SHADOW_CLONE,
    RETURN,
    SNEAK_RETURN,

    TELEPORTATION,
    AMBIDEXTERITY;

    @Override
    public String toString() {
        return StringUtil.macroCaseToCamelCase(this.name());
    }
}
