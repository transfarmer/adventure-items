package transfarmer.soulboundarmory.init;

import transfarmer.soulboundarmory.skill.common.SkillLeeching;
import transfarmer.soulboundarmory.skill.dagger.SkillReturn;
import transfarmer.soulboundarmory.skill.dagger.SkillShadowClone;
import transfarmer.soulboundarmory.skill.dagger.SkillSneakReturn;
import transfarmer.soulboundarmory.skill.dagger.SkillThrowing;
import transfarmer.soulboundarmory.skill.greatsword.SkillFreezing;
import transfarmer.soulboundarmory.skill.greatsword.SkillLeaping;
import transfarmer.soulboundarmory.skill.pick.SkillAmbidexterity;
import transfarmer.soulboundarmory.skill.pick.SkillTeleportation;
import transfarmer.soulboundarmory.skill.staff.SkillEndermanacle;
import transfarmer.soulboundarmory.skill.staff.SkillFireball;
import transfarmer.soulboundarmory.skill.staff.SkillPenetration;
import transfarmer.soulboundarmory.skill.staff.SkillVulnerability;
import transfarmer.soulboundarmory.skill.sword.SkillSummonLightning;

public class Skills {
    public static final String AMBIDEXTERITY = new SkillAmbidexterity().getRegistryName();
    public static final String ENDERMANACLE = new SkillEndermanacle().getRegistryName();
    public static final String FIREBALL = new SkillFireball().getRegistryName();
    public static final String FREEZING = new SkillFreezing().getRegistryName();
    public static final String LEAPING = new SkillLeaping().getRegistryName();
    public static final String LEECHING = new SkillLeeching().getRegistryName();
    public static final String PENETRATION = new SkillPenetration().getRegistryName();
    public static final String RETURN = new SkillReturn().getRegistryName();
    public static final String SHADOW_CLONE = new SkillShadowClone().getRegistryName();
    public static final String SNEAK_RETURN = new SkillSneakReturn().getRegistryName();
    public static final String SUMMON_LIGHTNING = new SkillSummonLightning().getRegistryName();
    public static final String TELEPORTATION = new SkillTeleportation().getRegistryName();
    public static final String THROWING = new SkillThrowing().getRegistryName();
    public static final String VULNERABILITY = new SkillVulnerability().getRegistryName();
}
