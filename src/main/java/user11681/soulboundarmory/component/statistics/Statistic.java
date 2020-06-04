package user11681.soulboundarmory.component.statistics;

import java.math.BigDecimal;
import javax.annotation.Nonnull;
import nerdhub.cardinal.components.api.util.NbtSerializable;
import net.minecraft.nbt.CompoundTag;

public class Statistic extends Number implements NbtSerializable {
    protected final Category category;
    protected final StatisticType type;

    protected BigDecimal value;
    protected double min;
    protected double max;
    protected int points;

    public Statistic(final Category category, final StatisticType statistic) {
        super();

        this.type = statistic;
        this.category = category;
        this.value = BigDecimal.valueOf(0);
        this.min = 0;
        this.max = Double.MAX_VALUE;
    }

    @Override
    public String toString() {
        return String.format("Statistic{name: %s, type: %s, min: %.3f, max: %.3f, value: %s}", this.type, this.category, this.min, this.max, this.value.toString());
    }

    public StatisticType getType() {
        return this.type;
    }

    public Category getCategory() {
        return this.category;
    }

    public double getMin() {
        return this.min;
    }

    public void setMin(final double min) {
        this.min = min;

        if (this.doubleValue() < min) {
            this.setValue(min);
        }
    }

    public boolean isAboveMin() {
        return this.greaterThan(this.min);
    }

    public double getMax() {
        return this.max;
    }

    public void setMax(final double max) {
        this.max = max;

        if (this.doubleValue() > max) {
            this.setValue(max);
        }
    }

    public boolean isBelowMax() {
        return this.lessThan(this.max);
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(final int points) {
        this.points = points;
    }

    public void incrementPoints(final int points) {
        this.points += points;
    }

    public void incrementPoints() {
        ++this.points;
    }

    public void setValue(final BigDecimal value) {
        this.value = value;
    }

    public void setValue(final Number value) {
        this.value = BigDecimal.valueOf(value.doubleValue());
    }

    public byte byteValue() {
        return this.value.byteValue();
    }

    public short shortValue() {
        return this.value.shortValue();
    }

    public int intValue() {
        return this.value.intValue();
    }

    public long longValue() {
        return this.value.longValue();
    }

    public float floatValue() {
        return this.value.floatValue();
    }

    public double doubleValue() {
        return this.value.doubleValue();
    }

    public boolean lessThan(final Number number) {
        return this.doubleValue() < number.doubleValue();
    }

    public boolean lessThanOrEqualTo(final Number number) {
        return !this.greaterThan(number);
    }

    public boolean greaterThan(final Number number) {
        return this.doubleValue() > number.doubleValue();
    }

    public boolean greaterThanOrEqualTo(final Number number) {
        return !this.lessThan(number);
    }

    public void add(final Number number) {
        this.value = this.value.add(number instanceof BigDecimal ? (BigDecimal) number : BigDecimal.valueOf(number.doubleValue()));
    }

    public CompoundTag toTag() {
        return this.toTag(new CompoundTag());
    }

    @Nonnull
    @Override
    public CompoundTag toTag(final CompoundTag tag) {
        tag.putDouble("min", this.min);
        tag.putDouble("max", this.max);
        tag.putString("value", this.value.toString());
        tag.putInt("points", this.points);

        return tag;
    }

    @Override
    public void fromTag(final CompoundTag tag) {
        this.min = tag.getDouble("min");
        this.max = tag.getDouble("max");
        this.value = new BigDecimal(tag.getString("value"));
        this.points = tag.getInt("points");
    }

    public void reset() {
        this.setValue(this.min);
        this.setPoints(0);
    }
}
