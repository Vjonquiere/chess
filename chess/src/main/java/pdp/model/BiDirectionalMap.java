package pdp.model;

import java.util.HashMap;
import java.util.Map;

public class BiDirectionalMap<K, V> {
  private final Map<K, V> forwardMap = new HashMap<>();
  private final Map<V, K> reverseMap = new HashMap<>();

  public void put(K key, V value) {
    if (forwardMap.containsKey(key) || reverseMap.containsKey(value)) {
      throw new IllegalArgumentException("Duplicate key or value not allowed");
    }
    forwardMap.put(key, value);
    reverseMap.put(value, key);
  }

  public V getFromKey(K key) {
    return forwardMap.get(key);
  }

  public K getFromValue(V value) {
    return reverseMap.get(value);
  }

  public void removeByKey(K key) {
    V value = forwardMap.remove(key);
    if (value != null) {
      reverseMap.remove(value);
    }
  }

  public void removeByValue(V value) {
    K key = reverseMap.remove(value);
    if (key != null) {
      forwardMap.remove(key);
    }
  }
}
