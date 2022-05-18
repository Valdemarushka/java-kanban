import Tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    ArrayList<Task> historyList = new ArrayList();

    @Override
    public void addInHistory(Task task) {
        int historySize = 10;
        if (task != null) {
            if (getHistory().size() < historySize) {
                historyList.add(task);
            } else {
                historyList.remove(0);
            }
        }
        historyList.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> history = historyList;
        return history;
    }
}
