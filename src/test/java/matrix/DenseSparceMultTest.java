package matrix;

import org.junit.Assert;
import org.junit.Test;

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
        DenseMatrix sd = a1.mul(b2);
        Assert.assertArrayEquals(sd.getData(), res.getData());
    }

    @Test
    public void testMulSparseSparse() {
        DenseMatrix ss = a1.mul(b1);
        Assert.assertArrayEquals(ss.getData(), res.getData());
    }

    @Test
    public void testMulDenseDense() {
        DenseMatrix dd = a2.mul(b2);
        Assert.assertArrayEquals(dd.getData(), res.getData());
    }

    @Test
    public void testMulSparseDense() {
        DenseMatrix ds = a2.mul(b1);
        Assert.assertArrayEquals(ds.getData(), res.getData());
    }
}
