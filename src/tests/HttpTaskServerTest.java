package tests;

import adapters.DurationAdapter;

import adapters.LocalDateTimeAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import network.*;

import org.junit.jupiter.api.Assertions;
import tasks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

class HttpTaskServerTest {
    HttpTaskManager manager;
    KVServer kvServer;
    HttpTaskServer httpTaskServer;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .serializeNulls()
            .create();
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
    public void beforeEach() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        URI uriKVServer = KVServer.getServerURL();
        manager = new HttpTaskManager(uriKVServer);
        httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
    }

    @AfterEach
    public void stopKVServer() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    //Проверка получения
    @Test
    void getAllTasksTest() throws IOException, InterruptedException {
        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        Task task2 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2002, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        manager.addTask(task1);
        manager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HashMap<Integer, Task> mapFromServer = gson.fromJson(response.body(), new TypeToken<HashMap<Integer, Task>>() {
        }.getType());
        Assertions.assertEquals(manager.viewAllTask().toString(), mapFromServer.toString());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void getTasksByIdTest() throws IOException, InterruptedException {
        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        manager.addTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskFromServer = gson.fromJson(response.body(), Task.class);
        Assertions.assertEquals(task1.toString(), taskFromServer.toString());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void getAllEpicTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        Epic epic2 = new Epic(TaskType.EPIC, "эпик включающий2010", "56", TaskStatus.NEW);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        SubTask subtask3 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        SubTask subtask4 = new SubTask(TaskType.SUBTASK, "2010", TaskStatus.DONE,
                LocalDateTime.of(2010, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 2);
        manager.addSubTask(subtask3);
        manager.addSubTask(subtask4);

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HashMap<Integer, Epic> mapFromServer = gson.fromJson(response.body(), new TypeToken<HashMap<Integer, Epic>>() {
        }.getType());
        Assertions.assertEquals(manager.viewAllEpic().toString(), mapFromServer.toString());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void getTasksEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        manager.addEpic(epic1);

        SubTask subtask3 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        manager.addSubTask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epicFromServer = gson.fromJson(response.body(), Epic.class);
        Assertions.assertEquals(epic1.toString(), epicFromServer.toString());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void getAllSubTaskTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        Epic epic2 = new Epic(TaskType.EPIC, "эпик включающий2010", "56", TaskStatus.NEW);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        SubTask subtask3 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        SubTask subtask4 = new SubTask(TaskType.SUBTASK, "2010", TaskStatus.DONE,
                LocalDateTime.of(2010, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 2);
        manager.addSubTask(subtask3);
        manager.addSubTask(subtask4);

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HashMap<Integer, SubTask> mapFromServer = gson.fromJson(response.body(), new TypeToken<HashMap<Integer, SubTask>>() {
        }.getType());
        Assertions.assertEquals(manager.viewAllSubtask().toString(), mapFromServer.toString());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void getSubTaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        manager.addEpic(epic1);

        SubTask subtask3 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        manager.addSubTask(subtask3);
        System.out.println(manager.viewAllSubtask());

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask taskFromServer = gson.fromJson(response.body(), SubTask.class);
        System.out.println(taskFromServer);
        Assertions.assertEquals(subtask3.toString(), taskFromServer.toString());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void getSubTaskOfEpicTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        manager.addEpic(epic1);

        SubTask subtask3 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        manager.addSubTask(subtask3);
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/subtask/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HashMap<Integer, SubTask> listFromServer = gson.fromJson(response.body(), new TypeToken<HashMap<Integer, SubTask>>() {
        }.getType());
        Assertions.assertEquals(listFromServer.toString(), epic1.getInnerSubTask().toString());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void getPrioritizedTasksTest() throws IOException, InterruptedException {
        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        Task task2 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2002, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        manager.addTask(task1);
        manager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> listFromServer = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        TreeSet<Task> treeFromServer = new TreeSet<>(startTimeСomparator);
        treeFromServer.addAll(listFromServer);


        Assertions.assertEquals(manager.getPrioritizedTasks().toString(), treeFromServer.toString());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void getTaskHistoryTest() throws IOException, InterruptedException {
        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        Task task2 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2002, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.getTaskById(2);
        manager.getTaskById(1);
        System.out.println(manager.getTaskHistory());
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ArrayList<Task> arrayListFromServer = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());

        Assertions.assertEquals(manager.getTaskHistory().toString(), arrayListFromServer.toString());
        Assertions.assertEquals(200, response.statusCode());
    }


    //Проверка добавления
    @Test
    void addTaskTest() throws IOException, InterruptedException {
        Task task1 = new Task(TaskType.TASK, "2001добавленный", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/task/");

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(task1));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .POST(body)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(task1.getName(), manager.getTaskById(1).getName());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void updateTaskTest() throws IOException, InterruptedException {
        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        manager.addTask(task1);

        Task task2 = new Task(TaskType.TASK, "2001обновленный", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");

        System.out.println(gson.toJson(task2));
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(task2));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .POST(body)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String name1 = manager.getTaskById(1).getName();
        String name2 = task2.getName();

        Assertions.assertEquals(name1, name2);
        Assertions.assertEquals(200, response.statusCode());

    }

    @Test
    void addEpicTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        epic1.setStartTime(LocalDateTime.of(2001, 1, 1, 1, 1, 1));
        epic1.setEndTime(LocalDateTime.of(2001, 1, 1, 1, 20, 1));
        epic1.setDuration(Duration.ofMinutes(20));
        // В общем тут я не смог разобраться почему адаптер даты не пропускает null-евые значения. пришлось заполнять
        // вручную. Можете подсказать почему при попытке добавить эпик с null-евыми датами адаптер ругается
        // на то что передается нулл, а не стринг? 3 дня бился, несколко раз переписывал все откатываясь на
        // начальное состояние проекта. Наставник молчит, одногруппники не могут помочь.

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(epic1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .POST(body)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(manager.getEpicById(1).getName(), epic1.getName());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void updateEpicTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        manager.addEpic(epic1);
        SubTask subtask3 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        manager.addSubTask(subtask3);

        Epic epic2 = epic1;
        epic2.setName("обновленный");
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(epic2));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .POST(body)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(epic2.getName(), manager.getEpicById(epic1.getId()).getName());
    }

    @Test
    void addSubTaskTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        manager.addEpic(epic1);

        SubTask subtask3 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(subtask3));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .POST(body)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(manager.getSubTaskById(2).getName(), subtask3.getName());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void updateSubTaskTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        manager.addEpic(epic1);
        SubTask subtask3 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        manager.addSubTask(subtask3);

        SubTask subTaskUpd = subtask3;
        subTaskUpd.setName("обновленный");
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskUpd));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .POST(body)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(subTaskUpd.getName(), manager.getSubTaskById(subtask3.getId()).getName());
    }

    //Проверка удаления
    @Test
    void deleteAllTaskTest() throws IOException, InterruptedException {
        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        manager.addTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .DELETE()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertTrue(manager.viewAllTask().isEmpty());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void deleteTaskByIdTest() throws IOException, InterruptedException {
        Task task1 = new Task(TaskType.TASK, "2001", TaskStatus.NEW,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "11");
        manager.addTask(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .DELETE()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertNull(manager.getTaskById(task1.getId()));
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void deleteAllEpicTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        manager.addEpic(epic1);

        SubTask subtask3 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        manager.addSubTask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .DELETE()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertTrue(manager.viewAllEpic().isEmpty());
        Assertions.assertTrue(manager.viewAllSubtask().isEmpty());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void deleteEpicByIdTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        manager.addEpic(epic1);

        SubTask subtask3 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        manager.addSubTask(subtask3);
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .DELETE()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertTrue(manager.viewAllEpic().isEmpty());
        Assertions.assertTrue(manager.viewAllSubtask().isEmpty());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void deleteAllSubTaskTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        manager.addEpic(epic1);

        SubTask subtask3 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        manager.addSubTask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .DELETE()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertTrue(manager.viewAllSubtask().isEmpty());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void deleteSubTaskByIdTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic(TaskType.EPIC, "эпик включающий2009", "55", TaskStatus.NEW);
        manager.addEpic(epic1);

        SubTask subtask3 = new SubTask(TaskType.SUBTASK, "2009", TaskStatus.DONE,
                LocalDateTime.of(2009, 1, 1, 1, 1, 1), Duration.ofMinutes(20), "66", 1);
        manager.addSubTask(subtask3);
        HttpClient client = HttpClient.newHttpClient();
        URI uriRequest = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRequest)
                .DELETE()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(manager.viewAllSubtask());
        Assertions.assertTrue(manager.viewAllSubtask().isEmpty());
        Assertions.assertEquals(200, response.statusCode());
    }
}