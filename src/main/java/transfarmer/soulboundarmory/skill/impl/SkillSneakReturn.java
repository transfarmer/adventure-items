package transfarmer.soulboundarmory.skill.impl;

import net.minecraft.util.ResourceLocation;
import transfarmer.soulboundarmory.skill.ISkill;
import transfarmer.soulboundarmory.skill.SkillBase;
import transfarmer.soulboundarmory.util.CollectionUtil;

import java.util.List;

public class SkillSneakReturn extends SkillBase {
    public SkillSneakReturn() {
        super("sneak_return");
    }

    @Override
    public List<ISkill> getDependencies() {
        return CollectionUtil.arrayList(new SkillThrowing());
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation("textures/items/lead.png");
    }
}
