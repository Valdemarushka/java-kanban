package managers;

import network.HttpTaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Managers {


    public static TaskManager getDefault(URI uri) throws IOException, URISyntaxException, InterruptedException {
        return new HttpTaskManager(uri);
    }

    public static TaskManager getFileBackedTasksManager() {
        return new FileBackedTasksManager();
    }

    public static TaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }


}
