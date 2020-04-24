package transfarmer.soulboundarmory.skill.impl;

import transfarmer.soulboundarmory.skill.ISkill;
import transfarmer.soulboundarmory.skill.ISkillContext;
import transfarmer.soulboundarmory.skill.SkillBase;

import java.util.ArrayList;
import java.util.List;

public class SkillTeleportation extends SkillBase {
    public SkillTeleportation() {
        super("teleportation");
    }

    @Override
    public List<ISkill> getDependencies() {
        return new ArrayList<>();
    }
}
