package transfarmer.soulboundarmory.skill.sword;

import org.jetbrains.annotations.Nonnull;
import transfarmer.soulboundarmory.skill.Skill;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SkillSummonLightning extends Skill {
    public SkillSummonLightning() {
        this(0);
    }

    public SkillSummonLightning(final int level) {
        super("summon_lightning", null, level);
    }

    @Nonnull
    @Override
    public @Nonnull List<Skill> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public boolean canBeUpgraded(final int points) {
        return this.canBeUpgraded();
    }
}
