package transfarmer.soulboundarmory.skill;

import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

public abstract class SkillBaseLevelable extends SkillBase implements ISkillLevelable {
    protected int level;
    protected int maxLevel;

    public SkillBaseLevelable(final String name, final int level, final int maxLevel) {
        super(name);

        this.level = level;
    }

    public SkillBaseLevelable(final String name, final int level) {
        this(name, level, -1);
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
    public boolean canBeUpgraded() {
        return this.learned && (this.maxLevel < 0 || this.level < this.maxLevel);
    }

    @Override
    public void upgrade() {
        this.level++;
    }

    @Override
    public int compareTo(@NotNull final ISkillLevelable other) {
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
