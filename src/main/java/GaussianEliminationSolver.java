import java.math.BigInteger;
import java.util.*;

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
    private static BigInteger[] initialize_coefficients(final int length) {
        BigInteger[] coefficients = new BigInteger[length];
        Arrays.fill(coefficients, BigInteger.ZERO);
        return coefficients;
    }

    private static HashMap<Integer, Rat> get_unique_solutions(final BigInteger[][] A, final BigInteger[] b) {
        HashMap<Integer, Rat> unique_solutions = new HashMap<>();
        Rat[][] rref = GaussianRREF.rrefAugmented(A, b);
        int current_lower_j = 0;
        for (Rat[] rats : rref) {
            for (; current_lower_j < rref[0].length - 1; ++current_lower_j) {
                if (rats[current_lower_j].isOne()) {
                    break;
                }
            }
            if (current_lower_j == rref[0].length - 1) {
                if (rats[current_lower_j].isZero()) {
                    break;
                } else {
                    return null;
                }
            }
            boolean is_unique = true;
            for (int j = current_lower_j + 1; j < rref[0].length - 1; ++j) {
                if (!rats[j].isZero()) {
                    is_unique = false;
                }
            }
            if (is_unique) {
                unique_solutions.put(current_lower_j, rats[rref[0].length - 1]);
            }
            ++current_lower_j;
        }
        return unique_solutions;
    }

    private static void process_number_point(final ArrayList<Pair<Integer, Integer>> block, final char[][] map, final boolean[][] prediction_tag, final Pair<Integer, Integer> number_point, final boolean[][] visited, final HashMap<Pair<Integer, Integer>, Integer> point_index, final ArrayList<BigInteger[]> A, final ArrayList<BigInteger> b) {
        if (!visited[number_point.getFirst()][number_point.getSecond()]) {
            int mines = 0;
            ArrayList<Integer> prediction_point_indices = new ArrayList<>();
            for (int[] unit_vector : MinesweeperState.unit_vectors) {
                int new_i = number_point.getFirst() + unit_vector[0];
                int new_j = number_point.getSecond() + unit_vector[1];
                if (new_i >= 0 && new_i < map.length && new_j >= 0 && new_j < map[0].length) {
                    if (prediction_tag[new_i][new_j]) {
                        prediction_point_indices.add(point_index.get(new Pair<>(new_i, new_j)));
                    } else if (MinesweeperState.MINE_FLAG == map[new_i][new_j]) {
                        ++mines;
                    }
                }
            }
            int total = MinesweeperState.to_number(map[number_point.getFirst()][number_point.getSecond()]) - mines;
            if (0 == total) {
                for (int prediction_point_index : prediction_point_indices) {
                    BigInteger[] coefficients = initialize_coefficients(block.size());
                    coefficients[prediction_point_index] = BigInteger.ONE;
                    A.add(coefficients);
                    b.add(BigInteger.ZERO);
                }
            } else if (total == prediction_point_indices.size()) {
                for (int prediction_point_index : prediction_point_indices) {
                    BigInteger[] coefficients = initialize_coefficients(block.size());
                    coefficients[prediction_point_index] = BigInteger.ONE;
                    A.add(coefficients);
                    b.add(BigInteger.ONE);
                }
            } else {
                BigInteger[] coefficients = initialize_coefficients(block.size());
                for (int prediction_point_index : prediction_point_indices) {
                    coefficients[prediction_point_index] = BigInteger.ONE;
                }
                A.add(coefficients);
                b.add(BigInteger.valueOf(total));
            }
            visited[number_point.getFirst()][number_point.getSecond()] = true;
        }
    }

    public static Pair<BigInteger[][], BigInteger[]> get_equations_from_block(final ArrayList<Pair<Integer, Integer>> block, final char[][] map, final boolean[][] prediction_tag) {
        HashMap<Pair<Integer, Integer>, Integer> point_index = new HashMap<>();
        for (int i = 0; i < block.size(); ++i) {
            point_index.put(block.get(i), i);
        }
        boolean[][] visited = new boolean[map.length][map[0].length];
        ArrayList<BigInteger[]> A = new ArrayList<>();
        ArrayList<BigInteger> b = new ArrayList<>();
        for (Pair<Integer, Integer> point : block) {
            ArrayList<Pair<Integer, Integer>> numbers_in_domain = MinesweeperState.get_numbers_in_domain(map, point.getFirst(), point.getSecond());
            for (Pair<Integer, Integer> number_point : numbers_in_domain) {
                process_number_point(block, map, prediction_tag, number_point, visited, point_index, A, b);
            }
        }
        return new Pair<>(A.toArray(new BigInteger[0][]), b.toArray(new BigInteger[0]));
    }

    public static ArrayList<Pair<Pair<Integer, Integer>, Character>> get_predictions_from_block(final ArrayList<Pair<Integer, Integer>> block, final char[][] map, final boolean[][] prediction_tag) {
        ArrayList<Pair<Pair<Integer, Integer>, Character>> predictions = new ArrayList<>();
        Pair<BigInteger[][], BigInteger[]> equations = get_equations_from_block(block, map, prediction_tag);
        HashMap<Integer, Rat> unique_solutions = get_unique_solutions(equations.getFirst(), equations.getSecond());
        if (null == unique_solutions) {
            return null;
        }
        for (int i = 0; i < block.size(); ++i) {
            if (unique_solutions.containsKey(i)) {
                Pair<Integer, Integer> point = block.get(i);
                Rat solution = unique_solutions.get(i);
                if (solution.isZero()) {
                    predictions.add(new Pair<>(point, MinesweeperState.ZERO));
                } else if (solution.isOne()) {
                    predictions.add(new Pair<>(point, MinesweeperState.ONE));
                } else {
                    return null;
                }
            }
        }
        return predictions;
    }

    public static ArrayList<Pair<Pair<Integer, Integer>, Character>> get_predictions_from_blocks(final ArrayList<ArrayList<Pair<Integer, Integer>>> blocks, final char[][] map, final boolean[][] prediction_tag) {
        ArrayList<Pair<Pair<Integer, Integer>, Character>> predictions = new ArrayList<>();
        for (ArrayList<Pair<Integer, Integer>> block : blocks) {
            ArrayList<Pair<Pair<Integer, Integer>, Character>> pred = get_predictions_from_block(block, map, prediction_tag);
            if (null == pred) {
                return null;
            }
            predictions.addAll(pred);
        }
        return predictions;
    }
}