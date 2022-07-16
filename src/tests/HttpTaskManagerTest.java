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
import java.util.List;

class HttpTaskManagerTest {
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
    void addTaskTest() {
        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        Task task1Equals = task1;
        task1Equals.setId(1);
        manager.addTask(task1);
        manager.loadManagerFromKVServer();
        Assertions.assertEquals(task1Equals, manager.normalTasks.get(task1.getId()));
    }

    @Test
    void addEpicTest() {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        Epic epic1Equals = epic1;
        epic1Equals.setId(1);
        manager.addEpic(epic1);
        manager.loadManagerFromKVServer();
        Assertions.assertEquals(epic1Equals, manager.epicTasks.get(epic1.getId()));
    }

    @Test
    void addSubTaskTest() {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        manager.addEpic(epic1);
        SubTask subtask1 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        SubTask subtask1Equals = subtask1;
        subtask1Equals.setId(2);
        manager.addSubTask(subtask1);
        manager.loadManagerFromKVServer();
        Assertions.assertEquals(subtask1Equals, manager.subTasks.get(subtask1.getId()));
    }


    @Test
    void updateTaskTest() {
        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        Task task1Upd = new Task(TaskType.TASK, "2004", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2006, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "22");
        Task taskForEuals = task1Upd;
        taskForEuals.setId(1);
        manager.addTask(task1);
        manager.updateTask(1, task1Upd);
        manager.loadManagerFromKVServer();
        Assertions.assertEquals(taskForEuals, manager.normalTasks.get(task1.getId()));
    }

    @Test
    void updateEpicTest() {
        Epic epic3 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        Epic epic3Upd = new Epic(TaskType.EPIC, "эпик включающий2010", "56", TaskStatus.NEW);
        Epic epicForEuals = epic3Upd;
        epicForEuals.setId(1);
        manager.addEpic(epic3);
        manager.updateEpic(1, epic3Upd);
        manager.loadManagerFromKVServer();
        Assertions.assertEquals(epicForEuals, manager.epicTasks.get(epic3.getId()));
    }

    @Test
    void updateSubTaskTest() {
        Epic epic3 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        manager.addEpic(epic3);

        SubTask subtask5 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.NEW,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        SubTask subtask5Upd = new SubTask(TaskType.SUBTASK, "2010", TaskStatus.DONE,
                LocalDateTime.of(2010, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        SubTask subtaskForEuals = subtask5Upd;
        subtaskForEuals.setId(2);
        manager.addSubTask(subtask5);
        manager.updateSubTask(2, subtask5Upd);
        manager.loadManagerFromKVServer();
        Assertions.assertEquals(subtaskForEuals, manager.subTasks.get(subtask5.getId()));
    }

    @Test
    void saveHistoryTest() throws IOException, URISyntaxException, InterruptedException {
        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        Task task2 = new Task(TaskType.TASK, "2002", TaskStatus.NEW,
                LocalDateTime.of(2002, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        manager.addTask(task1);
        manager.addTask(task2);
        List<Task> history = manager.getTaskHistory();
        manager.loadManagerFromKVServer();
        List<Task> historyForEquals = manager.getTaskHistory();
        Assertions.assertEquals(history, historyForEquals);
    }
}