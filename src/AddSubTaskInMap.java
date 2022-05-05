import java.util.HashMap;

public class AddSubTaskInMap {
    void addSubTaskInMap(SubTask subTask, HashMap<String, SubTask> map, String epicId) {
        if (map != null && subTask != null) {
            String id = "ST" + (map.size() + 1);
            subTask.setEpicId(epicId);
            map.put(id, subTask);
        }
    }
}
