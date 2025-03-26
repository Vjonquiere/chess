package pdp.model.board;

import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BitboardCache {
  private final int maxNb;
  private final AbstractMap<Long, CachedResult> cache = new ConcurrentHashMap<>();
  private final ConcurrentLinkedDeque<Long> accessOrder = new ConcurrentLinkedDeque<>();

  public BitboardCache(int maxNb) {
    this.maxNb = maxNb;
  }

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
