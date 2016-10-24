package matrix;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.atomic.*;

/**
 * Created by andrew on 21.09.16.
 */
public class DenseMatrix implements IMatrix {
    class DenseDenseVectorsMult implements Runnable {
        public void run() {
            try {
                int sum = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected long[][] data;

    public DenseMatrix(int row, int col) {
        data = new long[row][col];
    }

    public DenseMatrix(long[][] data) {
        this.data = data;
    }

    public DenseMatrix(String fileName) {
        File file = null;
        Scanner input = null;
        long [][] result = {};
        int currentLine = 0;
        String[] line = {};
        try {
            file = new File(fileName);
            input = new Scanner(file);
            if (input.hasNextLine()) {
                line = input.nextLine().split(" ");
                result = new long[line.length][line.length];
                for (int i = 0; i < line.length; ++i) {
                    result[currentLine][i] = Integer.parseInt(line[i]);
                }

                ++currentLine;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            while (input.hasNextLine()) {
                line = input.nextLine().split(" ");
                for (int i = 0; i < line.length; ++i) {
                    result[currentLine][i] = Integer.parseInt(line[i]);
                }

                ++currentLine;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        data = result;
    }

    public IMatrix mul(IMatrix m) {
        if (m instanceof DenseMatrix) {
            return mul((DenseMatrix) m);
        } else if (m instanceof SparseMatrix) {
            return mul((SparseMatrix) m);
        } else return null;
    }


    public DenseMatrix mul(SparseMatrix m) {
        int size = data.length;
        long result[][] = new long[size][size];
        int sum = 0;

        SparseMatrix mT = m.transposed();

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                sum = 0;
                for (int l = mT.pointer[j]; l < mT.pointer[j + 1]; ++l) {
                    sum += mT.values[l] * data[i][mT.cols[l]];
                }

                result[i][j] = sum;
            }
        }

        return new DenseMatrix(result);
    }

    public DenseMatrix mul(DenseMatrix m) {
        int row1 = data.length;
        int col1 = data[0].length;
        int row2 = m.data.length;
        int col2 = m.data[0].length;

        long mT[][] = new long[col2][row2];
        for (int i = 0; i < row2; ++i) {
            for (int j = 0; j < col2; ++j) {
                mT[i][j] = m.data[j][i];
            }
        }

        class Dispatcher {
            AtomicInteger value = new AtomicInteger(0);
        }

        long result[][] = new long[row1][col2];
        Dispatcher counter = new Dispatcher();

        class RowMult implements Runnable {
            Thread t;
            int i = 0;

            RowMult(String name){
                t = new Thread(this, name);
                t.start();
            }

            public void run() {
                while((i = counter.value.getAndIncrement()) < row1){
                    for (int j = 0; j < col2; ++j) {
                        int sum = 0;
                        for (int k = 0; k < col1; ++k) {
                            sum += data[i][k] * mT[j][k];
                        }

                        result[i][j] = sum;
                    }
                }
            }
        }

        RowMult one = new RowMult("one");
        RowMult two = new RowMult("two");
        RowMult three = new RowMult("three");
        RowMult four = new RowMult("four");
        RowMult five = new RowMult("five");
        RowMult six = new RowMult("six");
        RowMult seven = new RowMult("seven");

        try {
            one.t.join();
            two.t.join();
            three.t.join();
            four.t.join();
            five.t.join();
            six.t.join();
            seven.t.join();
        } catch (InterruptedException e) {
            System.out.println("Главный поток УЩЕМЛЁН");
        }

        return new DenseMatrix(result);
    }

    public long[][] getData() {
        return data;
    }

    public void toFile(String filename) {
        try {
            PrintWriter writer = new PrintWriter(filename);
            int n = data.length;
            int m = data[0].length;
            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < m; ++j) {
                    writer.print(data[i][j] + " ");
                }

                writer.println();
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
