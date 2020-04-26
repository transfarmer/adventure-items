package transfarmer.soulboundarmory.skill.impl;

import net.minecraft.util.ResourceLocation;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillBase;
import transfarmer.soulboundarmory.util.CollectionUtil;

import java.util.List;

import static transfarmer.soulboundarmory.skill.Skills.RETURN;

public class SkillSneakReturn extends SkillBase {
    public SkillSneakReturn() {
        super("sneak_return");
    }

    @Override
    public List<Skill> getDependencies() {
        return this.storage == null
                ? CollectionUtil.arrayList(new SkillReturn())
                : CollectionUtil.arrayList(this.storage.get(this.item, RETURN));
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation("textures/items/lead.png");
    }
}
