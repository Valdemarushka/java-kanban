package tests;

import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import tasks.Epic;
import tasks.SubTask;
import tasks.TaskStatus;
import tasks.TaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    TaskManager taskManager;
    Epic epic;
    SubTask subTask1;
    SubTask subTask2;

    @BeforeEach
    public void beforeEach() throws IOException {
        taskManager = new InMemoryTaskManager();
    }

    @AfterEach
    void clear() {
        taskManager.deleteAllTask();
        taskManager.deleteAllSubTask();
        taskManager.deleteAllEpic();
    }

    @Test
    void epicStatusWithoutSubTasks() {
        epic = new Epic(TaskType.EPIC, "эпик 1", "описание эпика 1", TaskStatus.NEW);
        taskManager.addEpic(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void epicStatusWithSubTasksStatusNew() {
        epic = new Epic(TaskType.EPIC, "эпик 1", "описание эпика 1", TaskStatus.NEW);
        taskManager.addEpic(epic);
        subTask1 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.NEW, LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        taskManager.addSubTask(subTask1);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void epicStatusWithSubTasksStatusInProgress() {
        epic = new Epic(TaskType.EPIC, "эпик 1", "описание эпика 1", TaskStatus.NEW);
        epic.setId(1);
        subTask1 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.IN_PROGRESS, LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        subTask1.setId(3);
        epic.addNewSubtaskInEpic(subTask1);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void epicStatusWithSubTasksStatusDone() {
        epic = new Epic(TaskType.EPIC, "эпик 1", "описание эпика 1", TaskStatus.NEW);
        epic.setId(1);
        subTask1 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE, LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        subTask1.setId(3);
        epic.addNewSubtaskInEpic(subTask1);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void epicStatusWithSubTasksStatusNewOrDone() {
        epic = new Epic(TaskType.EPIC, "эпик 1", "описание эпика 1", TaskStatus.NEW);
        epic.setId(1);
        subTask1 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.NEW, LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        subTask1.setId(2);
        subTask2 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE, LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        subTask1.setId(3);
        epic.addNewSubtaskInEpic(subTask1);
        epic.addNewSubtaskInEpic(subTask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    //извиняюсь. я не представляю каково это читать такие бредовые коды)
}