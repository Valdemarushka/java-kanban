package network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import managers.FileBackedTasksManager;
import tasks.*;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HttpTaskManager extends FileBackedTasksManager {
    KVTaskClient client;
    private String API_TOKEN;
    Gson gson;

    public HttpTaskManager(URI KVUri) throws IOException, InterruptedException {
        client = new KVTaskClient(KVUri);
        API_TOKEN = client.getApiToken();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        loadManagerFromKVServer();
    }

    @Override
    public void save() {
        if (viewAllTask().isEmpty()) {
            System.out.println("HTTPM: Тасков нет");//эти пометки сделаны для себя, что бы понимать где что делается.
        } else {
            String taskToGson = gson.toJson(viewAllTask());
            client.put("task", taskToGson);
            System.out.println("HTTPM: обновление тасков завершено");
        }

        if (viewAllEpic().isEmpty()) {
            System.out.println("HTTPM: Эпиков нет");
        } else {
            String epicToGson = gson.toJson(viewAllEpic());
            client.put("epic", epicToGson);
            System.out.println("HTTPM: обновление эпиков завершено");
        }

        if (viewAllSubtask().isEmpty()) {
            System.out.println("HTTPM: Сабтасков нет");
        } else {
            String subtaskToGson = gson.toJson(viewAllSubtask());
            client.put("subtask", subtaskToGson);
            System.out.println("HTTPM: обновление сабтасков завершено");
        }
        if (getTaskHistory() == null || getTaskHistory().isEmpty()) {
            System.out.println("HTTPM: Истории нет");
        } else {
            String historyToGson = gson.toJson(getTaskHistory());
            client.put("history", historyToGson);
            System.out.println("HTTPM: обновление истории завершено");
        }
    }

    public void loadManagerFromKVServer() {
        loadTasks();
        loadHistory();
        loadSortedTask();
    }

    public void loadTasks() {
        HashMap<Integer, Task> normalTasksLoaded = gson.fromJson(client.load("task"),
                new TypeToken<HashMap<Integer, Task>>() {
                }.getType());
        if (normalTasksLoaded != null) {
            for (Task task : normalTasksLoaded.values()) {
                normalTasks.put(task.getId(), task);
                if (taskIndex <= task.getId()) {
                    taskIndex = task.getId() + 1;
                }
            }
        }

        HashMap<Integer, Epic> epicTasksLoaded = gson.fromJson(client.load("epic"),
                new TypeToken<HashMap<Integer, Epic>>() {
                }.getType());
        if (epicTasksLoaded != null) {
            for (Epic epic : epicTasksLoaded.values()) {
                epicTasks.put(epic.getId(), epic);
                if (taskIndex <= epic.getId()) {
                    taskIndex = epic.getId() + 1;
                }
            }
        }

        HashMap<Integer, SubTask> subTasksLoaded = gson.fromJson(client.load("subtask"),
                new TypeToken<HashMap<Integer, SubTask>>() {
                }.getType());
        if (subTasksLoaded != null) {
            for (SubTask subTask : subTasksLoaded.values()) {
                subTasks.put(subTask.getId(), subTask);
                if (taskIndex <= subTask.getId()) {
                    taskIndex = subTask.getId() + 1;
                }
            }
        }
    }

    void loadHistory() {
        ArrayList<Task> historyLoaded = gson.fromJson(client.load("history"), new TypeToken<List<Task>>() {
        }.getType());
        if (historyLoaded != null) {
            for (Task task : historyLoaded) {
                historyManager.add(task);
            }
        }
    }

    void loadSortedTask() {
        if (!normalTasks.isEmpty() && normalTasks != null) {
            sortedTasks.addAll(normalTasks.values());
        }
        if (!subTasks.isEmpty() && subTasks != null) {
            sortedTasks.addAll(subTasks.values());
        }
    }
}
