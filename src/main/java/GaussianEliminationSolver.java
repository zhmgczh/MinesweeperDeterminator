import java.math.BigInteger;
import java.util.Scanner;

class Rat {
    public static final Rat ZERO = new Rat(BigInteger.ZERO, BigInteger.ONE);
    public static final Rat ONE = new Rat(BigInteger.ONE, BigInteger.ONE);
    private final BigInteger num;
    private final BigInteger den;

    public Rat(BigInteger num, BigInteger den) {
        if (den.equals(BigInteger.ZERO)) throw new ArithmeticException("denominator is zero");
        if (den.compareTo(BigInteger.ZERO) < 0) {
            num = num.negate();
            den = den.negate();
        }
        if (num.equals(BigInteger.ZERO)) {
            this.num = BigInteger.ZERO;
            this.den = BigInteger.ONE;
        } else {
            BigInteger g = num.abs().gcd(den);
            this.num = num.divide(g);
            this.den = den.divide(g);
        }
    }

    public BigInteger getNum() {
        return num;
    }

    public BigInteger getDen() {
        return den;
    }

    public static Rat of(BigInteger x) {
        return new Rat(x, BigInteger.ONE);
    }

    public boolean isZero() {
        return num.equals(BigInteger.ZERO);
    }

    public boolean isOne() {
        return num.equals(den);
    }

    public boolean isInteger() {
        return den.equals(BigInteger.ONE);
    }

    public BigInteger toBigInteger() {
        return num.divide(den);
    }

    public Rat neg() {
        return new Rat(num.negate(), den);
    }

    public Rat add(Rat o) {
        BigInteger n = this.num.multiply(o.den).add(o.num.multiply(this.den));
        BigInteger d = this.den.multiply(o.den);
        return new Rat(n, d);
    }

    public Rat sub(Rat o) {
        return add(o.neg());
    }

    public Rat mul(Rat o) {
        BigInteger a = this.num, b = this.den, c = o.num, d = o.den;
        BigInteger g1 = a.abs().gcd(d);
        BigInteger g2 = c.abs().gcd(b);
        a = a.divide(g1);
        d = d.divide(g1);
        c = c.divide(g2);
        b = b.divide(g2);
        return new Rat(a.multiply(c), b.multiply(d));
    }

    public Rat div(Rat o) {
        if (o.num.equals(BigInteger.ZERO)) throw new ArithmeticException("divide by zero");
        return this.mul(new Rat(o.den, o.num));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rat rat = (Rat) o;
        return num.multiply(rat.den).equals(den.multiply(rat.num));
    }

    @Override
    public String toString() {
        if (den.equals(BigInteger.ONE)) return num.toString();
        return num.toString() + "/" + den;
    }
}

class GaussianRREF {
    public static Rat[][] rrefAugmented(BigInteger[][] A, BigInteger[] b) {
        int m = A.length;
        int n = (m == 0) ? 0 : A[0].length;
        if (b.length != m) throw new IllegalArgumentException("b length must equal number of rows in A");
        Rat[][] M = new Rat[m][n + 1];
        for (int i = 0; i < m; ++i) {
            if (A[i].length != n) throw new IllegalArgumentException("A must be rectangular");
            for (int j = 0; j < n; ++j) {
                BigInteger v = A[i][j];
                M[i][j] = Rat.of(v);
            }
            M[i][n] = Rat.of(b[i]);
        }
        int row = 0;
        for (int col = 0; col < n && row < m; ++col) {
            int piv = -1;
            for (int r = row; r < m; ++r) {
                if (!M[r][col].isZero()) {
                    piv = r;
                    break;
                }
            }
            if (piv == -1) continue;
            if (piv != row) {
                Rat[] tmp = M[piv];
                M[piv] = M[row];
                M[row] = tmp;
            }
            Rat pivotVal = M[row][col];
            for (int j = col; j <= n; ++j) {
                M[row][j] = M[row][j].div(pivotVal);
            }
            for (int r = 0; r < m; ++r) {
                if (r == row) continue;
                Rat factor = M[r][col];
                if (factor.isZero()) continue;
                for (int j = col; j <= n; ++j) {
                    M[r][j] = M[r][j].sub(factor.mul(M[row][j]));
                }
            }
            ++row;
        }
        return M;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int m = sc.nextInt();
        int n = sc.nextInt();
        BigInteger[][] A = new BigInteger[m][n];
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) A[i][j] = sc.nextBigInteger();
        }
        BigInteger[] b = new BigInteger[m];
        for (int i = 0; i < m; ++i) b[i] = sc.nextBigInteger();
        Rat[][] rref = GaussianRREF.rrefAugmented(A, b);
        for (int i = 0; i < m; ++i) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; ++j) {
                sb.append(rref[i][j]).append(' ');
            }
            sb.append("| ").append(rref[i][n]);
            System.out.println(sb.toString().trim());
        }
    }
}

public class GaussianEliminationSolver {

}