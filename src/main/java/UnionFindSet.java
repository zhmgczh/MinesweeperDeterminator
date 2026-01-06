import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class UnionFindSet<T> {
    private final int[] parent;
    private final ArrayList<T> objects;
    private final HashMap<T, Integer> map;

    public UnionFindSet(final HashSet<T> objects) {
        int n = objects.size();
        parent = new int[n];
        this.objects = new ArrayList<>(n);
        map = new HashMap<>();
        int index = 0;
        for (T object : objects) {
            this.objects.add(object);
            map.put(object, index);
            parent[index] = index;
            ++index;
        }
    }

    public HashSet<T> get_objects() {
        return new HashSet<>(objects);
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