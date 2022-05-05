import java.util.HashMap;

public class DeleteTaskById {
    void deleteTaskById(HashMap<String, Object> map, String Id) {
        if (map.containsKey(Id)) {
            map.remove(Id);
        }
    }
}
