import Managers.*;

import Tasks.*;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();


        Task task1 = new Task(TaskType.TASK, "Первая", TaskStatus.NEW, "1");
        Task task2 = new Task(TaskType.TASK, "Вторая", TaskStatus.NEW, "2");
        Task task3 = new Task(TaskType.TASK, "Третья", TaskStatus.NEW, "3");
        Task task4 = new Task(TaskType.TASK, "Четвертая", TaskStatus.NEW, "4");
        Task task5 = new Task(TaskType.TASK, "пять", TaskStatus.NEW, "5");


        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addTask(task4);

        manager.getTaskById(2);
        manager.getTaskById(3);
        manager.getTaskById(4);



        System.out.println("____________________________проверка пустой истории");
        System.out.println(manager.getTaskHistory());
        System.out.println("____________________________проверка добавления певого звена");
        manager.getTaskById(1);
        System.out.println(manager.getTaskHistory());
        System.out.println("____________________________проверка добавления последующих звеньев");
        manager.getTaskById(2);
        manager.getTaskById(3);
        manager.getTaskById(4);
        System.out.println(manager.getTaskHistory());
        System.out.println("____________________________проверка удаления первого звена");
        manager.deleteTaskById(1);
        System.out.println(manager.getTaskHistory());
        System.out.println("____________________________проверка добавления певого звена повторно");
        manager.addTask(task1);
        manager.getTaskById(5);
        System.out.println(manager.getTaskHistory());
        System.out.println("____________________________проверка добавления удаления дублей");
        manager.getTaskById(2);
        System.out.println(manager.getTaskHistory());
    }
}
