package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {

    private Integer id;
    private TaskType type;
    private String name;
    private String description;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(TaskType type, String name, TaskStatus status, LocalDateTime startTime, Duration duration,
                String description) {
        this.type = type;
        this.name = name;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.description = description;
    }

    public Task(TaskType type, String name, String description, TaskStatus status) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = status;
    }


    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
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

    public String getIdInString() {
        if (id == null) {
            return null;
        } else {
            return id.toString();
        }

    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm");
        String formatDateTime;
        if (startTime == null) {
            formatDateTime = "null";
        } else {
            formatDateTime = startTime.format(formatter);
        }

        String idString;
        if (id == null) {
            idString = "null";
        } else {
            idString = id.toString();
        }

        String taskStatus;
        if (status == null) {
            taskStatus = "null";
        } else {
            taskStatus = status.toString();
        }

        String taskDuration;
        if (duration == null) {
            taskDuration = "null";
        } else {
            taskDuration = duration.toString();
        }


        return String.join(",", idString, type.toString(), name, taskStatus, formatDateTime,
                taskDuration, description);
    }
}
