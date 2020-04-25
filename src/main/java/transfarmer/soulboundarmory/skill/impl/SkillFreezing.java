package transfarmer.soulboundarmory.skill.impl;

import net.minecraft.util.ResourceLocation;
import transfarmer.soulboundarmory.skill.ISkill;
import transfarmer.soulboundarmory.skill.SkillBaseLevelable;
import transfarmer.soulboundarmory.util.CollectionUtil;

import java.util.List;

public class SkillFreezing extends SkillBaseLevelable {
    public SkillFreezing() {
        this(0);
    }

    public SkillFreezing(final int level) {
        super("freezing", level);
    }

    @Override
    public List<ISkill> getDependencies() {
        return CollectionUtil.arrayList(new SkillLeaping());
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation("textures/items/snowball.png");
    }
}
