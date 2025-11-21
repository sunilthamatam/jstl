/*
 * Concurrent HashMap implementation using Intel TBB
 *
 * To build with TBB support:
 * 1. Install TBB: sudo apt install libtbb-dev (Linux) / brew install tbb (macOS)
 * 2. Add to CMakeLists.txt:
 *    find_package(TBB REQUIRED)
 *    target_link_libraries(jstl PRIVATE TBB::tbb)
 * 3. Compile with -DUSE_TBB flag
 */

#include "jstl_concurrent_hashmap.h"

#ifdef USE_TBB

#include <tbb/concurrent_hash_map.h>
#include <stdexcept>

// TBB concurrent hash map
struct ConcurrentHashMap {
    tbb::concurrent_hash_map<int64_t, int64_t> map;
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

        // Use accessor for thread-safe insertion
        tbb::concurrent_hash_map<int64_t, int64_t>::accessor acc;
        cm->map.insert(acc, key);
        acc->second = value;

    } catch (...) {
        // Silent failure
    }
}

int64_t jstl_concurrent_hashmap_get(jstl_concurrent_hashmap_t map, int64_t key) {
    if (!map) return 0;
    try {
        ConcurrentHashMap* cm = static_cast<ConcurrentHashMap*>(map);

        // Use const_accessor for thread-safe read (allows multiple concurrent readers)
        tbb::concurrent_hash_map<int64_t, int64_t>::const_accessor acc;
        if (cm->map.find(acc, key)) {
            return acc->second;
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

        tbb::concurrent_hash_map<int64_t, int64_t>::const_accessor acc;
        return cm->map.find(acc, key) ? 1 : 0;

    } catch (...) {
        return 0;
    }
}

void jstl_concurrent_hashmap_remove(jstl_concurrent_hashmap_t map, int64_t key) {
    if (!map) return;
    try {
        static_cast<ConcurrentHashMap*>(map)->map.erase(key);
    } catch (...) {
        // Silent failure
    }
}

size_t jstl_concurrent_hashmap_size(jstl_concurrent_hashmap_t map) {
    if (!map) return 0;
    try {
        return static_cast<ConcurrentHashMap*>(map)->map.size();
    } catch (...) {
        return 0;
    }
}

void jstl_concurrent_hashmap_clear(jstl_concurrent_hashmap_t map) {
    if (!map) return;
    try {
        static_cast<ConcurrentHashMap*>(map)->map.clear();
    } catch (...) {
        // Silent failure
    }
}

int jstl_concurrent_hashmap_is_empty(jstl_concurrent_hashmap_t map) {
    if (!map) return 1;
    try {
        return static_cast<ConcurrentHashMap*>(map)->map.empty() ? 1 : 0;
    } catch (...) {
        return 1;
    }
}

int jstl_concurrent_hashmap_put_if_absent(jstl_concurrent_hashmap_t map, int64_t key, int64_t value) {
    if (!map) return 0;
    try {
        ConcurrentHashMap* cm = static_cast<ConcurrentHashMap*>(map);

        // Atomic insert - returns true if inserted, false if already exists
        tbb::concurrent_hash_map<int64_t, int64_t>::accessor acc;
        if (cm->map.insert(acc, key)) {
            acc->second = value;
            return 1;  // Inserted
        }
        return 0;  // Already exists

    } catch (...) {
        return 0;
    }
}

} // extern "C"

#endif // USE_TBB
