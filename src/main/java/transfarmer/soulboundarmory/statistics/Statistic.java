package transfarmer.soulboundarmory.statistics;

public abstract class Statistic {
    protected int index;

    protected Statistic(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }
}
