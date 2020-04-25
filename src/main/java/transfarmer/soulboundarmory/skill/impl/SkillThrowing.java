package transfarmer.soulboundarmory.skill.impl;

import net.minecraft.util.ResourceLocation;
import transfarmer.soulboundarmory.skill.ISkill;
import transfarmer.soulboundarmory.skill.SkillBase;

import java.util.ArrayList;
import java.util.List;

public class SkillThrowing extends SkillBase {
    public SkillThrowing() {
        super("throwing");
    }

    @Override
    public List<ISkill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation("textures/items/arrow.png");
    }
}
