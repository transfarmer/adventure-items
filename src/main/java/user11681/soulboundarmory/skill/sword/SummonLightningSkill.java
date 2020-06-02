package user11681.soulboundarmory.skill.sword;

import net.minecraft.util.Identifier;
import user11681.soulboundarmory.skill.Skill;

public class SummonLightningSkill extends Skill {
    public SummonLightningSkill(final Identifier identifier) {
        super(identifier);
    }

    @Override
    public int getCost(final boolean learned, final int level) {
        return 3;
    }
}
