/*
 * Concurrent HashMap implementation using std::shared_mutex
 *
 * This is a fallback implementation when TBB is not available.
 * Uses read-write locks for medium contention overhead.
 *
 * Performance: ~3-10x overhead compared to ~2-3x for TBB
 */

#include "jstl_concurrent_hashmap.h"

#ifndef USE_TBB

#include <unordered_map>
#include <shared_mutex>
#include <stdexcept>

// Concurrent hash map using shared_mutex for read-write locking
struct ConcurrentHashMap {
    std::unordered_map<int64_t, int64_t> map;
    mutable std::shared_mutex mtx;  // Mutable to allow const methods to lock
};

extern "C" {

jstl_concurrent_hashmap_t jstl_concurrent_hashmap_create() {
    try {
        return new ConcurrentHashMap();
    } catch (...) {
        return nullptr;
    }
}

void jstl_concurrent_hashmap_destroy(jstl_concurrent_hashmap_t map) {
    if (map) {
        delete static_cast<ConcurrentHashMap*>(map);
    }
}

void jstl_concurrent_hashmap_put(jstl_concurrent_hashmap_t map, int64_t key, int64_t value) {
    if (!map) return;
    try {
        ConcurrentHashMap* cm = static_cast<ConcurrentHashMap*>(map);

        // Exclusive lock for write
        std::unique_lock<std::shared_mutex> lock(cm->mtx);
        cm->map[key] = value;

    } catch (...) {
        // Silent failure
    }
}

int64_t jstl_concurrent_hashmap_get(jstl_concurrent_hashmap_t map, int64_t key) {
    if (!map) return 0;
    try {
        ConcurrentHashMap* cm = static_cast<ConcurrentHashMap*>(map);

        // Shared lock for read (allows multiple concurrent readers)
        std::shared_lock<std::shared_mutex> lock(cm->mtx);
        auto it = cm->map.find(key);
        if (it != cm->map.end()) {
            return it->second;
        }
        return 0;

    } catch (...) {
        return 0;
    }
}

int jstl_concurrent_hashmap_contains_key(jstl_concurrent_hashmap_t map, int64_t key) {
    if (!map) return 0;
    try {
        ConcurrentHashMap* cm = static_cast<ConcurrentHashMap*>(map);

        std::shared_lock<std::shared_mutex> lock(cm->mtx);
        return cm->map.find(key) != cm->map.end() ? 1 : 0;

    } catch (...) {
        return 0;
    }
}

void jstl_concurrent_hashmap_remove(jstl_concurrent_hashmap_t map, int64_t key) {
    if (!map) return;
    try {
        ConcurrentHashMap* cm = static_cast<ConcurrentHashMap*>(map);

        std::unique_lock<std::shared_mutex> lock(cm->mtx);
        cm->map.erase(key);

    } catch (...) {
        // Silent failure
    }
}

size_t jstl_concurrent_hashmap_size(jstl_concurrent_hashmap_t map) {
    if (!map) return 0;
    try {
        ConcurrentHashMap* cm = static_cast<ConcurrentHashMap*>(map);

        std::shared_lock<std::shared_mutex> lock(cm->mtx);
        return cm->map.size();

    } catch (...) {
        return 0;
    }
}

void jstl_concurrent_hashmap_clear(jstl_concurrent_hashmap_t map) {
    if (!map) return;
    try {
        ConcurrentHashMap* cm = static_cast<ConcurrentHashMap*>(map);

        std::unique_lock<std::shared_mutex> lock(cm->mtx);
        cm->map.clear();

    } catch (...) {
        // Silent failure
    }
}

int jstl_concurrent_hashmap_is_empty(jstl_concurrent_hashmap_t map) {
    if (!map) return 1;
    try {
        ConcurrentHashMap* cm = static_cast<ConcurrentHashMap*>(map);

        std::shared_lock<std::shared_mutex> lock(cm->mtx);
        return cm->map.empty() ? 1 : 0;

    } catch (...) {
        return 1;
    }
}

int jstl_concurrent_hashmap_put_if_absent(jstl_concurrent_hashmap_t map, int64_t key, int64_t value) {
    if (!map) return 0;
    try {
        ConcurrentHashMap* cm = static_cast<ConcurrentHashMap*>(map);

        // Need exclusive lock for atomic check-and-insert
        std::unique_lock<std::shared_mutex> lock(cm->mtx);

        auto result = cm->map.insert({key, value});
        return result.second ? 1 : 0;  // 1 if inserted, 0 if already exists

    } catch (...) {
        return 0;
    }
}

} // extern "C"

#endif // !USE_TBB
