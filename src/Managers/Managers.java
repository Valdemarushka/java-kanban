package Managers;

import java.io.File;
import java.io.IOException;

public class Managers {

//    public static TaskManager getDefault() {
//        return new InMemoryTaskManager();
//    }

    public static FileBackedTasksManager getDefault() throws IOException {
        return new FileBackedTasksManager(new File("C:\\Users\\Paradize\\dev\\java-kanban\\src\\data", "data.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }


}

