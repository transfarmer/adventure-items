package user11681.soulboundarmory.skill.dagger;

import net.minecraft.util.Identifier;
import user11681.soulboundarmory.skill.Skill;
import user11681.soulboundarmory.registry.Skills;

public class ShadowCloneSkill extends Skill {
    public ShadowCloneSkill(final Identifier identifier) {
        super(identifier);
    }

    @Override
    public void initDependencies() {
        this.dependencies.add(Skills.THROWING);
    }

    @Override
    public int getCost(final boolean learned, final int level) {
        return 2;
    }
}
