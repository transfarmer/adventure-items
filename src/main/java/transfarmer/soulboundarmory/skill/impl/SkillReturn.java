package transfarmer.soulboundarmory.skill.impl;

import transfarmer.soulboundarmory.skill.ISkill;
import transfarmer.soulboundarmory.skill.SkillBaseLevelable;
import transfarmer.soulboundarmory.util.CollectionUtil;

import java.util.List;

public class SkillReturn extends SkillBaseLevelable {
    public SkillReturn() {
        this(0);
    }

    public SkillReturn(final int level) {
        super("return", level);
    }

    @Override
    public List<ISkill> getDependencies() {
        return CollectionUtil.arrayList(new SkillThrowing());
    }
}
