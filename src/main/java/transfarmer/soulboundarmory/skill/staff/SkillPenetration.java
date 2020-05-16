package transfarmer.soulboundarmory.skill.staff;

import transfarmer.soulboundarmory.skill.Skill;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SkillPenetration extends Skill {
    public SkillPenetration() {
        this(0);
    }
    public SkillPenetration(final int level) {
        super("penetration", null, level);
    }

    @Override
    @Nonnull
    public List<Skill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return this.learned ? this.level + 1 : 2;
    }
}
