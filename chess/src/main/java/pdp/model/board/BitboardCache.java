package pdp.model.board;

import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/** Class used to store cache composed of bitboards to avoid recalculating too many methods. */
public class BitboardCache {
  private final int maxNb;
  private final AbstractMap<Long, CachedResult> cache = new ConcurrentHashMap<>();
  private final ConcurrentLinkedDeque<Long> accessOrder = new ConcurrentLinkedDeque<>();

  /**
   * Creates an instance of cache, with the maximum number of elements inside given as an argument.
   *
   * @param maxNb maximum number of elements in the cache
   */
  public BitboardCache(int maxNb) {
    this.maxNb = maxNb;
  }

  /**
   * Retrieves the cached result corresponding to the given hash if it exists. If not, creates it
   * and returns it.
   *
   * @param hash Zobrist hashing corresponding to a board
   * @return instance of cache corresponding to the hash
   */
  public CachedResult getOrCreate(long hash) {
    final boolean[] isNew = {false};
    CachedResult result =
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
          int numToRemove = maxNb / 10 + 1;
          for (int i = 0; i < numToRemove; i++) {
            Long oldestKey = accessOrder.pollFirst();
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
