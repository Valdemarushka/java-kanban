import java.util.HashMap;

public class AddTaskInHashMap {

    void addTaskInHashMap(String taskId, Object task,HashMap map)
    {
        if(map == null){
            return;
        }else{map.put(taskId,task);}


    }
}
