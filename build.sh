#!/bin/bash
set -e

echo "================================"
echo "Building JSTL Native Library"
echo "================================"
echo ""

# Create build directory in jstl-core
mkdir -p jstl-core/build
cd jstl-core/build

# Run CMake
echo "Running CMake..."
cmake ..

# Build the native library
echo ""
echo "Building native library..."

# Detect number of CPU cores (cross-platform)
if command -v nproc > /dev/null 2>&1; then
    # Linux
    CORES=$(nproc)
elif command -v sysctl > /dev/null 2>&1; then
    # macOS
    CORES=$(sysctl -n hw.ncpu)
else
    # Default fallback
    CORES=2
fi

make -j${CORES}

echo ""
echo "✓ Native library built successfully!"
echo "  Library location: $(pwd)/lib/"
ls -lh lib/

cd ../..

echo ""
echo "================================"
echo "Building Java Code"
echo "================================"
echo ""

# Build Java code
mvn install

echo ""
echo "✓ Build completed successfully!"
echo ""
echo "To run examples:"
echo "  ./run-examples.sh"
echo ""
echo "Or run individual examples:"
echo "  cd jstl-examples"
echo "  mvn exec:java                           # Runs all examples"
echo "  mvn exec:java -Parraylist               # ArrayList example"
echo "  mvn exec:java -Phashmap                 # HashMap example"
echo "  mvn exec:java -Phashset                 # HashSet example"
