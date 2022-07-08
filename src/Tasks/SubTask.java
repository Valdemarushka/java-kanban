package Tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubTask extends Task {
    Integer epicId;

    public SubTask(TaskType type, String name, TaskStatus status, LocalDateTime startTime,
                   Duration duration, String description, Integer epicId) {
        super(type, name, status, startTime, duration, description);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm");
        String formatDateTime = getStartTime().format(formatter);
        return String.join(",", getId().toString(), getType().toString(), getName(), getStatus().toString(),
                formatDateTime, getDuration().toString(), getDescription(), getEpicId().toString());
    }
}
