package it.unibo.oop.lab.workers02;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix {

    private final int threads;

    public MultiThreadedSumMatrix(final int threads) {
        this.threads = threads;
    }

    @Override
    public double sum(final double[][] matrix) {
        final int rowsForThread = Math.round((float) matrix.length / threads);
        final int columnsForThread = Math.round((float) matrix[0].length / threads);
        int sum = 0;

        final List<Worker> workers = new LinkedList<>();
        for (int r = 0; r * rowsForThread < matrix.length; r++) {
            for (int c = 0; c * columnsForThread < matrix[0].length; c++) {
                final Worker worker = new Worker(r * rowsForThread, c * columnsForThread, rowsForThread, columnsForThread, matrix);
                worker.start();
                workers.add(worker);
            }
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
        private final double[][] mat;

        Worker(final int firstRow, final int firstColumn, final int rows, final int columns, final double[][] mat) {
            super();
            this.mat = new double[rows][columns];
            for (int r = 0; r < rows && (firstRow + r) < mat.length; r++) {
                for (int c = 0; c < columns && (firstColumn + c) < mat[0].length; c++) {
                    this.mat[r][c] = mat[firstRow + r][firstColumn + c];
                }
            }
        }

        public void run() {
            sum = 0;
            for (int i = 0; i < mat.length; i++) {
                for (int j = 0; j < mat[0].length; j++) {
                    sum += mat[i][j];
                }
            }
        }

        public double getSum() {
            return sum;
        }
    }

}
