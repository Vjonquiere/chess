package pdp.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Bidirectional map. Find an element either by the key or the value.
 *
 * @param <K> class of the keys
 * @param <V> class of the values
 */
public class BiDirectionalMap<K, V> {
  /** Map mapping the first type (K) to the second (V). */
  private final Map<K, V> forwardMap = new HashMap<>();

  /** Map mapping the second type (V) to the first (K). */
  private final Map<V, K> reverseMap = new HashMap<>();

  /**
   * Add a new element to the bidirectional map.
   *
   * @param key element's key
   * @param value element's value
   */
  public void put(final K key, final V value) {
    if (forwardMap.containsKey(key) || reverseMap.containsKey(value)) {
      throw new IllegalArgumentException("Duplicate key or value not allowed");
    }
    forwardMap.put(key, value);
    reverseMap.put(value, key);
  }

  /**
   * Retrieves the of value of an element value from its key.
   *
   * @param key key of the element
   * @return value of the key.
   */
  public V getFromKey(final K key) {
    return forwardMap.get(key);
  }

  /**
   * Retrieves the key of an element from its value.
   *
   * @param value value of the element
   * @return key of the value
   */
  public K getFromValue(final V value) {
    return reverseMap.get(value);
  }

  /**
   * Remove an element of the bidirectional map by its key.
   *
   * @param key key to find and remove.
   */
  public void removeByKey(final K key) {
    final V value = forwardMap.remove(key);
    if (value != null) {
      reverseMap.remove(value);
    }
  }

  /**
   * Remove an element of the bidirectional map by its value.
   *
   * @param value value to find and remove.
   */
  public void removeByValue(final V value) {
    final K key = reverseMap.remove(value);
    if (key != null) {
      forwardMap.remove(key);
    }
  }
}
