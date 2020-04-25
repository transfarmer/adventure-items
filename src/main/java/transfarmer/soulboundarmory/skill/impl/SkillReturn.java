package transfarmer.soulboundarmory.skill.impl;

import transfarmer.soulboundarmory.skill.ISkill;
import transfarmer.soulboundarmory.skill.SkillBaseLevelable;
import transfarmer.soulboundarmory.util.CollectionUtil;

import java.util.List;

import static transfarmer.soulboundarmory.skill.Skills.THROWING;

public class SkillReturn extends SkillBaseLevelable {
    public SkillReturn() {
        this(0);
    }

    public SkillReturn(final int level) {
        super("return", level);
    }

    @Override
    public List<ISkill> getDependencies() {
        return this.storage == null
                ? CollectionUtil.arrayList(new SkillThrowing())
                : CollectionUtil.arrayList(this.storage.get(this.item, THROWING.getRegistryName()));
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public boolean canBeUpgraded(final int points) {
        return this.canBeUpgraded();
    }
}
