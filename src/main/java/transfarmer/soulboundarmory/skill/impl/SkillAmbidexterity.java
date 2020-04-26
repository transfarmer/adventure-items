package transfarmer.soulboundarmory.skill.impl;

import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillBase;

import java.util.ArrayList;
import java.util.List;

public class SkillAmbidexterity extends SkillBase {
    public SkillAmbidexterity() {
        super("ambidexterity");
    }

    @Override
    public List<Skill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return 3;
    }
}
