import java.util.*;

public class Graph<T> {
    public record Edge<T>(T to, int weight) {
    }

    private static class Node {
        int to;
        int weight;
        Node next;

        public Node(int to, int weight) {
            this.to = to;
            this.weight = weight;
            this.next = null;
        }
    }

    private final ArrayList<T> nodes;
    private final HashMap<T, Integer> map;
    private final Node[] head;

    public Graph(final HashSet<T> nodes) {
        if (null == nodes) {
            throw new IllegalArgumentException("Nodes cannot be null.");
        }
        int n = nodes.size();
        this.nodes = new ArrayList<>(nodes);
        map = new HashMap<>((int) (n / 0.75f) + 1);
        head = new Node[n];
        for (int i = 0; i < n; ++i) {
            map.put(this.nodes.get(i), i);
        }
    }

    private void add_edge(int u, int v, int w) {
        Node node = new Node(v, w);
        node.next = head[u];
        head[u] = node;
    }

    public void add_edge(T u, T v, int w) {
        if (!map.containsKey(u)) {
            throw new NoSuchElementException("Node " + u + " not found");
        } else if (!map.containsKey(v)) {
            throw new NoSuchElementException("Node " + v + " not found");
        }
        add_edge(map.get(u), map.get(v), w);
    }

    public Iterable<Edge<T>> get_neighbors(T u) {
        if (!map.containsKey(u)) {
            throw new NoSuchElementException("Node " + u + " not found");
        }
        int index = map.get(u);
        return () -> new Iterator<>() {
            private Node current = head[index];

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Edge<T> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Edge<T> edge = new Edge<>(nodes.get(current.to), current.weight);
                current = current.next;
                return edge;
            }
        };
    }

    public ArrayList<T> get_bfs_order(T root) {
        if (!map.containsKey(root)) {
            throw new NoSuchElementException("Node " + root + " not found");
        }
        boolean[] visited = new boolean[nodes.size()];
        ArrayList<T> bfs = new ArrayList<>();
        Queue<Integer> queue = new ArrayDeque<>();
        int root_index = map.get(root);
        queue.offer(root_index);
        visited[root_index] = true;
        while (!queue.isEmpty()) {
            int node = queue.poll();
            bfs.add(nodes.get(node));
            Node cursor = head[node];
            while (cursor != null) {
                int to = cursor.to;
                if (!visited[to]) {
                    queue.offer(to);
                    visited[to] = true;
                }
                cursor = cursor.next;
            }
        }
        return bfs;
    }
}