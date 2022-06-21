package Tasks;

import java.util.HashMap;

public class Epic extends Task {
    HashMap<Integer, SubTask> innerSubTask = new HashMap();

    public Epic(TaskType type, String name, TaskStatus status, String description) {
        super(type, name, status, description);
    }

    public void addNewSubtaskinEpic(Integer id, SubTask subTask) {
        if (subTask != null) {
            innerSubTask.put(id, subTask);
        }
    }

    public HashMap<Integer, SubTask> getInnerSubTask() {
        return innerSubTask;
    }

    public void setInnerSubTask(HashMap<Integer, SubTask> innerSubTask) {
        if (innerSubTask != null) {
            this.innerSubTask = innerSubTask;
        }
    }


}
