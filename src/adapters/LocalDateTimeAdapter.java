package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter formatterReader = DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(localDateTime.format(formatterReader));
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();//вот эта строчка вызывыет ошибки, при null значениях даты в эпиках
        if (value != null) {
            return LocalDateTime.parse(value, formatterReader);
        } else {
            return null;
        }

    }
}