package tests;

import Managers.HistoryManager;
import Managers.TaskManager;
import Tasks.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import static Managers.Managers.getDefault;
import static Managers.Managers.getDefaultHistory;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager manager = getDefault();
    HistoryManager historyManager= getDefaultHistory();
    Task task1 = new Task(TaskType.TASK,"2001", TaskStatus.NEW, LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20),"11");
    Task task2 = new Task(TaskType.TASK, "2004", TaskStatus.NEW, LocalDateTime.of(2006, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "22");
    Task task3 = new Task(TaskType.TASK, "2005", TaskStatus.NEW, LocalDateTime.of(2003, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "22");
    Task task4 = new Task(TaskType.TASK, "2006", TaskStatus.NEW, LocalDateTime.of(2004, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "22");
    Epic epic3 = new Epic(TaskType.EPIC, "эпик включающий2009", "55",TaskStatus.NEW);
    Epic epic4 = new Epic(TaskType.EPIC, "эпик включающий2010", "55",TaskStatus.NEW);
    SubTask subtask4 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE, LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
    SubTask subtask5 = new SubTask(TaskType.SUBTASK, "2010", TaskStatus.DONE, LocalDateTime.of(2010, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);

    @AfterEach
    void clear(){
        manager.deleteAllTask();
        manager.deleteAllSubTask();
        manager.deleteAllEpic();
    }
    @Test
    void remove() {
    }

    @Test
    void doublingTask() {
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addTask(task4);
        historyManager.add(manager.getTaskById(1));
        historyManager.add(manager.getTaskById(2));
        historyManager.add(manager.getTaskById(1));
        historyManager.add(manager.getTaskById(2));
        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    void deletefromBetween() {
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addTask(task4);
        historyManager.add(manager.getTaskById(1));
        historyManager.add(manager.getTaskById(2));
        historyManager.add(manager.getTaskById(3));
        historyManager.add(manager.getTaskById(4));
        historyManager.remove(3);
        assertEquals(task4, historyManager.getHistory().get(2));
    }

    @Test
    void deletefromEnd() {
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addTask(task4);
        historyManager.add(manager.getTaskById(1));
        historyManager.add(manager.getTaskById(2));
        historyManager.add(manager.getTaskById(3));
        historyManager.add(manager.getTaskById(4));
        historyManager.remove(4);
        assertEquals(task3, historyManager.getHistory().get(2));
    }

    @Test
    void deletefromStart() {
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addTask(task4);
        historyManager.add(manager.getTaskById(1));
        historyManager.add(manager.getTaskById(2));
        historyManager.add(manager.getTaskById(3));
        historyManager.add(manager.getTaskById(4));


        historyManager.remove(1);
        assertEquals(task2, historyManager.getHistory().get(0));
    }

    @Test
    void addInHistory() {
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addTask(task4);
        historyManager.add(manager.getTaskById(1));
        historyManager.add(manager.getTaskById(2));
        historyManager.add(manager.getTaskById(3));
        historyManager.add(manager.getTaskById(4));
        assertEquals(4, historyManager.getHistory().size());
    }

    @Test
    void getEmptyHistory() {
        assertEquals(0, historyManager.getHistory().size());
    }
}