import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        HashMap <String,Object> normalTasks = new HashMap<>();
        HashMap <String,Object> epicTasks = new HashMap<>();
        HashMap <String,Object> epicSubTasks = new HashMap<>();

        Scanner scanner = new Scanner(System.in);
        AddTaskInHashMap addTaskInHashMap = new AddTaskInHashMap();


        while (true) {
            printMenu();
            int command = scanner.nextInt();

            if (command == 1) {//создать задачу

                CreateTask createTask = new CreateTask();
                Object task  = createTask.createObjectTask((normalTasks.size()+1));
                addTaskInHashMap.addTaskInHashMap(createTask.taskId,task,normalTasks);
                System.out.println(normalTasks.values());
                System.out.println(normalTasks.keySet());


            } else if (command == 2) {

            } else if (command == 3) {

            } else if (command == 4) {

            } else if (command == 5) {

            } else if (command == 6) {
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
//        System.out.println("2 - Обновить задачу ");
//        System.out.println("3 - Увидеть список всех задач");
//        System.out.println("4 - Увидеть задачу по ID");
//        System.out.println("5 - Увидеть все подзадачи эпика по ID");
//        System.out.println("6 - ");
//        System.out.println("7 - Удалить задачу");
//        System.out.println("8 - Удалить все задачи");
        System.out.println("9 - Выход");
    }


}

