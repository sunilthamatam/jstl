#include "jstl_hashmap.h"
#include <unordered_map>

// C++ wrapper around std::unordered_map
struct HashMap {
    std::unordered_map<int64_t, int64_t> map;
};

// Iterator wrapper
struct HashMapIterator {
    std::unordered_map<int64_t, int64_t>::iterator current;
    std::unordered_map<int64_t, int64_t>::iterator end;
};

extern "C" {

jstl_hashmap_t jstl_hashmap_create() {
    try {
        return new HashMap();
    } catch (...) {
        return nullptr;
    }
}

void jstl_hashmap_destroy(jstl_hashmap_t map) {
    if (map) {
        delete static_cast<HashMap*>(map);
    }
}

void jstl_hashmap_put(jstl_hashmap_t map, int64_t key, int64_t value) {
    if (!map) return;
    try {
        static_cast<HashMap*>(map)->map[key] = value;
    } catch (...) {
        // Silent failure
    }
}

int64_t jstl_hashmap_get(jstl_hashmap_t map, int64_t key) {
    if (!map) return 0;
    try {
        HashMap* hm = static_cast<HashMap*>(map);
        auto it = hm->map.find(key);
        if (it != hm->map.end()) {
            return it->second;
        }
        return 0;
    } catch (...) {
        return 0;
    }
}

int jstl_hashmap_contains_key(jstl_hashmap_t map, int64_t key) {
    if (!map) return 0;
    try {
        HashMap* hm = static_cast<HashMap*>(map);
        return hm->map.find(key) != hm->map.end() ? 1 : 0;
    } catch (...) {
        return 0;
    }
}

void jstl_hashmap_remove(jstl_hashmap_t map, int64_t key) {
    if (!map) return;
    try {
        static_cast<HashMap*>(map)->map.erase(key);
    } catch (...) {
        // Silent failure
    }
}

size_t jstl_hashmap_size(jstl_hashmap_t map) {
    if (!map) return 0;
    try {
        return static_cast<HashMap*>(map)->map.size();
    } catch (...) {
        return 0;
    }
}

void jstl_hashmap_clear(jstl_hashmap_t map) {
    if (!map) return;
    try {
        static_cast<HashMap*>(map)->map.clear();
    } catch (...) {
        // Silent failure
    }
}

int jstl_hashmap_is_empty(jstl_hashmap_t map) {
    if (!map) return 1;
    try {
        return static_cast<HashMap*>(map)->map.empty() ? 1 : 0;
    } catch (...) {
        return 1;
    }
}

jstl_hashmap_iterator_t jstl_hashmap_iterator_create(jstl_hashmap_t map) {
    if (!map) return nullptr;
    try {
        HashMap* hm = static_cast<HashMap*>(map);
        HashMapIterator* iter = new HashMapIterator();
        iter->current = hm->map.begin();
        iter->end = hm->map.end();
        return iter;
    } catch (...) {
        return nullptr;
    }
}

int jstl_hashmap_iterator_has_next(jstl_hashmap_iterator_t iter) {
    if (!iter) return 0;
    try {
        HashMapIterator* it = static_cast<HashMapIterator*>(iter);
        return it->current != it->end ? 1 : 0;
    } catch (...) {
        return 0;
    }
}

jstl_hashmap_entry_t jstl_hashmap_iterator_next(jstl_hashmap_iterator_t iter) {
    jstl_hashmap_entry_t entry = {0, 0};
    if (!iter) return entry;
    try {
        HashMapIterator* it = static_cast<HashMapIterator*>(iter);
        if (it->current != it->end) {
            entry.key = it->current->first;
            entry.value = it->current->second;
            ++(it->current);
        }
        return entry;
    } catch (...) {
        return entry;
    }
}

void jstl_hashmap_iterator_destroy(jstl_hashmap_iterator_t iter) {
    if (iter) {
        delete static_cast<HashMapIterator*>(iter);
    }
}

} // extern "C"
