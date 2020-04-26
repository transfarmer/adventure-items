package transfarmer.soulboundarmory.statistics;

import transfarmer.soulboundarmory.skill.Skill;

import java.util.HashSet;
import java.util.Set;

public interface ISkillType {
    Set<ISkillType> SKILLS = new HashSet<>();

    static ISkillType get(final String name) {
        for (final ISkillType skill : SKILLS) {
            if (skill.toString().equals(name)) {
                return skill;
            }
        }

        return null;
    }

    @Override
    String toString();

    Skill getSkill();
}
