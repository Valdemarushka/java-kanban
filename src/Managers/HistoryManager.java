package Managers;

import Tasks.Task;

import java.util.ArrayList;

public interface HistoryManager {

    void addInHistory(Task task);

    ArrayList<Task> getHistory();
}
