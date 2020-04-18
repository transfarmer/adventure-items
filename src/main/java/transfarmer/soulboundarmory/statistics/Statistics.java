package transfarmer.soulboundarmory.statistics;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Statistics<I extends IItem, C extends ICategory, S extends IStatistic> implements INBTSerializable<NBTTagCompound> {
    private Map<I, Map<C, Map<S, Statistic<C, S>>>> statistics;
    private final Map<String, I> itemNBTNames = new HashMap<>();
    private final Map<String, C> categoryNBTNames = new HashMap<>();
    private final Map<String, S> statisticNBTNames = new HashMap<>();

    public Statistics(final I[] items, final C[] categories, final S[][] statisticNames, final double[][] min) {
        this.statistics = new HashMap<>(items.length, 1);

        for (final I item : items) {
            final Map<C, Map<S, Statistic<C, S>>> statisticCategory = new HashMap<>(categories.length, 1);

            this.statistics.put(item, statisticCategory);

            for (int j = 0; j < categories.length; j++) {
                final Map<S, Statistic<C, S>> statisticTypes = new HashMap<>();

                statisticCategory.put(categories[j], statisticTypes);

                final S[] names = statisticNames[j];
                final double[] statisticMin = min[j];

                for (int k = 0; k < statisticNames.length; k++) {
                    statisticTypes.put(names[k], new Statistic<>(categories[j], names[k], statisticMin[k]));
                }
            }

            this.itemNBTNames.put(item.toString(), item);
        }

        for (final C category : categories) {
            this.categoryNBTNames.put(category.toString(), category);
        }

        for (final S[] category : statisticNames) {
            for (final S statistic : category) {
                final String key = statistic.toString();

                if (!this.statisticNBTNames.containsKey(key)) {
                    this.statisticNBTNames.put(key, statistic);
                }
            }
        }
    }

    public Map<C, Map<S, Statistic<C, S>>> get(final I itemName) {
        return this.statistics.get(itemName);
    }

    public Map<S, Statistic<C, S>> get(final I item, final C category) {
        return this.get(item).get(category);
    }

    public Statistic<C, S> get(final I itemType, final S type) {
        for (final Map<S, Statistic<C, S>> category : this.get(itemType).values()) {
            final Statistic<C, S> statistic = category.get(type);

            if (statistic != null) {
                return statistic;
            }
        }

        return null;
    }

    public void set(final I itemType, final S name, final Number value) {
        this.get(itemType, name).setValue(value);
    }

    public void set(final I itemType, C category, final Map<S, Statistic<C, S>> newValues) {
        this.get(itemType).put(category, newValues);
    }

    public void set(final I itemType, final Map<C, Map<S, Statistic<C, S>>> itemMap) {
        this.statistics.put(itemType, itemMap);
    }

    public void set(Map<I, Map<C, Map<S, Statistic<C, S>>>> statistics) {
        this.statistics = statistics;
    }

    public Statistic<C, S> add(final I itemType, final S statisticType, final Number value) {
        final Statistic<C, S> statistic = this.get(itemType, statisticType);

        statistic.addInPlace(value);

        return statistic;
    }

    public Map<I, Map<C, Map<S, Statistic<C, S>>>> get() {
        return this.statistics;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound tag = new NBTTagCompound();

        for (final I item : this.get().keySet()) {
            tag.setTag(item.toString(), this.serializeNBT(item));
        }

        return tag;
    }

    public NBTTagCompound serializeNBT(final I item) {
        final NBTTagCompound tag = new NBTTagCompound();
        final Map<C, Map<S, Statistic<C, S>>> categories = this.get(item);

        for (final C category : categories.keySet()) {
            tag.setTag(category.toString(), this.serializeNBT(item, category));
        }

        return tag;
    }

    public NBTTagCompound serializeNBT(final I item, final C category) {
        final NBTTagCompound tag = new NBTTagCompound();
        final Map<S, Statistic<C, S>> statistics = this.get(item, category);

        for (final Statistic<C, S> statistic : statistics.values()) {
            tag.setTag(statistic.getType().toString(), statistic.serializeNBT());
        }

        return tag;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound tag) {
        for (final String key : tag.getKeySet()) {
            this.deserializeNBT((NBTTagCompound) tag.getTag(key), this.itemNBTNames.get(key));
        }
    }

    public void deserializeNBT(@Nullable final NBTTagCompound tag, final I item) {
        if (tag != null) {
            for (final String key : tag.getKeySet()) {
                this.deserializeNBT((NBTTagCompound) tag.getTag(key), item, this.categoryNBTNames.get(key));
            }
        }
    }

    public void deserializeNBT(@Nullable final NBTTagCompound tag, final I item, final C category) {
        if (tag != null) {
            for (final String key : tag.getKeySet()) {
                final NBTTagCompound statisticNBT = (NBTTagCompound) tag.getTag(key);
                final Statistic<C, S> statistic = new Statistic<>(category, this.statisticNBTNames.get(key), statisticNBT.getDouble("min"),
                        new BigDecimal(statisticNBT.getString("value")), statisticNBT.getInteger("points")
                );

                this.set(item, statistic.getType(), statistic);
            }
        }
    }
}
