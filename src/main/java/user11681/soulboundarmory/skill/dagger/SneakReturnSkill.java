package user11681.soulboundarmory.skill.dagger;

import net.minecraft.util.Identifier;
import user11681.soulboundarmory.skill.Skill;
import user11681.soulboundarmory.skill.Skills;

public class SneakReturnSkill extends Skill {
    public SneakReturnSkill(final Identifier identifier) {
        super(identifier, Skills.RETURN);
    }

    @Override
    public int getCost(final boolean learned, final int level) {
        return 1;
    }
}
