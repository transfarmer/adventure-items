package user11681.soulboundarmory.skill.pick;

import net.minecraft.util.Identifier;
import user11681.soulboundarmory.skill.Skill;

public class AmbidexteritySkill extends Skill {
    public AmbidexteritySkill(final Identifier identifier) {
        super(identifier);
    }

    @Override
    public int getCost(final boolean learned, final int level) {
        return 5;
    }
}
