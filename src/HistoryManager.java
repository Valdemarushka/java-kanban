import Tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {
    List<Task> historyList = new ArrayList<>();

    void addInHistory(Task task);

    ArrayList<Task> getHistory();
}
