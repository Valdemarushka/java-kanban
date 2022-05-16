import Tasks.Epic;
import Tasks.SubTask;
import Tasks.Task;
import Tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {
    HashMap<Integer, Task> normalTasks = new HashMap<>();
    HashMap<Integer, Epic> epicTasks = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();
    public int taskIndex = 1;


    public void addTask(Task task);

    public void addEpic(Epic epic);

    public void addSubTask(SubTask subTask, Integer epicId);

    public void deleteAllTask();

    public void deleteAllEpic();

    public void deleteAllSubTask();

    public void deleteTaskById(Integer Id);

    public void deleteEpicById(Integer id) ;

    public void deleteSubTaskById(Integer id) ;

    public Task getTaskById(Integer id);

    public Epic getEpicById(Integer id);

    public SubTask getSubTaskById(Integer id);

    public void updateTask(Integer taskID, Task newTaskObject) ;

    public void updateEpic(Integer taskID, Epic newTaskObject) ;

    public void updateSubTask(Integer taskID, SubTask newTaskObject) ;

    public void changeEpicStatus(Integer epicID) ;

    public void viewAllTask(HashMap<String, Object> map) ;

    public HashMap<Integer, SubTask> viewSubTaskOfEpic(Integer epicID) ;
}
