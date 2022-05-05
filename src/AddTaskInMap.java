import java.util.HashMap;

public class AddTaskInMap {
    void addTaskInHashMap(HashMap<String, Object> map, Task task) {
        if (map != null) {
            String id = "NT" + (map.size() + 1);
            map.put(id, task);
        }
    }
}
