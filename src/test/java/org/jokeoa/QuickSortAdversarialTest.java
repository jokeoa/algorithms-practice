package org.jokeoa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Специальные "adversarial" тесты для QuickSort - случаи, которые могут
 * выявить проблемы в реализации или показать худшую производительность.
 */
public class QuickSortAdversarialTest {

    @Test
    @DisplayName("Классический worst-case: отсортированный массив")
    void testWorstCaseSorted() {

        int[] array = generateSortedArray(1000);

        long startTime = System.nanoTime();
        SortMetrics metrics = QuickSort.sortWithMetrics(array);
        long endTime = System.nanoTime();


        double timeMs = (endTime - startTime) / 1_000_000.0;
        assertTrue(timeMs < 100, "Сортировка заняла слишком много времени: " + timeMs + " мс");

        int maxAllowedDepth = (int)(3 * Math.log(1000) / Math.log(2)) + 20;
        assertTrue(metrics.getMaxRecursionDepth() < maxAllowedDepth,
                "Глубина рекурсии слишком большая: " + metrics.getMaxRecursionDepth());

        assertTrue(isSorted(array));

        System.out.println("Worst-case тест (отсортированный массив):");
        System.out.println("  Время: " + timeMs + " мс");
        System.out.println("  Глубина рекурсии: " + metrics.getMaxRecursionDepth());
        System.out.println("  Средний баланс pivot: " + metrics.getAveragePartitionBalance() + "%");
    }

    @Test
    @DisplayName("Все элементы одинаковые - тест на дубликаты")
    void testAllIdentical() {
        int[] sizes = {100, 500, 1000};

        for (int size : sizes) {
            int[] array = new int[size];
            Arrays.fill(array, 42);

            SortMetrics metrics = QuickSort.sortWithMetrics(array);


            assertTrue(isSorted(array));

            int maxDepth = (int)(2 * Math.log(size) / Math.log(2)) + 10;
            assertTrue(metrics.getMaxRecursionDepth() <= maxDepth,
                    String.format("Размер %d: глубина %d > ожидаемой %d",
                            size, metrics.getMaxRecursionDepth(), maxDepth));
        }
    }

    @Test
    @DisplayName("Паттерн 'органные трубы' - убывание, затем возрастание")
    void testOrganPipePattern() {
        int[] array = generateOrganPipeArray(200);

        SortMetrics metrics = QuickSort.sortWithMetrics(array);

        assertTrue(isSorted(array));

        System.out.println("Organ pipe тест:");
        System.out.println("  Глубина рекурсии: " + metrics.getMaxRecursionDepth());
        System.out.println("  Количество сравнений: " + metrics.getTotalComparisons());
        System.out.println("  Средний баланс pivot: " + metrics.getAveragePartitionBalance() + "%");
    }

    @Test
    @DisplayName("Много повторяющихся значений - тест стабильности")
    void testManyDuplicatesStability() {
        int[] array = generateArrayWithFewUniqueValues(1000, 10);

        SortMetrics metrics = QuickSort.sortWithMetrics(array);

        assertTrue(isSorted(array));

        System.out.println("Тест множественных дубликатов:");
        System.out.println("  Время выполнения: " + metrics.getExecutionTimeMs() + " мс");
        System.out.println("  Количество разделений: " + metrics.getPartitionCalls());
        System.out.println("  Худшее разделение: " + metrics.getWorstPartition() + "% отклонения");
    }

    @Test
    @DisplayName("Массив с 'плохими' pivot выборами")
    void testBadPivotScenarios() {

        int[] array = createBadPivotArray(500);

        SortMetrics metrics = QuickSort.sortWithMetrics(array);

        assertTrue(isSorted(array));

        assertTrue(metrics.getExecutionTimeMs() < 50,
                "Время выполнения слишком большое: " + metrics.getExecutionTimeMs() + " мс");

        System.out.println("Bad pivot тест:");
        System.out.println("  Средний баланс: " + metrics.getAveragePartitionBalance() + "%");
        System.out.println("  Лучшее разделение: " + metrics.getBestPartition() + "%");
        System.out.println("  Худшее разделение: " + metrics.getWorstPartition() + "%");
    }

    @Test
    @DisplayName("Стресс-тест: много маленьких массивов")
    void testManySmallArrays() {

        int totalTime = 0;
        int totalTests = 1000;

        for (int i = 0; i < totalTests; i++) {
            int size = 2 + i % 15;
            int[] array = generateRandomArray(size);

            long start = System.nanoTime();
            QuickSort.sort(array);
            long end = System.nanoTime();

            totalTime += (end - start);

            assertTrue(isSorted(array), "Массив " + i + " размера " + size + " не отсортирован");
        }

        double avgTimeNs = totalTime / (double) totalTests;
        System.out.println("Стресс-тест маленьких массивов:");
        System.out.println("  Среднее время на массив: " + avgTimeNs / 1000 + " микросекунд");
        System.out.println("  Всего протестировано: " + totalTests + " массивов");
    }

    @Test
    @DisplayName("Экстремально большой массив (если память позволяет)")
    void testVeryLargeArray() {
        try {
            int size = 100_000;
            int[] array = generateRandomArray(size);

            long start = System.currentTimeMillis();
            SortMetrics metrics = QuickSort.sortWithMetrics(array);
            long end = System.currentTimeMillis();

            assertTrue(isSorted(array));

            System.out.println("Тест большого массива (100K элементов):");
            System.out.println("  Время: " + (end - start) + " мс");
            System.out.println("  Глубина рекурсии: " + metrics.getMaxRecursionDepth());
            System.out.println("  Сравнений: " + metrics.getTotalComparisons());
            System.out.println("  Обменов: " + metrics.getTotalSwaps());

        } catch (OutOfMemoryError e) {
            System.out.println("Недостаточно памяти для теста большого массива");
        }
    }


    private int[] generateSortedArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = i;
        }
        return array;
    }

    private int[] generateOrganPipeArray(int size) {
        int[] array = new int[size];
        int mid = size / 2;

        // Убывание до середины
        for (int i = 0; i < mid; i++) {
            array[i] = mid - i;
        }

        // Возрастание от середины
        for (int i = mid; i < size; i++) {
            array[i] = i - mid + 1;
        }

        return array;
    }

    private int[] generateArrayWithFewUniqueValues(int size, int uniqueCount) {
        int[] array = new int[size];
        java.util.Random random = new java.util.Random(42);

        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(uniqueCount);
        }

        return array;
    }

    private int[] createBadPivotArray(int size) {
        int[] array = new int[size];


        for (int i = 0; i < size * 0.9; i++) {
            array[i] = i % 10;
        }

        for (int i = (int)(size * 0.9); i < size; i++) {
            array[i] = 1000 + i;
        }


        java.util.Random random = new java.util.Random(42);
        for (int i = 0; i < size; i++) {
            int j = random.nextInt(size);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }

        return array;
    }

    private int[] generateRandomArray(int size) {
        int[] array = new int[size];
        java.util.Random random = new java.util.Random(42);
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(size * 2);
        }
        return array;
    }

    private boolean isSorted(int[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[i-1]) {
                return false;
            }
        }
        return true;
    }
}