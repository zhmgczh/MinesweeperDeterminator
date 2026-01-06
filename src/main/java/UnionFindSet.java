import java.util.ArrayList;
import java.util.HashMap;

public class UnionFindSet<T> {
    private final int[] parent;
    private final ArrayList<T> objects;
    private final HashMap<T, Integer> map;

    public UnionFindSet(final ArrayList<T> objects) {
        int n = objects.size();
        parent = new int[n];
        this.objects = objects;
        map = new HashMap<>();
        for (int i = 0; i < n; ++i) {
            map.put(objects.get(i), i);
            parent[i] = i;
        }
    }

    private int find(final int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public T find(final T x) {
        return objects.get(find(map.get(x)));
    }

    private void union(final int x, final int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX != rootY) {
            parent[rootY] = rootX;
        }
    }

    public void union(final T x, final T y) {
        union(map.get(x), map.get(y));
    }
}