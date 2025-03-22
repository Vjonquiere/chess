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
    CachedResult result = cache.get(hash);
    if (result != null) {
      return result;
    }

    CachedResult newResult = new CachedResult();
    CachedResult existing = cache.putIfAbsent(hash, newResult);
    if (existing != null) {
      return existing;
    }

    accessOrder.addLast(hash);
    evictIfNecessary();

    return newResult;
  }

  private void evictIfNecessary() {
    if (cache.size() <= maxNb) {
      return;
    }

    int numToRemove = Math.max(1, maxNb / 10);
    for (int i = 0; i < numToRemove; i++) {
      Long oldestKey = accessOrder.pollFirst();
      if (oldestKey == null) {
        break;
      }
      cache.remove(oldestKey);
    }
  }
}
