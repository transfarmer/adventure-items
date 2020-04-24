package transfarmer.soulboundarmory.skill.impl;

import transfarmer.soulboundarmory.skill.ISkill;
import transfarmer.soulboundarmory.skill.SkillBaseLevelable;

import java.util.ArrayList;
import java.util.List;

public class SkillSummonLightning extends SkillBaseLevelable {
    public SkillSummonLightning() {
        this(0);
    }

    public SkillSummonLightning(final int level) {
        super("summon_lightning", level, "causes a lightning bolt to be summoned when used");
    }

    @Override
    public List<ISkill> getDependencies() {
        return new ArrayList<>();
    }
}
