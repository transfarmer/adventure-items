package transfarmer.soulboundarmory.skill.staff;

import transfarmer.soulboundarmory.skill.Skill;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SkillEndermanacle extends Skill {
    public SkillEndermanacle() {
        this(0);
    }

    public SkillEndermanacle(final int level) {
        super("endermanacle", null, level);
    }

    @Nonnull
    @Override
    public List<Skill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return this.learned ? this.level + 1 : 3;
    }
}
