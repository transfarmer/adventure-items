package transfarmer.soulboundarmory.skill.impl;

import transfarmer.soulboundarmory.skill.ISkill;
import transfarmer.soulboundarmory.skill.SkillBase;

import java.util.ArrayList;
import java.util.List;

public class SkillTeleportation extends SkillBase {
    public SkillTeleportation() {
        super("teleportation", "teleports mined items to its user's inventory");
    }

    @Override
    public List<ISkill> getDependencies() {
        return new ArrayList<>();
    }
}
