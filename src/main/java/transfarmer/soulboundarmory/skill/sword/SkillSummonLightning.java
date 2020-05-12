package transfarmer.soulboundarmory.skill.sword;

import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillBaseLevelable;

import java.util.ArrayList;
import java.util.List;

public class SkillSummonLightning extends SkillBaseLevelable {
    public SkillSummonLightning() {
        this(0);
    }

    public SkillSummonLightning(final int level) {
        super("summon_lightning", null, level);
    }

    @Override
    public @NotNull List<Skill> getDependencies() {
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
