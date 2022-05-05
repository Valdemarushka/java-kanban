import java.util.ArrayList;
import java.util.HashMap;

public class UpdateTask {

    void updateTask(HashMap<String, Object> mapTask, String taskID, Task newTaskObject) {
        if (mapTask != null && newTaskObject != null) {
            mapTask.put(taskID, newTaskObject);
        }
    }

    void updateEpic(HashMap<String, Object> mapTask, String taskID, EpicTask newTaskObject) {
        if (mapTask != null && newTaskObject != null) {
            mapTask.put(taskID, newTaskObject);
        }
    }

    void updateSubTask(HashMap<String, SubTask> mapSubTask, String taskID, SubTask newTaskObject, String epicId,
                       HashMap<String, EpicTask> mapEpicTask) {
        if (mapSubTask != null && newTaskObject != null) {
            newTaskObject.setEpicId(epicId);
            mapSubTask.put(taskID, newTaskObject);
            changeEpicStatus(epicId, mapSubTask, mapEpicTask);
        }
    }

    void changeEpicStatus(String epicID, HashMap<String, SubTask> mapSubTask, HashMap<String, EpicTask> mapEpicTask) {
        ArrayList<String> subTasksStatus = new ArrayList<>();
        for (String keySubTask : mapSubTask.keySet()) {
            if (mapSubTask.get(keySubTask).getEpicId().equals(epicID)) {
                subTasksStatus.add(mapSubTask.get(keySubTask).getStatus());
            }
        }
        String epicStatus;
        if (subTasksStatus.contains("in_progress")) {
            epicStatus = "in_progress";
        } else if (!subTasksStatus.contains("done") && !subTasksStatus.contains("in_progress")) {
            epicStatus = "new";
        } else if (!subTasksStatus.contains("in_progress") && !subTasksStatus.contains("new")) {
            epicStatus = "done";
        } else {
            epicStatus = "new";
        }
        EpicTask refreshedEpic = mapEpicTask.get(epicID);
        refreshedEpic.setStatus(epicStatus);
        mapEpicTask.put(epicID, refreshedEpic);
    }
}
