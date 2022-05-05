import java.util.HashMap;

public class ViewSubTaskOfEpic {

    Object viewSubTaskOfEpic(String epicID, HashMap<String, SubTask> mapSubTask, HashMap<String,
            EpicTask> mapEpicTask) {
        HashMap<String, SubTask> subTaskOfEpic = new HashMap<>();
        for (String keySubTask : mapSubTask.keySet()) {
            if (mapSubTask.get(keySubTask).getEpicId().equals(epicID)) {
                subTaskOfEpic.put(keySubTask, mapSubTask.get(keySubTask));
            }
        }
        return subTaskOfEpic;
    }
}
