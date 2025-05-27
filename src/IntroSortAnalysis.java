import java.io.*;
import java.util.*;

public class IntroSortAnalysis {

    static class IntroSort {
        private long iterations;

        public void sort(int[] arr) {
            if (arr == null || arr.length == 0) return;
            iterations = 0;
            int maxDepth = (int) (2 * Math.log(arr.length) / Math.log(2));
            introSort(arr, 0, arr.length - 1, maxDepth);
        }

        private void introSort(int[] arr, int low, int high, int maxDepth) {
            if (high - low <= 16) {
                insertionSort(arr, low, high);
                return;
            }

            if (maxDepth == 0) {
                heapSort(arr, low, high);
                return;
            }

            int pivot = partition(arr, low, high);
            introSort(arr, low, pivot - 1, maxDepth - 1);
            introSort(arr, pivot + 1, high, maxDepth - 1);
        }

        private int partition(int[] arr, int low, int high) {
            int pivot = arr[high];
            int i = low - 1;

            for (int j = low; j < high; j++) {
                iterations++;
                if (arr[j] <= pivot) {
                    i++;
                    swap(arr, i, j);
                    iterations++;
                }
            }
            swap(arr, i + 1, high);
            iterations++;
            return i + 1;
        }

        private void heapSort(int[] arr, int low, int high) {
            // Построение кучи
            for (int i = (high - low) / 2 + low; i >= low; i--) {
                heapify(arr, i, high, low);
            }

            for (int i = high; i > low; i--) {
                swap(arr, low, i);
                heapify(arr, low, i - 1, low);
                iterations++;
            }
        }

        private void heapify(int[] arr, int root, int end, int offset) {
            int largest = root;
            int left = 2 * (root - offset) + 1 + offset;
            int right = 2 * (root - offset) + 2 + offset;

            iterations++;
            if (left <= end && arr[left] > arr[largest]) largest = left;

            iterations++;
            if (right <= end && arr[right] > arr[largest]) largest = right;

            if (largest != root) {
                swap(arr, root, largest);
                iterations++;
                heapify(arr, largest, end, offset);
            }
        }

        private void insertionSort(int[] arr, int low, int high) {
            for (int i = low + 1; i <= high; i++) {
                int key = arr[i];
                int j = i - 1;
                iterations++;

                while (j >= low && arr[j] > key) {
                    arr[j + 1] = arr[j];
                    j--;
                    iterations += 2;
                }
                arr[j + 1] = key;
            }
        }

        private void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }

        public long getIterations() {
            return iterations;
        }
    }
    static class DataGenerator {
        public static void generate(int numDatasets) {
            Random rand = new Random();
            for (int i = 1; i <= numDatasets; i++) {
                int size = rand.nextInt(9901) + 100;
                String filename = String.format("dataset_%d_%d.txt", i, size);

                try (FileWriter fw = new FileWriter(filename)) {
                    for (int j = 0; j < size; j++) {
                        fw.write(rand.nextInt(100000) + " ");
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка генерации: " + filename);
                }
            }
        }
    }
    static class DataLoader {
        public static int[] load(String filename) {
            List<Integer> data = new ArrayList<>();
            try (Scanner sc = new Scanner(new File(filename))) {
                while (sc.hasNextInt()) data.add(sc.nextInt());
            } catch (Exception e) {
                System.err.println("Ошибка чтения: " + filename);
            }
            return data.stream().mapToInt(i -> i).toArray();
        }

        public static int parseSize(String filename) {
            return Integer.parseInt(filename.split("_")[2].replace(".txt", ""));
        }
    }
    public static void main(String[] args) {
        DataGenerator.generate(50);
        List<String> results = new ArrayList<>();
        results.add("Dataset,Size,Time(ns),Iterations");

        IntroSort sorter = new IntroSort();
        File dataDir = new File(".");

        for (File file : dataDir.listFiles()) {
            String name = file.getName();
            if (!name.startsWith("dataset_")) continue;
            int[] data = DataLoader.load(name);
            int size = DataLoader.parseSize(name);
            long start = System.nanoTime();
            sorter.sort(data);
            long time = System.nanoTime() - start;
            results.add(String.format("%s,%d,%d,%d",
                    name, size, time, sorter.getIterations()));
        }

        try (FileWriter fw = new FileWriter("results.csv")) {
            for (String line : results) fw.write(line + "\n");
        } catch (Exception e) {
            System.err.println("Ошибка записи результатов");
        }
    }
}