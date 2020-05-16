package transfarmer.soulboundarmory.skill.dagger;

import net.minecraft.util.Identifier;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.Skills;

public class SneakReturnSkill extends Skill {
    public SneakReturnSkill(final Identifier identifier) {
        super(identifier, Skills.RETURN);
    }

    @Override
    public int getCost(final boolean learned, final int level) {
        return 1;
    }
}
