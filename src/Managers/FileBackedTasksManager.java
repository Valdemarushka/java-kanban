package Managers;

import Exeption.ManagerSaveException;
import Tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.util.StringJoiner;

import static java.nio.file.Files.createDirectory;


public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private final File autoSave;

    public FileBackedTasksManager(File autoSave) throws IOException {
        this.autoSave = autoSave;

        String value = readFileInString(autoSave);
        managerFromString(value);

    }


    /* ПРОВЕРКА ФАЙЛА И ДЕРИКТОРИИ*/
    static void checkOrCreateDirAndFile(File autoSave) throws IOException {
        String home = autoSave.getParent();
        String FileName = autoSave.getName();

        if (!Files.exists(Paths.get(home))) {
            System.out.println("Дериктории нет,пробуем создать новую");
            Path directory = createDirectory(Paths.get(home));
            if (!Files.exists(Paths.get(home))) {
                throw new ManagerSaveException("Невозможно создать директорию для сохранения");
            }
            System.out.println("Дериктория создана");
        } else {
            System.out.println("Дериктория существует");
        }

        if (!Files.exists(Paths.get(home, FileName))) {
            System.out.println("Файла сохранения нет. Создаем новый");
            Path testFile = Files.createFile(Paths.get(home, "Data.csv"));
            if (!Files.exists(Paths.get(home, FileName))) {
                throw new ManagerSaveException("Невозможно создать файл для сохранения");
            }
            System.out.println("Файл создан");
        } else {
            System.out.println("Файл существует");
        }
    }

    /* ЧТЕНИЕ ФАЙЛА В СТРОКУ*/
    public String readFileInString(File autoSave) throws IOException {
        checkOrCreateDirAndFile(autoSave);
        StringJoiner value = new StringJoiner("\n");
        Reader fileReader = new FileReader(autoSave);
        BufferedReader br = new BufferedReader(fileReader);
        while (br.ready()) {
            String line = br.readLine();
            value.add(line);
        }
        return value.toString();
    }


    /* ТАСКИ И ИСТОРИЯ В СТРОКУ И В ФАЙЛ*/
    public void save() throws IOException {// он будет сохранять текущее состояние менеджера в указанный файл
        StringJoiner managerData = new StringJoiner("\n");
        managerData.add("id,type,name,status,description,epic");
        HashMap<Integer, Task> allTask = new HashMap<>();
        allTask.putAll(normalTasks);
        allTask.putAll(epicTasks);
        allTask.putAll(subTasks);
        for (Task task : allTask.values()) {
            managerData.add(task.toString());
        }
        if (!historyManager.getHistory().isEmpty()) {
            managerData.add("HISTORYSPLIT" + historyToString(historyManager));
        }
        try (FileWriter fileWriter = new FileWriter(autoSave)) {
            fileWriter.write(managerData.toString());
            fileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файла сохранения");
        }


    }

    /* ИСТОРИЯ В СТРОКУ*/
    static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringJoiner historyInString = new StringJoiner(",");
        for (Task task : history) {

            historyInString.add(Integer.toString(task.getId()));

        }

        return historyInString.toString();
    }


    /*УСТАНОВКА ТИПА ТАСКА*/
    TaskStatus setTaskType(String value) {
        switch (value) {
            case ("NEW"):
                return TaskStatus.NEW;
            case ("IN_PROGRESS"):
                return TaskStatus.IN_PROGRESS;
            case ("DONE"):
                return TaskStatus.DONE;
            default:
                return null;
        }
    }


    /*ВОССТАНОВЛЕНИЕ МЕНЕДЖЕРА ИЗ СТРОКИ*/
    void managerFromString(String value) {
        String splitter = "HISTORYSPLIT";
        String tasksString = "";
        String historyString = "";
        if (value.contains(splitter)) {
            String[] taskAndHistory = value.split(splitter);
            tasksString = taskAndHistory[0];
            historyString = taskAndHistory[1];
        } else {
            tasksString = value;
        }
        if (!tasksString.isBlank()) {
            tasksFromString(tasksString);
        }
        if (!historyString.isBlank()) {
            historyFromString(historyString);
        }

    }

    /*ТАСКИ ИЗ СТРОКИ+ЗАПОЛНЕНИЕ HASHMAP*/
    void tasksFromString(String taskString) {
        String[] splitTasksInString = taskString.split("\n");
        for (int i = 1; i < splitTasksInString.length; i++) {

            String[] splitValue = splitTasksInString[i].split(",");
            Integer id = Integer.parseInt(splitValue[0]);
            TaskType type;
            String name = splitValue[2];
            TaskStatus status = setTaskType(splitValue[3]);
            String description = splitValue[4];

            if (splitValue[1].equals("TASK")) {
                type = TaskType.TASK;
                Task task = new Task(type, name, status, description);
                task.setId(id);
                normalTasks.put(task.getId(), task);
                if (taskIndex <= task.getId()) {
                    taskIndex = task.getId() + 1;
                }
            } else if (splitValue[1].equals("EPIC")) {
                type = TaskType.EPIC;
                Epic epic = new Epic(type, name, status, description);
                epic.setId(id);
                epicTasks.put(epic.getId(), epic);
                if (taskIndex < epic.getId()) {
                    taskIndex = epic.getId() + 1;
                }
            } else {
                type = TaskType.SUB;
                int epicId = Integer.parseInt(splitValue[5]);
                SubTask subTask = new SubTask(type, name, status, description, epicId);
                subTask.setId(id);
                subTasks.put(subTask.getId(), subTask);
                if (taskIndex < subTask.getId()) {
                    taskIndex = subTask.getId() + 1;
                }
            }
        }
    }

    /*ИСТОРИЯ ИЗ СТРОКИ+ЗАПОЛНЕНИЕ HASHMAP*/
    void historyFromString(String historyInString) {
        String[] history = historyInString.split(",");
        for (String indexTask : history) {
            Integer index = Integer.parseInt(indexTask);
            if (normalTasks.containsKey(index)) {
                historyManager.add(normalTasks.get(index));
            } else if (epicTasks.containsKey(index)) {
                historyManager.add(epicTasks.get(index));
            } else if (subTasks.containsKey(index)) {
                historyManager.add(subTasks.get(index));
            }
        }

    }


    public Task taskFromString(String value)/* ТАСК ИЗ СТРОКИ*/ {
        String[] splitValue = value.split(",");
        Integer id = Integer.parseInt(splitValue[0]);
        TaskType type;
        String name = splitValue[2];
        String description = splitValue[4];
        TaskStatus status;
        switch (splitValue[3]) {
            case ("NEW"):
                status = TaskStatus.NEW;
            case ("IN_PROGRESS"):
                status = TaskStatus.IN_PROGRESS;
            case ("DONE"):
                status = TaskStatus.DONE;
            default:
                status = TaskStatus.NEW;
        }
        if (splitValue[1].equals("TASK")) {
            type = TaskType.TASK;
            Task task = new Task(type, name, status, description);
            task.setId(id);
            return task;
        } else if (splitValue[1].equals("EPIC")) {
            type = TaskType.EPIC;
            Epic epic = new Epic(type, name, status, description);
            epic.setId(id);
            return epic;
        } else {
            type = TaskType.SUB;
            int epicId = Integer.parseInt(splitValue[5]);
            SubTask subTask = new SubTask(type, name, status, description, epicId);
            subTask.setId(id);
            return subTask;
        }
    }


    @Override
    public void addTask(Task task) throws IOException {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) throws IOException {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask, Integer epicId) throws IOException {
        super.addSubTask(subTask, epicId);
        save();
    }

    @Override
    public void deleteAllTask() throws IOException {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllEpic() throws IOException {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubTask() throws IOException {
        super.deleteAllSubTask();
        save();
    }

    @Override
    public void deleteTaskById(Integer id) throws IOException {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(Integer id) throws IOException {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(Integer id) throws IOException {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public Task getTaskById(Integer id) throws IOException {
        historyManager.add(normalTasks.get(id));
        save();
        return normalTasks.get(id);

    }

    @Override
    public Epic getEpicById(Integer id) throws IOException {
        historyManager.add(epicTasks.get(id));
        save();
        return epicTasks.get(id);

    }

    @Override
    public SubTask getSubTaskById(Integer id) throws IOException {
        historyManager.add(subTasks.get(id));
        save();
        return subTasks.get(id);
    }

    @Override
    public void updateTask(Integer taskID, Task newTaskObject) throws IOException {
        super.updateTask(taskID, newTaskObject);
        save();
    }

    @Override
    public void updateEpic(Integer taskID, Epic newTaskObject) throws IOException {
        super.updateEpic(taskID, newTaskObject);
        save();
    }

    @Override
    public void updateSubTask(Integer taskID, SubTask newTaskObject) throws IOException {
        super.updateSubTask(taskID, newTaskObject);
        save();
    }

    @Override
    public void changeEpicStatus(Integer epicID) throws IOException {
        super.changeEpicStatus(epicID);
        save();
    }

    @Override
    public void viewAllTask(HashMap<Integer, Task> map) {
        super.viewAllTask(map);
    }

    @Override
    public HashMap<Integer, SubTask> viewSubTaskOfEpic(Integer epicID) {
        return super.viewSubTaskOfEpic(epicID);
    }

    @Override
    public List<Task> getTaskHistory() {
        return super.getTaskHistory();
    }
}
