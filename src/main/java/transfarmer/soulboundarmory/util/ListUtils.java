package transfarmer.soulboundarmory.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ListUtils {
    @SafeVarargs
    public static <T> List<T> fromArray(Collection<? extends T>... from) {
        final List<T> to = new ArrayList<>();

        for (final Collection<? extends T> list : from) {
            to.addAll(list);
        }

        return to;
    }

    @SafeVarargs
    public static <T> List<T> fromArray(T[]... from) {
        final List<T> to = new ArrayList<>();

        for (final T[] array : from) {
            to.addAll(Arrays.asList(array));
        }

        return to;
    }

    @SafeVarargs
    public static <T> List<T> arrayList(T... from) {
        final List<T> to = new ArrayList<>();

        for (final T element : from) {
            to.add(element);
        }

        return to;
    }

    @SafeVarargs
    public static <T> List<T> arrayList(Collection<T> collection, T... elements) {
        final List<T> to = new ArrayList<>(collection);

        for (final T element : elements) {
            to.add(element);
        }

        return to;
    }
}
