package io.github.ezforever.thatorthis.internal;

import java.io.Serial;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 *  <a href="https://stackoverflow.com/a/54871433">https://stackoverflow.com/a/54871433</a> <br>
 * Fabric not allow use other packages when loading {@link net.fabricmc.loader.impl.launch.knot.KnotClassDelegate#isValidParentUrl(URL, String)}
 */
@SuppressWarnings("JavadocReference")
public class BidirectionalMap<K, V> extends HashMap<K, V> {
    @Serial
    private static final long serialVersionUID = 1L;
    public HashMap<V, K> inversedMap = new HashMap<>();

    public Map<V, K> inverse() {
        return Map.copyOf(inversedMap);
    }

    @Override
    public V remove(Object key) {
        V val = super.remove(key);
        inversedMap.remove(val);
        return val;
    }

    @Override
    public V get(Object key) {
        return super.get(key);
    }

    @Override
    public V put(K key, V value) {
        inversedMap.put(value, key);
        return super.put(key, value);
    }
}
