package transfarmer.soulboundarmory.skill.impl;

import transfarmer.soulboundarmory.skill.ISkill;
import transfarmer.soulboundarmory.skill.SkillBase;
import transfarmer.soulboundarmory.util.CollectionUtil;

import java.util.List;

public class SkillShadowClone extends SkillBase {
    public SkillShadowClone() {
        super("shadow_clone", "causes a clone to spawn when an enemy is hit");
    }

    @Override
    public List<ISkill> getDependencies() {
        return CollectionUtil.arrayList(new SkillThrowing());
    }
}
