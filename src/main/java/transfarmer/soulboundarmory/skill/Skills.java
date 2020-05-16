package transfarmer.soulboundarmory.skill;

import net.minecraft.util.Identifier;
import transfarmer.soulboundarmory.Main;
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

import java.util.HashMap;
import java.util.Map;

public class Skills {
    protected static final Map<Identifier, Skill> REGISTRY = new HashMap<>();

    public static final Skill AMBIDEXTERITY = register(new SkillAmbidexterity(), "ambidexterity");
    public static final Skill ENDERMANACLE = register(new SkillEndermanacle(), "endermanacle");
    public static final Skill FIREBALL = register(new SkillFireball(), "fireball");
    public static final Skill FREEZING = register(new SkillFreezing(), "freezing");
    public static final Skill LEAPING = register(new SkillLeaping(), "leaping");
    public static final Skill LEECHING = register(new SkillLeeching(), "leeching");
    public static final Skill PENETRATION = register(new SkillPenetration(), "penetration");
    public static final Skill RETURN = register(new SkillReturn(), "return");
    public static final Skill SHADOW_CLONE = register(new SkillShadowClone(), "shadow_clone");
    public static final Skill SNEAK_RETURN = register(new SkillSneakReturn(), "sneak_return");
    public static final Skill SUMMON_LIGHTNING = register(new SkillSummonLightning(), "summon_lightning");
    public static final Skill TELEPORTATION = register(new SkillTeleportation(), "teleportation");
    public static final Skill THROWING = register(new SkillThrowing(), "throwing");
    public static final Skill VULNERABILITY = register(new SkillVulnerability(), "vulnerability");

    public static Skill register(final Skill skill, final String path) {
        return register(skill, new Identifier(Main.MOD_ID, path));
    }

    public static Skill register(final Skill skill, final Identifier identifier) {
        REGISTRY.put(identifier, skill);

        return skill;
    }

    public static Identifier getID(final String string) {
        for (final Identifier identifier : REGISTRY.keySet()) {
            if (string.equals(identifier.toString())) {
                return identifier;
            }
        }

        return null;
    }

    public static Skill get(final String string) {
        return get(getID(string));
    }

    public static Skill get(final Identifier identifier) {
        return REGISTRY.get(identifier);
    }
}
