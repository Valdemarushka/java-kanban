import java.util.HashMap;

public class AddEpicTaskInMap extends AddTaskInMap {

    void addTaskInHashMap(HashMap<String, EpicTask> map, EpicTask epic) {
        if (map != null) {
            String id = "ET" + (map.size() + 1);
            map.put(id, epic);
        }
    }
}
