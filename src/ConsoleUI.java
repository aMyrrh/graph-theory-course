import java.io.File;
import java.io.IOException;
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
                    "(для создания нового графа оставьте поле пустым): ");
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
        LOOP:
        while (true) {
            clearConsole();
            printMenu();
            System.out.print("Введите номер команды: ");
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
                            waitForEnter(reader);
                        } catch (RuntimeException e) {
                            System.out.println("При работе с файлом допущена ошибка!");
                            waitForEnter(reader);
                        }
                        break;
                    default:
                        System.out.println("Введите верную команду - цифру 0-6!");
                        waitForEnter(reader);
                }

            } catch (NumberFormatException _) {
                System.out.println("Введите верную команду - цифру 0-6!");
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
