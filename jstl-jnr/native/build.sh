#!/bin/bash
cd "$(dirname "$0")"
mkdir -p build && cd build
cmake .. && make -j$(nproc)
echo "Library built at: $(pwd)/lib/"
