package transfarmer.soulboundarmory.statistics;

public abstract class Statistic {
    protected int index;
    protected String name;

    protected Statistic(final int index, final String name) {
        this.index = index;
        this.name = name;
    }

    protected Statistic() {}

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Statistic && obj.toString().equals(this.toString());
    }

    public int getIndex() {
        return this.index;
    }

    public String toString() {
        return this.name;
    }
}