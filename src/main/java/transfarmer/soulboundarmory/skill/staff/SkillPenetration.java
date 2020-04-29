package transfarmer.soulboundarmory.skill.staff;

import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillBaseLevelable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SkillPenetration extends SkillBaseLevelable {
    public SkillPenetration() {
        this(0);
    }
    public SkillPenetration(final int level) {
        super("penetration", level);
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
