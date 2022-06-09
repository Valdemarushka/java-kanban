package Managers;

import Tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(Integer id);

    List<Task> getHistory();

    public boolean containTaskInHistory(Integer id);

}
