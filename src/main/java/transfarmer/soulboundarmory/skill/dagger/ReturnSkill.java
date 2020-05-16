package transfarmer.soulboundarmory.skill.dagger;

import net.minecraft.util.Identifier;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.Skills;

public class ReturnSkill extends Skill {
    public ReturnSkill(final Identifier identifier) {
        super(identifier, Skills.THROWING);
    }

    @Override
    public int getCost(final boolean learned, final int level) {
        return 2;
    }
}
