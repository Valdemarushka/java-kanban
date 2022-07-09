package managers;

import exeption.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.util.StringJoiner;

import static java.nio.file.Files.createDirectory;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private final File fileForSave;
    final String HEAD_SAVE_FILE = "id,type,name,status,description,startTime,duration,epic";
    final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm");

    public FileBackedTasksManager(File fileForSave) {
        this.fileForSave = fileForSave;
        String value = readFileInString(fileForSave);
        managerFromString(value);
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
    }


    //__________Проверка/создание файла и дериктории__________
    void checkOrCreateDirAndFile(File fileForSave) {
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

    //__________Конвертация файла в строку__________
    public String readFileInString(File fileForSave) {
        checkOrCreateDirAndFile(fileForSave);
        StringJoiner value = new StringJoiner("\n");
        try (BufferedReader br = new BufferedReader(new FileReader(fileForSave))) {
            while (br.ready()) {
                String line = br.readLine();
                value.add(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файла сохранения" + e.getMessage());
        }
        return value.toString();
    }

    //__________Конвертация информации в строку+запись в файл__________
    public void save() {
        StringJoiner managerData = new StringJoiner("\n");
        managerData.add(HEAD_SAVE_FILE);
        for (Task task : normalTasks.values()) {
            managerData.add(task.toString());
        }
        for (Task task : epicTasks.values()) {
            managerData.add(task.toString());
        }
        for (Task task : subTasks.values()) {
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

    //__________Конвертация истории в строку__________
    static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringJoiner historyInString = new StringJoiner(",");
        for (Task task : history) {
            historyInString.add(Integer.toString(task.getId()));
        }
        return historyInString.toString();
    }


    //__________Задаине типа таска__________
    TaskStatus setTaskType(String value) {
        if (value.equals("null")) {
            return null;
        } else {
            return TaskStatus.valueOf(value);
        }
    }

    //__________Восстановление менеджера из строки__________
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

    //__________Восстановление тасков из строки__________
    void tasksFromString(String taskString) {
        String[] splitTasksInString = taskString.split("\n");
        for (int i = 1; i < splitTasksInString.length; i++) {

            String[] splitValue = splitTasksInString[i].split(",");
            Integer id = Integer.parseInt(splitValue[0]);
            TaskType taskType = TaskType.valueOf(splitValue[1]);
            String name = splitValue[2];
            TaskStatus status = setTaskType(splitValue[3]);

            LocalDateTime startTime;
            if (splitValue[4].equals("null")) {
                startTime = null;
            } else {
                startTime = LocalDateTime.parse(splitValue[4], DATE_FORMATTER);
            }

            Duration duration;
            if (splitValue[5].equals("null")) {
                duration = null;
            } else {
                duration = Duration.parse(splitValue[5]);
            }

            String description = splitValue[6];
            if (taskType == TaskType.TASK) {
                Task task = new Task(taskType, name, status, startTime, duration, description);
                task.setId(id);
                normalTasks.put(task.getId(), task);
                if (taskIndex <= task.getId()) {
                    taskIndex = task.getId() + 1;
                }

            } else if (taskType == TaskType.EPIC) {
                Epic epic = new Epic(taskType, name, description, status);
                epic.setId(id);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                epic.setEndTime(epic.getEndTime());
                epicTasks.put(epic.getId(), epic);
                if (taskIndex < epic.getId()) {
                    taskIndex = epic.getId() + 1;
                }

            } else {
                int epicId = Integer.parseInt(splitValue[7]);
                SubTask subTask = new SubTask(taskType, name, status, startTime, duration, description, epicId);
                subTask.setId(id);
                subTasks.put(subTask.getId(), subTask);
                Epic epic = epicTasks.get(epicId);
                epic.addNewSubtaskInEpic(subTask);
                if (taskIndex < subTask.getId()) {
                    taskIndex = subTask.getId() + 1;
                }
            }
        }
    }

    //__________Восстановление истории просмотров тасков и сорторовки из строки__________
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
        sortedTasks.addAll(normalTasks.values());
        sortedTasks.addAll(epicTasks.values());
        sortedTasks.addAll(subTasks.values());
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
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
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
        // тут проблема в том что  save() должен стоять именно мужду двух этих методов. по этому я и не использую
        // родительские методы
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

}
