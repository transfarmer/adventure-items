package user11681.soulboundarmory.skill.staff;

import net.minecraft.util.Identifier;
import user11681.soulboundarmory.skill.Skill;

public class PenetrationSkill extends Skill {
    public PenetrationSkill(final Identifier identifier) {
        super(identifier);
    }

    @Override
    public int getCost(final boolean learned, final int level) {
        return learned ? level + 1 : 2;
    }
}
