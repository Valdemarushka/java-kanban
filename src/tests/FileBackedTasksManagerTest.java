package tests;

import managers.FileBackedTasksManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest {


    TaskManager createTaskManager() {
        return new FileBackedTasksManager(new File("src/data", "data.csv"));
    }

    @AfterEach
    void clear() {
        taskManager.deleteAllTask();
        taskManager.deleteAllSubTask();
        taskManager.deleteAllEpic();
    }


    @Test
    void SaveAndLoadFileWithEmptyHistory() {
        taskManager.addTask(task1);
        assertTrue(taskManager.getTaskHistory().isEmpty(), "история не пуста");
        TaskManager taskManager2 = new FileBackedTasksManager(new File("src/data", "data.csv"));
        assertTrue(taskManager2.getTaskHistory().isEmpty(), "история не пуста");
        assertFalse(taskManager2.viewAllTask().isEmpty(), "таск не восстановился");
    }


    @Test
    void SaveAndLoadFileWithEmptyEpic() {
        taskManager.addEpic(epic3);
        assertTrue(taskManager.getTaskHistory().isEmpty(), "история не пуста");
        TaskManager taskManager2 = new FileBackedTasksManager(new File("src/data", "data.csv"));
        assertTrue(taskManager2.getTaskHistory().isEmpty(), "история не пуста");
        assertFalse(taskManager2.viewAllEpic().isEmpty(), "таск не восстановился");
    }

    @Test
    void SaveAndLoadFileWithEmptyTasks() {
        assertTrue(taskManager.viewAllTask().isEmpty(), "таски не пусты");
        assertTrue(taskManager.viewAllEpic().isEmpty(), "таски не пусты");
        assertTrue(taskManager.viewAllSubtask().isEmpty(), "таски не пусты");
        TaskManager taskManager2 = new FileBackedTasksManager(new File("src/data", "data.csv"));
        assertTrue(taskManager2.getTaskHistory().isEmpty(), "история не пуста");
        assertTrue(taskManager2.viewAllEpic().isEmpty(), "список пуст");
    }

    @Test
    void addEpicTEST() {
        super.addEpicTEST();
    }

    @Test
    void addSubTaskTEST() {
        super.addSubTaskTEST();
    }

    @Test
    void deleteAllTaskTEST() {
        super.deleteAllTaskTEST();
    }

    @Test
    void deleteAllEpicTEST() {
        super.deleteAllEpicTEST();
    }

    @Test
    void deleteAllSubTaskTEST() {
        super.deleteAllSubTaskTEST();
    }

    @Test
    void deleteTaskByIdTEST() {
        super.deleteTaskByIdTEST();
    }

    @Test
    void deleteEpicByIdTEST() {
        super.deleteEpicByIdTEST();
    }

    @Test
    void deleteSubTaskByIdTEST() {
        super.deleteSubTaskByIdTEST();
    }

    @Test
    void getTaskByIdTEST() {
        super.getTaskByIdTEST();
    }

    @Test
    void getEpicByIdTEST() {
        super.getEpicByIdTEST();
    }

    @Test
    void getSubTaskByIdTEST() {
        super.getSubTaskByIdTEST();
    }

    @Test
    void updateTaskTEST() {
        super.updateTaskTEST();
    }

    @Test
    void updateEpicTEST() {
        super.updateEpicTEST();
    }

    @Test
    void updateSubTaskTEST() {
        super.updateSubTaskTEST();
    }

    @Test
    void viewAllTaskTEST() {
        super.viewAllTaskTEST();
    }

    @Test
    void viewSubTaskOfEpicTEST() {
        super.viewSubTaskOfEpicTEST();
    }

    @Test
    void getTaskHistoryTEST() {
        super.getTaskHistoryTEST();
    }

    @Test
    void getPrioritizedTasksTEST() {
        super.getPrioritizedTasksTEST();
    }
}

