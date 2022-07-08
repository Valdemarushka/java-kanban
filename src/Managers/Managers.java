package Managers;

import java.io.File;
import java.io.IOException;

public class Managers {

    public static FileBackedTasksManager getDefault()  {
        return new FileBackedTasksManager(new File("src/data", "data.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
