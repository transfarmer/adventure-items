package transfarmer.soulboundarmory.util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class IndexedLinkedHashMap<K, V> extends LinkedHashMap<K, V> implements IndexedMap<K, V> {
    public IndexedLinkedHashMap() {
        super();
    }

    public IndexedLinkedHashMap(final int initialCapacity) {
        super(initialCapacity);
    }

    @Nonnull
    @Override
    public List<K> keyList() {
        return new ArrayList<>(this.keySet());
    }

    @Override
    @Nonnull
    public List<V> valueList() {
        return new ArrayList<>(this.values());
    }

    @Override
    public K getKey(final int index) {
        return this.keyList().get(index);
    }

    @Override
    public V getValue(final int index) {
        return this.valueList().get(index);
    }

    @Override
    public int indexOfKey(final K key) {
        return this.keyList().indexOf(key);
    }

    @Override
    @Nonnull
    public Iterator<K> iterator() {
        return this.keyList().iterator();
    }
}
