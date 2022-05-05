import java.util.HashMap;

public class GetTaskById {
    Object getTaskById(HashMap<String, Object> map, String Id) {
        if (map.containsKey(Id)) {
            return map.get(Id);
        } else {
            return null;
        }
    }
}
