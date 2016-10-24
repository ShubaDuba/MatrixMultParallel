package matrix;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by andrew on 12.10.16.
 */
public class DenseSparceMultTest {
    SparseMatrix a1;
    DenseMatrix a2;
    SparseMatrix b1;
    DenseMatrix b2;
    DenseMatrix res;

    public DenseSparceMultTest()  {
        try {
            a1 = new SparseMatrix("src/test/test1.txt");
            a2 = new DenseMatrix("src/test/test1.txt");
            b1 = new SparseMatrix("src/test/test2.txt");
            b2 = new DenseMatrix("src/test/test2.txt");
            res = new DenseMatrix("src/test/test_result.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMulDenseSparse() {
        DenseMatrix sd = (DenseMatrix) a1.mul(b2);
        assertEquals(sd.getData(), res.getData());
    }

    @Test
    public void testMulSparseSparse() {
        DenseMatrix ss = (DenseMatrix) a1.mul(b1);
        assertEquals(ss.getData(), res.getData());
    }

    @Test
    public void testMulDenseDense() {
        DenseMatrix dd = (DenseMatrix) a2.mul(b2);
        assertEquals(dd.getData(), res.getData());
    }

    @Test
    public void testMulSparseDense() {
        DenseMatrix ds = (DenseMatrix) a2.mul(b1);
        assertEquals(ds.getData(), res.getData());
    }
}
