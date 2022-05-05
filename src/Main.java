import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        HashMap<String, Object> normalTasks = new HashMap<>();
        HashMap<String, EpicTask> epicTasks = new HashMap<>();
        HashMap<String, SubTask> epicSubTasks = new HashMap<>();
        Scanner scanner = new Scanner(System.in);

        AddTaskInMap addTaskInMap = new AddTaskInMap();
        AddEpicTaskInMap addEpicTaskInMap = new AddEpicTaskInMap();
        AddSubTaskInMap addSubTaskInMap = new AddSubTaskInMap();
        UpdateTask updateTask = new UpdateTask();
        ViewAllTask viewAllTask = new ViewAllTask();

        Task task1 = new Task("Задача 1", "описание задачи 1", "new");
        EpicTask epic1 = new EpicTask("Эпик 1", "описание задачи 1", "new");
        SubTask subTask1 = new SubTask("подзадача1", "описание подзадачи1", "new");
        Task task2 = new Task("Задача 2", "описание задачи 2", "new");
        SubTask subTask3 = new SubTask("подзадача33", "описание подзадачи33", "done");


        while (true) {
            printMenu();
            int command = scanner.nextInt();

            if (command == 1) {//создать задачу
                addTaskInMap.addTaskInHashMap(normalTasks, task1);
                System.out.println(normalTasks);
                addTaskInMap.addTaskInHashMap(normalTasks, task2);
                System.out.println(normalTasks);

            } else if (command == 2) {//создать эпик
                addEpicTaskInMap.addTaskInHashMap(epicTasks, epic1);
                System.out.println(epicTasks.keySet());
                System.out.println(epicTasks.values());


            } else if (command == 3) {//создать подзадачу
                addSubTaskInMap.addSubTaskInMap(subTask1, epicSubTasks, "ET1");
                addSubTaskInMap.addSubTaskInMap(subTask1, epicSubTasks, "ET1");

                System.out.println(epicSubTasks.keySet());
                System.out.println(epicSubTasks.values());

            } else if (command == 4) {//обновить задачу
                updateTask.updateTask(normalTasks, "NT1", task2);
                System.out.println(normalTasks);
                updateTask.updateTask(normalTasks, "NT2", task1);
                System.out.println(normalTasks);
                updateTask.updateSubTask(epicSubTasks, "ST1", subTask3, "ET1", epicTasks);
                System.out.println(epicSubTasks);
                System.out.println(epicTasks);
                updateTask.updateSubTask(epicSubTasks, "ST2", subTask3, "ET1", epicTasks);
                System.out.println(epicSubTasks);
                System.out.println(epicTasks);
            } else if (command == 5) {
                System.out.println("Выход");
                break;
            } else {
                System.out.println("Извините, такой команды пока нет.");

            }
        }
    }

    public static void printMenu() {
        System.out.println("Что вы хотите сделать? ");
        System.out.println("1 - Создать задачу ");
        System.out.println("2 - Создать эпик ");
        System.out.println("3 - Создать подзадачи ");
        System.out.println("4 - Обновить задачи ");
        System.out.println("5 - Выход");
    }


}

