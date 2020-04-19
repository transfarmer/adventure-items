package transfarmer.soulboundarmory.statistics.base.iface;

import java.util.ArrayList;
import java.util.List;

public interface IStatistic {
    List<IStatistic> STATISTICS = new ArrayList<>();

    @Override
    String toString();

    static IStatistic get(final String string) {
        for (final IStatistic type : STATISTICS) {
            if (type.toString().equals(string)) {
                return type;
            }
        }

        return null;
    }
}
