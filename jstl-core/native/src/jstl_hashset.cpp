#include "jstl_hashset.h"
#include <unordered_set>

// C++ wrapper around std::unordered_set
struct HashSet {
    std::unordered_set<int64_t> set;
};

// Iterator wrapper
struct HashSetIterator {
    std::unordered_set<int64_t>::iterator current;
    std::unordered_set<int64_t>::iterator end;
};

extern "C" {

jstl_hashset_t jstl_hashset_create() {
    try {
        return new HashSet();
    } catch (...) {
        return nullptr;
    }
}

void jstl_hashset_destroy(jstl_hashset_t set) {
    if (set) {
        delete static_cast<HashSet*>(set);
    }
}

int jstl_hashset_add(jstl_hashset_t set, int64_t value) {
    if (!set) return 0;
    try {
        auto result = static_cast<HashSet*>(set)->set.insert(value);
        return result.second ? 1 : 0;  // Returns 1 if inserted, 0 if already present
    } catch (...) {
        return 0;
    }
}

int jstl_hashset_contains(jstl_hashset_t set, int64_t value) {
    if (!set) return 0;
    try {
        HashSet* hs = static_cast<HashSet*>(set);
        return hs->set.find(value) != hs->set.end() ? 1 : 0;
    } catch (...) {
        return 0;
    }
}

int jstl_hashset_remove(jstl_hashset_t set, int64_t value) {
    if (!set) return 0;
    try {
        return static_cast<HashSet*>(set)->set.erase(value) > 0 ? 1 : 0;
    } catch (...) {
        return 0;
    }
}

size_t jstl_hashset_size(jstl_hashset_t set) {
    if (!set) return 0;
    try {
        return static_cast<HashSet*>(set)->set.size();
    } catch (...) {
        return 0;
    }
}

void jstl_hashset_clear(jstl_hashset_t set) {
    if (!set) return;
    try {
        static_cast<HashSet*>(set)->set.clear();
    } catch (...) {
        // Silent failure
    }
}

int jstl_hashset_is_empty(jstl_hashset_t set) {
    if (!set) return 1;
    try {
        return static_cast<HashSet*>(set)->set.empty() ? 1 : 0;
    } catch (...) {
        return 1;
    }
}

jstl_hashset_iterator_t jstl_hashset_iterator_create(jstl_hashset_t set) {
    if (!set) return nullptr;
    try {
        HashSet* hs = static_cast<HashSet*>(set);
        HashSetIterator* iter = new HashSetIterator();
        iter->current = hs->set.begin();
        iter->end = hs->set.end();
        return iter;
    } catch (...) {
        return nullptr;
    }
}

int jstl_hashset_iterator_has_next(jstl_hashset_iterator_t iter) {
    if (!iter) return 0;
    try {
        HashSetIterator* it = static_cast<HashSetIterator*>(iter);
        return it->current != it->end ? 1 : 0;
    } catch (...) {
        return 0;
    }
}

int64_t jstl_hashset_iterator_next(jstl_hashset_iterator_t iter) {
    if (!iter) return 0;
    try {
        HashSetIterator* it = static_cast<HashSetIterator*>(iter);
        if (it->current != it->end) {
            int64_t value = *(it->current);
            ++(it->current);
            return value;
        }
        return 0;
    } catch (...) {
        return 0;
    }
}

void jstl_hashset_iterator_destroy(jstl_hashset_iterator_t iter) {
    if (iter) {
        delete static_cast<HashSetIterator*>(iter);
    }
}

} // extern "C"
