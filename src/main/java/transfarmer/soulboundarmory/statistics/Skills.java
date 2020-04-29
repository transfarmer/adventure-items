package transfarmer.soulboundarmory.statistics;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillLevelable;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Skills implements Iterable<IItem>, INBTSerializable<NBTTagCompound> {
    private final Map<IItem, Map<String, Skill>> skills;

    public Skills(final List<IItem> items, final Skill[]... skills) {
        this.skills = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            final Map<String, Skill> skillMap = new LinkedHashMap<>();
            final IItem item = items.get(i);

            this.skills.put(item, skillMap);

            for (final Skill skill : skills[i]) {
                skillMap.put(skill.getRegistryName(), skill);
                skill.setStorage(this, item);
            }
        }
    }

    public Map<IItem, Map<String, Skill>> get() {
        return this.skills;
    }

    public Map<String, Skill> get(final IItem item) {
        return this.get().get(item);
    }

    public Skill get(final IItem item, final String name) {
        return this.get(item).get(name);
    }

    public void put(final IItem item, Skill skill) {
        this.get().get(item).put(skill.getRegistryName(), skill);
    }

    public boolean contains(final IItem item, final String name) {
        final Skill skill = this.skills.get(item).get(name);

        return skill != null && skill.isLearned();
    }

    public boolean contains(final IItem item, final Skill skill) {
        if (item == null || skill == null) {
            return false;
        }

        final Skill instance = this.get(item, skill.getRegistryName());

        if (skill instanceof SkillLevelable && instance instanceof SkillLevelable) {
            return instance.isLearned() && ((SkillLevelable) instance).getLevel() >= ((SkillLevelable) skill).getLevel();
        }

        return instance != null && instance.isLearned();
    }

    public boolean contains(final IItem item, final SkillLevelable skill, final int level) {
        final SkillLevelable instance = (SkillLevelable) this.get(item, skill.getRegistryName());

        return instance != null && instance.isLearned() && instance.getLevel() >= level;
    }

    public void reset() {
        for (final IItem item : this) {
            this.reset(item);
        }
    }

    public void reset(final IItem item) {
        for (final Skill skill : this.get(item).values()) {
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

        for (final Skill skill : this.get(item).values()) {
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
            final Skill skill = this.get(item, skillName);

            if (skill != null) {
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
