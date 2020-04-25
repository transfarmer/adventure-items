package transfarmer.soulboundarmory.skill.impl;

import net.minecraft.util.ResourceLocation;
import transfarmer.soulboundarmory.skill.ISkill;
import transfarmer.soulboundarmory.skill.SkillBaseLevelable;
import transfarmer.soulboundarmory.util.CollectionUtil;

import java.util.List;

import static transfarmer.soulboundarmory.skill.Skills.LEAPING;

public class SkillFreezing extends SkillBaseLevelable {
    public SkillFreezing() {
        this(0);
    }

    public SkillFreezing(final int level) {
        super("freezing", level);
    }

    @Override
    public List<ISkill> getDependencies() {
        return this.storage == null
                ? CollectionUtil.arrayList(new SkillLeaping())
                : CollectionUtil.arrayList(this.storage.get(this.item, LEAPING.getRegistryName()));
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation("textures/items/snowball.png");
    }

    @Override
    public boolean canBeUpgraded(final int points) {
        return this.canBeUpgraded();
    }
}
