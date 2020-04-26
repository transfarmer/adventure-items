package transfarmer.soulboundarmory.skill.impl;

import net.minecraft.util.ResourceLocation;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillBaseLevelable;

import java.util.ArrayList;
import java.util.List;

public class SkillLeaping extends SkillBaseLevelable {
    public SkillLeaping() {
        this(0);
    }

    public SkillLeaping(final int level) {
        super("leaping", level, -1);
    }

    @Override
    public List<Skill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation("textures/items/rabbit_foot.png");
    }
}
