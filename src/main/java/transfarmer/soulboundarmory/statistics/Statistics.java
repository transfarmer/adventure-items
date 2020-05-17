package transfarmer.soulboundarmory.statistics;

import nerdhub.cardinal.components.api.util.NbtSerializable;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Statistics implements NbtSerializable, Iterable<Category> {
    private final Map<Category, Map<StatisticType, Statistic>> categories;

    protected Statistics() {
        this.categories = new HashMap<>();
    }

    public static Builder builder() {
        return new Builder(new Statistics());
    }

    public Map<StatisticType, Statistic> get(final Category category) {
        return this.categories.get(category);
    }

    public Statistic get(final StatisticType type) {
        for (final Map<StatisticType, Statistic> category : this.get().values()) {
            final Statistic statistic = category.get(type);

            if (statistic != null) {
                return statistic;
            }
        }

        return null;
    }

    public void set(final StatisticType name, final Number value) {
        this.get(name).setValue(value);
    }

    public void set(Category category, final Map<StatisticType, Statistic> newValues) {
        this.categories.put(category, newValues);
    }

    public Statistic add(final StatisticType statisticType, final Number value) {
        final Statistic statistic = this.get(statisticType);

        statistic.add(value);

        return statistic;
    }

    public Statistic get(final Category category, final StatisticType statistic) {
        return this.get(category).get(statistic);
    }

    public Map<Category, Map<StatisticType, Statistic>> get() {
        return this.categories;
    }

    public int size(final Category category) {
        return this.categories.get(category).size();
    }

    @Nonnull
    @Override
    public CompoundTag toTag(@Nonnull final CompoundTag tag) {
        for (final Category category : this) {
            tag.put(category.toString(), this.toTag(category));
        }

        return tag;
    }

    public CompoundTag toTag(final Category category) {
        final CompoundTag tag = new CompoundTag();

        for (final Statistic statistic : this.get(category).values()) {
            tag.put(statistic.getType().toString(), statistic.toTag());
        }

        return tag;
    }

    @Override
    public void fromTag(final CompoundTag tag) {
        for (final String key : tag.getKeys()) {
            this.fromTag(tag.getCompound(key), Category.valueOf(key));
        }
    }

    public void fromTag(final CompoundTag tag, final Category category) {
        if (category != null) {
            for (final String identifier : tag.getCompound(category.toString()).getKeys()) {
                final Statistic statistic = this.get(StatisticType.valueOf(identifier));

                if (statistic != null) {
                    statistic.fromTag(tag.getCompound(identifier));
                }
            }
        }
    }

    public void reset() {
        for (final Category category : this) {
            this.reset(category);
        }
    }

    public void reset(final Category category) {
        for (final Statistic statistic : this.get(category).values()) {
            statistic.reset();
        }
    }

    @Nonnull
    @Override
    public Iterator<Category> iterator() {
        return this.categories.keySet().iterator();
    }

    public static class Builder {
        protected final Statistics statistics;

        protected Builder(final Statistics statistics) {
            this.statistics = statistics;
        }

        public Builder category(final Category categoryType, final StatisticType... statisticTypes) {
            for (final StatisticType statisticType : statisticTypes) {
                final Map<StatisticType, Statistic> category = this.statistics.get(categoryType);

                category.put(statisticType, new Statistic(categoryType, statisticType, 0));
            }

            return this;
        }

        public Builder min(final double min, final Category category) {
            for (final StatisticType statistic : this.statistics.get(category).keySet()) {
                this.min(min, statistic);
            }

            return this;
        }

        public Builder min(final double min, final StatisticType statistic) {
            this.statistics.get(statistic).setMin(min);

            return this;
        }

        public Statistics build() {
            return this.statistics;
        }
    }
}
