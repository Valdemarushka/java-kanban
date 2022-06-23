package Managers;

import Exeption.ManagerSaveException;
import Tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.util.StringJoiner;

import static java.nio.file.Files.createDirectory;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private final File fileForSave;

    public FileBackedTasksManager(File fileForSave) throws IOException {
        this.fileForSave = fileForSave;
        /* "название переменной похоже больше на флаг, а не файл :)"
    не совсем понял что имеется ввиду. Исправил интуитивно)*/

        String value = readFileInString(fileForSave);
        managerFromString(value);
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task(TaskType.TASK, "Первая", TaskStatus.NEW, "1");
        Epic epic2 = new Epic(TaskType.EPIC, "Вторая", TaskStatus.NEW, "2");
        SubTask task3 = new SubTask(TaskType.SUBTASK, "Третья", TaskStatus.NEW, "3", 2);
        Task task4 = new Task(TaskType.TASK, "Четвертая", TaskStatus.NEW, "4");
        Task task5 = new Task(TaskType.TASK, "пять", TaskStatus.NEW, "5");

        System.out.println(manager.getTaskHistory());

        System.out.println("____________________________проверка добавления таска");
        manager.addTask(task1);
        System.out.println(manager.getTaskById(1));

        System.out.println("____________________________проверка добавления эпика");
        manager.addEpic(epic2);
        System.out.println(manager.getEpicById(2));

        System.out.println("____________________________проверка добавления сабтаска");
        manager.addSubTask(task3, 2);
        System.out.println(manager.getSubTaskById(3));

        System.out.println("____________________________проверка заполненной истории");
        System.out.println(manager.getTaskHistory());

        System.out.println("____________________________проверка изменения истории");
        System.out.println(manager.getTaskById(1));
        System.out.println(manager.getTaskHistory());

        System.out.println("____________________________проверка восстановления информации из файла");

        TaskManager newMan = new FileBackedTasksManager(new File("src/data", "data.csv"));
        System.out.println(newMan.getTaskHistory());
        System.out.println(newMan.getTaskById(1));
        System.out.println(newMan.getEpicById(2));
        System.out.println(newMan.getSubTaskById(3));
    }

    /* ПРОВЕРКА ФАЙЛА И ДЕРИКТОРИИ*/
    static void checkOrCreateDirAndFile(File fileForSave) throws IOException {
        String home = fileForSave.getParent();
        String FileName = fileForSave.getName();

        if (Files.exists(Paths.get(home))) {
            System.out.println("Дериктория существует");
        } else {
            System.out.println("Дериктории нет,пробуем создать новую");
            try {
                createDirectory(Paths.get(home));
            } catch (IOException e) {
                throw new ManagerSaveException("Невозможно создать директорию для сохранения" + e.getMessage());
            }
            System.out.println("Дериктория создана");
        }

        if (Files.exists(Paths.get(home, FileName))) {
            System.out.println("Файл существует");
        } else {
            System.out.println("Файла сохранения нет. Создаем новый");
            try {
                Files.createFile(Paths.get(home, "Data.csv"));
            } catch (IOException e) {
                throw new ManagerSaveException("Невозможно создать файл для сохранения" + e.getMessage());
            }
            System.out.println("Файл создан");
        }
    }

    /* ЧТЕНИЕ ФАЙЛА В СТРОКУ*/
    public String readFileInString(File fileForSave) throws IOException {
        checkOrCreateDirAndFile(fileForSave);
        StringJoiner value = new StringJoiner("\n");
        try (Reader fileReader = new FileReader(fileForSave);
             BufferedReader br = new BufferedReader(fileReader);) {
            while (br.ready()) {
                String line = br.readLine();
                value.add(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файла сохранения" + e.getMessage());
        }
        return value.toString();
    }


    /* ТАСКИ И ИСТОРИЯ В СТРОКУ И В ФАЙЛ*/
    public void save() {// он будет сохранять текущее состояние менеджера в указанный файл
        final String HEAD_SAVE_FILE = "id,type,name,status,description,epic";
        StringJoiner managerData = new StringJoiner("\n");
        managerData.add(HEAD_SAVE_FILE);
        HashMap<Integer, Task> allTask = new HashMap<>();
        allTask.putAll(normalTasks);
        allTask.putAll(epicTasks);
        allTask.putAll(subTasks);
        for (Task task : allTask.values()) {
            managerData.add(task.toString());
        }
        if (!historyManager.getHistory().isEmpty()) {
            managerData.add("\n    \n" + historyToString(historyManager));
        }
        try (FileWriter fileWriter = new FileWriter(fileForSave)) {
            fileWriter.write(managerData.toString());
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
            case "NEW":
                return TaskStatus.NEW;
            case "IN_PROGRESS":
                return TaskStatus.IN_PROGRESS;
            case "DONE":
                return TaskStatus.DONE;
            default:
                return null;
        }
    }

    /*ВОССТАНОВЛЕНИЕ МЕНЕДЖЕРА ИЗ СТРОКИ*/
    void managerFromString(String value) {
        String splitter = "\n    \n";
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
                type = TaskType.SUBTASK;
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

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask, Integer epicId) {
        super.addSubTask(subTask, epicId);
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public Task getTaskById(Integer id) {
        historyManager.add(normalTasks.get(id));
        save();
        return normalTasks.get(id);

    }

    @Override
    public Epic getEpicById(Integer id) {
        historyManager.add(epicTasks.get(id));
        save();
        return epicTasks.get(id);

    }

    @Override
    public SubTask getSubTaskById(Integer id) {
        historyManager.add(subTasks.get(id));
        save();
        return subTasks.get(id);
    }

    @Override
    public void updateTask(Integer taskID, Task newTaskObject) {
        super.updateTask(taskID, newTaskObject);
        save();
    }

    @Override
    public void updateEpic(Integer taskID, Epic newTaskObject) {
        super.updateEpic(taskID, newTaskObject);
        save();
    }

    @Override
    public void updateSubTask(Integer taskID, SubTask newTaskObject) {
        super.updateSubTask(taskID, newTaskObject);
        save();
    }

    @Override
    public void changeEpicStatus(Integer epicID) {
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
