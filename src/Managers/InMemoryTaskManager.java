package Managers;

import Tasks.Epic;
import Tasks.SubTask;
import Tasks.Task;
import Tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    public HashMap<Integer, Task> normalTasks = new HashMap<>();
    public HashMap<Integer, Epic> epicTasks = new HashMap<>();
    public HashMap<Integer, SubTask> subTasks = new HashMap<>();
    HistoryManager historyManager = Managers.getDefaultHistory();
    public int taskIndex = 1;

    @Override
    public void addTask(Task task) {
        if (normalTasks != null) {
            task.setId(taskIndex);
            normalTasks.put(task.getId(), task);
            taskIndex++;
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epicTasks != null) {
            epic.setId(taskIndex);
            epicTasks.put(epic.getId(), epic);
            taskIndex++;
        }
    }

    @Override
    public void addSubTask(SubTask subTask, Integer epicId) {
        if (subTasks != null && subTask != null) {
            subTask.setId(taskIndex);
            subTasks.put(subTask.getId(), subTask);
            Epic epic = epicTasks.get(epicId);
            epic.addNewSubtaskinEpic(taskIndex, subTask);
            changeEpicStatus(epicId);
            taskIndex++;
        }
    }

    @Override
    public void deleteAllTask() {
        if (normalTasks != null) {
            for (Integer taskId : normalTasks.keySet()) {
                historyManager.remove(taskId);
            }
            normalTasks.clear();

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
        }
    }

    @Override
    public void deleteAllSubTask() {
        if (subTasks != null) {
            for (Integer taskId : subTasks.keySet()) {
                historyManager.remove(taskId);
            }

            subTasks.clear();

        }
    }

    @Override
    public void deleteTaskById(Integer id) {
        normalTasks.remove(id);
        historyManager.remove(id);

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
            changeEpicStatus(subTaskForDelete.getEpicId());
        }
        historyManager.remove(id);
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
        }
    }

    @Override
    public void updateEpic(Integer taskID, Epic newTaskObject) {
        if (epicTasks != null && newTaskObject != null) {
            newTaskObject.setId(taskID);
            newTaskObject.setInnerSubTask(epicTasks.get(taskID).getInnerSubTask());
            epicTasks.put(taskID, newTaskObject);
        }
    }

    @Override
    public void updateSubTask(Integer taskID, SubTask newTaskObject) {
        Integer epicId = subTasks.get(taskID).getEpicId();
        if (subTasks != null && newTaskObject != null) {
            newTaskObject.setEpicId(epicId);
            newTaskObject.setId(taskID);
            subTasks.put(taskID, newTaskObject);
            changeEpicStatus(epicId);
            Epic epic = epicTasks.get(epicId);
            epic.addNewSubtaskinEpic(taskID, newTaskObject);
        }
    }

    @Override
    public void changeEpicStatus(Integer epicID) {

        ArrayList<TaskStatus> subTasksStatus = new ArrayList<>();
        for (Integer keySubTask : subTasks.keySet()) {
            if (subTasks.get(keySubTask).getEpicId().equals(epicID)) {
                subTasksStatus.add(subTasks.get(keySubTask).getStatus());
            }
        }
        System.out.println();
        TaskStatus epicStatus;
        if (subTasksStatus.contains(TaskStatus.DONE) && (!subTasksStatus.contains(TaskStatus.IN_PROGRESS)
                && !subTasksStatus.contains(TaskStatus.NEW))) {
            epicStatus = TaskStatus.DONE;
        } else if (subTasksStatus.contains(TaskStatus.NEW) && (!subTasksStatus.contains(TaskStatus.IN_PROGRESS)
                && !subTasksStatus.contains(TaskStatus.DONE))) {
            epicStatus = TaskStatus.NEW;
        } else {
            epicStatus = TaskStatus.IN_PROGRESS;
        }
        epicTasks.get(epicID).setStatus(epicStatus);
    }

    @Override
    public void viewAllTask(HashMap<Integer, Task> map) {
        if (map != null) {
            System.out.println(map);
        }
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
