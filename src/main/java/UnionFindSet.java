import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;

public class UnionFindSet<T> {
    private int count;
    private final int[] parent;
    private final int[] rank;
    private final ArrayList<T> objects;
    private final HashMap<T, Integer> map;

    public UnionFindSet(final HashSet<T> objects) {
        if (null == objects) {
            throw new IllegalArgumentException("Objects cannot be null");
        }
        count = objects.size();
        parent = new int[count];
        rank = new int[count];
        this.objects = new ArrayList<>(objects);
        map = new HashMap<>((int) (count / 0.75) + 1);
        for (int i = 0; i < count; ++i) {
            map.put(this.objects.get(i), i);
            parent[i] = i;
        }
    }

    public int get_count() {
        return count;
    }

    private int find(int x) {
        int root = x;
        while (parent[root] != root) {
            root = parent[root];
        }
        while (parent[x] != root) {
            int next = parent[x];
            parent[x] = root;
            x = next;
        }
        return root;
    }

    public T find(final T x) {
        if (!map.containsKey(x)) {
            throw new NoSuchElementException("Element " + x + " not found");
        }
        return objects.get(find(map.get(x)));
    }

    private void union(final int x, final int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX != rootY) {
            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                ++rank[rootX];
            }
            --count;
        }
    }

    public void union(final T x, final T y) {
        if (!map.containsKey(x)) {
            throw new NoSuchElementException("Element " + x + " not found");
        } else if (!map.containsKey(y)) {
            throw new NoSuchElementException("Element " + y + " not found");
        }
        union(map.get(x), map.get(y));
    }

    public boolean connected(T x, T y) {
        if (!map.containsKey(x)) {
            throw new NoSuchElementException("Element " + x + " not found");
        } else if (!map.containsKey(y)) {
            throw new NoSuchElementException("Element " + y + " not found");
        }
        return find(map.get(x)) == find(map.get(y));
    }
}