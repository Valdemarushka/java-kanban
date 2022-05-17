import Tasks.Task;

import java.util.ArrayList;

public interface HistoryManager {
    ArrayList<Task> historyList = new ArrayList<>();

    void addInHistory(Task task);

     ArrayList<Task> getHistory();
}
