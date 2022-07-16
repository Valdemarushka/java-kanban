package tests;


import network.*;

import org.junit.jupiter.api.Assertions;
import tasks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

class HttpTaskServerTest {
    HttpTaskManager manager;
    KVServer server;
    URI uriKVServer;

    @BeforeEach
    void start() throws IOException, InterruptedException {
        server = new KVServer();
        server.start();
        uriKVServer = KVServer.getServerURL();
        manager = new HttpTaskManager(uriKVServer);
    }

    @AfterEach
    public void stopKVServer() {
        server.stop();
    }

    @Test
    void getTest() {
        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        SubTask subtask1 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 2);
        Task taskForEquals = task1;
        taskForEquals.setId(1);
        Epic epic1ForEquals = epic1;
        epic1ForEquals.setId(2);
        SubTask subtask1ForEquals = subtask1;
        subtask1ForEquals.setId(3);
        epic1ForEquals.addNewSubtaskInEpic(subtask1);

        HashMap<Integer, Task> taskHashMap = new HashMap<>();
        HashMap<Integer, Epic> epicHashMap = new HashMap<>();
        HashMap<Integer, SubTask> subtaskHashMap = new HashMap<>();
        taskHashMap.put(1, taskForEquals);
        epicHashMap.put(2, epic1ForEquals);
        subtaskHashMap.put(3, subtask1ForEquals);
        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubTask(subtask1);
        manager.loadManagerFromKVServer();
        Assertions.assertEquals(manager.getTaskById(1), taskHashMap.get(1), "таск не совпадает");
        Assertions.assertEquals(manager.getEpicById(2), epicHashMap.get(2), "эпик не совпадает");
        Assertions.assertEquals(manager.getSubTaskById(3), subtaskHashMap.get(3), "сабтаск не совпадает");
        Assertions.assertEquals(manager.getEpicById(2).getInnerSubTask(), epicHashMap.get(2).getInnerSubTask(),
                "внутреннай сабтаск не совпадает");
        Assertions.assertEquals(manager.viewAllTask(), taskHashMap, "хэшмап тасков не совпадает");
        Assertions.assertEquals(manager.viewAllEpic(), epicHashMap, "хэшмап эпиков не совпадает");
        Assertions.assertEquals(manager.viewAllSubtask(), subtaskHashMap, "хэшмап сабтасков не совпадает");
    }

    @Test
    void deleteTest() {
        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        SubTask subtask1 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 2);

        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubTask(subtask1);
        manager.loadManagerFromKVServer();
        manager.deleteTaskById(1);
        manager.deleteSubTaskById(3);
        manager.deleteEpicById(2);
        Assertions.assertNull(manager.getTaskById(1), "таск не удален");
        Assertions.assertNull(manager.getEpicById(2), "эпик не удален");
        Assertions.assertNull(manager.getSubTaskById(3), "сабтаск не удален");

        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubTask(subtask1);
        manager.loadManagerFromKVServer();
        manager.deleteAllTask();
        manager.deleteAllSubTask();
        manager.deleteAllEpic();
        Assertions.assertNull(manager.getTaskById(4), "таски не удалены");
        Assertions.assertNull(manager.getEpicById(5), "эпики не удалены");
        Assertions.assertNull(manager.getSubTaskById(6), "сабтаски не удалены");
    }
}