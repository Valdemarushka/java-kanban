package Managers;

import Tasks.Epic;
import Tasks.SubTask;
import Tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {


    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask, Integer epicId);

    void deleteAllTask();

    void deleteAllEpic();

    void deleteAllSubTask();

    void deleteTaskById(Integer Id);

    void deleteEpicById(Integer id);

    void deleteSubTaskById(Integer id);

    Task getTaskById(Integer id);

    Epic getEpicById(Integer id);

    SubTask getSubTaskById(Integer id);

    void updateTask(Integer taskID, Task newTaskObject);

    void updateEpic(Integer taskID, Epic newTaskObject);

    void updateSubTask(Integer taskID, SubTask newTaskObject);

    void changeEpicStatus(Integer epicID);

    void viewAllTask(HashMap<Integer, Task> map);

    HashMap<Integer, SubTask> viewSubTaskOfEpic(Integer epicID);

    ArrayList<Task> getTaskHistory();
}
