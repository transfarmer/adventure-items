package transfarmer.soulboundarmory.skill.impl;

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
}
