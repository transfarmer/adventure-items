package transfarmer.soulboundarmory.statistics;

import nerdhub.cardinal.components.api.util.NbtSerializable;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

public class Statistic extends BigDecimal implements NbtSerializable {
    protected Category category;
    protected StatisticType type;

    protected BigDecimal value;
    protected double min;
    protected int points;

    public Statistic(final Category category, final StatisticType statistic, final double min) {
        this(category, statistic, min, BigDecimal.valueOf(min));
    }

    public Statistic(final Category category, final StatisticType statistic, final double min, final BigDecimal value) {
        this(category, statistic, min, value, 0);
    }

    public Statistic(final Category category, final StatisticType statistic, final double min, final BigDecimal value, final int points) {
        super(min);

        this.type = statistic;
        this.category = category;
        this.min = min;
        this.value = value;
        this.points = points;
    }

    public Statistic(final CompoundTag tag) {
        super(0);

        this.fromTag(tag);
    }

    @Override
    public String toString() {
        return String.format("Statistic{name: %s, type: %s, min: %f, value: %f}", this.type, this.category, this.min, this.value.doubleValue());
    }

    public Statistic clone(final BigDecimal value) {
        return new Statistic(this.category, this.type, this.min, value, this.points);
    }

    public StatisticType getType() {
        return this.type;
    }

    public Category getCategory() {
        return this.category;
    }

    public double min() {
        return this.min;
    }

    public void setMin(final double min) {
        this.min = min;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(final int points) {
        this.points = points;
    }

    public void addPoints(final int points) {
        this.points += points;
    }

    public void addPoint() {
        this.points++;
    }

    public void setValue(final BigDecimal value) {
        this.value = value;
    }

    public void setValue(final Number value) {
        this.value = BigDecimal.valueOf(value.doubleValue());
    }

    public boolean aboveMin() {
        return this.greaterThan(this.min);
    }

    @Override
    public byte byteValue() {
        return this.value.byteValue();
    }

    @Override
    public short shortValue() {
        return this.value.shortValue();
    }

    @Override
    public int intValue() {
        return this.value.intValue();
    }

    @Override
    public long longValue() {
        return this.value.longValue();
    }

    @Override
    public float floatValue() {
        return this.value.floatValue();
    }

    @Override
    public double doubleValue() {
        return this.value.doubleValue();
    }

    public boolean greaterThan(final Number number) {
        return this.doubleValue() > number.doubleValue();
    }

    public boolean greaterThanOrEqualTo(final Number number) {
        return this.greaterThan(number) || this.doubleValue() == number.doubleValue();
    }

    public boolean lessThan(final Number number) {
        return this.doubleValue() < number.doubleValue();
    }

    public boolean lessThanOrEqualTo(final Number number) {
        return this.lessThan(number) || this.doubleValue() == number.doubleValue();
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
        tag.putString("value", this.value.toString());
        tag.putInt("points", this.points);

        return tag;
    }

    @Override
    public void fromTag(final CompoundTag tag) {
        this.min = tag.getDouble("min");
        this.value = new BigDecimal(tag.getString("value"));
        this.points = tag.getInt("points");
    }

    public void reset() {
        this.setValue(this.min);
        this.setPoints(0);
    }
}
