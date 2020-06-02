package user11681.soulboundarmory.skill;

import net.minecraft.util.Identifier;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.registry.Registries;
import user11681.soulboundarmory.skill.common.NourishmentSkill;
import user11681.soulboundarmory.skill.dagger.ReturnSkill;
import user11681.soulboundarmory.skill.dagger.ShadowCloneSkill;
import user11681.soulboundarmory.skill.dagger.SneakReturnSkill;
import user11681.soulboundarmory.skill.dagger.ThrowingSkill;
import user11681.soulboundarmory.skill.greatsword.FreezingSkill;
import user11681.soulboundarmory.skill.greatsword.LeapingSkill;
import user11681.soulboundarmory.skill.pick.AmbidexteritySkill;
import user11681.soulboundarmory.skill.pick.PullSkill;
import user11681.soulboundarmory.skill.staff.EndermanacleSkill;
import user11681.soulboundarmory.skill.staff.FireballSkill;
import user11681.soulboundarmory.skill.staff.HealingSkill;
import user11681.soulboundarmory.skill.staff.PenetrationSkill;
import user11681.soulboundarmory.skill.staff.VulnerabilitySkill;
import user11681.soulboundarmory.skill.sword.SummonLightningSkill;

public class Skills {
    public static final Skill AMBIDEXTERITY = Registries.SKILL.register(new AmbidexteritySkill(new Identifier(Main.MOD_ID, "ambidexterity")));
    public static final Skill ENDERMANACLE = Registries.SKILL.register(new EndermanacleSkill(new Identifier(Main.MOD_ID, "endermanacle")));
    public static final Skill FIREBALL = Registries.SKILL.register(new FireballSkill(new Identifier(Main.MOD_ID, "fireball")));
    public static final Skill FREEZING = Registries.SKILL.register(new FreezingSkill(new Identifier(Main.MOD_ID, "freezing")));
    public static final Skill HEALING = Registries.SKILL.register(new HealingSkill(new Identifier(Main.MOD_ID, "healing")));
    public static final Skill LEAPING = Registries.SKILL.register(new LeapingSkill(new Identifier(Main.MOD_ID, "leaping")));
    public static final Skill NOURISHMENT = Registries.SKILL.register(new NourishmentSkill(new Identifier(Main.MOD_ID, "nourishment")));
    public static final Skill PENETRATION = Registries.SKILL.register(new PenetrationSkill(new Identifier(Main.MOD_ID, "penetration")));
    public static final Skill PULL = Registries.SKILL.register(new PullSkill(new Identifier(Main.MOD_ID, "pull")));
    public static final Skill RETURN = Registries.SKILL.register(new ReturnSkill(new Identifier(Main.MOD_ID, "return")));
    public static final Skill SHADOW_CLONE = Registries.SKILL.register(new ShadowCloneSkill(new Identifier(Main.MOD_ID, "shadow_clone")));
    public static final Skill SNEAK_RETURN = Registries.SKILL.register(new SneakReturnSkill(new Identifier(Main.MOD_ID, "sneak_return")));
    public static final Skill SUMMON_LIGHTNING = Registries.SKILL.register(new SummonLightningSkill(new Identifier(Main.MOD_ID, "summon_lightning")));
    public static final Skill THROWING = Registries.SKILL.register(new ThrowingSkill(new Identifier(Main.MOD_ID, "throwing")));
    public static final Skill VULNERABILITY = Registries.SKILL.register(new VulnerabilitySkill(new Identifier(Main.MOD_ID, "vulnerability")));

    public static Identifier getDefaultTextureLocation(final Skill skill) {
        final Identifier identifier = skill.getIdentifier();

        return new Identifier(identifier.getNamespace(), String.format("skill/%s.png", identifier.getPath()));
    }
}
