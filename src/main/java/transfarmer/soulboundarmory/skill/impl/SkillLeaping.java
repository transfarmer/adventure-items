package transfarmer.soulboundarmory.skill.impl;

import net.minecraft.util.ResourceLocation;
import transfarmer.soulboundarmory.skill.ISkill;
import transfarmer.soulboundarmory.skill.SkillBaseLevelable;

import java.util.ArrayList;
import java.util.List;

public class SkillLeaping extends SkillBaseLevelable {
    public SkillLeaping() {
        this(0);
    }

    public SkillLeaping(final int level) {
        super("leaping", level);
    }

    @Override
    public List<ISkill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation("textures/items/feather.png");
    }
}
