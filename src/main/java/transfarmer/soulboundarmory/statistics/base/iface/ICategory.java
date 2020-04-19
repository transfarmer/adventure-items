package transfarmer.soulboundarmory.statistics.base.iface;

import java.util.ArrayList;
import java.util.List;

public interface ICategory {
    List<ICategory> CATEGORIES = new ArrayList<>();

    @Override
    String toString();

    static ICategory get(final String string) {
        for (final ICategory category : CATEGORIES) {
            if (category.toString().equals(string)) {
                return category;
            }
        }

        return null;
    }
}
