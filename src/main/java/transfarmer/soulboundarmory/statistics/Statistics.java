package transfarmer.soulboundarmory.statistics;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import transfarmer.soulboundarmory.statistics.base.iface.ICategory;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statistics implements INBTSerializable<CompoundTag> {
    private final Map<IItem, Map<ICategory, Map<IStatistic, Statistic>>> statistics;

    public Statistics(final List<IItem> items, final ICategory[] categories, final IStatistic[][] statisticNames,
                      final double[][][] min) {
        this.statistics = new HashMap<>(items.size(), 1);

        for (int i = 0, itemsLength = items.size(); i < itemsLength; i++) {
            final IItem item = items.get(i);
            final Map<ICategory, Map<IStatistic, Statistic>> statisticCategory = new HashMap<>(categories.length, 1);

            this.statistics.put(item, statisticCategory);

            for (int j = 0; j < categories.length; j++) {
                final Map<IStatistic, Statistic> statisticTypes = new HashMap<>();

                statisticCategory.put(categories[j], statisticTypes);

                final IStatistic[] names = statisticNames[j];

                for (int k = 0; k < names.length; k++) {
                    statisticTypes.put(names[k], new Statistic(categories[j], names[k], min[i][j][k]));
                }
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        for (final IItem item : this.get().keySet()) {
            builder.append(this.toString(item));
        }

        return String.format("\t%s{\n%s\t}", super.toString(), builder.toString());
    }

    public String toString(final IItem item) {
        final StringBuilder builder = new StringBuilder();

        for (final ICategory category : this.get(item).keySet()) {
            builder.append(this.toString(item, category));
        }

        return String.format("\t\t%s{\n%s\t\t}\n", item, builder.toString());
    }

    public String toString(final IItem item, final ICategory category) {
        final StringBuilder builder = new StringBuilder();

        for (final IStatistic statistic : this.get(item, category).keySet()) {
            builder.append(String.format("\t\t\t\t%s\n", this.get(item, category, statistic)));
        }

        return String.format("\t\t\t%s{\n%s\t\t\t}\n", category, builder.toString());
    }

    public Map<ICategory, Map<IStatistic, Statistic>> get(final IItem item) {
        return this.statistics.get(item);
    }

    public Map<IStatistic, Statistic> get(final IItem item, final ICategory category) {
        return this.get(item).get(category);
    }

    public Statistic get(final IItem item, final IStatistic type) {
        for (final Map<IStatistic, Statistic> category : this.get(item).values()) {
            final Statistic statistic = category.get(type);

            if (statistic != null) {
                return statistic;
            }
        }

        return null;
    }

    public Statistic get(final IItem item, final ICategory category, final IStatistic statistic) {
        return this.get(item, category).get(statistic);
    }

    public void set(final IItem item, final IStatistic name, final Number value) {
        this.get(item, name).setValue(value);
    }

    public void set(final IItem itemType, ICategory category, final Map<IStatistic, Statistic> newValues) {
        this.get(itemType).put(category, newValues);
    }

    public void set(final IItem itemType, final Map<ICategory, Map<IStatistic, Statistic>> itemMap) {
        this.statistics.put(itemType, itemMap);
    }

    public Statistic add(final IItem itemType, final IStatistic statisticType, final Number value) {
        final Statistic statistic = this.get(itemType, statisticType);

        statistic.add(value);

        return statistic;
    }

    public Map<IItem, Map<ICategory, Map<IStatistic, Statistic>>> get() {
        return this.statistics;
    }

    @Override
    public CompoundTag serializeNBT() {
        final CompoundTag tag = new CompoundTag();

        for (final IItem item : this.get().keySet()) {
            tag.put(item.toString(), this.serializeNBT(item));
        }

        return tag;
    }

    public CompoundTag serializeNBT(final IItem item) {
        final CompoundTag tag = new CompoundTag();
        final Map<ICategory, Map<IStatistic, Statistic>> categories = this.get(item);

        for (final ICategory category : categories.keySet()) {
            tag.put(category.toString(), this.serializeNBT(item, category));
        }

        return tag;
    }

    public CompoundTag serializeNBT(final IItem item, final ICategory category) {
        final CompoundTag tag = new CompoundTag();
        final Map<IStatistic, Statistic> statistics = this.get(item, category);

        for (final Statistic statistic : statistics.values()) {
            tag.put(statistic.getType().toString(), statistic.serializeNBT());
        }

        return tag;
    }

    @Override
    public void deserializeNBT(final CompoundTag tag) {
        if (tag != null) {
            for (final String key : tag.getKeys()) {
                this.deserializeNBT(tag.getCompound(key), IItem.get(key));
            }
        }
    }

    public void deserializeNBT(@Nullable final CompoundTag tag, final IItem item) {
        if (tag != null) {
            for (final String key : tag.getKeys()) {
                this.deserializeNBT(tag.getCompound(key), item, ICategory.get(key));
            }
        }
    }

    public void deserializeNBT(@Nullable final CompoundTag tag, final IItem item, final ICategory category) {
        if (tag != null) {
            for (final String key : tag.getKeys()) {
                final CompoundTag statisticNBT = tag.getCompound(key);
                final IStatistic type = IStatistic.get(key);

                if (type != null) {
                    final Statistic statistic = new Statistic(category, type, statisticNBT.getDouble("min"),
                            new BigDecimal(statisticNBT.getString("value")), statisticNBT.getInteger("points")
                    );

                    this.set(item, statistic.getType(), statistic);
                }

            }
        }
    }

    public void reset() {
        for (final IItem item : this.get().keySet()) {
            this.reset(item);
        }
    }

    public void reset(final IItem item) {
        for (final ICategory category : this.get(item).keySet()) {
            this.reset(item, category);
        }
    }

    public void reset(final IItem item, final ICategory category) {
        for (final Statistic statistic : this.get(item, category).values()) {
            statistic.reset();
        }
    }
}
