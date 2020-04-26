package transfarmer.soulboundarmory.skill;

import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

public abstract class SkillBaseLevelable extends SkillBase implements SkillLevelable {
    protected int maxLevel;
    protected int level;

    public SkillBaseLevelable(final String name, final int level) {
        this(name, level, -1);
    }

    public SkillBaseLevelable(final String name, final int level, final int maxLevel) {
        super(name);

        this.level = level;
        this.maxLevel = maxLevel;
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
    public boolean canBeUpgraded(final int points) {
        return this.canBeUpgraded() && points >= this.getCost();
    }

    @Override
    public boolean canBeUpgraded() {
        return this.learned && (this.getCost() >= 0 || this.maxLevel < 0 || this.level < this.maxLevel);
    }

    @Override
    public void upgrade() {
        this.level++;
    }

    @Override
    public int compareTo(@NotNull final SkillLevelable other) {
        return this.getLevel() - other.getLevel();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound tag = super.serializeNBT();

        tag.setInteger("level", this.level);
        tag.setInteger("maxLevel", this.maxLevel);

        return tag;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound tag) {
        super.deserializeNBT(tag);

        this.level = tag.getInteger("level");
        this.maxLevel = tag.getInteger("maxLevel");
    }
}
