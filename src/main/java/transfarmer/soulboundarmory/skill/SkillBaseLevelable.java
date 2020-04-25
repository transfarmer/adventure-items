package transfarmer.soulboundarmory.skill;

import org.jetbrains.annotations.NotNull;

public abstract class SkillBaseLevelable extends SkillBase implements ISkillLevelable {
    protected int level;

    protected SkillBaseLevelable(final String name, final int level) {
        super(name);

        this.level = level;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public void setLevel(final int level) {
        this.level = level;
    }

    @Override
    public int compareTo(@NotNull final ISkillLevelable other) {
        return this.getLevel() - other.getLevel();
    }
}
