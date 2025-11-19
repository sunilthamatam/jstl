package com.jstl.examples;

import com.jstl.OffHeapHashSet;

/**
 * Example demonstrating OffHeapHashSet usage
 */
public class HashSetExample {
    public static void main(String[] args) {
        System.out.println("=== OffHeapHashSet Example ===\n");

        // Using try-with-resources for automatic cleanup
        try (OffHeapHashSet set = new OffHeapHashSet()) {
            System.out.println("Created new OffHeapHashSet");
            System.out.println("Initial size: " + set.size());
            System.out.println("Is empty: " + set.isEmpty());

            // Add elements
            System.out.println("\nAdding elements 1-10...");
            for (long i = 1; i <= 10; i++) {
                boolean added = set.add(i * 10);
                System.out.println("  Added " + (i * 10) + ": " + added);
            }
            System.out.println("Size after additions: " + set.size());

            // Try adding duplicate
            System.out.println("\nTrying to add duplicate (50):");
            boolean added = set.add(50);
            System.out.println("  Added: " + added + " (should be false)");
            System.out.println("  Size: " + set.size());

            // Check contains
            System.out.println("\nChecking elements:");
            System.out.println("Contains 50: " + set.contains(50));
            System.out.println("Contains 99: " + set.contains(99));

            // Remove element
            System.out.println("\nRemoving 50:");
            boolean removed = set.remove(50);
            System.out.println("  Removed: " + removed);
            System.out.println("  Contains 50: " + set.contains(50));
            System.out.println("  Size: " + set.size());

            // Try removing non-existent
            System.out.println("\nTrying to remove non-existent (999):");
            removed = set.remove(999);
            System.out.println("  Removed: " + removed + " (should be false)");

            // Performance test
            System.out.println("\n=== Performance Test ===");
            set.clear();
            int n = 1_000_000;

            long start = System.nanoTime();
            for (long i = 0; i < n; i++) {
                set.add(i);
            }
            long end = System.nanoTime();

            double ms = (end - start) / 1_000_000.0;
            System.out.println("Added " + n + " elements in " + String.format("%.2f", ms) + " ms");
            System.out.println("Average time per addition: " + String.format("%.2f", ms / n * 1000) + " ns");
            System.out.println("Final size: " + set.size());

            // Contains test
            start = System.nanoTime();
            int found = 0;
            for (long i = 0; i < n; i++) {
                if (set.contains(i)) {
                    found++;
                }
            }
            end = System.nanoTime();

            ms = (end - start) / 1_000_000.0;
            System.out.println("\nChecked " + n + " elements in " + String.format("%.2f", ms) + " ms");
            System.out.println("Average time per check: " + String.format("%.2f", ms / n * 1000) + " ns");
            System.out.println("Found: " + found);

            // Remove test
            start = System.nanoTime();
            int removeCount = 0;
            for (long i = 0; i < n; i += 2) {  // Remove every other element
                if (set.remove(i)) {
                    removeCount++;
                }
            }
            end = System.nanoTime();

            ms = (end - start) / 1_000_000.0;
            System.out.println("\nRemoved " + removeCount + " elements in " + String.format("%.2f", ms) + " ms");
            System.out.println("Average time per removal: " + String.format("%.2f", ms / removeCount * 1000) + " ns");
            System.out.println("Final size: " + set.size());

            System.out.println("\n✓ Memory will be freed automatically when try block exits");
        }

        System.out.println("\n✓ All native resources cleaned up!");
    }
}
