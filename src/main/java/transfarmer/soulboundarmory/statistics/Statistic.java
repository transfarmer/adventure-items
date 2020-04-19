package transfarmer.soulboundarmory.statistics;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import transfarmer.soulboundarmory.statistics.base.iface.ICategory;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Statistic extends BigDecimal implements INBTSerializable<NBTTagCompound> {
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

    public Statistic add(final Number number) {
        return this.clone(this.value.add(BigDecimal.valueOf(number.doubleValue())));
    }

    public void addInPlace(final Number number) {
        this.value = this.value.add(BigDecimal.valueOf(number.doubleValue()));
    }

    public Statistic subtract(final Number number) {
        return this.clone(this.value.subtract(BigDecimal.valueOf(number.doubleValue())));
    }

    public Statistic multiply(final Number number) {
        return this.clone(this.value.multiply(BigDecimal.valueOf(number.doubleValue())));
    }

    public Statistic divide(final Number number) {
        return this.clone(this.value.divide(BigDecimal.valueOf(number.doubleValue()), RoundingMode.HALF_UP));
    }

    public Statistic add(final BigDecimal number) {
        return this.clone(this.value.add(number));
    }

    public void addInPlace(final BigDecimal number) {
        this.value = this.value.add(number);
    }

    public Statistic subtract(final BigDecimal number) {
        return this.clone(this.value.subtract(number));
    }

    public Statistic multiply(final BigDecimal number) {
        return this.clone(this.value.multiply(number));
    }

    public Statistic divide(final BigDecimal number) {
        return this.clone(this.value.divide(number, RoundingMode.HALF_UP));
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound tag = new NBTTagCompound();

        tag.setDouble("min", this.min);
        tag.setString("value", this.value.toString());
        tag.setInteger("points", this.points);

        return tag;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound tag) {
        this.min = tag.getDouble("min");
        this.value = new BigDecimal(tag.getString("value"));
        this.points = tag.getInteger("points");
    }

    public void reset() {
        this.setValue(this.min);
        this.setPoints(0);
    }
}
