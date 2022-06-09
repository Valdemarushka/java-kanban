import Managers.*;

import Tasks.*;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager manager = (InMemoryTaskManager) Managers.getDefault();
        //TaskManager manager = Managers.getDefault(); разобраться на будущее как впихнуть сюда это выражение

        Task task1 = new Task("Первая", "1", TaskStatus.NEW);
        Task task2 = new Task("Вторая", "2", TaskStatus.NEW);
        Task task3 = new Task("Третья", "3", TaskStatus.NEW);
        Task task4 = new Task("Четвертая", "4", TaskStatus.NEW);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addTask(task4);


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

        System.out.println("____________________________проверка удаления всех задач");
        manager.deleteAllTask();
        System.out.println(manager.getTaskHistory());
    }
}

