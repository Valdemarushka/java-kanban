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


    public void addTask(Task task) {
        if (normalTasks != null) {
            task.setId(taskIndex);
            normalTasks.put(task.getId(), task);
            taskIndex++;
        }
    }

    public void addEpic(Epic epic) {
        if (epicTasks != null) {
            epic.setId(taskIndex);
            epicTasks.put(epic.getId(), epic);
            taskIndex++;
        }
    }

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

    public void deleteAllTask() {
        if (normalTasks != null) {
            normalTasks.clear();
        }
    }

    public void deleteAllEpic() {
        if (epicTasks != null) {
            epicTasks.clear();
            subTasks.clear();
        }
    }

    public void deleteAllSubTask() {
        if (subTasks != null) {
            subTasks.clear();
            epicTasks.clear();
        }
    }

    public void deleteTaskById(Integer Id) {
        normalTasks.remove(Id);
    }

    public void deleteEpicById(Integer id) {//тут действую черездобавление, потому что при удалении напрямую из мапы
        //порождается какое то исключение,которое не могу победить:(
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

    public void deleteSubTaskById(Integer id) {//сам ничего не понял что сделал ночью:D переписал заново.
        //удивительно что оно работало вроде:)
        if (subTasks.containsKey(id)) {
            SubTask subTaskForDelete = subTasks.get(id);
            HashMap<Integer, SubTask> newInnerSubTask = epicTasks.get(subTaskForDelete.getEpicId()).getInnerSubTask();
            if (newInnerSubTask != null) {
                newInnerSubTask.remove(id);
            }
            subTasks.remove(id);
            changeEpicStatus(subTaskForDelete.getEpicId());
        }
    }


    public Task getTaskById(Integer id) {
        return normalTasks.get(id);
    }

    public Epic getEpicById(Integer id) {
        return epicTasks.get(id);
    }

    public SubTask getSubTaskById(Integer id) {
        return subTasks.get(id);
    }


    public void updateTask(Integer taskID, Task newTaskObject) {
        if (normalTasks != null && newTaskObject != null) {
            newTaskObject.setId(taskID);
            normalTasks.put(taskID, newTaskObject);
        }
    }

    public void updateEpic(Integer taskID, Epic newTaskObject) {
        if (epicTasks != null && newTaskObject != null) {
            newTaskObject.setId(taskID);
            newTaskObject.setInnerSubTask(epicTasks.get(taskID).getInnerSubTask());
            epicTasks.put(taskID, newTaskObject);
        }
    }

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


    public void changeEpicStatus(Integer epicID) {

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

    public void viewAllTask(HashMap<String, Object> map) {
        if (map != null) {
            System.out.println(map);
        }
    }

    public HashMap<Integer, SubTask> viewSubTaskOfEpic(Integer epicID) {
        Epic epic = epicTasks.get(epicID);
        return epic.getInnerSubTask();
    }
}
