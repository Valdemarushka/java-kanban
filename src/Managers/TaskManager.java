package Managers;

import Tasks.Epic;
import Tasks.SubTask;
import Tasks.Task;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {


    void addTask(Task task) throws IOException;

    void addEpic(Epic epic) throws IOException;

    void addSubTask(SubTask subTask, Integer epicId) throws IOException;

    void deleteAllTask() throws IOException;

    void deleteAllEpic() throws IOException;

    void deleteAllSubTask() throws IOException;

    void deleteTaskById(Integer Id) throws IOException;

    void deleteEpicById(Integer id) throws IOException;

    void deleteSubTaskById(Integer id) throws IOException;

    Task getTaskById(Integer id) throws IOException;

    Epic getEpicById(Integer id) throws IOException;

    SubTask getSubTaskById(Integer id) throws IOException;

    void updateTask(Integer taskID, Task newTaskObject) throws IOException;

    void updateEpic(Integer taskID, Epic newTaskObject) throws IOException;

    void updateSubTask(Integer taskID, SubTask newTaskObject) throws IOException;

    void changeEpicStatus(Integer epicID) throws IOException;

    void viewAllTask(HashMap<Integer, Task> map);

    HashMap<Integer, SubTask> viewSubTaskOfEpic(Integer epicID);

    List<Task> getTaskHistory();

}
