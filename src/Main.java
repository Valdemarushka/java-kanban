import Tasks.Epic;
import Tasks.SubTask;
import Tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager manager = (InMemoryTaskManager) Managers.getDefault();
        //TaskManager manager1 = Managers.getDefault();
        //не могу понять почему, но использование закомеченной реализации не дает возможность нормально обращаться к
        // мапам менеджера.так что оставил как есть. если направите куда надо, то постараюсь исправить.


        Epic epic1 = new Epic("эпик1", "описаине эпика 1", TaskStatus.NEW);
        SubTask subTask = new SubTask("1 Подзадача эпика1", "Описаине 1 подзадачи эпика1", TaskStatus.NEW, 1);
        SubTask subTask2 = new SubTask("2 Подзадача эпика1", "Описаине 2 подзадачи эпика1", TaskStatus.NEW, 1);
        Epic epic2 = new Epic("эпик2", "описаине эпика 2", TaskStatus.NEW);
        SubTask subTask3 = new SubTask("1 Подзадача эпика2", "Описаине 1 подзадачи эпика2", TaskStatus.NEW, 4);
        Epic epic2new = new Epic("эпик2 новый", "описаине эпика 2 новый", TaskStatus.NEW);
        SubTask subTask3new = new SubTask("1 Подзадача эпика2 новая", "Описаине 1 подзадачи эпика2 новая", TaskStatus.DONE, 4);


        manager.addEpic(epic1);
        manager.addSubTask(subTask, epic1.getId());
        manager.addSubTask(subTask2, epic1.getId());
        manager.addEpic(epic2);
        manager.addSubTask(subTask3, epic2.getId());
        manager.getEpicById(1);
        manager.getSubTaskById(2);
        System.out.println("начало проверки истории");
        System.out.println(manager.getTaskHistory());
        System.out.println("конец проверки истории");

        System.out.println(epic1);
        System.out.println(subTask);
        System.out.println(subTask2);
        System.out.println(epic2);
        System.out.println(subTask3);
        System.out.println(manager.subTasks);
        System.out.println(manager.epicTasks);
        System.out.println(' ');
        System.out.println(manager.epicTasks.get(epic2.getId()));
        System.out.println("апдейт эпика 2");
        manager.updateEpic(epic2.getId(), epic2new);
        System.out.println(manager.epicTasks.get(epic2.getId()));
        System.out.println(' ');
        System.out.println(manager.subTasks.get(5));
        System.out.println(manager.epicTasks.get(epic2.getId()));
        System.out.println("апдейт сабтаска 5 '");
        manager.updateSubTask(5, subTask3new);
        System.out.println(manager.subTasks.get(5));
        System.out.println(manager.epicTasks.get(epic2.getId()));
        System.out.println(' ');
        System.out.println(manager.epicTasks);
        System.out.println(manager.subTasks);
        System.out.println("удаляем эпик 1");
        manager.deleteEpicById(1);
        System.out.println(manager.epicTasks);
        System.out.println(manager.subTasks);
        System.out.println(' ');
        System.out.println(manager.subTasks);
        System.out.println("делет сабтаска");
        manager.deleteSubTaskById(5);
        System.out.println(manager.subTasks);
        System.out.println(' ');

    }
}

