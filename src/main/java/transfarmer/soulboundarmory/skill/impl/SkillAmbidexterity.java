package transfarmer.soulboundarmory.skill.impl;

import transfarmer.soulboundarmory.skill.ISkill;
import transfarmer.soulboundarmory.skill.SkillBase;

import java.util.ArrayList;
import java.util.List;

public class SkillAmbidexterity extends SkillBase {
    public SkillAmbidexterity() {
        super("ambidexterity");
    }

    @Override
    public List<ISkill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return 3;
    }
}
