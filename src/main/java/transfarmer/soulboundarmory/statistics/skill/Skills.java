package transfarmer.soulboundarmory.statistics.skill;

import transfarmer.soulboundarmory.statistics.skill.impl.*;

public class Skills {
    public static final ISkill LEAPING = new SkillLeaping();

    public static final ISkill SUMMON_LIGHTNING = new SkillSummonLightning();

    public static final ISkill THROWING = new SkillThrowing();
    public static final ISkill SHADOW_CLONE = new SkillShadowClone();
    public static final ISkill RETURN = new SkillReturn();
    public static final ISkill SNEAK_RETURN = new SkillSneakReturn();

    public static final ISkill TELEPORTATION = new SkillTeleportation();
    public static final ISkill AMBIDEXTERITY = new SkillAmbidexterity();
}
