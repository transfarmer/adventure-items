package transfarmer.soulboundarmory.skill.impl;

import net.minecraft.util.ResourceLocation;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillBase;

import java.util.ArrayList;
import java.util.List;

public class SkillTeleportation extends SkillBase {
    public SkillTeleportation() {
        super("teleportation");
    }

    @Override
    public List<Skill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation("textures/items/ender_pearl.png");
    }
}
