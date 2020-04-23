package transfarmer.soulboundarmory.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"UseBulkOperation", "ManualArrayToCollectionCopy"})
public class CollectionUtil {
    @SafeVarargs
    public static <T> List<T> fromCollections(Collection<? extends T>... from) {
        final List<T> to = new ArrayList<>();

        for (final Collection<? extends T> list : from) {
            to.addAll(list);
        }

        return to;
    }

    @SafeVarargs
    public static <T> List<T> arrayList(T[]... from) {
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

    @SafeVarargs
    public static <T> Set<T> hashSet(T... from) {
        final Set<T> to = new HashSet<T>(from.length);

        for (final T element : from) {
            to.add(element);
        }

        return to;
    }

    @SafeVarargs
    public static <K, V> Map<K, V> hashMap(final K[] keys, V... values) {
        final Map<K, V> to = new HashMap<>(keys.length, 1);

        return hashMap(to, keys, values);
    }

    @SafeVarargs
    public static <K, V> Map<K, V> hashMap(final Map<K, V> original, K[] keys, V... values) {
        for (int i = 0; i < keys.length; i++) {
            original.put(keys[i], values[i]);
        }

        return original;
    }

    @SafeVarargs
    public static <T> void addAll(final Collection<T> collection, T... elements) {
        for (final T element : elements) {
            collection.add(element);
        }
    }
}
