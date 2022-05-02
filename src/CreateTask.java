import java.util.HashMap;
import java.util.Scanner;

public class CreateTask {

    String taskId;
    String name;
    String description;
    Integer status;


    Object createObjectTask(int taskNumber) {
        Scanner scanner = new Scanner(System.in);

        taskId = "NT" + taskNumber;

        System.out.println("Ведите заголовок задачи.");
        name = scanner.nextLine();
        if (name == null) {
            name = "Заголовок";
        }

        System.out.println("Ведите описание задачи.");
        description = scanner.nextLine();
        if (description == null) {
            description = "Описание";
        }

        status = 1;

        Task task = new Task(taskId, name, description,  status);

        return task;

    }
}
