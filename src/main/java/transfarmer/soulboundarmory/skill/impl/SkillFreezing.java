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
        super("freezing", level, "freezes enemies if passed while leaping", "freezes nearby enemies when landing from a leap");
    }

    @Override
    public List<ISkill> getDependencies() {
        return CollectionUtil.arrayList(new SkillLeaping());
    }
}
