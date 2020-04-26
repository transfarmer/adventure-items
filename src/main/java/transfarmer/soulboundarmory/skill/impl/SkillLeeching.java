package transfarmer.soulboundarmory.skill.impl;

import net.minecraft.util.ResourceLocation;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillBaseLevelable;

import java.util.ArrayList;
import java.util.List;

public class SkillLeeching extends SkillBaseLevelable {
    public SkillLeeching() {
        this(0);
    }

    public SkillLeeching(final int level) {
        super("leeching", level, -1);
    }

    @Override
    public List<Skill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return !this.learned ? 3 : this.level + 1;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation("textures/items/rotten_flesh.png");
    }
}
