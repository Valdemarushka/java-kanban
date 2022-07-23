import managers.Managers;
import managers.TaskManager;
import network.HttpTaskManager;
import network.HttpTaskServer;
import network.KVServer;
import network.KVTaskClient;
import tasks.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {

        Integer id = null;
        System.out.println(id);

        System.out.println("______________ попытка запуска сервера");
        KVServer kvServer = new KVServer();
        kvServer.start();
        URI uriKVServer = KVServer.getServerURL();
        System.out.println("______________ вызова менеджера и загрузки задач с сервера");
        TaskManager manager = Managers.getDefault(uriKVServer);
        System.out.println("______________ Запуск http сервера менеджера");
        new HttpTaskServer(manager).start();

        System.out.println("______________ создание задач");

        Epic epic3 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        SubTask subtask5 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        manager.addEpic(epic3);
        manager.addSubTask(subtask5);
        System.out.println(manager.getEpicById(1).getInnerSubTask());

/*
        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        Task task2 = new Task(TaskType.TASK, "2004", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2006, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "22");
        Epic epic3 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        Epic epic4 = new Epic(TaskType.EPIC, "эпик включающий2010", "56", TaskStatus.NEW);
        SubTask subtask5 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 2);
        SubTask subtask6 = new SubTask(TaskType.SUBTASK, "2010", TaskStatus.DONE,
                LocalDateTime.of(2010, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 2);

        System.out.println("______________ добавление task1");
        manager.addTask(task1);
        System.out.println("______________ добавление epic2");
        manager.addEpic(epic3);

       System.out.println("______________ добавление subtask3");
        manager.addSubTask(subtask5);
        System.out.println("______________ обновление task1");
        manager.updateTask(1, task2);
        System.out.println("______________ обновление epic2");
        manager.updateEpic(1, epic4);
        System.out.println("______________ вызов task1");
        System.out.println(manager.getTaskById(1));
        System.out.println("______________ вызов epic2");
        System.out.println(manager.getEpicById(2));
        System.out.println("______________ проверка истории" + manager.getTaskHistory());*/


    }
}
