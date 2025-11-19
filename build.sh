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
make -j$(nproc)

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
