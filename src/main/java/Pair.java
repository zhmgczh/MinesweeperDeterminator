import java.util.Objects;

public class Pair<T1, T2> {
    private final T1 first;
    private final T2 second;

    public Pair(final T1 first, final T2 second) {
        this.first = first;
        this.second = second;
    }

    public T1 getFirst() {
        return first;
    }

    public T2 getSecond() {
        return second;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Pair<?, ?> pair = (Pair<?, ?>) o;
        if (!Objects.equals(first, pair.first)) return false;
        return Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ')';
    }
}