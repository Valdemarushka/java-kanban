/*
package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import tasks.Task;
import tasks.TaskStatus;
import tasks.TaskType;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskAdapter extends TypeAdapter<Task> {

    DateTimeFormatter formatterReader = DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm");

    @Override
    public void write(JsonWriter writer, Task task) throws IOException {
        writer.beginObject();
        writer.name("id");
        writer.value(task.getId());
        writer.name("type");
        writer.value(task.getType().toString());
        writer.name("name");
        writer.value(task.getName());
        writer.name("description");
        writer.value(task.getDescription());
        writer.name("status");
        writer.value(task.getStatus().toString());
        writer.name("duration");
        writer.value(task.getDuration().toString());
        writer.name("startTime");
        writer.value(task.getStartTime().toString());
        writer.endObject();
    }


    @Override
    public Task read(JsonReader reader) throws IOException {
        Task task = new Task(TaskType.TASK, "", TaskStatus.NEW, null, null, null);
        reader.beginObject();
        String fieldname = null;
        while (reader.hasNext()) {
            JsonToken token = reader.peek();

            if (token.equals(JsonToken.NAME)) {
                fieldname = reader.nextName();
            }
            if ("id".equals(fieldname)) {
                token = reader.peek();
                task.setId(reader.nextInt());
            }
            if ("type".equals(fieldname)) {
                token = reader.peek();
                task.setType(TaskType.valueOf(reader.nextString()));
            }

            if ("name".equals(fieldname)) {
                token = reader.peek();
                task.setName(reader.nextString());
            }
            if ("description".equals(fieldname)) {
                token = reader.peek();
                task.setDescription(reader.nextString());
            }
            if ("status".equals(fieldname)) {
                token = reader.peek();
                String typeString = reader.nextString();
                switch (typeString) {
                    case "null":
                        task.setStatus(null);
                        break;
                    case "DONE":
                        task.setStatus(TaskStatus.DONE);
                        break;
                    case "IN_PROGRESS":
                        task.setStatus(TaskStatus.IN_PROGRESS);
                        break;
                    default:
                        task.setStatus(TaskStatus.NEW);
                        break;
                }
            }
            if ("duration".equals(fieldname)) {
                token = reader.peek();
                task.setDuration(Duration.parse(reader.nextString()));
            }
            if ("startTime".equals(fieldname)) {
                token = reader.peek();
                task.setStartTime(LocalDateTime.parse(reader.nextString(), formatterReader));
            }
        }
        reader.endObject();
        return task;
    }
}

*/
