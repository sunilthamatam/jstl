#!/bin/bash
set -e

echo "Running all JSTL examples..."
echo ""

# Navigate to examples module and run
cd jstl-examples
mvn exec:java

echo ""
echo "✓ All examples completed!"
