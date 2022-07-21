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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

class HttpTaskManagerTest {
    HttpTaskManager manager;
    KVServer server;
    URI uriKVServer;
    Comparator<Task> startTimeСomparator = (t1, t2) -> {
        if (t1.getStartTime() == null && t2.getStartTime() == null) {
            return t1.getId() - t2.getId(); //Тут я все таки добавил при отсутствии времени, сортировку по индексу.
        } else if (t1.getStartTime() == null) {
            return -1;
        } else if (t2.getStartTime() == null) {
            return 1;
        } else {
            return t1.getStartTime().compareTo(t2.getStartTime());
        }
    };

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
    void getPrioritizedTasksTest() {
        TreeSet<Task> sortedTasksForEquals = new TreeSet<>(startTimeСomparator);

        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        Task task2 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2000, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        Task task3 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2003, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");

        sortedTasksForEquals.add(task1);
        sortedTasksForEquals.add(task2);
        sortedTasksForEquals.add(task3);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        Assertions.assertEquals(sortedTasksForEquals, manager.getPrioritizedTasks());


    }

    //проверка добавления задач
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
        Assertions.assertEquals(subtask1Equals,
                manager.epicTasks.get(epic1.getId()).getInnerSubTask().get(subtask1.getId()));

    }

    //проверка удаления задач
    @Test
    void deleteTaskTestById() {
        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        manager.addTask(task1);
        manager.loadManagerFromKVServer();
        manager.deleteTaskById(1);
        Assertions.assertNull(manager.getTaskById(1), "таск не удален");
    }

    @Test
    void deleteEpicTestById() {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        manager.addEpic(epic1);
        manager.loadManagerFromKVServer();
        manager.deleteEpicById(1);
        Assertions.assertNull(manager.getEpicById(1), "эпик не удален");
    }

    @Test
    void deleteSubTaskTestById() {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        SubTask subtask1 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 2);
        manager.addEpic(epic1);
        manager.addSubTask(subtask1);
        manager.loadManagerFromKVServer();
        manager.deleteTaskById(subtask1.getId());
        Assertions.assertNull(manager.getSubTaskById(subtask1.getId()), "таск не удален");
        Assertions.assertNull(manager.getEpicById(epic1.getId()).getInnerSubTask().get(subtask1.getId())
                , "внутренний сабтасктаск не удален");
    }

    @Test
    void deleteAllTaskTest() {
        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        Task task2 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.loadManagerFromKVServer();
        manager.deleteAllTask();
        Assertions.assertTrue(manager.normalTasks.isEmpty(), "таски не удалены");
    }

    @Test
    void deleteAllEpicTest() {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        Epic epic2 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.loadManagerFromKVServer();
        manager.deleteAllEpic();
        Assertions.assertTrue(manager.epicTasks.isEmpty(), "эпики не удалены");
    }

    @Test
    void deleteAllSubTaskTest() {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        SubTask subtask1 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 2);
        Epic epic2 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        SubTask subtask2 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 2);
        manager.addEpic(epic1);
        manager.addSubTask(subtask1);
        manager.addEpic(epic2);
        manager.addSubTask(subtask2);
        manager.loadManagerFromKVServer();
        manager.deleteAllSubTask();
        Assertions.assertTrue(manager.subTasks.isEmpty(), "таск не удален");

    }

    //проверка получения задач

    @Test
    void getTaskTest() {
        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        Task task1Equals = task1;
        task1Equals.setId(1);
        manager.addTask(task1);
        manager.loadManagerFromKVServer();
        Assertions.assertEquals(task1Equals, manager.normalTasks.get(task1.getId()));
    }

    @Test
    void getEpicTest() {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        Epic epic1Equals = epic1;
        epic1Equals.setId(1);
        manager.addEpic(epic1);
        manager.loadManagerFromKVServer();
        Assertions.assertEquals(epic1Equals, manager.epicTasks.get(epic1.getId()));
    }

    @Test
    void getSubTaskTest() {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        manager.addEpic(epic1);
        SubTask subtask1 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        SubTask subtask1Equals = subtask1;
        subtask1Equals.setId(2);
        manager.addSubTask(subtask1);
        manager.loadManagerFromKVServer();
        Assertions.assertEquals(subtask1Equals, manager.subTasks.get(subtask1.getId()));
        Assertions.assertEquals(subtask1Equals,
                manager.epicTasks.get(epic1.getId()).getInnerSubTask().get(subtask1.getId()));

    }

    @Test
    void getAllTaskTest() {
        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        Task task2 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2002, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        HashMap<Integer, Task> mapForEquals = new HashMap<>();

        Task task1Equals = task1;
        task1Equals.setId(1);
        mapForEquals.put(task1Equals.getId(), task1Equals);

        Task task2Equals = task2;
        task2Equals.setId(2);
        mapForEquals.put(task2Equals.getId(), task2Equals);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.loadManagerFromKVServer();

        Assertions.assertEquals(mapForEquals, manager.viewAllTask());
    }

    @Test
    void getAllEpicTest() {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        Epic epic2 = new Epic(TaskType.EPIC, "эпик включающий2001", "55", TaskStatus.NEW);
        HashMap<Integer, Epic> mapForEquals = new HashMap<>();

        Epic epic1Equals = epic1;
        epic1Equals.setId(1);
        mapForEquals.put(epic1Equals.getId(), epic1Equals);

        Epic epic2Equals = epic2;
        epic2Equals.setId(2);
        mapForEquals.put(epic2Equals.getId(), epic2Equals);


        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.loadManagerFromKVServer();
        Assertions.assertEquals(mapForEquals, manager.viewAllEpic());
    }

    @Test
    void getAllSubTaskTest() {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        manager.addEpic(epic1);

        SubTask subtask1 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        SubTask subtask2 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2010, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        HashMap<Integer, SubTask> mapForEquals = new HashMap<>();

        SubTask subtask1Equals = subtask1;
        subtask1Equals.setId(2);
        mapForEquals.put(subtask1Equals.getId(), subtask1Equals);

        SubTask subtask2Equals = subtask2;
        subtask2Equals.setId(3);
        mapForEquals.put(subtask2Equals.getId(), subtask2Equals);

        manager.addSubTask(subtask1);
        manager.addSubTask(subtask2);
        manager.loadManagerFromKVServer();
        Assertions.assertEquals(mapForEquals, manager.viewAllSubtask());
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