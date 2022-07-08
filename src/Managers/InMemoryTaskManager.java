package Managers;

import Tasks.Epic;
import Tasks.SubTask;
import Tasks.Task;
import Tasks.TaskStatus;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    public HashMap<Integer, Task> normalTasks = new HashMap<>();
    public HashMap<Integer, Epic> epicTasks = new HashMap<>();
    public HashMap<Integer, SubTask> subTasks = new HashMap<>();
    public HistoryManager historyManager = Managers.getDefaultHistory();
    public int taskIndex = 1;

    Comparator<Task> comparator = (Task task1, Task task2) -> {
        if (task1.getStartTime() != null && task2.getStartTime() != null) {
            if (task1.getStartTime().isBefore(task2.getStartTime())) {
                return 1;
            } else if (task1.getStartTime().equals(task2.getStartTime())) {
                return 0;
            } else return -1;
        } else if (task1.getStartTime() == null && task2.getStartTime() == null) {
            return task1.getId() - task2.getId();
        } else if (task1.getStartTime() == null && task2.getStartTime() != null) {
            return 1;
        } else {
            return -1;
        }
    };
    public TreeSet<Task> sortedTasks = new TreeSet<>(comparator);

    public void refreshSortedTasks() {
        sortedTasks.clear();
        sortedTasks.addAll(normalTasks.values());
        sortedTasks.addAll(subTasks.values());
        System.out.println("обновление сортировки прошло успешно");
    }


    boolean timeNotBusy(Task task) {
        if (task.getStartTime() != null && task.getEndTime() != null) {
            LocalDateTime startTask = task.getStartTime();
            LocalDateTime endTask = task.getEndTime();
            TreeSet<Task> tasks = getPrioritizedTasks();
            if (task.getStartTime() != null && task.getDuration() != null) {
                for (Task treeTask : tasks) {
                    if (treeTask.getStartTime() != null && treeTask.getEndTime() != null) {
                        LocalDateTime startTreeTask = treeTask.getStartTime();
                        LocalDateTime endTreeTask = treeTask.getEndTime();//извиняюсь за нагромождение
                        if (startTask.isBefore(endTreeTask) && startTask.isAfter(startTreeTask)
                                || endTask.isBefore(endTreeTask) && endTask.isAfter(startTreeTask)
                                || startTask.isBefore(startTreeTask) && endTask.isAfter(endTreeTask)
                                || startTask.equals(startTreeTask) || endTask.equals(endTreeTask)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }


    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return sortedTasks;
    }


    @Override
    public void addTask(Task task) {
        if (normalTasks != null && task != null) {
            if (timeNotBusy(task)) {
                task.setId(taskIndex);
                normalTasks.put(task.getId(), task);
                taskIndex++;
                System.out.println("Таск добавлен"+task);
                refreshSortedTasks();
            } else {
                System.out.println("Пересекает время другого таска. Таск не добавлен.");
            }
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epicTasks != null && epic != null) {
            if (timeNotBusy(epic)) {
                epic.setId(taskIndex);
                refreshEpicStatus(epic);
                epicTasks.put(epic.getId(), epic);
                taskIndex++;
                System.out.println("Эпик добавлен"+epic);
            } else {
                System.out.println("Пересекает время другого таска. Эпик не добавлен.");
            }
        }
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (subTasks != null && subTask != null && epicTasks != null) {
            if (epicTasks.containsKey(subTask.getEpicId())) {
                if (timeNotBusy(subTask)) {
                    subTask.setId(taskIndex);
                    subTasks.put(subTask.getId(), subTask);
                    System.out.println("Сабтаск добавлен"+subTask);
                    Epic epic = epicTasks.get(subTask.getEpicId());
                    if (epic != null) {
                        epic.addNewSubtaskInEpic(subTask);
                        updateEpic(epic.getId(), epic);
                        taskIndex++;
                    }
                } else {
                    System.out.println("время занято другим таском. Сабтаск не добавлен.");
                }
                refreshSortedTasks();
            } else {
                System.out.println("Нет эпика с таким Id");
            }
        }
    }

    @Override
    public void deleteAllTask() {
        if (normalTasks != null) {
            for (Integer taskId : normalTasks.keySet()) {
                historyManager.remove(taskId);
            }
            normalTasks.clear();
            refreshSortedTasks();
        }
    }

    @Override
    public void deleteAllEpic() {
        if (epicTasks != null) {
            for (Integer taskId : epicTasks.keySet()) {
                historyManager.remove(taskId);
            }
            for (Integer taskId : subTasks.keySet()) {
                historyManager.remove(taskId);
            }
            epicTasks.clear();
            subTasks.clear();
            refreshSortedTasks();
        }
    }

    @Override
    public void deleteAllSubTask() {
        if (subTasks != null) {
            for (Integer taskId : subTasks.keySet()) {
                historyManager.remove(taskId);
            }
            subTasks.clear();
            refreshSortedTasks();
        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        normalTasks.remove(id);
        historyManager.remove(id);
        refreshSortedTasks();

    }

    @Override
    public void deleteEpicById(Integer id) {
        if (epicTasks != null && epicTasks.containsKey(id) && subTasks != null) {
            Epic epic = epicTasks.get(id);
            HashMap<Integer, SubTask> innerSubTaskMap = epic.getInnerSubTask();
            for (Integer subTasksKey : innerSubTaskMap.keySet()) {
                subTasks.remove(subTasksKey);
            }
            epicTasks.remove(id);
        }
        historyManager.remove(id);
        refreshSortedTasks();
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        if (subTasks.containsKey(id)) {
            SubTask subTaskForDelete = subTasks.get(id);
            HashMap<Integer, SubTask> newInnerSubTask = epicTasks.get(subTaskForDelete.getEpicId()).getInnerSubTask();
            if (newInnerSubTask != null) {
                newInnerSubTask.remove(id);
            }
            subTasks.remove(id);
            refreshEpicStatus(epicTasks.get(subTaskForDelete.getEpicId()));
        }
        historyManager.remove(id);
        refreshSortedTasks();
    }

    @Override
    public Task getTaskById(Integer id) {
        historyManager.add(normalTasks.get(id));
        return normalTasks.get(id);
    }

    @Override
    public Epic getEpicById(Integer id) {

        historyManager.add(epicTasks.get(id));
        return epicTasks.get(id);
    }

    @Override
    public SubTask getSubTaskById(Integer id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public void updateTask(Integer taskID, Task newTaskObject) {
        if (normalTasks != null && newTaskObject != null) {
            newTaskObject.setId(taskID);
            normalTasks.put(taskID, newTaskObject);
            refreshSortedTasks();
        }
    }

    @Override
    public void updateEpic(Integer taskID, Epic newTaskObject) {
        if (epicTasks != null && newTaskObject != null) {
            newTaskObject.setId(taskID);
            newTaskObject.setInnerSubTask(epicTasks.get(taskID).getInnerSubTask());
            refreshEpicStatus(newTaskObject);
            epicTasks.put(taskID, newTaskObject);
            refreshSortedTasks();
            System.out.println("Эпик обновлен ");
        }
    }

    @Override
    public void updateSubTask(Integer taskID, SubTask newTaskObject) {
        Integer epicId = subTasks.get(taskID).getEpicId();
        if (subTasks != null && newTaskObject != null) {
            newTaskObject.setEpicId(epicId);
            newTaskObject.setId(taskID);
            subTasks.put(taskID, newTaskObject);
            refreshEpicStatus(epicTasks.get(epicId));
            Epic epic = epicTasks.get(epicId);
            epic.addNewSubtaskInEpic(newTaskObject);
            refreshSortedTasks();
        }
    }

    @Override
    public void refreshEpicStatus(Epic epic) {

        TaskStatus epicStatus;
        if (epic.getInnerSubTask().isEmpty()) {
            epicStatus = TaskStatus.NEW;
        } else {
            ArrayList<TaskStatus> subStatusList = new ArrayList<>();
            for (SubTask sub : epic.getInnerSubTask().values()) {
                subStatusList.add(sub.getStatus());
            }
            if (subStatusList.contains(TaskStatus.DONE) && (!subStatusList.contains(TaskStatus.IN_PROGRESS)
                    && !subStatusList.contains(TaskStatus.NEW))) {
                epicStatus = TaskStatus.DONE;
            } else if (subStatusList.contains(TaskStatus.NEW) && (!subStatusList.contains(TaskStatus.IN_PROGRESS)
                    && !subStatusList.contains(TaskStatus.DONE))) {
                epicStatus = TaskStatus.NEW;
            } else {
                epicStatus = TaskStatus.IN_PROGRESS;
            }
        }
        epic.setStatus(epicStatus);
    }

    @Override
    public HashMap<Integer,Task> viewAllTask() {
        if (normalTasks != null) {
            return normalTasks;
        }
        return null;
    }
    @Override
    public HashMap<Integer,Epic> viewAllEpic() {
        if (epicTasks != null) {
            return epicTasks;
        }
        return null;
    }
    @Override
    public HashMap<Integer,SubTask> viewAllSubtask() {
        if (subTasks != null) {
            return subTasks;
        }
        return null;
    }

    @Override
    public HashMap<Integer, SubTask> viewSubTaskOfEpic(Integer epicID) {
        Epic epic = epicTasks.get(epicID);
        return epic.getInnerSubTask();
    }

    @Override
    public List<Task> getTaskHistory() {
        return historyManager.getHistory();
    }
}
