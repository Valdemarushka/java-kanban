package Tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Epic extends Task {
    HashMap<Integer, SubTask> innerSubTask = new HashMap();
    LocalDateTime endTime;

    public Epic(TaskType type, String name, String description,TaskStatus status) {
        super(type, name, description, status);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void refreshTimeEpic(){
        if (getInnerSubTask().isEmpty()) {
         setStartTime(null);
         setEndTime(null);
         setDuration(null);
            System.out.println("Не удалось установить время эпика");
        } else {
            LocalDateTime startTime = LocalDateTime.of(3000, 1, 1, 1, 1, 1);
            LocalDateTime endTime = LocalDateTime.of(1000, 1, 1, 1, 1, 1);
            Duration duration = Duration.ZERO;
            for (SubTask subTask : innerSubTask.values()) {
                LocalDateTime subTaskStartTime = subTask.getStartTime();
                LocalDateTime subTaskEndTime = subTask.getEndTime();
                duration = duration.plus(subTask.getDuration());
                if (subTaskStartTime.isBefore(startTime)) {
                    startTime = subTaskStartTime;
                }
                if (subTaskEndTime.isAfter(endTime)) {
                    endTime = subTaskEndTime;
                }
            }
            setStartTime(startTime);
            System.out.println("установлена дата начала эпика"+startTime);
            setEndTime(endTime);
            System.out.println("установлена дата конца эпика"+endTime);
            setDuration(duration);
            System.out.println("установлена продолжительность эпика"+duration);

        }
    }

    public void addNewSubtaskInEpic(SubTask subTask) {
        if (subTask != null) {
            innerSubTask.put(subTask.getId(), subTask);
            refreshTimeEpic();
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
