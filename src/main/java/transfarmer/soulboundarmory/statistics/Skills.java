package transfarmer.soulboundarmory.statistics;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.skill.ISkill;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.util.CollectionUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Skills implements Iterable<IItem>, INBTSerializable<NBTTagCompound> {
    private final Map<IItem, Map<String, ISkill>> skills;

    public Skills(final IItem[] items, final ISkill[]... skills) {
        this.skills = new HashMap<>();

        for (int i = 0; i < items.length; i++) {
            for (final ISkill skill : skills[i]) {
                this.skills.put(items[i], CollectionUtil.hashMap(skill.getRegistryName(), skill));
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
                tag.setString(skill.getRegistryName(), "");
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
            }
        }
    }

    @NotNull
    @Override
    public Iterator<IItem> iterator() {
        return this.get().keySet().iterator();
    }
}
