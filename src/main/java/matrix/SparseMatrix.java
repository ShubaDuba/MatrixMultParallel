package matrix;

import com.sun.rowset.internal.Row;

import java.io.PrintWriter;
import java.util.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by andrew on 21.09.16.
 */
public class SparseMatrix implements IMatrix {
    protected int size;
    protected long[] values;
    protected int[] cols;
    protected int[] pointer;

    private SparseMatrix() {}

    //sorry
    private int[] toIntArray(ArrayList<Integer> a) {
        int[] result = new int[a.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = a.get(i);
        }

        return result;
    }

    private long[] toLongArray(ArrayList<Integer> a) {
        long[] result = new long[a.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = a.get(i);
        }

        return result;
    }

    public SparseMatrix(String fileName) {
        ArrayList<Integer> a = new ArrayList<>();
        ArrayList<Integer> c = new ArrayList<>();
        ArrayList<Integer> p = new ArrayList<>();
        p.add(0);
        int currentLine = 0;
        String[] line = {};
        int nonZero = 0;
        try {
            File file = new File(fileName);
            Scanner input = new Scanner(file);
            while (input.hasNextLine()) {
                nonZero = 0;
                line = input.nextLine().split(" ");
                for (int i = 0; i < line.length; ++i) {
                    if (!line[i].equals("0")) {
                        a.add(Integer.parseInt(line[i]));
                        c.add(i);
                        ++nonZero;
                    }
                }

                currentLine += nonZero;
                p.add(currentLine);
            }

            values = this.toLongArray(a);
            cols = this.toIntArray(c);
            pointer = this.toIntArray(p);
            size = line.length;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public IMatrix mul(IMatrix m){
        if (m instanceof DenseMatrix) {
            return mul((DenseMatrix) m);
        } else if (m instanceof SparseMatrix) {
            return mul((SparseMatrix) m);
        } else return null;
    }


    public DenseMatrix mul (SparseMatrix m) {
        long[][] result = new long[size][m.size];
        SparseMatrix mT = m.transposed();

        class Dispatcher {
            AtomicInteger value = new AtomicInteger(0);
        }

        Dispatcher counter = new Dispatcher();

        class RowMult implements Runnable{
            Thread t;
            int i = 0;
            long tmp[];

            public RowMult(String threadName) {
                t = new Thread(this, threadName);
                tmp = new long[size];
                t.start();
            }

            public void run() {
                while ((i = counter.value.getAndIncrement()) < size) {
                    for (int j = pointer[i]; j < pointer[i + 1]; ++j) {
                        tmp[cols[j]] = values[j];
                    }

                    for (int n = 0; n < size; ++n) {
                        int sum = 0;
                        for (int l = mT.pointer[n]; l < mT.pointer[n + 1]; ++l) {
                            sum += mT.values[l] * tmp[mT.cols[l]];
                        }

                        result[i][n] = sum;
                    }

                    for (int k = 0; k < size; ++k) {
                        tmp[k] = 0;
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

//        long[] tmp = new long[size];
//        int sum = 0;
//        for (int i = 0; i < pointer.length - 1; ++i) {
//            for (int j = pointer[i]; j < pointer[i + 1]; ++j) {
//                tmp[cols[j]] = values[j];
//            }
//
//            for (int n = 0; n < size; ++n) {
//                sum = 0;
//                for (int l = mT.pointer[n]; l < mT.pointer[n + 1]; ++l) {
//                    sum += mT.values[l] * tmp[mT.cols[l]];
//                }
//
//                result[i][n] = sum;
//            }
//
//            for (int k = 0; k < size; ++k) {
//                tmp[k] = 0;
//            }
//        }

        return new DenseMatrix(result);
    }

    public DenseMatrix mul(DenseMatrix m) {
        long[][] result = new long[size][size];
        int row2 = m.data.length;
        int col2 = m.data[0].length;

        long mT[][] = new long[col2][row2];
        for (int i = 0; i < row2; ++i) {
            for (int j = 0; j < col2; ++j) {
                mT[i][j] = m.data[j][i];
            }
        }

        int sum = 0;
        long[] tmp = new long[size];

        for (int i = 0; i < pointer.length - 1; ++i) {
            for (int j = pointer[i]; j < pointer[i + 1]; ++j) {
                tmp[cols[j]] = values[j];
            }

            for (int n = 0; n < size; ++n) {
                sum = 0;
                for (int l = 0; l < size; ++l) {
                    sum += mT[n][l] * tmp[l];
                }

                result[i][n] = sum;
            }

            for (int k = 0; k < size; ++k) {
                tmp[k] = 0;
            }
        }

        return new DenseMatrix(result);
    }

    public SparseMatrix transposed() {
        class Pair {
            long value;
            int row;

            public Pair(long value, int row) {
                this.value = value;
                this.row = row;
            }
        }

        SparseMatrix result = new SparseMatrix();
        result.size = size;
        result.pointer = new int[size + 1];
        result.pointer[0] = 0;
        ArrayList<ArrayList<Pair>> colsArray = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            colsArray.add(new ArrayList<>());
        }

        for (int i = 0; i < pointer.length - 1; ++i) {
            for (int j = pointer[i]; j < pointer[i + 1]; ++j) {
                colsArray.get(cols[j]).add(new Pair(values[j], i));
            }
        }

        result.values = new long[values.length];
        result.cols = new int[cols.length];
        int current = 0;
        int colSize = 0;
        Pair tmp;
        ArrayList<Pair> col;
        for (int i = 0; i < size; ++i) {
            col = colsArray.get(i);
            colSize = col.size();
            for (int j = 0; j < colSize; ++j) {
                tmp = col.get(j);
                result.values[current] = tmp.value;
                result.cols[current++] = tmp.row;
            }

            result.pointer[i + 1] = result.pointer[i] + colSize;
        }

        return result;
    }

    public int getSize() {
        return size;
    }

    public void toFile(String filename) {
        try {
            PrintWriter writer = new PrintWriter(filename);

            long[] tmp = new long[size];
            int sum = 0;
            for (int i = 0; i < pointer.length - 1; ++i) {
                for (int j = pointer[i]; j < pointer[i + 1]; ++j) {
                    tmp[cols[j]] = values[j];
                }

                for (int n = 0; n < size; ++n) {
                    writer.print(tmp[n] + " ");
                }

                writer.println();
                for (int k = 0; k < size; ++k) {
                    tmp[k] = 0;
                }
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
