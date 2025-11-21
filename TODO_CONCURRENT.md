# TODO: Implementing Concurrent Collections

## Phase 1: Proof of Concept ✅
- [x] Design document (CONCURRENT.md)
- [x] Header file for concurrent hash map
- [x] TBB implementation
- [x] shared_mutex fallback implementation
- [x] CMakeLists with TBB support

## Phase 2: Complete C++ Implementation
- [ ] Build and test TBB version
- [ ] Build and test shared_mutex version
- [ ] Benchmark both implementations
- [ ] Add concurrent hash set
- [ ] Consider concurrent vector (or document why ArrayList is challenging)

## Phase 3: Java Bindings
- [ ] Create NativeConcurrentHashMap.java (Panama FFM bindings)
- [ ] Create OffHeapConcurrentHashMap.java (user API)
- [ ] Add thread-safe guarantees to documentation
- [ ] Implement putIfAbsent and atomic operations

## Phase 4: Testing
- [ ] Unit tests for concurrent operations
- [ ] Multi-threaded stress tests
- [ ] Benchmark suite comparing:
  - Non-concurrent vs concurrent
  - TBB vs shared_mutex
  - Different thread counts (1, 10, 50, 100)
  - Different read/write ratios

## Phase 5: Documentation
- [ ] Update README with concurrent features
- [ ] Add examples for concurrent usage
- [ ] Document performance characteristics
- [ ] Add thread-safety guarantees

## Phase 6: Advanced Features (Optional)
- [ ] Investigate Folly for even lower overhead
- [ ] Consider custom lock-striping implementation
- [ ] Add concurrent queue/stack
- [ ] Memory ordering optimizations

## Notes

### Why ArrayList is Hard to Make Concurrent
- Resizing requires exclusive access (can't insert during resize)
- Random access is easy (read-only with shared lock)
- Sequential iteration is problematic
- Better alternatives:
  - ConcurrentSkipListSet (sorted set)
  - Lock-free queue for append-only
  - Consider not providing concurrent ArrayList

### Installation Requirements
Users need TBB installed:
```bash
# Linux
sudo apt install libtbb-dev

# macOS
brew install tbb

# Windows
vcpkg install tbb
```

### Build Commands
```bash
# With TBB (recommended)
cmake -DUSE_TBB=ON ..
make

# Without TBB (fallback)
cmake ..
make
```
