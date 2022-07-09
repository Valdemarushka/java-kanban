package tests;

import managers.TaskManager;
import tasks.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    public T taskManager;

    abstract T createTaskManager();

    @BeforeEach
    private void updateTaskManager() {
        taskManager = createTaskManager();
    }

    Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW, LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
    Task task2 = new Task(TaskType.TASK, "2004", TaskStatus.NEW, LocalDateTime.of(2006, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "22");
    Task task3 = new Task(TaskType.TASK, "2005", TaskStatus.NEW, LocalDateTime.of(2003, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "22");
    Task task4 = new Task(TaskType.TASK, "2006", TaskStatus.NEW, LocalDateTime.of(2004, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "22");
    Epic epic3 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
    Epic epic4 = new Epic(TaskType.EPIC, "эпик включающий2010", "55", TaskStatus.NEW);
    SubTask subtask4 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE, LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
    SubTask subtask5 = new SubTask(TaskType.SUBTASK, "2010", TaskStatus.DONE, LocalDateTime.of(2010, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);

    @AfterEach
    void clear() {
        taskManager.deleteAllTask();
        taskManager.deleteAllSubTask();
        taskManager.deleteAllEpic();

    }

    @Test
    void addTaskTEST() {
        taskManager.addTask(task1);
        assertNotNull(taskManager.getTaskById(task1.getId()), "задача null");
        Assertions.assertFalse(taskManager.viewAllTask().isEmpty());


    }

    @Test
    void addEpicTEST() {
        taskManager.addEpic(epic3);
        assertNotNull(taskManager.getEpicById(epic3.getId()), "задача null");
    }

    @Test
    void addSubTaskTEST() {
        taskManager.addEpic(epic3);
        assertNotNull(taskManager.getEpicById(epic3.getId()), "задача null");
        taskManager.addSubTask(subtask4);
        assertNotNull(taskManager.getSubTaskById(subtask4.getId()), "задача null");
    }

    @Test
    void deleteAllTaskTEST() {
        taskManager.addTask(task1);
        taskManager.deleteAllTask();
        Assertions.assertTrue(taskManager.viewAllTask().isEmpty());
    }

    @Test
    void deleteAllEpicTEST() {
        taskManager.addEpic(epic3);
        taskManager.deleteAllEpic();
        Assertions.assertTrue(taskManager.viewAllEpic().isEmpty());
    }

    @Test
    void deleteAllSubTaskTEST() {
        taskManager.addEpic(epic3);
        taskManager.addSubTask(subtask4);
        taskManager.deleteAllSubTask();
        Assertions.assertTrue(taskManager.viewAllSubtask().isEmpty());
    }

    @Test
    void deleteTaskByIdTEST() {
        taskManager.addTask(task1);
        Assertions.assertFalse(taskManager.viewAllTask().isEmpty());

        taskManager.deleteTaskById(1);
        Assertions.assertTrue(taskManager.viewAllTask().isEmpty());
    }

    @Test
    void deleteEpicByIdTEST() {
        taskManager.addEpic(epic3);
        Assertions.assertFalse(taskManager.viewAllEpic().isEmpty());
        taskManager.deleteEpicById(1);
        Assertions.assertTrue(taskManager.viewAllEpic().isEmpty());
    }

    @Test
    void deleteSubTaskByIdTEST() {
        taskManager.addEpic(epic3);
        taskManager.addSubTask(subtask4);
        Assertions.assertFalse(taskManager.viewAllSubtask().isEmpty(), "пусто");
        taskManager.deleteSubTaskById(subtask4.getId());
        Assertions.assertNotNull(taskManager.getSubTaskById(subtask4.getId()), "не пуста");
    }

    @Test
    void getTaskByIdTEST() {
        taskManager.addTask(task1);
        assertEquals(task1, taskManager.getTaskById(1));
        Assertions.assertNull(taskManager.getTaskById(500), "неверный идентификатор ломает");
    }

    @Test
    void getEpicByIdTEST() {
        taskManager.addEpic(epic3);
        assertEquals(epic3, taskManager.getEpicById(1));
        Assertions.assertNull(taskManager.getEpicById(500), "неверный идентификатор ломает");
    }

    @Test
    void getSubTaskByIdTEST() {
        taskManager.addEpic(epic3);
        taskManager.addTask(task1);
        assertEquals(task1, taskManager.getTaskById(2));
        Assertions.assertNull(taskManager.getSubTaskById(500), "неверный идентификатор ломает");
    }

    @Test
    void updateTaskTEST() {
        taskManager.addTask(task1);
        taskManager.updateTask(1, task2);
        assertEquals(task2, taskManager.getTaskById(1));
        taskManager.updateTask(500, task2);
        assertFalse(taskManager.viewAllTask().containsKey(500), "добавилась левая задача");
    }

    @Test
    void updateEpicTEST() {
        taskManager.addEpic(epic3);
        taskManager.updateEpic(1, epic4);
        taskManager.updateEpic(500, epic4);
        assertEquals(epic4, taskManager.getEpicById(1));
        assertFalse(taskManager.viewAllEpic().containsKey(500), "добавилась левая задача");
    }

    @Test
    void updateSubTaskTEST() {
        taskManager.addEpic(epic3);
        taskManager.addSubTask(subtask4);
        taskManager.updateSubTask(2, subtask5);
        taskManager.updateSubTask(500, subtask5);
        assertEquals(subtask5, taskManager.getSubTaskById(2));
        assertFalse(taskManager.viewAllSubtask().containsKey(500), "добавилась левая задача");

    }

    @Test
    void viewAllTaskTEST() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        int i = 2;
        assertEquals(taskManager.viewAllTask().size(), i);
    }

    @Test
    void viewSubTaskOfEpicTEST() {
        taskManager.addEpic(epic3);
        taskManager.addSubTask(subtask4);
        taskManager.addSubTask(subtask5);
        int i = 2;
        assertEquals(taskManager.viewSubTaskOfEpic(1).size(), i);
        assertNull(taskManager.viewSubTaskOfEpic(500));
    }

    @Test
    void getTaskHistoryTEST() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        int i = 2;


        assertEquals(taskManager.getTaskHistory().size(), i);
    }

    @Test
    void getPrioritizedTasksTEST() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addTask(task4);
        int i = 4;
        assertEquals(taskManager.getPrioritizedTasks().size(), i);
        assertEquals(taskManager.getPrioritizedTasks().first(), task1);
        assertEquals(taskManager.getPrioritizedTasks().last(), task2);


    }
}