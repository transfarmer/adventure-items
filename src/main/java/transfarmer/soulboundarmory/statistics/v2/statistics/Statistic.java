package transfarmer.soulboundarmory.statistics.v2.statistics;

import java.math.*;

public class Statistic extends BigDecimal {
    private BigDecimal value;
    private final String name;
    private final String type;
    private final double min;
    private int points;

    public Statistic(final String name, final String type, final double min) {
        super(min);

        this.name = name;
        this.type = type;
        this.min = min;
    }

    public Statistic(final String name, final String type) {
        this(name, type, 0);
    }

    @Override
    public String toString() {
        return String.format("Statistic{name: %s, type: %s, min: %f, value: %f}", this.name, this.type, this.min, this.value.doubleValue());
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public double getMin() {
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

    public void set(final BigDecimal value) {
        this.value = value;
    }

    public void set(final Number value) {
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
        return (Statistic) this.value.add(BigDecimal.valueOf(number.doubleValue()));
    }

    public Statistic subtract(final Number number) {
        return (Statistic) this.value.subtract(BigDecimal.valueOf(number.doubleValue()));
    }

    public Statistic multiply(final Number number) {
        return (Statistic) this.value.multiply(BigDecimal.valueOf(number.doubleValue()));
    }

    public Statistic divide(final Number number) {
        return (Statistic) this.value.divide(BigDecimal.valueOf(number.doubleValue()), RoundingMode.HALF_UP);
    }

    public Statistic add(final Statistic number) {
        return (Statistic) this.value.add(BigDecimal.valueOf(number.doubleValue()));
    }

    public Statistic subtract(final Statistic number) {
        return (Statistic) this.value.subtract(BigDecimal.valueOf(number.doubleValue()));
    }

    public Statistic multiply(final Statistic number) {
        return (Statistic) this.value.multiply(BigDecimal.valueOf(number.doubleValue()));
    }

    public Statistic divide(final Statistic number) {
        return (Statistic) this.value.divide(BigDecimal.valueOf(number.doubleValue()), RoundingMode.HALF_UP);
    }
}
