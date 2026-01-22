import java.util.*;

public class RawGraph {
    public record Edge(int to, int weight) {
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

    private final Node[] head;

    public RawGraph(int n) {
        head = new Node[n];
    }

    public void add_edge(int u, int v, int w) {
        Node node = new Node(v, w);
        node.next = head[u];
        head[u] = node;
    }

    public Iterable<Edge> get_neighbors(int u) {
        return () -> new Iterator<>() {
            private Node current = head[u];

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Edge next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Edge edge = new Edge(current.to, current.weight);
                current = current.next;
                return edge;
            }
        };
    }

    public ArrayList<Integer> get_bfs_order(int root) {
        boolean[] visited = new boolean[head.length];
        ArrayList<Integer> bfs = new ArrayList<>();
        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(root);
        visited[root] = true;
        while (!queue.isEmpty()) {
            int node = queue.poll();
            bfs.add(node);
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