package transfarmer.soulboundarmory.skill.staff;

import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillBaseLevelable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static transfarmer.soulboundarmory.skill.Skills.VULNERABILITY;

public class SkillEndermanacle extends SkillBaseLevelable {
    public SkillEndermanacle() {
        this(0);
    }

    public SkillEndermanacle(final int level) {
        super("endermanacle", level);
    }

    @Nonnull
    @Override
    public List<Skill> getDependencies() {
        return Collections.singletonList(this.storage.get(this.item, VULNERABILITY));
    }

    @Override
    public int getCost() {
        return this.learned ? this.level + 1 : 3;
    }
}
