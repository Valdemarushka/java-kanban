package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import network.HttpTaskManager;
import network.KVServer;
import network.LocalDateTimeAdapter;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

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
