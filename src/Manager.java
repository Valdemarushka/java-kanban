import Tasks.Epic;
import Tasks.SubTask;
import Tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    HashMap<Integer, Task> normalTasks = new HashMap<>();
    HashMap<Integer, Epic> epicTasks = new HashMap<>();
    HashMap<Integer, SubTask> epicSubTasks = new HashMap<>();
    public int taskIndex = 1;


    void addTask(Task task) {
        if (normalTasks != null) {
            Integer id = taskIndex;
            taskIndex++;
            normalTasks.put(id, task);
        }
    }

    void addEpic(Epic epic) {
        if (epicTasks != null) {
            Integer id = taskIndex;
            taskIndex++;
            epicTasks.put(id, epic);
        }
    }

    void addSubTask(SubTask subTask, Integer epicId) {
        if (epicSubTasks != null && subTask != null) {
            Integer id = taskIndex;
            taskIndex++;
            epicSubTasks.put(id, subTask);

            Epic epic = epicTasks.get(epicId);
            epic.addNewSubtaskinEpic(taskIndex, subTask);
        }
    }

    void deleteAllTask(HashMap<String, Object> map) {
        if (map != null) {
            map.clear();
        }
    }

    void deleteTaskById(HashMap<Integer, Object> map, Integer Id) {
        if (map.containsKey(Id)) {
            map.remove(Id);
        }
    }

    Object getTaskById(HashMap<String, Object> map, Integer Id) {
        if (map.containsKey(Id)) {
            return map.get(Id);
        } else {
            return null;
        }
    }

    void updateTask(Integer taskID, Task newTaskObject) {
        if (normalTasks != null && newTaskObject != null) {
            normalTasks.put(taskID, newTaskObject);
        }
    }

    void updateEpic(Integer taskID, Epic newTaskObject) {
        if (epicTasks != null && newTaskObject != null) {
            epicTasks.put(taskID, newTaskObject);
        }
    }

    void updateSubTask(Integer taskID, SubTask newTaskObject, Integer epicId) {
        if (epicSubTasks != null && newTaskObject != null) {
            newTaskObject.setEpicId(epicId);
            epicSubTasks.put(taskID, newTaskObject);
            changeEpicStatus(epicId);
        }
        Epic epic = epicTasks.get(epicId);
        epic.addNewSubtaskinEpic(taskID, newTaskObject);
    }


    enum taskStatus{
        IN_PROGRESS,
        NEW,
        DONE;


    }
    void changeEpicStatus(Integer epicID) {
        ArrayList<String> subTasksStatus = new ArrayList<>();
        for (Integer keySubTask : epicSubTasks.keySet()) {
            if (epicSubTasks.get(keySubTask).getEpicId().equals(epicID)) {
                subTasksStatus.add(epicSubTasks.get(keySubTask).getStatus());
            }
        }
        String epicStatus;

        if (subTasksStatus.contains(taskStatus.IN_PROGRESS.toString())) {
            epicStatus = taskStatus.IN_PROGRESS.toString();
        } else if (!subTasksStatus.contains(taskStatus.DONE.toString()) && !subTasksStatus.contains(taskStatus.IN_PROGRESS.toString())) {
            epicStatus = taskStatus.NEW.toString();
        } else if (!subTasksStatus.contains(taskStatus.IN_PROGRESS.toString()) && !subTasksStatus.contains("NEW")) {
            epicStatus = taskStatus.DONE.toString();
        } else {
            epicStatus = taskStatus.NEW.toString();
        }
        Epic refreshedEpic = epicTasks.get(epicID);
        refreshedEpic.setStatus(epicStatus);
        epicTasks.put(epicID, refreshedEpic);
    }

    void viewAllTask(HashMap<String, Object> map) {
        if (map != null) {
            System.out.println(map);
        }
    }

    HashMap<Integer, SubTask> viewSubTaskOfEpic(Integer epicID) {
        Epic epic = epicTasks.get(epicID);
        return epic.getInnerSubTask();
    }
}
