package transfarmer.soulboundarmory.statistics;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import transfarmer.soulboundarmory.statistics.base.iface.ICategory;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;

import java.math.BigDecimal;

public class Statistic extends BigDecimal implements INBTSerializable<CompoundTag> {
    private final ICategory category;
    private final IStatistic type;
    private double min;
    private BigDecimal value;
    private int points;

    public Statistic(final ICategory category, final IStatistic statistic, final double min, final BigDecimal value, final int points) {
        super(min);

        this.type = statistic;
        this.category = category;
        this.min = min;
        this.value = value;
        this.points = points;
    }

    public Statistic(final ICategory category, final IStatistic statistic, final double min, final BigDecimal value) {
        this(category, statistic, min, value, 0);
    }

    public Statistic(final ICategory category, final IStatistic statistic, final double min) {
        this(category, statistic, min, BigDecimal.valueOf(min));
    }

    @Override
    public String toString() {
        return String.format("Statistic{name: %s, type: %s, min: %f, value: %f}", this.type, this.category, this.min, this.value.doubleValue());
    }

    public Statistic clone(final BigDecimal value) {
        return new Statistic(this.category, this.type, this.min, value, this.points);
    }

    public IStatistic getType() {
        return this.type;
    }

    public ICategory getCategory() {
        return this.category;
    }

    public double min() {
        return this.min;
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

    @Override
    public CompoundTag serializeNBT() {
        final CompoundTag tag = new CompoundTag();

        tag.setDouble("min", this.min);
        tag.setString("value", this.value.toString());
        tag.putInt("points", this.points);

        return tag;
    }

    @Override
    public void deserializeNBT(final CompoundTag tag) {
        this.min = tag.getDouble("min");
        this.value = new BigDecimal(tag.getString("value"));
        this.points = tag.getInteger("points");
    }

    public void reset() {
        this.setValue(this.min);
        this.setPoints(0);
    }
}
