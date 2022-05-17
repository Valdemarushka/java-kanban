import java.util.List;
import java.util.ArrayList;
public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
// почитал ответы в слаке еще раз на свежую голову. что то более менее вроде понять удалось. но не до конца:)
}
