package transfarmer.soulboundarmory.statistics;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.skill.ISkill;
import transfarmer.soulboundarmory.skill.ISkillLevelable;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Skills implements Iterable<IItem>, INBTSerializable<NBTTagCompound> {
    private final Map<IItem, Map<String, ISkill>> skills;

    public Skills(final List<IItem> items, final ISkill[]... skills) {
        this.skills = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            final Map<String, ISkill> skillMap = new HashMap<>();
            final IItem item = items.get(i);

            this.skills.put(item, skillMap);

            for (final ISkill skill : skills[i]) {
                skillMap.put(skill.getRegistryName(), skill);
                skill.setStorage(this, item);
            }
        }
    }

    public Map<IItem, Map<String, ISkill>> get() {
        return this.skills;
    }

    public Map<String, ISkill> get(final IItem item) {
        return this.get().get(item);
    }

    public ISkill get(final IItem item, final String name) {
        return this.get(item).get(name);
    }

    public void put(final IItem item, ISkill skill) {
        this.get().get(item).put(skill.getRegistryName(), skill);
    }

    public boolean contains(final IItem item, final ISkill skill) {
        final ISkill instance = this.get(item, skill.getRegistryName());

        if (skill instanceof ISkillLevelable && instance instanceof ISkillLevelable) {
            return instance.isLearned() && ((ISkillLevelable) instance).getLevel() >= ((ISkillLevelable) skill).getLevel();
        }

        return instance != null && instance.isLearned();
    }

    public boolean contains(final IItem item, final ISkillLevelable skill, final int level) {
        final ISkillLevelable instance = (ISkillLevelable) this.get(item, skill.getRegistryName());

        return instance != null && instance.isLearned() && instance.getLevel() >= level;
    }

    public void reset() {
        for (final IItem item : this) {
            this.reset(item);
        }
    }

    public void reset(final IItem item) {
        for (final ISkill skill : this.get(item).values()) {
            if (skill.hasDependencies())
                this.get(item).clear();
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound tag = new NBTTagCompound();

        for (final IItem item : this.get().keySet()) {
            if (item != null) {
                tag.setTag(item.toString(), this.serializeNBT(item));
            }
        }

        return tag;
    }

    public NBTTagCompound serializeNBT(final IItem item) {
        final NBTTagCompound tag = new NBTTagCompound();

        for (final ISkill skill : this.get(item).values()) {
            if (skill != null) {
                tag.setTag(skill.getRegistryName(), skill.serializeNBT());
            }
        }

        return tag;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound tag) {
        for (final String itemName : tag.getKeySet()) {
            final IItem item = IItem.get(itemName);

            if (item != null) {
                this.deserializeNBT(tag.getCompoundTag(itemName), item);
            }
        }
    }

    public void deserializeNBT(final NBTTagCompound tag, final IItem item) {
        for (final String skillName : tag.getKeySet()) {
            final ISkill skill = ISkill.get(skillName);

            if (skill != null) {
                this.put(item, skill);

                skill.deserializeNBT(tag.getCompoundTag(skillName));
            }
        }
    }

    @NotNull
    @Override
    public Iterator<IItem> iterator() {
        return this.get().keySet().iterator();
    }
}
