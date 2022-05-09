import Tasks.Epic;
import Tasks.SubTask;
import Tasks.Task;
import Tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    HashMap<Integer, Task> normalTasks = new HashMap<>();
    HashMap<Integer, Epic> epicTasks = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();
    public int taskIndex = 1;


    void addTask(Task task) {
        if (normalTasks != null) {
            task.setId(taskIndex);
            normalTasks.put(task.getId(), task);
            taskIndex++;
        }
    }

    void addEpic(Epic epic) {
        if (epicTasks != null) {
            epic.setId(taskIndex);
            epicTasks.put(epic.getId(), epic);
            taskIndex++;
        }
    }

    void addSubTask(SubTask subTask, Integer epicId) {
        if (subTasks != null && subTask != null) {
            subTask.setId(taskIndex);
            subTasks.put(subTask.getId(), subTask);
            Epic epic = epicTasks.get(epicId);
            epic.addNewSubtaskinEpic(taskIndex, subTask);
            changeEpicStatus(epicId);
            taskIndex++;
        }
    }

    void deleteAllTask() {
        if (normalTasks != null) {
            normalTasks.clear();
        }
    }

    void deleteAllEpic() {
        if (epicTasks != null) {
            epicTasks.clear();
            subTasks.clear();
        }
    }

    void deleteAllSubTask() {
        if (subTasks != null) {
            subTasks.clear();
            epicTasks.clear();
        }
    }

    void deleteTaskById(Integer Id) {
        if (normalTasks.containsKey(Id)) {
            normalTasks.remove(Id);
        }
    }

    void deleteEpicById(Integer id) {
        if (epicTasks != null && epicTasks.containsKey(id) && subTasks != null) {
            HashMap<Integer, SubTask> refreshSubTasks = new HashMap<>();
            for (Integer subTasksKey : subTasks.keySet()) {
                if (subTasks.get(subTasksKey).getEpicId() != id) {
                    refreshSubTasks.put(subTasksKey, subTasks.get(subTasksKey));

                }
            }
            epicTasks.remove(id);
            subTasks = refreshSubTasks;
        }
    }

    void deleteSubTaskById(Integer id) {
        if (subTasks.containsKey(id)) {
            Integer epicId = subTasks.get(id).getEpicId();
            SubTask subTaskForDelete = subTasks.get(id);
            Epic epicRefreshed = epicTasks.get(epicId);
            HashMap<Integer, SubTask> newInnerSubTask = epicRefreshed.getInnerSubTask();
            if (newInnerSubTask != null) {
                epicTasks.get(subTaskForDelete.getEpicId()).setInnerSubTask(newInnerSubTask);
            }
            subTasks.remove(id);
            changeEpicStatus(epicId);
        }
    }


    Object getTaskById(HashMap<String, Object> map, Integer id) {
        return map.get(id);
    }

    void updateTask(Integer taskID, Task newTaskObject) {
        if (normalTasks != null && newTaskObject != null) {
            newTaskObject.setId(taskID);
            normalTasks.put(taskID, newTaskObject);
        }
    }

    void updateEpic(Integer taskID, Epic newTaskObject) {
        if (epicTasks != null && newTaskObject != null) {
            newTaskObject.setId(taskID);
            newTaskObject.setInnerSubTask(epicTasks.get(taskID).getInnerSubTask());
            epicTasks.put(taskID, newTaskObject);
        }
    }

    void updateSubTask(Integer taskID, SubTask newTaskObject) {
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


    void changeEpicStatus(Integer epicID) {

        ArrayList<TaskStatus> subTasksStatus = new ArrayList<>();
        for (Integer keySubTask : subTasks.keySet()) {
            if (subTasks.get(keySubTask).getEpicId().equals(epicID)) {
                subTasksStatus.add(subTasks.get(keySubTask).getStatus());
            }
        }

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
