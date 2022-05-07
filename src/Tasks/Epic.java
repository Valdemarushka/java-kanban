package Tasks;

import java.util.HashMap;

public class Epic extends Task {
    HashMap<Integer,SubTask> innerSubTask = new HashMap();

    public void addNewSubtaskinEpic(Integer id,SubTask subTask) {
        if(subTask!=null){
            innerSubTask.put(id,subTask);
        }
    }

    public HashMap<Integer,SubTask> getInnerSubTask() {
        return innerSubTask;
    }

    public Epic(String name, String description, String status) {
        super(name, description, status);
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
