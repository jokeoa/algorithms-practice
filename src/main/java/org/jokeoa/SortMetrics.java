package org.jokeoa;

/**
 * Класс для сбора метрик работы алгоритмов сортировки.
 * Помогает понять, как алгоритм ведет себя на практике.
 */
public class SortMetrics {
    private int maxRecursionDepth;     // Максимальная глубина рекурсии
    private int currentDepth;          // Текущая глубина рекурсии
    private int totalComparisons;      // Общее количество сравнений
    private int totalArrayAccesses;    // Общее количество обращений к массиву
    private long startTime;            // Время начала сортировки
    private long endTime;              // Время окончания сортировки

    /**
     * Сбрасываем все счетчики перед началом новой сортировки
     */
    public void reset() {
        maxRecursionDepth = 0;
        currentDepth = 0;
        totalComparisons = 0;
        totalArrayAccesses = 0;
        startTime = 0;
        endTime = 0;
    }

    /**
     * Вызывается при входе в рекурсивную функцию
     */
    public void enterRecursion() {
        currentDepth++;
        if (currentDepth > maxRecursionDepth) {
            maxRecursionDepth = currentDepth;
        }
    }

    /**
     * Вызывается при выходе из рекурсивной функции
     */
    public void exitRecursion() {
        currentDepth--;
    }

    /**
     * Увеличиваем счетчик сравнений (когда сравниваем два элемента)
     */
    public void recordComparison() {
        totalComparisons++;
    }

    /**
     * Увеличиваем счетчик обращений к массиву (чтение или запись)
     */
    public void recordArrayAccess() {
        totalArrayAccesses++;
    }

    /**
     * Отмечаем начало сортировки
     */
    public void startTiming() {
        startTime = System.nanoTime();
    }

    /**
     * Отмечаем конец сортировки
     */
    public void endTiming() {
        endTime = System.nanoTime();
    }

    // Геттеры для получения собранных метрик
    public int getMaxRecursionDepth() {
        return maxRecursionDepth;
    }

    public int getTotalComparisons() {
        return totalComparisons;
    }

    public int getTotalArrayAccesses() {
        return totalArrayAccesses;
    }

    /**
     * Возвращает время выполнения в миллисекундах
     */
    public double getExecutionTimeMs() {
        return (endTime - startTime) / 1_000_000.0;
    }

    /**
     * Красиво выводим все собранные метрики
     */
    public void printMetrics(String algorithmName, int arraySize) {
        System.out.println("=== Метрики для " + algorithmName + " ===");
        System.out.println("Размер массива: " + arraySize);
        System.out.println("Максимальная глубина рекурсии: " + maxRecursionDepth);
        System.out.println("Общее количество сравнений: " + totalComparisons);
        System.out.println("Общее количество обращений к массиву: " + totalArrayAccesses);
        System.out.printf("Время выполнения: %.3f мс%n", getExecutionTimeMs());

        // Вычисляем теоретическую глубину рекурсии для сравнения
        int theoreticalDepth = (int) Math.ceil(Math.log(arraySize) / Math.log(2));
        System.out.println("Теоретическая глубина (log₂ n): " + theoreticalDepth);

        if (maxRecursionDepth <= theoreticalDepth + 1) {
            System.out.println("✓ Глубина рекурсии в ожидаемых пределах");
        } else {
            System.out.println("⚠ Глубина рекурсии больше ожидаемой");
        }
    }
}