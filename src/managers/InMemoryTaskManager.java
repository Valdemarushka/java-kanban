package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    public HashMap<Integer, Task> normalTasks = new HashMap<>();
    public HashMap<Integer, Epic> epicTasks = new HashMap<>();
    public HashMap<Integer, SubTask> subTasks = new HashMap<>();
    public HistoryManager historyManager = Managers.getDefaultHistory();
    public int taskIndex = 0;

    private int nextIndex() {
        return ++taskIndex;
    }

    //__________Работа с сортированным списком__________
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

    public TreeSet<Task> sortedTasks = new TreeSet<>(startTimeСomparator);

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return sortedTasks;
    }

    //__________Добавление задач__________
    @Override
    public void addTask(Task task) {
        if (normalTasks != null && task != null) {
            if (timeNotBusy(task)) {
                task.setId(nextIndex());
                normalTasks.put(task.getId(), task);
                System.out.println("Таск добавлен\n" + task);
                sortedTasks.add(task);
            } else {
                System.out.println("Пересекает время другого таска. Таск не добавлен.");
            }
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epicTasks != null && epic != null) {
            epic.setId(nextIndex());
            epic.updateEpicStatus();
            epicTasks.put(epic.getId(), epic);
            System.out.println("Эпик добавлен\n" + epic);
        }
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (subTasks != null && subTask != null && epicTasks != null) {
            if (epicTasks.containsKey(subTask.getEpicId())) {
                if (timeNotBusy(subTask)) {
                    subTask.setId(nextIndex());
                    subTasks.put(subTask.getId(), subTask);
                    System.out.println("Сабтаск добавлен\n" + subTask);
                    Epic epic = epicTasks.get(subTask.getEpicId());
                    if (epic != null) {
                        epic.addNewSubtaskInEpic(subTask);
                        updateEpic(epic.getId(), epic);
                        sortedTasks.add(subTask);
                    }
                } else {
                    System.out.println("время занято другим таском. Сабтаск не добавлен.");
                }

            } else {
                System.out.println("Нет эпика с таким Id");
            }
        }
    }

    boolean timeNotBusy(Task task) {
        if (task.getStartTime() != null && task.getEndTime() != null) {
            LocalDateTime startTask = task.getStartTime();
            LocalDateTime endTask = task.getEndTime();
            if (task.getStartTime() != null && task.getDuration() != null) {
                for (Task treeTask : getPrioritizedTasks()) {
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

    //__________Удаление задач__________
    @Override
    public void deleteAllTask() {
        if (normalTasks != null && sortedTasks != null && historyManager != null) {
            for (Task task : normalTasks.values()) {
                historyManager.remove(task.getId());
                sortedTasks.remove(task);
            }
            normalTasks.clear();
            System.out.println("Все таски удалены.");
        }
    }

    @Override
    public void deleteAllEpic() {
        if (epicTasks != null && subTasks != null && sortedTasks != null && historyManager != null) {
            for (Task task : epicTasks.values()) {
                historyManager.remove(task.getId());
            }
            for (Task task : subTasks.values()) {
                historyManager.remove(task.getId());
                sortedTasks.remove(task);
            }
            epicTasks.clear();
            subTasks.clear();
            System.out.println("Все эпики и сабтаски удалены.");
        }
    }

    @Override
    public void deleteAllSubTask() {
        if (subTasks != null && sortedTasks != null && historyManager != null) {
            for (Task task : subTasks.values()) {
                historyManager.remove(task.getId());
                sortedTasks.remove(task);
            }
            subTasks.clear();
            System.out.println("Все сабтаски удалены.");

        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        if (normalTasks != null && sortedTasks != null && historyManager != null && normalTasks.containsKey(id)) {
            sortedTasks.remove(normalTasks.get(id));
            if (historyManager.getHistory().contains(normalTasks.get(id))) {
                historyManager.remove(id);
            }
            normalTasks.remove(id);
        }
    }

    @Override
    public void deleteEpicById(Integer id) {
        if (epicTasks != null && epicTasks.containsKey(id) && subTasks != null && sortedTasks != null) {
            historyManager.remove(id);
            Epic epic = epicTasks.get(id);
            HashMap<Integer, SubTask> innerSubTaskMap = epic.getInnerSubTask();
            for (Task subtask : innerSubTaskMap.values()) {
                subTasks.remove(subtask.getId());
                sortedTasks.remove(subtask);
            }
            epicTasks.remove(id);
        }
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        if (subTasks.containsKey(id) && epicTasks != null && sortedTasks != null) {
            SubTask subTaskForDelete = subTasks.get(id);
            sortedTasks.remove(subTaskForDelete);
            Epic epicForUpdate = epicTasks.get(subTaskForDelete.getEpicId());
            HashMap<Integer, SubTask> newInnerSubTask = epicForUpdate.getInnerSubTask();
            if (newInnerSubTask != null) {
                newInnerSubTask.remove(id);
            }
            epicForUpdate.setInnerSubTask(newInnerSubTask);
            Integer epicId = epicForUpdate.getId();
            updateEpic(epicId, epicForUpdate);
            historyManager.remove(id);
            subTasks.remove(id);
            System.out.println("сабтаск удален");
        } else {
            System.out.println("сабтаск не удален");
        }

    }

    //__________Получение задач__________
    @Override
    public Task getTaskById(Integer id) {
        if (normalTasks != null && normalTasks.containsKey(id)) {
            historyManager.add(normalTasks.get(id));
            return normalTasks.get(id);
        }
        return null;
    }

    @Override
    public Epic getEpicById(Integer id) {
        if (epicTasks != null && epicTasks.containsKey(id)) {
            historyManager.add(epicTasks.get(id));
            return epicTasks.get(id);
        }
        return null;
    }

    @Override
    public SubTask getSubTaskById(Integer id) {
        if (subTasks != null && subTasks.containsKey(id)) {
            historyManager.add(subTasks.get(id));
            return subTasks.get(id);
        }
        return null;
    }

    //__________Обновление задач__________
    @Override
    public void updateTask(Integer taskID, Task newTaskObject) {
        if (normalTasks != null && newTaskObject != null && normalTasks.containsKey(taskID)) {
            newTaskObject.setId(taskID);
            normalTasks.put(taskID, newTaskObject);
            System.out.println("Эпик обновлен ");
        }
    }

    @Override
    public void updateEpic(Integer taskID, Epic newTaskObject) {
        if (epicTasks != null && newTaskObject != null && epicTasks.containsKey(taskID)) {
            newTaskObject.setId(taskID);
            newTaskObject.setInnerSubTask(epicTasks.get(taskID).getInnerSubTask());
            newTaskObject.updateEpicStatus();
            epicTasks.put(taskID, newTaskObject);
            System.out.println("Эпик обновлен ");
        }
    }

    @Override
    public void updateSubTask(Integer taskID, SubTask newTaskObject) {
        if (subTasks != null && newTaskObject != null && subTasks.containsKey(taskID)) {
            Integer epicId = subTasks.get(taskID).getEpicId();
            newTaskObject.setEpicId(epicId);
            newTaskObject.setId(taskID);
            subTasks.put(taskID, newTaskObject);
            System.out.println("Сабтаск обновлен ");
            Epic epicForUpdate = epicTasks.get(epicId);
            HashMap<Integer, SubTask> newInnerSubTask = epicForUpdate.getInnerSubTask();
            if (newInnerSubTask != null) {
                newInnerSubTask.put(taskID, newTaskObject);
            }
            epicForUpdate.setInnerSubTask(newInnerSubTask);
            updateEpic(epicId, epicForUpdate);
            System.out.println("Эпик тоже обновлен ");
        } else {
            System.out.println("ошибка");
        }
    }

    //__________Просмотр задач__________

    @Override
    public HashMap<Integer, Task> viewAllTask() {
        if (normalTasks != null) {
            return normalTasks;
        }
        return null;
    }

    @Override
    public HashMap<Integer, Epic> viewAllEpic() {
        if (epicTasks != null) {
            return epicTasks;
        }
        return null;
    }

    @Override
    public HashMap<Integer, SubTask> viewAllSubtask() {
        if (subTasks != null) {
            return subTasks;
        }
        return null;
    }

    @Override
    public HashMap<Integer, SubTask> viewSubTaskOfEpic(Integer epicID) {
        if (epicTasks != null && epicTasks.containsKey(epicID)) {
            Epic epic = epicTasks.get(epicID);
            return epic.getInnerSubTask();
        }
        return null;
    }

    @Override
    public List<Task> getTaskHistory() {
        return historyManager.getHistory();
    }
}
