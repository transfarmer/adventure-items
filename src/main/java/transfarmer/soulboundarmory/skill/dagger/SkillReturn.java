package transfarmer.soulboundarmory.skill.dagger;

import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillBaseLevelable;
import transfarmer.farmerlib.util.CollectionUtil;

import java.util.List;

import static transfarmer.soulboundarmory.init.Skills.THROWING;

public class SkillReturn extends SkillBaseLevelable {
    public SkillReturn() {
        this(0);
    }

    public SkillReturn(final int level) {
        super("return", null, level);
    }

    @Override
    public @NotNull List<Skill> getDependencies() {
        return this.storage == null
                ? CollectionUtil.arrayList(new SkillThrowing())
                : CollectionUtil.arrayList(this.storage.get(this.item, THROWING));
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public boolean canBeUpgraded(final int points) {
        return this.canBeUpgraded();
    }
}
