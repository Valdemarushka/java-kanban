package network;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import tasks.Epic;
import tasks.SubTask;
import tasks.TaskStatus;
import tasks.TaskType;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubTaskAdapter extends TypeAdapter<SubTask> {

    DateTimeFormatter formatterReader = DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm");

    @Override
    public void write(JsonWriter writer, SubTask subTask) throws IOException {
        writer.beginObject();
        writer.name("epicId");
        writer.value(subTask.getEpicId());
        writer.name("id");
        writer.value(subTask.getId());
        writer.name("type");
        writer.value(subTask.getType().toString());
        writer.name("name");
        writer.value(subTask.getName());
        writer.name("description");
        writer.value(subTask.getDescription());
        writer.name("status");
        writer.value(subTask.getStatus().toString());
        writer.name("duration");
        writer.value(subTask.getDuration().toString());
        writer.name("startTime");
        writer.value(subTask.getStartTime().format(formatterReader));
        writer.endObject();
    }


    @Override
    public SubTask read(JsonReader reader) throws IOException {
        reader.beginObject();
        Integer id = null;
        TaskType type = null;
        String name = "";
        String description = "";
        TaskStatus status = null;
        Duration duration = null;
        LocalDateTime startTime = null;
        Integer epicId = null;

        String fieldname = null;
        while (reader.hasNext()) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.NAME)) {
                //get the current token
                fieldname = reader.nextName();
            }
            if ("id".equals(fieldname)) {
                //move to next token
                token = reader.peek();
                id = reader.nextInt();
            }
            if ("epicId".equals(fieldname)) {
                //move to next token
                token = reader.peek();
                epicId = reader.nextInt();
            }

            if ("type".equals(fieldname)) {
                //move to next token
                token = reader.peek();
                String typeString = reader.nextString();
                if (typeString.equals("null")) {
                    type = null;
                } else if (typeString.equals("TASK")) {
                    type = TaskType.TASK;
                } else if (typeString.equals("EPIC")) {
                    type = TaskType.EPIC;
                } else {
                    type = TaskType.SUBTASK;
                }
            }
            if ("name".equals(fieldname)) {
                //move to next token
                token = reader.peek();
                name = reader.nextString();
            }

            if ("description".equals(fieldname)) {
                //move to next token
                token = reader.peek();
                description = reader.nextString();
            }

            if ("status".equals(fieldname)) {
                //move to next token
                token = reader.peek();
                String typeString = reader.nextString();
                if (typeString.equals("null")) {
                    status = null;
                } else if (typeString.equals("NEW")) {
                    status = TaskStatus.NEW;
                } else if (typeString.equals("IN_PROGRESS")) {
                    status = TaskStatus.IN_PROGRESS;
                } else {
                    status = TaskStatus.DONE;
                }
            }

            if ("duration".equals(fieldname)) {
                //move to next token
                token = reader.peek();
                duration = Duration.parse(reader.nextString());
            }

            if ("startTime".equals(fieldname)) {
                //move to next token
                token = reader.peek();
                startTime = LocalDateTime.parse(reader.nextString(), formatterReader);
            }
            reader.endObject();
        }
        SubTask subTask = new SubTask(type, name, status, startTime, duration, description, epicId);
        subTask.setId(id);
        return subTask;
    }
}

