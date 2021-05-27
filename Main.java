import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    static class Edge {
        public Node to;
        public Node from;
        public int weight;

        public Edge(Node from, Node to, int weight) {
            this.to = to;
            this.weight = weight;
            this.from = from;
        }
    }
    static class Node {
        public int id;

        public Node(int id) {
            this.id = id;
        }

        HashSet<Edge> nexts = new HashSet<Edge>();
        public Edge previous;
        public int cost = Integer.MAX_VALUE;
        public boolean visited = false;
        public void addNext(Edge edge) {
            nexts.add(edge);
        }

    }
    static class Graph {
        public Node start;
        public Node end;
        public HashMap<Integer, Node> nodes = new HashMap<>();
        public void addEdge(int a, int b, int weight) {
            if (!nodes.containsKey(a)) {
                nodes.put(a, new Node(a));
            }
            if (!nodes.containsKey(b)) {
                nodes.put(b, new Node(b));
            }
            if (weight != 32767) {
                var node1 = nodes.get(a);
                var node2 = nodes.get(b);
                node1.nexts.add(new Edge(node1, node2, weight));
            }

        }
        public void setStart(int a) {
            start = nodes.get(a);
            start.cost = 0;
        }
        public void setEnd(int b) {
            end = nodes.get(b);
        }
        public boolean findPath() {
            for (int i = 0; i < nodes.size(); i++) {
                AtomicReference<Node> currentNode = new AtomicReference<>(null);
                nodes.values().forEach(node -> {
                    if (!node.visited && (currentNode.get() == null || node.cost < currentNode.get().cost)) {
                        currentNode.set(node);
                    }
                });
                if (currentNode.get().cost == Integer.MAX_VALUE)
                    return false;
                currentNode.get().visited = true;
                currentNode.get().nexts.forEach(
                        edge -> {
                            if (currentNode.get().cost + edge.weight < edge.to.cost) {
                                edge.to.cost = currentNode.get().cost + edge.weight;
                                edge.to.previous = edge;
                            }
                        }
                );
            }
            return true;
        }
    }
    public static void main(String[] args) throws FileNotFoundException {
        var graph = new Graph();
        var scanner = new Scanner(new File("in.txt"));
        var count = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < count; i++) {
            var paths = scanner.nextLine()
                    .split(" ");
            for (int j = 0; j < paths.length; j++) {
                graph.addEdge(i, j, Integer.parseInt(paths[j]));
            }
        }
        graph.setStart(Integer.parseInt(scanner.nextLine())-1);
        graph.setEnd(Integer.parseInt(scanner.nextLine())-1);
        var printWriter = new PrintWriter("out.txt");
        graph.findPath();
        if (graph.end.cost != Integer.MAX_VALUE) {
            var result = Stream.iterate(graph.end,x -> x != graph.start, x -> x.previous.from)
                    .collect(Collectors.toList());
            printWriter.write("Y\n");
            printWriter.write(String.valueOf(graph.start.id+1)+ " ");
            for (int i = result.size()-1; i >= 0; i--) {
                printWriter.write(String.valueOf(result.get(i).id+1) + " ");
            }
            printWriter.write("\n");
            var mul = Stream.iterate(graph.end,x -> x != graph.start, x -> x.previous.from)
                    .map(x -> x.previous.weight)
                    .reduce((x,y) -> x*y).orElse(-1);
            printWriter.write(String.valueOf(mul));
        } else {
            printWriter.write("N"+"\n");
        };
        printWriter.close();
    }
}
