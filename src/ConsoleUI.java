import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        Graph graph = creationHandler(reader);
        commandHandler(reader, graph);
    }

    public static Graph creationHandler(Scanner reader) {
        while (true) {
            System.out.print("Введите имя файла для графа " +
                    "(для создания нового графа оставьте поле пустым): \n");
            String file = reader.nextLine();
            if (file.isBlank()) {
                boolean directed = false;
                LOOP:
                while (true) {
                    System.out.print("Новый граф ориентированный? y/n ");
                    String ans = reader.nextLine();
                    switch (ans.toLowerCase()) {
                        case "y":
                        case "yes":
                            directed = true;
                            break LOOP;
                        case "n":
                        case "no":
                            break LOOP;
                        default:
                            System.out.println("Введите верный ответ на вопрос!\n");
                    }
                }
                return new Graph(directed);
            } else {
                try {
                    return new Graph(new File(file));
                } catch (IOException e) {
                    System.out.println("Не удалось открыть файл: " + file);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public static void commandHandler(Scanner reader, Graph graph) {
        List<Graph> graphList = new ArrayList<>();
        List<String> fileNameList = new ArrayList<>();
        graphList.add(graph);
        fileNameList.add("Main Graph");
        LOOP:
        while (true) {
            clearConsole();
            printMenu();
            System.out.print("\nВведите номер команды: ");
            try {
                int user = Integer.parseInt(reader.nextLine());
                switch (user) {
                    case 0:
                        break LOOP;
                    case 1:
                        clearConsole();
                        System.out.println(graph);
                        waitForEnter(reader);
                        break;
                    case 2:
                        clearConsole();
                        System.out.print("Введите уникальное название для вершины: ");
                        graph.addVertex(reader.nextLine());
                        System.out.println("Вершина успешно добавлена!");
                        waitForEnter(reader);
                        break;
                    case 3:
                        clearConsole();
                        System.out.print("Введите название удаляемой вершины: ");
                        graph.deleteVertex(reader.nextLine());
                        System.out.println("Вершина успешно удалена!");
                        waitForEnter(reader);
                        break;
                    case 4:
                        clearConsole();
                        System.out.println("Введите ребро в формате \"Вершина1-Вершина2 [вес] [метка]\", поля в [] - необязательны:");
                        graph.addEdge(reader.nextLine());
                        System.out.println("Ребро успешно добавлено!");
                        waitForEnter(reader);
                        break;
                    case 5:
                        clearConsole();
                        System.out.print("Введите ребро в формате \"Вершина1-Вершина2\" :");
                        graph.deleteEdge(reader.nextLine());
                        System.out.println("Ребро успешно удалено!");
                        waitForEnter(reader);
                        break;
                    case 6:
                        clearConsole();
                        System.out.println("Введите имя файла: ");
                        String newFileName = reader.nextLine();
                        try {
                            graph.saveInFile(new File(newFileName));
                            System.out.println("Граф сохранен в файл: " + newFileName);
                            fileNameList.set(graphList.indexOf(graph), newFileName);
                            waitForEnter(reader);
                        } catch (RuntimeException e) {
                            System.out.println("При работе с файлом допущена ошибка!");
                            waitForEnter(reader);
                        }
                        break;
                    case 7:
                        clearConsole();
                        System.out.println("Введите номер графа для копирования: ");
                        int n = 0;
                        for (int i = 0; i < graphList.size(); i++) {
                            System.out.println((i + 1) + " " + fileNameList.get(i));
                            n++;
                        }
                        try {
                            int userInt = Integer.parseInt(reader.nextLine());
                            if (userInt > n || userInt < 1) {
                                System.out.println("Введите верную команду - цифру 1-" + n + "!");
                                waitForEnter(reader);
                                break;
                            }
                            Graph graphCopy = new Graph(graphList.get(userInt - 1));
                            graphList.set(graphList.indexOf(graph), graphCopy);
                            graph = graphCopy;
                            System.out.println("Граф под номером " + userInt + " успешно скопирован!");
                            waitForEnter(reader);
                        } catch (NumberFormatException e) {
                            System.out.println("Введите верную команду - цифру 1-" + n + "!");
                            waitForEnter(reader);
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                            waitForEnter(reader);
                        }
                        break;
                    case 8:
                        clearConsole();
                        System.out.println("Введите имя файла для работы с еще одним графом:");
                        String file = reader.nextLine();
                        try {
                            graphList.add(new Graph(new File(file)));
                            fileNameList.add(file);
                            System.out.println("Граф успешно добавлен в список для работы! Выберите его командой 9!");
                            waitForEnter(reader);
                        } catch (IOException e) {
                            System.out.println("Не удалось открыть файл: " + file);
                            waitForEnter(reader);
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                            waitForEnter(reader);
                        }
                        break;
                    case 9:
                        clearConsole();
                        System.out.println("Выберите граф для работы:");
                        int n1 = 0;
                        for (int i = 0; i < graphList.size(); i++) {
                            System.out.println((i + 1) + " " + fileNameList.get(i));
                            n1++;
                        }
                        System.out.println("Выберите номер графа: ");
                        try {
                            int userInt = Integer.parseInt(reader.nextLine());
                            if (userInt > n1 || userInt < 1) {
                                throw new NumberFormatException("Введите верную команду - цифру 1-" + n1 + "!");
                            }
                            graph = graphList.get(userInt - 1);
                            System.out.println("Граф выбран!");
                            waitForEnter(reader);
                        } catch (NumberFormatException e) {
                            System.out.println("Введите верную команду - цифру 1-" + n1 + "!");
                            waitForEnter(reader);
                        }
                        break;
                    case 10:
                        clearConsole();
                        System.out.println("Введите имя вершины:");
                        try {
                            List<String> result = graph.getAllInputVertices(reader.nextLine());
                            if (result.isEmpty())
                                System.out.println("Входящих вершин не обнаружено!");
                            else {
                                System.out.println("Входящие вершины:");
                                result.forEach(System.out::println);
                            }
                            waitForEnter(reader);
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                            waitForEnter(reader);
                        }
                        break;
                    case 11:
                        clearConsole();
                        List<String> vertices = graph.getLeafVertices();
                        if (vertices.isEmpty()) {
                            System.out.println("\"Висячих\" вершин в графе нет!");
                        } else {
                            System.out.println("\"Висячие\" вершины графа:");
                            vertices.forEach(System.out::println);
                        }
                        waitForEnter(reader);
                        break;
                    case 12:
                        clearConsole();
                        Graph newGraph = graph.graphWithoutLeafVertices();
                        System.out.println(newGraph);
                        System.out.println();
                        String name = fileNameList.get(graphList.indexOf(graph)) + " w/o leafs";
                        graphList.add(newGraph);
                        fileNameList.add(name);
                        System.out.println("Граф успешно добавлен в список для работы под названием: \"" + name + "\"!" +
                                "\nВыберите его командой 9!");
                        waitForEnter(reader);
                        break;
                    default:
                        System.out.println("Введите верную команду - цифру 0-12!");
                        waitForEnter(reader);
                }

            } catch (NumberFormatException _) {
                System.out.println("Введите верную команду - цифру 0-12!");
                waitForEnter(reader);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                waitForEnter(reader);
            }
        }
    }

    static void printMenu() {
        System.out.println("1 - Показать граф");
        System.out.println("2 - Добавить вершину");
        System.out.println("3 - Удалить вершину");
        System.out.println("4 - Добавить ребро");
        System.out.println("5 - Удалить ребро");
        System.out.println("6 - Сохранить граф в файл");
        System.out.println("7 - Копировать граф из другого графа");
        System.out.println("8 - Добавить граф для работы");
        System.out.println("9 - Поменять граф для работы");
        System.out.println("10 - Найти все входящие вершины для заданной (только для орграфа)");
        System.out.println("11 - Получить все \"висячие\" вершины");
        System.out.println("12 - Получить граф без ребер ведущих в висячие вершины");
        System.out.println("0 - Выход");
    }

    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void waitForEnter(Scanner scanner) {
        System.out.println("Нажмите Enter, чтобы вернуться в меню...");
        scanner.nextLine();
    }
}
