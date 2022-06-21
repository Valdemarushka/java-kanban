package Tasks;

import java.util.StringJoiner;

public class Task {

    private Integer id;
    private TaskType type;
    private String name;
    private String description;
    private TaskStatus status;

    public Task(TaskType type, String name, TaskStatus status, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.join(",", id.toString(), type.toString(), name, status.toString(), description);

    }


}
