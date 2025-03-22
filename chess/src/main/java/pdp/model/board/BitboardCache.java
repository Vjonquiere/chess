package pdp.model.board;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BitboardCache {
  private final int maxNb;
  private final ConcurrentHashMap<Long, CachedResult> cache = new ConcurrentHashMap<>();
  private final ConcurrentLinkedDeque<Long> accessOrder = new ConcurrentLinkedDeque<>();

  public BitboardCache(int maxNb) {
    this.maxNb = maxNb;
  }

  public CachedResult getOrCreate(long hash) {
    return cache.computeIfAbsent(
        hash,
        k -> {
          evictIfNecessary();
          accessOrder.addLast(k);
          return new CachedResult();
        });
  }

  private void evictIfNecessary() {
    if (cache.size() >= this.maxNb) {
      int numToRemove = this.maxNb / 10 + 1;
      synchronized (accessOrder) {
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
