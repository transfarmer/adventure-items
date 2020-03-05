package transfarmer.soulboundarmory.statistics.tool;

import transfarmer.soulboundarmory.statistics.SoulDatum;

public class SoulToolDatum extends SoulDatum {
    public static final SoulDatum TOOL_DATA = new SoulToolDatum();

    protected SoulToolDatum(final int index, final String name) {
        super(index, name);
    }

    public SoulToolDatum() {
        super();
    }
}
