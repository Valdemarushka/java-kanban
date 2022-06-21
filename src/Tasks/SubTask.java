package Tasks;

public class SubTask extends Task {
    Integer epicId;

    public SubTask(TaskType type, String name, TaskStatus status, String description, Integer epicId) {
        super(type, name, status, description);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return String.join(",", getId().toString(), getType().toString(), getName(), getStatus().toString(),
                getDescription(), getEpicId().toString());
    }
}
