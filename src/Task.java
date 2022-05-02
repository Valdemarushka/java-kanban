public class Task {
     String normalTaskId;
     String name;
     String description;
     Integer status;

    public Task(String normalTaskId, String name, String description,  Integer status) {
        this.normalTaskId = normalTaskId;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getNormalTaskId() {
        return normalTaskId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNormalTaskId(String normalTaskId) {
        this.normalTaskId = normalTaskId;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
