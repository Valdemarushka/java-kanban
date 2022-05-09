package Tasks;

import java.util.HashMap;

public class Epic extends Task {
    HashMap<Integer, SubTask> innerSubTask = new HashMap();

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
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

    @Override
    public String toString() {
        return "EpicTask{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", innerSubTask='" + getInnerSubTask() + '\'' +
                '}';
    }
}
