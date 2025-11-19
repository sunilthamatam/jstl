package com.jstl.examples;

/**
 * Runs all examples
 */
public class AllExamples {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   Java Off-Heap Collections using C++ STL             ║");
        System.out.println("║   Powered by Java 21 Panama Foreign Function & Memory ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        try {
            ArrayListExample.main(args);
            System.out.println("\n" + "=".repeat(60) + "\n");

            HashMapExample.main(args);
            System.out.println("\n" + "=".repeat(60) + "\n");

            HashSetExample.main(args);
            System.out.println("\n" + "=".repeat(60) + "\n");

            System.out.println("╔════════════════════════════════════════════════════════╗");
            System.out.println("║             All examples completed successfully!       ║");
            System.out.println("╚════════════════════════════════════════════════════════╝");
        } catch (Exception e) {
            System.err.println("\n✗ Error running examples:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
