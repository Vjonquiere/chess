package pdp.model.board;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/** Class used to store cache composed of bitboards to avoid recalculating too many methods. */
public class BitboardCache {
  /** Maximum number of elements in the cache. */
  private final int maxNb;

  /** Map storing a hash of a board and a cache result. Structure used for the cache. */
  private final Map<Long, CachedResult> cache = new ConcurrentHashMap<>();

  /**
   * Queue to store the access to the different cache elements. Helps for the replacement of values
   * in the cache.
   */
  private final ConcurrentLinkedDeque<Long> accessOrder = new ConcurrentLinkedDeque<>();

  /**
   * Creates an instance of cache, with the maximum number of elements inside given as an argument.
   *
   * @param maxNb maximum number of elements in the cache
   */
  public BitboardCache(final int maxNb) {
    this.maxNb = maxNb;
  }

  /**
   * Retrieves the cached result corresponding to the given hash if it exists. If not, creates it
   * and returns it.
   *
   * @param hash Zobrist hashing corresponding to a board
   * @return instance of cache corresponding to the hash
   */
  public CachedResult getOrCreate(final long hash) {
    final boolean[] isNew = {false};
    final CachedResult result =
        cache.computeIfAbsent(
            hash,
            k -> {
              isNew[0] = true;
              accessOrder.addLast(k);
              return new CachedResult();
            });
    if (isNew[0]) {
      evictIfNecessary();
    }
    return result;
  }

  private void evictIfNecessary() {
    if (cache.size() >= maxNb) {
      synchronized (accessOrder) {
        if (cache.size() >= maxNb) {
          final int numToRemove = maxNb / 10 + 1;
          for (int i = 0; i < numToRemove; i++) {
            final Long oldestKey = accessOrder.pollFirst();
            if (oldestKey != null) {
              cache.remove(oldestKey);
            } else {
              break;
            }
          }
        }
      }
    }
  }
}
