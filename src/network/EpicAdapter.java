package network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import tasks.Epic;
import tasks.SubTask;
import tasks.TaskStatus;
import tasks.TaskType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class EpicAdapter extends TypeAdapter<Epic> {

    DateTimeFormatter formatterReader = DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm");
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    @Override
    public void write(JsonWriter writer, Epic epic) throws IOException {
        writer.beginObject();
        writer.name("innerSubTask");
        writer.value(epic.getInnerSubTask().toString());
        writer.name("endTime");
        writer.value(epic.getEndTimeString());
        writer.name("id");
        writer.value(epic.getId());
        writer.name("type");
        writer.value(epic.getType().toString());
        writer.name("name");
        writer.value(epic.getName());
        writer.name("description");
        writer.value(epic.getDescription());
        writer.name("status");
        writer.value(epic.getStatus().toString());
        writer.name("duration");
        writer.value(epic.getDurationString());
        writer.name("startTime");
        writer.value(epic.getStartTimeString());
        writer.endObject();
    }

    @Override
    public Epic read(JsonReader reader) throws IOException {
        reader.beginObject();
        HashMap<Integer, SubTask> innerSubTask = new HashMap<>();
        Integer id = null;
        TaskType type = null;
        String name = "";
        String description = "";
        TaskStatus status = null;
        Duration duration = null;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        String fieldname = null;

        while (reader.hasNext()) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.NAME)) {
                fieldname = reader.nextName();
            }
            if ("innerSubTask".equals(fieldname)) {
                token = reader.peek();
                String innerSubTaskString = reader.nextString();
                Type typeHashMap = new TypeToken<HashMap<Integer, SubTask>>() {
                }.getType();
                innerSubTask = gson.fromJson(innerSubTaskString, typeHashMap);
            }
            if ("endTime".equals(fieldname)) {
                token = reader.peek();
                if (reader.nextString().equals("null")) {
                    endTime = null;
                } else {
                    String endTimeString = reader.nextString();
                    endTime = LocalDateTime.parse(endTimeString, formatterReader);
                }
            }

            if ("id".equals(fieldname)) {
                token = reader.peek();
                id = reader.nextInt();
            }

            if ("type".equals(fieldname)) {
                token = reader.peek();
                type = TaskType.EPIC;
            }
            if ("name".equals(fieldname)) {
                token = reader.peek();
                name = reader.nextString();
            }

            if ("description".equals(fieldname)) {
                token = reader.peek();
                description = reader.nextString();
            }

            if ("status".equals(fieldname)) {
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
                token = reader.peek();
                if (reader.nextString().equals("null")) {
                    duration = null;
                } else {
                    duration = Duration.parse(reader.nextString());
                }

            }

            if ("startTime".equals(fieldname)) {
                token = reader.peek();
                if (reader.nextString().equals("null")) {
                    startTime = null;
                } else {
                    startTime = LocalDateTime.parse(reader.nextString(), formatterReader);
                }
            }
        }

        Epic epic = new Epic(type, name, description, status);
        epic.setInnerSubTask(innerSubTask);
        epic.setEndTime(endTime);
        epic.setId(id);
        epic.setDuration(duration);
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        reader.endObject();
        return epic;
    }
}

