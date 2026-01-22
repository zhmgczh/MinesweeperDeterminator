public class RawUnionFindSet {
    private int count;
    private final int[] parent;
    private final int[] rank;

    public RawUnionFindSet(int n) {
        count = n;
        parent = new int[count];
        rank = new int[count];
        for (int i = 0; i < count; ++i) {
            parent[i] = i;
        }
    }

    public int get_count() {
        return count;
    }

    public int find(int x) {
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

    public void union(final int x, final int y) {
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

    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }
}