import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Graph {
    private boolean directed;
    private final Map<String, Neighbors> allVertices = new HashMap<>();

    public Graph() {
    }

    public Graph(boolean directed){
        this.directed = directed;
    }

    public Graph(File file) throws IOException {
        try (Scanner reader = new Scanner(file, StandardCharsets.UTF_8)) {
            if (reader.hasNextLine()) {
                String checkDirection = reader.nextLine();
                if (!checkDirection.equals("directed") && !checkDirection.equals("undirected"))
                    throw new IllegalArgumentException("Первая строка файла должна содержать \n " +
                            "информацию об ориентированности графа: directed/undirected");
                directed = checkDirection.equals("directed");
            } else {
                throw new IllegalArgumentException("Заполните файл!");
            }

            while (reader.hasNextLine()) {
                String stringLine = reader.nextLine();
                String pattern = "[\\d\\p{L}]+-[\\d\\p{L}]+";
                if (stringLine.matches(pattern)
                        || stringLine.matches(pattern + " (|-)\\d+(|(\\.\\d+))")
                        || stringLine.matches(pattern + " (|-)\\d+(|(\\.\\d+)) ([ \\d\\p{L}\\p{Punct}])+")
                        || stringLine.matches("[\\d\\p{L}]+")
                ) {
                    String[] line = stringLine.split("[- ]");
                    if (!allVertices.containsKey(line[0]))
                        addVertex(line[0]);
                    if (line.length > 1 && !allVertices.containsKey(line[1]))
                        addVertex(line[1]);
                    if (line.length != 1) {
                        addEdge(stringLine);
                    }
                } else throw new IllegalArgumentException("Неверный формат строки! Правильный формат: \n" +
                        "Имя_вершины1-Имя_вершины2 [вес ребра] [метка ребра]");
            }

        } catch (IOException e) {
            throw new IOException(e);
        }

    }

    public Graph(Graph other) {
        this.directed = other.directed;

        for (String s : other.allVertices.keySet()) {
            this.addVertex(s);
        }

        for (String s : other.allVertices.keySet()) {
            for (Map.Entry<String, EdgeInfo> vertex
                    : other.allVertices.get(s).neighborsMap.entrySet()) {
                if (directed) {
                    this.allVertices.get(s).neighborsMap.put(
                            vertex.getKey(),
                            new EdgeInfo(vertex.getValue())
                    );
                } else {
                    if (!this.allVertices.get(s).neighborsMap.containsKey(vertex.getKey())) {
                        EdgeInfo e = new EdgeInfo(vertex.getValue());
                        this.allVertices.get(s).neighborsMap.put(
                                vertex.getKey(),
                                e
                        );
                        this.allVertices.get(vertex.getKey()).neighborsMap.put(
                                s,
                                e
                        );
                    }
                }
            }
        }
    }

    public void addVertex(String name) {
        if (allVertices.containsKey(name)){
            throw new IllegalArgumentException("Вершина уже существует!");
        }
        else
            allVertices.put(name, new Neighbors());
    }


    public void addEdge(String info) {
        info = info.replaceFirst("-", " ");
        String[] line = info.split(" ", 4);
        if (!allVertices.containsKey(line[0]) || !allVertices.containsKey(line[1]))
            throw new IllegalArgumentException("При вводе вершин допущена ошибка: Подобной пары вершин не существует!");
        Map<String, EdgeInfo> map = allVertices.get(line[0]).neighborsMap;
        Map<String, EdgeInfo> directedMap = allVertices.get(line[1]).neighborsMap;
        if (map.containsKey(line[1])) {
            throw new IllegalArgumentException("При вводе вершин допущена ошибка: Такое ребро уже существует!");
        } else {
            EdgeInfo edgeInfo = new EdgeInfo();
            try {
                edgeInfo.weight = (line.length > 2) ? Double.parseDouble(line[2]) : null;
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Неверный формат ввода веса!");
            }
            edgeInfo.label = (line.length > 3) ? line[3] : null;
            map.put(line[1], edgeInfo);
            if (!directed) {
                directedMap.put(line[0], edgeInfo);
            }
        }
    }


    public void deleteVertex(String name) {
        if (allVertices.containsKey(name)) {
            for (Neighbors value : allVertices.values()) {
                value.neighborsMap.remove(name);
            }
            allVertices.remove(name);
        } else
            throw new IllegalArgumentException("Введите существующую вершину!");
    }

    public void deleteEdge(String info) {
        if (info.matches("[\\d\\p{L}]+-[\\d\\p{L}]+")) {
            String[] edge = info.split("-");
            String from = edge[0], to = edge[1];
            if (allVertices.containsKey(from)
                    && allVertices.get(from).neighborsMap.containsKey(to)) {
                allVertices.get(from).neighborsMap.remove(to);
                if (!directed) {
                    allVertices.get(to).neighborsMap.remove(from);
                }
            } else {
                throw new IllegalArgumentException("Введенного ребра не существует!");
            }

        } else {
            throw new IllegalArgumentException("Введен неверный формат удаления. \n" +
                    "Правильный формат: from-to");
        }
    }

    public void saveInFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(directed ? "directed\n" : "undirected\n");
            Map<String, List<String>> writed = new HashMap<>();
            for (Map.Entry<String, Neighbors> from : allVertices.entrySet()) {
                if (!allVertices.get(from.getKey()).neighborsMap.isEmpty()) {
                    for (Map.Entry<String, EdgeInfo> to : from.getValue().neighborsMap.entrySet()) {
                        if (!directed) {
                            if (writed.containsKey(from.getKey()) && writed.get(from.getKey()).contains(to.getKey()) ||
                                    writed.containsKey(to.getKey()) && writed.get(to.getKey()).contains(from.getKey()))
                                continue;
                        }
                        writer.write(from.getKey() + "-" + to.getKey());
                        if (to.getValue().weight != null) {
                            writer.write(" " + to.getValue().weight);
                        }
                        if (to.getValue().label != null) {
                            writer.write(" " + to.getValue().label);
                        }
                        writer.write("\n");
                        writed.putIfAbsent(from.getKey(), new ArrayList<>());
                        writed.get(from.getKey()).add(to.getKey());
                    }
                } else {
                    writer.write(from.getKey() + "\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(directed ? "directed\n" : "undirected\n");
        for (Map.Entry<String, Neighbors> from : allVertices.entrySet()) {
            if (!allVertices.get(from.getKey()).neighborsMap.isEmpty()) {
                result.append(from.getKey()).append(": ");
                for (Map.Entry<String, EdgeInfo> to : from.getValue().neighborsMap.entrySet()) {
                    result.append(to.getKey());
                    if (to.getValue().weight != null || to.getValue().label != null) {
                        result.append("(");
                        if (to.getValue().weight != null) {
                            result.append(to.getValue().weight);
                            if (to.getValue().label != null) {
                                result.append('-');
                            }
                        }
                        if (to.getValue().label != null) {
                            result.append(to.getValue().label);
                        }
                        result.append(")");
                    }
                    result.append(", ");
                }
                result.delete(result.lastIndexOf(","), result.lastIndexOf(",")+2);
                result.append('\n');
            } else {
                result.append(from.getKey()).append("\n");
            }
        }
        return result.toString();
    }

    public List<Edge> getListOfEdges(){
        List<Edge> result = new ArrayList<>();
        for (Map.Entry<String, Neighbors> from : allVertices.entrySet()) {
            if (!allVertices.get(from.getKey()).neighborsMap.isEmpty()) {
                for (Map.Entry<String, EdgeInfo> to : from.getValue().neighborsMap.entrySet()) {
                    Edge e = new Edge(from.getKey(), to.getKey(), to.getValue().weight, to.getValue().label);
                    if (!directed) {
                        Edge e1 = new Edge(to.getKey(), from.getKey(), to.getValue().weight, to.getValue().label);
                        if (result.contains(e1) || result.contains(e))
                            continue;
                    }
                    result.add(e);
                }
            }
        }
        return result;
    }
}


// Содержит инфу о ребрах / дугах соседних к одной вершине: направление, вес, метка
class Neighbors {
    Map<String, EdgeInfo> neighborsMap = new HashMap<>();

    public Neighbors() {
    }
}

// Содержит инфу о дуге / ребре
class EdgeInfo {
    Double weight;
    String label;

    public EdgeInfo() {
    }

    public EdgeInfo(Double weight, String label) {
        this.label = label;
        this.weight = weight;
    }

    public EdgeInfo(EdgeInfo other) {
        this.weight = other.weight;
        this.label = other.label;
    }
}

// Содержит инфу для списка ребер
record Edge(String from,
            String to,
            Double weight,
            String label) {
}