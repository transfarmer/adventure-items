package transfarmer.soulboundarmory.statistics.base.iface;

import java.util.ArrayList;
import java.util.List;

public interface IItem {
    List<IItem> ITEMS = new ArrayList<>();

    @Override
    String toString();

    static IItem get(final String string) {
        for (final IItem item : ITEMS) {
            if (item.toString().equals(string)) {
                return item;
            }
        }

        return null;
    }
}
