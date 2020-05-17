package transfarmer.soulboundarmory.skill;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.Identifier;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.skill.common.NourishmentSkill;
import transfarmer.soulboundarmory.skill.dagger.ReturnSkill;
import transfarmer.soulboundarmory.skill.dagger.ShadowCloneSkill;
import transfarmer.soulboundarmory.skill.dagger.SneakReturnSkill;
import transfarmer.soulboundarmory.skill.dagger.ThrowingSkill;
import transfarmer.soulboundarmory.skill.greatsword.FreezingSkill;
import transfarmer.soulboundarmory.skill.greatsword.LeapingSkill;
import transfarmer.soulboundarmory.skill.pick.AmbidexteritySkill;
import transfarmer.soulboundarmory.skill.pick.PullSkill;
import transfarmer.soulboundarmory.skill.staff.EndermanacleSkill;
import transfarmer.soulboundarmory.skill.staff.FireballSkill;
import transfarmer.soulboundarmory.skill.staff.PenetrationSkill;
import transfarmer.soulboundarmory.skill.staff.HealingSkill;
import transfarmer.soulboundarmory.skill.staff.VulnerabilitySkill;
import transfarmer.soulboundarmory.skill.sword.SummonLightningSkill;

public class Skills {
    protected static final BiMap<Identifier, Skill> REGISTRY = HashBiMap.create();

    public static final Skill AMBIDEXTERITY = register(new AmbidexteritySkill(new Identifier(Main.MOD_ID, "ambidexterity")));
    public static final Skill ENDERMANACLE = register(new EndermanacleSkill(new Identifier(Main.MOD_ID, "endermanacle")));
    public static final Skill FIREBALL = register(new FireballSkill(new Identifier(Main.MOD_ID, "fireball")));
    public static final Skill FREEZING = register(new FreezingSkill(new Identifier(Main.MOD_ID, "freezing")));
    public static final Skill HEALING = register(new HealingSkill(new Identifier(Main.MOD_ID, "healing")));
    public static final Skill LEAPING = register(new LeapingSkill(new Identifier(Main.MOD_ID, "leaping")));
    public static final Skill NOURISHMENT = register(new NourishmentSkill(new Identifier(Main.MOD_ID, "nourishment")));
    public static final Skill PENETRATION = register(new PenetrationSkill(new Identifier(Main.MOD_ID, "penetration")));
    public static final Skill PULL = register(new PullSkill(new Identifier(Main.MOD_ID, "pull")));
    public static final Skill RETURN = register(new ReturnSkill(new Identifier(Main.MOD_ID, "return")));
    public static final Skill SHADOW_CLONE = register(new ShadowCloneSkill(new Identifier(Main.MOD_ID, "shadow_clone")));
    public static final Skill SNEAK_RETURN = register(new SneakReturnSkill(new Identifier(Main.MOD_ID, "sneak_return")));
    public static final Skill SUMMON_LIGHTNING = register(new SummonLightningSkill(new Identifier(Main.MOD_ID, "summon_lightning")));
    public static final Skill THROWING = register(new ThrowingSkill(new Identifier(Main.MOD_ID, "throwing")));
    public static final Skill VULNERABILITY = register(new VulnerabilitySkill(new Identifier(Main.MOD_ID, "vulnerability")));

    public static Skill register(final Skill skill) {
        REGISTRY.put(skill.identifier, skill);

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

    public static Identifier getDefaultTextureLocation(final Skill skill) {
        final Identifier identifier = skill.getIdentifier();

        return new Identifier(identifier.getNamespace(), String.format("skill/%s.png", identifier.getPath()));
    }
}
