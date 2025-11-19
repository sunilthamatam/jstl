#include "jstl_arraylist.h"
#include <vector>
#include <stdexcept>

// C++ wrapper around std::vector
struct ArrayList {
    std::vector<int64_t> vec;
};

extern "C" {

jstl_arraylist_t jstl_arraylist_create() {
    try {
        return new ArrayList();
    } catch (...) {
        return nullptr;
    }
}

void jstl_arraylist_destroy(jstl_arraylist_t list) {
    if (list) {
        delete static_cast<ArrayList*>(list);
    }
}

void jstl_arraylist_add(jstl_arraylist_t list, int64_t value) {
    if (!list) return;
    try {
        static_cast<ArrayList*>(list)->vec.push_back(value);
    } catch (...) {
        // Silent failure for now
    }
}

int64_t jstl_arraylist_get(jstl_arraylist_t list, size_t index) {
    if (!list) return 0;
    try {
        ArrayList* al = static_cast<ArrayList*>(list);
        if (index >= al->vec.size()) return 0;
        return al->vec[index];
    } catch (...) {
        return 0;
    }
}

void jstl_arraylist_set(jstl_arraylist_t list, size_t index, int64_t value) {
    if (!list) return;
    try {
        ArrayList* al = static_cast<ArrayList*>(list);
        if (index < al->vec.size()) {
            al->vec[index] = value;
        }
    } catch (...) {
        // Silent failure
    }
}

void jstl_arraylist_remove(jstl_arraylist_t list, size_t index) {
    if (!list) return;
    try {
        ArrayList* al = static_cast<ArrayList*>(list);
        if (index < al->vec.size()) {
            al->vec.erase(al->vec.begin() + index);
        }
    } catch (...) {
        // Silent failure
    }
}

size_t jstl_arraylist_size(jstl_arraylist_t list) {
    if (!list) return 0;
    try {
        return static_cast<ArrayList*>(list)->vec.size();
    } catch (...) {
        return 0;
    }
}

void jstl_arraylist_clear(jstl_arraylist_t list) {
    if (!list) return;
    try {
        static_cast<ArrayList*>(list)->vec.clear();
    } catch (...) {
        // Silent failure
    }
}

int jstl_arraylist_is_empty(jstl_arraylist_t list) {
    if (!list) return 1;
    try {
        return static_cast<ArrayList*>(list)->vec.empty() ? 1 : 0;
    } catch (...) {
        return 1;
    }
}

size_t jstl_arraylist_capacity(jstl_arraylist_t list) {
    if (!list) return 0;
    try {
        return static_cast<ArrayList*>(list)->vec.capacity();
    } catch (...) {
        return 0;
    }
}

void jstl_arraylist_reserve(jstl_arraylist_t list, size_t capacity) {
    if (!list) return;
    try {
        static_cast<ArrayList*>(list)->vec.reserve(capacity);
    } catch (...) {
        // Silent failure
    }
}

} // extern "C"
