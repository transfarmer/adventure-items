package transfarmer.soulboundarmory.capability.soulbound;

import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.skill.ISkill;

public interface ISkillable {
    ISkill[] getSkills();

    ISkill[] getSkills(IItem type);
}
