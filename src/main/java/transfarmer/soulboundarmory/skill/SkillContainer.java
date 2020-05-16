package transfarmer.soulboundarmory.skill;

import nerdhub.cardinal.components.api.util.NbtSerializable;
import net.minecraft.nbt.CompoundTag;
import transfarmer.soulboundarmory.statistics.SkillStorage;

import javax.annotation.Nonnull;
import java.util.List;

public class SkillContainer implements NbtSerializable {
    protected final Skill skill;
    protected final int maxLevel;
    protected final SkillStorage storage;

    protected boolean learned;
    protected int level;

    public SkillContainer(final Skill skill, final SkillStorage storage) {
        this.skill = skill;
        this.level = 0;
        this.maxLevel = skill.maxLevel;
        this.storage = storage;
    }

    public Skill getSkill() {
        return this.skill;
    }

    public List<Skill> getDependencies() {
        return this.skill.getDependencies();
    }

    public boolean hasDependencies() {
        return this.skill.hasDependencies();
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public void upgrade() {
        this.level++;
    }

    public boolean canBeUpgraded(final int points) {
        return this.canBeUpgraded() && points >= this.getCost();
    }

    public boolean canBeUpgraded() {
        return this.learned && (this.getCost() >= 0 || this.maxLevel < 0 || this.level < this.maxLevel);
    }

    public int getCost() {
        return this.skill.getCost(this.level);
    }

    public boolean isLearned() {
        return this.learned;
    }

    public boolean canBeLearned() {
        for (final Skill dependency : this.skill.getDependencies()) {
            if (!this.storage.get(dependency).isLearned()) {
                return false;
            }
        }

        return !this.learned;
    }

    public boolean canBeLearned(final int points) {
        return this.canBeLearned() && points >= this.getCost();
    }

    public void learn() {
        this.learned = true;
    }

    public int compareTo(@Nonnull final SkillContainer other) {
        return this.getLevel() - other.getLevel();
    }

    @Nonnull
    @Override
    public CompoundTag toTag(final CompoundTag tag) {
        tag.putBoolean("learned", this.learned);
        tag.putInt("level", this.level);

        return tag;
    }

    @Override
    public void fromTag(final CompoundTag tag) {
        this.learned = tag.getBoolean("learned");
        this.level = tag.getInt("level");
    }
}
