package it.unibo.oop.lab.workers02;

import java.util.LinkedList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix {

    private final int threads;

    public MultiThreadedSumMatrix(final int threads) {
        this.threads = threads;
    }

    @Override
    public double sum(final double[][] matrix) {
        double sum = 0;
        final int elements = matrix.length * matrix[0].length;
        final int valuesForThread =  elements / threads;

        final List<Worker> workers = new LinkedList<>();
        for (int i = 0; i < this.threads; i++) {
            final Worker w;
            System.err.println("Processed by thread x: " + (-(i * valuesForThread) + (elements)));
            if (i == this.threads - 1) {
                w = new Worker(i * valuesForThread, elements, matrix); 
            } else {
                w = new Worker(i * valuesForThread, i * valuesForThread + valuesForThread, matrix);
            }
            w.start();
            workers.add(w);
        }

        for (final Worker w : workers) {
            try {
                w.join();
                sum += w.getSum();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return sum;
    }

    private class Worker extends Thread {

        private double sum;
        private final List<Double> values = new LinkedList<>();

        Worker(final int start, final int end, final double[][] mat) {
            for (int i = start; i < end && i < mat.length * mat[0].length; i++) {
                System.err.println(i);
                values.add(mat[i / mat.length][i % mat[0].length]);
            }
            System.err.println("List has elements: " + values.size());
        }

        public void run() {
            sum = values.stream()
                        .reduce((d1, d2) -> d1 + d2)
                        .orElse(0.0);
        }

        public double getSum() {
            return sum;
        }
    }

}
