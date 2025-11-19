package com.jstl.examples;

import com.jstl.OffHeapArrayList;

/**
 * Example demonstrating OffHeapArrayList usage
 */
public class ArrayListExample {
    public static void main(String[] args) {
        System.out.println("=== OffHeapArrayList Example ===\n");

        // Using try-with-resources for automatic cleanup
        try (OffHeapArrayList list = new OffHeapArrayList()) {
            System.out.println("Created new OffHeapArrayList");
            System.out.println("Initial size: " + list.size());
            System.out.println("Is empty: " + list.isEmpty());

            // Add elements
            System.out.println("\nAdding elements 1-10...");
            for (long i = 1; i <= 10; i++) {
                list.add(i * 100);
            }
            System.out.println("Size after additions: " + list.size());
            System.out.println("Capacity: " + list.capacity());

            // Get elements
            System.out.println("\nGetting elements:");
            for (int i = 0; i < list.size(); i++) {
                System.out.println("  list[" + i + "] = " + list.get(i));
            }

            // Modify element
            System.out.println("\nSetting list[5] = 999");
            list.set(5, 999);
            System.out.println("list[5] is now: " + list.get(5));

            // Remove element
            System.out.println("\nRemoving element at index 0");
            list.remove(0);
            System.out.println("New size: " + list.size());
            System.out.println("New list[0]: " + list.get(0));

            // Reserve capacity
            System.out.println("\nReserving capacity for 1000 elements");
            list.reserve(1000);
            System.out.println("Capacity after reserve: " + list.capacity());

            // Performance test
            System.out.println("\n=== Performance Test ===");
            list.clear();
            int n = 1_000_000;

            long start = System.nanoTime();
            for (long i = 0; i < n; i++) {
                list.add(i);
            }
            long end = System.nanoTime();

            double ms = (end - start) / 1_000_000.0;
            System.out.println("Added " + n + " elements in " + String.format("%.2f", ms) + " ms");
            System.out.println("Average time per element: " + String.format("%.2f", ms / n * 1000) + " ns");
            System.out.println("Final size: " + list.size());

            // Access test
            start = System.nanoTime();
            long sum = 0;
            for (int i = 0; i < n; i++) {
                sum += list.get(i);
            }
            end = System.nanoTime();

            ms = (end - start) / 1_000_000.0;
            System.out.println("\nAccessed " + n + " elements in " + String.format("%.2f", ms) + " ms");
            System.out.println("Average time per access: " + String.format("%.2f", ms / n * 1000) + " ns");
            System.out.println("Sum: " + sum);

            System.out.println("\n✓ Memory will be freed automatically when try block exits");
        }

        System.out.println("\n✓ All native resources cleaned up!");
    }
}
