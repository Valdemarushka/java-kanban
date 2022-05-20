package Managers;

import Tasks.Task;

import java.util.ArrayList;
import java.util.Collections;

public class InMemoryHistoryManager implements HistoryManager {

    ArrayList<Task> historyList = new ArrayList();
    final int MAX_HISTORY_SIZE = 10;

    @Override
    public void addInHistory(Task task) {
        if (task != null) {
            if (getHistory().size() < MAX_HISTORY_SIZE) {
                historyList.add(task);
            } else {
                historyList.remove(0);
                historyList.add(task);
            }
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyList;
    }
}
